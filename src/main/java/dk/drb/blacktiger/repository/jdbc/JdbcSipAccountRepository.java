package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
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
    
    private class CreateComputerCallerSP extends StoredProcedure {
        
        public CreateComputerCallerSP(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "create_computer_caller"); //@number,@name,@email,@hall,@key
            declareParameter(new SqlParameter("number", Types.VARCHAR));
            declareParameter(new SqlParameter("name", Types.VARCHAR));
            declareParameter(new SqlParameter("email", Types.VARCHAR));
            declareParameter(new SqlParameter("hall", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            compile();
        }

        public void execute(String phoneNumber, String name, String email, String hall) {
            Map<String, Object> params = new HashMap<>();
            params.put("number", phoneNumber);
            params.put("name", name);
            params.put("email", email);
            params.put("hall", hall);
            params.put("key", encryptionKey);
            execute(params);
        }
    }
    
    private class GetSipSP extends StoredProcedure {
        
        public GetSipSP(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "get_sip_credentials"); //"+46123456789","1XO1Z08",@key,@phone,@name,@sip_id,@pass
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
    }
    @Override
    public SipAccount findOneByKeyAndPhonenumber(String key, String phoneNumber) {
        GetSipSP sp = new GetSipSP(jdbcTemplate);
        Map<String, String> map = sp.execute(phoneNumber, key);
        
        if(map.get("name") == null &&
                map.get("phoneNumber") == null &&
                map.get("sip_id") == null &&
                map.get("sip_pass") == null) {
            return null;
        } else {
            SipAccount account = new SipAccount();
            account.setName(map.get("name"));
            account.setPhoneNumber(map.get("phoneNumber"));
            account.setSipId(map.get("sip_id"));
            account.setSipPass(map.get("sip_pass"));
            return account;
        }
    }

    @Override
    public void save(String hall, SipAccount account) {
        CreateComputerCallerSP sp = new CreateComputerCallerSP(jdbcTemplate);
        sp.execute(account.getPhoneNumber(), account.getName(), account.getEmail(), hall);
    }
    
}
