package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.PhonebookEntry;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author michael
 */
public class JdbcContactRepositoryIT {
    
    private JdbcContactRepository repo;
    
    @Before
    public void init() {
        repo = new JdbcContactRepository();
        DataSource dataSource = new DriverManagerDataSource("jdbc:mysql://192.168.50.2:3306/telesal", "root", "root");
        repo.setDataSource(dataSource);
        repo.setEncryptionKey("enckey");
    }
    


    /**
     * Test of save method, of class JdbcPhonebookRepository.
     */
    @Test
    public void testGetAndSave() {
        System.out.println("save");
        Contact orgEntry = repo.findByRoomId("H45-0000-1");
        
        Contact entry = new Contact("Jane Doe", "jane@doe.dk", "+4512341234", "Doooe!");
        repo.save("H45-0000-1", entry);
        
        entry = repo.findByRoomId("H45-0000-1");
        assertEquals("Jane Doe", entry.getName());
        
        
        repo.save("H45-0000-1", orgEntry);
        
        entry = repo.findByRoomId("H45-0000-1");
        assertEquals(orgEntry.getName(), entry.getName());
        
    }
    
}
