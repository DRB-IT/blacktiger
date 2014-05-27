package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
public class JdbcSipAccountRepository implements SipAccountRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcSipAccountRepository.class);
    private JdbcTemplate jdbcTemplate;
    private String encryptionKey;


    
    private class CreateComputerListenerSP extends StoredProcedure {
        
        public CreateComputerListenerSP(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "create_computer_listener"); //@e164,@name,@email,@hall,@mailtext, @key
            declareParameter(new SqlParameter("e164", Types.VARCHAR));
            declareParameter(new SqlParameter("name", Types.VARCHAR));
            declareParameter(new SqlParameter("email", Types.VARCHAR));
            declareParameter(new SqlParameter("hall", Types.VARCHAR));
            declareParameter(new SqlParameter("mailtext", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            compile();
        }

        public void execute(String phoneNumber, String name, String email, String hall, String mailText) {
            LOG.debug("Executing Stored Procedure [phoneNumber={};name={};email={};hall={};mailtext={}]", 
                    new Object[]{phoneNumber, name, email, hall, mailText});
            Map<String, Object> params = new HashMap<>();
            params.put("e164", phoneNumber);
            params.put("name", name);
            params.put("email", email);
            params.put("hall", hall);
            params.put("mailtext", mailText);
            params.put("key", encryptionKey);
            Map<String, Object> result = execute(params);
            LOG.info("Result: {}", result);
        }
    }
    
    private class SendPasswordSP extends StoredProcedure {
        
        public SendPasswordSP(JdbcTemplate jdbcTemplate) {
            //navn, mobilnummer, e-mail; salens by, salens tlf-nummer, email-subject, email-tekst til teknisk ansvarlig, email-tekst til ansøger.
            super(jdbcTemplate, "recover_password"); 
            setFunction(true);
            
            declareParameter(new SqlOutParameter("result", Types.BOOLEAN));
            declareParameter(new SqlParameter("name", Types.VARCHAR));
            declareParameter(new SqlParameter("phoneNumber", Types.VARCHAR));
            declareParameter(new SqlParameter("email", Types.VARCHAR));
            declareParameter(new SqlParameter("cityOfHall", Types.VARCHAR));
            declareParameter(new SqlParameter("phoneNumberOfHall", Types.VARCHAR));
            declareParameter(new SqlParameter("emailSubject", Types.VARCHAR));
            declareParameter(new SqlParameter("emailTextManager", Types.VARCHAR));
            declareParameter(new SqlParameter("emailTextUser", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            compile();
        }

        public void execute(String name, String phoneNumber, String email, String cityOfHall, String phoneNumberOfHall, String emailSubject, 
                String emailTextManager, String emailTextUser) {
            LOG.debug("Executing Stored Procedure [name={};phoneNumber={};email={};cityOfHall={};phoneNumberOfHall={}]", 
                    new Object[]{name, phoneNumber, email, cityOfHall, phoneNumberOfHall});
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("phoneNumber", phoneNumber);
            params.put("email", email);
            params.put("cityOfHall", cityOfHall);
            params.put("phoneNumberOfHall", phoneNumberOfHall);
            params.put("emailSubject", emailSubject);
            params.put("emailTextManager", emailTextManager);
            params.put("emailTextUser", emailTextUser);
            params.put("key", encryptionKey);
            Map<String, Object> result = execute(params);
            LOG.info("Result: {}", result);
        }
    }
    
    private class GetSipSP extends StoredProcedure {
        
        public GetSipSP(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "get_sip_credentials"); 
            declareParameter(new SqlParameter("user_number", Types.VARCHAR));
            declareParameter(new SqlParameter("user_key", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            declareParameter(new SqlOutParameter("phoneNumber", Types.VARCHAR));
            declareParameter(new SqlOutParameter("name", Types.VARCHAR));
            declareParameter(new SqlOutParameter("sip_id", Types.VARCHAR));
            declareParameter(new SqlOutParameter("sip_pass", Types.VARCHAR));
            compile();
        }

        public Map execute(String number, String key) {
            LOG.debug("Executing Stored Procedure [number=;key=]", number, key);
            Map<String, Object> params = new HashMap<>();
            params.put("user_number", number);
            params.put("user_key", key);
            params.put("key", encryptionKey);
            return execute(params);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    @PostConstruct
    protected void init() {
        Assert.notNull(encryptionKey, "Encryption key not set.");
        Assert.notNull(jdbcTemplate, "datasource not set.");
    }
    @Override
    public SipAccount findOneByKeyAndPhonenumber(String key, String phoneNumber) {
        LOG.debug("Retrieving phonenumber [key={};phoneNumber={}]", key, phoneNumber);
        GetSipSP sp = new GetSipSP(jdbcTemplate);
        Map<String, String> map = sp.execute(phoneNumber, key);
        
        if(map.get("name") == null &&
                map.get("phoneNumber") == null &&
                map.get("sip_id") == null &&
                map.get("sip_pass") == null) {
            LOG.debug("No data in data returned from stored procedure. Returning null to caller.");
            return null;
        } else {
            LOG.debug("Data successfully retrieved from stored procedure. Building and returning SipAccount object.");
            SipAccount account = new SipAccount();
            account.setName(map.get("name"));
            account.setPhoneNumber(map.get("phoneNumber"));
            account.setSipId(map.get("sip_id"));
            account.setSipPass(map.get("sip_pass"));
            return account;
        }
    }

    @Override
    public boolean create(String hall, SipAccount account, String mailText) {
        LOG.info("Creating new SipAccount. [hall={};account={}]", hall, account);
        CreateComputerListenerSP sp = new CreateComputerListenerSP(jdbcTemplate);
        
        try {
            sp.execute(account.getPhoneNumber(), account.getName(), account.getEmail(), hall, mailText);
            return true;
        } catch(Exception e) {
            LOG.error("Error while saving sipaccount.", e);
            return false;
        }
    }
    
        @Override
    public boolean sendPasswordEmail(String name, String phoneNumber, String email, String cityOfHall, String phoneNumberOfHall, String emailSubject, String emailTextManager, String emailTextUser) {
        LOG.info("Sending password for sipaccount. [name={};phoneNumber={}]", name, phoneNumber);
        SendPasswordSP sp = new SendPasswordSP(jdbcTemplate);
        
        try {
            sp.execute(name, phoneNumber, email, cityOfHall, phoneNumberOfHall, emailSubject, emailTextManager, emailTextUser);
            return true;
        } catch(Exception e) {
            LOG.error("Error while sending password.", e);
            return false;
        }
    }
}
