package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.repository.ContactRepository;
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
public class JdbcContactRepository implements ContactRepository {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcPhonebookRepository.class);
    private JdbcTemplate jdbcTemplate;
    private String encryptionKey;
    
    private class UpdateContactSP extends StoredProcedure {
        
        public UpdateContactSP(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "update_contact");
            setFunction(true);
            declareParameter(new SqlOutParameter("result", Types.VARCHAR));
            declareParameter(new SqlParameter("hall", Types.VARCHAR));
            declareParameter(new SqlParameter("name", Types.VARCHAR));
            declareParameter(new SqlParameter("email", Types.VARCHAR));
            declareParameter(new SqlParameter("phone", Types.VARCHAR));
            declareParameter(new SqlParameter("comment", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            compile();
        }

        public String execute(String hall, Contact contact) {
            Map<String, Object> params = new HashMap<>();
            params.put("hall", hall);
            params.put("name", contact.getName());
            params.put("email", contact.getEmail());
            params.put("phone", contact.getPhoneNumber());
            params.put("comment", contact.getComment());
            params.put("key", encryptionKey);
            return (String) execute(params).get("result");
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
    public Contact findByRoomId(String roomId) {
        GetHallInfoSP sp = new GetHallInfoSP(jdbcTemplate);
        Map<String, String> data = sp.execute(roomId, encryptionKey);
        
        String name = (String)data.get("name");
        if(name == null || name.startsWith("*ERROR*")) {
            LOG.debug("Stored procedure returned a result specifying an error. Ignoring result. [message={}]", name);
            return null;
        } else {
            return new Contact(data.get("name"), data.get("email"), data.get("phone"), data.get("comment"));
        }
        
    }

    @Override
    public void save(String roomId, Contact contact) {
        UpdateContactSP sp = new UpdateContactSP(jdbcTemplate);
        String result = sp.execute(roomId, contact);
    }
    
}
