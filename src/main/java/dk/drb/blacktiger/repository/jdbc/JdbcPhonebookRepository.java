package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.repository.PhonebookRepository;
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
 * An implementation of the phonebook repository that integrates with the stored procedures available.
 */
public class JdbcPhonebookRepository implements PhonebookRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcPhonebookRepository.class);
    private JdbcTemplate jdbcTemplate;
    private String encryptionKey;
    
    private class GetNameStoredProcedure extends StoredProcedure {
        
        public GetNameStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "get_call_info");
            declareParameter(new SqlParameter("number", Types.VARCHAR));
            declareParameter(new SqlParameter("hall", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            declareParameter(new SqlOutParameter("e164", Types.VARCHAR));
            declareParameter(new SqlOutParameter("name", Types.VARCHAR));
            declareParameter(new SqlOutParameter("type", Types.VARCHAR));
            compile();
        }

        public Map execute(String number, String hall) {
            Map<String, Object> params = new HashMap<>();
            params.put("number", number);
            params.put("hall", hall);
            params.put("key", encryptionKey);
            return execute(params);
        }
    }
    
    private class ChangeNameStoredProcedure extends StoredProcedure {
        
        public ChangeNameStoredProcedure(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "rename_caller");
            setFunction(true);
            declareParameter(new SqlOutParameter("result", Types.VARCHAR));
            declareParameter(new SqlParameter("number", Types.VARCHAR));
            declareParameter(new SqlParameter("name", Types.VARCHAR));
            declareParameter(new SqlParameter("hall", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            compile();
        }

        public String execute(String number, String name, String hall) {
            Map<String, Object> params = new HashMap<>();
            params.put("number", number);
            params.put("name", name);
            params.put("hall", hall);
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
    public PhonebookEntry findByCallerId(String hallCalling, String number) {
        GetNameStoredProcedure procedure = new GetNameStoredProcedure(jdbcTemplate);
        Map data = procedure.execute(number, hallCalling);
        
        String name = (String)data.get("name");
        if(name.startsWith("*ERROR*")) {
            LOG.debug("Stored procedure returned a result specifying an error. Ignoring result. [message={}]", name);
            return null;
        } else {
            return new PhonebookEntry((String)data.get("e164"), name); 
        }
    }

    @Override
    public PhonebookEntry save(String hallCalling, PhonebookEntry entry) {
        ChangeNameStoredProcedure procedure = new ChangeNameStoredProcedure(jdbcTemplate);
        String name = procedure.execute(entry.getNumber(), entry.getName(), hallCalling);
        return new PhonebookEntry(entry.getNumber(), name);
    }
    
}
