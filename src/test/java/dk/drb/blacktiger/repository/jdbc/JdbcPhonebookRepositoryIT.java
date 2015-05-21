package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.CallType;
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
public class JdbcPhonebookRepositoryIT {
    
    private JdbcPhonebookRepository repo;
    
    @Before
    public void init() {
        repo = new JdbcPhonebookRepository();
        DataSource dataSource = new DriverManagerDataSource("jdbc:mysql://192.168.50.2:3306/telesal", "root", "root");
        repo.setDataSource(dataSource);
        repo.setEncryptionKey("enckey");
    }
    
    /**
     * Test of findByCallerId method, of class JdbcPhonebookRepository.
     */
    @Test
    public void testFindByCallerId() {
        System.out.println("findByCallerId");
        
        PhonebookEntry result = repo.findByCallerId("H45-0000-1", "L000000000");
        assertNotNull("Entry was not found", result);
        
        assertEquals("John Doe", result.getName());
        assertEquals(CallType.Sip, result.getCallType());
    }

    /**
     * Test of save method, of class JdbcPhonebookRepository.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        PhonebookEntry entry = new PhonebookEntry("+4599999901", "Jane Doe", CallType.Sip);
        PhonebookEntry result = repo.save("H45-0000-1", entry);
        assertEquals("Jane Doe", result.getName());
        
        result = repo.findByCallerId("H45-0000-1", "+4599999901");
        assertEquals("Jane Doe", result.getName());
    }
    
}
