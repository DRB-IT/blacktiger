package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.model.SipAccount;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 *
 * @author michael
 */
public class JdbcSipAccountRepositoryIT {
    
    private JdbcSipAccountRepository repo;
    
    @Before
    public void init() {
        repo = new JdbcSipAccountRepository();
        DataSource dataSource = new DriverManagerDataSource("jdbc:mysql://192.168.50.2:3306/telesal", "root", "root");
        repo.setDataSource(dataSource);
        repo.setEncryptionKey("enckey");
    }
    
    @Test
    public void testSaveOk() {
        SipAccount account = new SipAccount("John Doe", "john@doe.com", "+4512341234");
        boolean accepted = repo.save("H45-0000-1", account);
        assertTrue(accepted);
    }
    
    @Test
    public void testSaveFails() {
        SipAccount account = new SipAccount("Jane Doe", "jane@doe.com", "34234&%&€&#%");
        boolean accepted = repo.save("H45-0000-1", account);
        assertFalse(accepted);
    }
    
}
