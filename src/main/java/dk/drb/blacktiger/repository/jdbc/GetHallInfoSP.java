package dk.drb.blacktiger.repository.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

/**
 *
 * @author michael
 */
public class GetHallInfoSP extends StoredProcedure {
    
    public GetHallInfoSP(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "get_hall_info");
        declareParameter(new SqlParameter("hall", Types.VARCHAR));
        declareParameter(new SqlParameter("key", Types.VARCHAR));
        declareParameter(new SqlOutParameter("country_code", Types.VARCHAR));
        declareParameter(new SqlOutParameter("zip", Types.VARCHAR));
        declareParameter(new SqlOutParameter("city", Types.VARCHAR));
        declareParameter(new SqlOutParameter("hallNo", Types.VARCHAR));
        declareParameter(new SqlOutParameter("dialin", Types.VARCHAR));
        declareParameter(new SqlOutParameter("name", Types.VARCHAR));
        declareParameter(new SqlOutParameter("email", Types.VARCHAR));
        declareParameter(new SqlOutParameter("phone", Types.VARCHAR));
        declareParameter(new SqlOutParameter("comment", Types.VARCHAR));

        compile();
    }

    public Map execute(String hall, String encryptionKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("hall", hall);
        params.put("key", encryptionKey);
        return execute(params);
    }
}
