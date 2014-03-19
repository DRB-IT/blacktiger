package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.repository.PhonebookRepository;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

/**
 * An implementation of the phonebook repository that integrates with the stored procedures available.
 */
public class JdbcPhonebookRepository implements PhonebookRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcPhonebookRepository.class);
    private JdbcTemplate jdbcTemplate;
    private String encryptionKey;
    
    private class GetNameStoredProcedure extends StoredProcedure {
        
        private static final String SQL = "sysdate";

        public GetNameStoredProcedure (JdbcTemplate jdbcTemplate) {
            setJdbcTemplate(jdbcTemplate);
            setFunction(true);
            setSql("call get_call_info(?,?,?,?,?,?)");
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

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    @Override
    public PhonebookEntry findByCallerId(String number) {
        GetNameStoredProcedure procedure = new GetNameStoredProcedure(jdbcTemplate);
        Map data = procedure.execute(number, "<UNKNOWN>");
        
        return new PhonebookEntry((String)data.get("name"), (String)data.get("e164"));
    }

    @Override
    public PhonebookEntry save(PhonebookEntry entry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
