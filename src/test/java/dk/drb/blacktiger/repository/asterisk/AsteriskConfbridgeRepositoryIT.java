package dk.drb.blacktiger.repository.asterisk;

import java.util.List;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class AsteriskConfbridgeRepositoryIT {
    
    public AsteriskConfbridgeRepositoryIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        asteriskServer = new DefaultAsteriskServer("localhost", "vagrant", "vagrant");
        asteriskServer.initialize();
        repository = new AsteriskConfbridgeParticipantsRepository();
        repository.setAsteriskServer(asteriskServer);
    }
    
    @After
    public void tearDown() {
    }

    private AsteriskServer asteriskServer;
    private AsteriskConfbridgeParticipantsRepository repository;
    
    /**
     * Test of onManagerEvent method, of class AsteriskConfbridgeRepository.
     */
    @Test
    public void testListRooms() {
        System.out.println("onManagerEvent");
        //List result = repository.findRooms();
        //assertTrue(result.size() > 0);
    }

    
    
}
