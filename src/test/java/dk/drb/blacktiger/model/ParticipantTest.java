package dk.drb.blacktiger.model;

import java.util.Date;
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
public class ParticipantTest {
    
    
    @Test
    public void testEquals() {
        Participant instance = new Participant("channel", "callerId", "name", true, true, CallType.Sip, new Date());
        Participant instance2 = new Participant("channel", "callerId", "name", true, true, CallType.Sip, new Date());
        assertEquals(instance, instance2);
        
        instance2.setChannel("channel2");
        assertFalse(instance.equals(instance2));
        
    }

    
    
}
