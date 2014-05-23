package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.RoomInfoRepository;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
public class JdbcRoomInfoRepository implements RoomInfoRepository {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcPhonebookRepository.class);
    private JdbcTemplate jdbcTemplate;
    private String encryptionKey;
    
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
    
    public Room findById(String id) {
        GetHallInfoSP sp = new GetHallInfoSP(jdbcTemplate);
        Map<String, String> data = sp.execute(id, encryptionKey);
        
        String name = (String)data.get("name");
        if(name.startsWith("*ERROR*")) {
            LOG.debug("Stored procedure returned a result specifying an error. Ignoring result. [message={}]", name);
            return null;
        } else {
            Room room = new Room(id);
            room.setCity(data.get("city"));
            room.setPostalCode(data.get("zip"));
            room.setHallNumber(data.get("hallNo"));
            room.setPhoneNumber(data.get("dialin"));
            room.setCountryCallingCode(data.get("country_code"));
            room.setName(room.getCity());
            
            if(room.getHallNumber() != null) {
                room.setName(", " + room.getHallNumber());
            }
            return room;
            
        }
        
    }
}
