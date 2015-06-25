package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.Room;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

/**
 *
 * @author michael
 */
public class SearchHallInfoSP extends StoredProcedure {
    private class ResultMapper  implements RowMapper<Room> {

        @Override
        public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
            Room room = new Room(rs.getString("sip_id"));
            room.setPhoneNumber(rs.getString("dialin_phone"));
            
            Contact contact = new Contact(rs.getString("contact_name"),
                    rs.getString("contact_email"),
                    rs.getString("contact_phone"),
                    rs.getString("user_comment"));

            room.setContact(contact);
            return room;
        }
        
    }
    
    public SearchHallInfoSP(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "search_hall");
        
        declareParameter(new SqlParameter("search", Types.VARCHAR));
        declareParameter(new SqlParameter("key", Types.VARCHAR));
        declareParameter(new SqlReturnResultSet("rs", new ResultMapper()));
        compile();
    }

    public List<Room> execute(String search, String encryptionKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("search", search);
        params.put("key", encryptionKey);
        return (List<Room>) execute(params).get("rs");
    }
}
