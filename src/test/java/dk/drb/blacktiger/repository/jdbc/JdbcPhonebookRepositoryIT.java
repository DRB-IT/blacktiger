package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.security.StoredProcedureAuthenticationProvider;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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
        
        PhonebookEntry result = repo.findByCallerId("#00000000");
        assertNotNull(result);
        
        assertEquals("John Doe", result.getName());
        
    }

    /**
     * Test of save method, of class JdbcPhonebookRepository.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        PhonebookEntry entry = new PhonebookEntry("+4599999901", "Jane Doe");
        PhonebookEntry result = repo.save(entry);
        assertEquals("Jane Doe", result.getName());
        
        result = repo.findByCallerId("+4599999901");
        assertEquals("Jane Doe", result.getName());
    }
    
}
