package dk.drb.blacktiger.security;

import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 *
 * @author michael
 */
public class StoredProcedureAuthenticationProviderIT {
    


    /**
     * Test of authenticate method, of class StoredProcedureAuthenticationProvider.
     */
    @Test
    public void testAuthenticate() {
        System.out.println("authenticate");
        Authentication authentication = new UsernamePasswordAuthenticationToken("H45-0000", "Test-12345");
        DataSource dataSource = new DriverManagerDataSource("jdbc:mysql://192.168.50.2:3306/telesal", "root", "root");
        StoredProcedureAuthenticationProvider instance = new StoredProcedureAuthenticationProvider();
        instance.setDataSource(dataSource);
        instance.setEncryptionKey("enckey");
        
        Authentication result = instance.authenticate(authentication);
        assertNotNull(result);
        
    }

    /**
     * Test of supports method, of class StoredProcedureAuthenticationProvider.
     */
    @Test
    public void testSupports() {
        System.out.println("supports");
        StoredProcedureAuthenticationProvider instance = new StoredProcedureAuthenticationProvider();
        boolean result = instance.supports(UsernamePasswordAuthenticationToken.class);
        assertTrue(result);
    }
}