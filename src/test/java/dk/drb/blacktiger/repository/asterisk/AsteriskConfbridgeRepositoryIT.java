package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ParticipantCommentRequestEvent;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.util.Caller;
import java.util.List;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class AsteriskConfbridgeRepositoryIT {
    
    @Before
    public void setUp() {
        asteriskServer = new DefaultAsteriskServer("192.168.50.2", "vagrant", "vagrant");
        asteriskServer.initialize();
        repository = new Asterisk11ConfbridgeRepository();
        repository.setAsteriskServer(asteriskServer);
    }
    
    @After
    public void tearDown() {
        asteriskServer.shutdown();
    }
    private AsteriskServer asteriskServer;
    private Asterisk11ConfbridgeRepository repository;
    
    @Test
    public void testListRooms() throws Exception {
        Caller caller = null;
        
        try {
        List<Room> rooms = repository.findAll();
        assertEquals(0, rooms.size());
        
        caller = new Caller("H45-0002", "12345", "sip:+4500000002@192.168.50.2");
        caller.register();
        caller.call();
            
        Thread.sleep(2000);
        
        repository.handleEventQueue();
        
        rooms = repository.findAll();
        assertEquals(1, rooms.size());
        
        Room room = repository.findOne(rooms.get(0).getId());
        assertNotNull(room);
        
        } catch(Throwable t) {
            if(caller != null) {
                caller.hangup();
                Thread.sleep(200);
                caller.unregister();
                Thread.sleep(100);
            }
        }
    }

    @Test
    public void testParticipantAnswerRequest() throws Exception {
        Caller caller = null;
        final boolean[] answerRequested = {false}; 
        ConferenceEventListener eventListener = new ConferenceEventListener() {

            @Override
            public void onParticipantEvent(ConferenceEvent event) {
                if(event instanceof ParticipantCommentRequestEvent) {
                    answerRequested[0] = true;
                }
            }
        };
        
        try {
            caller = new Caller("H45-0002", "12345", "sip:+4500000002@192.168.50.2");
            caller.register();
            caller.call();

            Thread.sleep(2000);

            caller.sendDtmf('1');

            repository.addEventListener(eventListener);
            repository.handleEventQueue();
            repository.removeEventListener(eventListener);

            assertTrue(answerRequested[0]);

        
        } catch(Throwable t) {
            caller.hangup();
            Thread.sleep(200);
            caller.unregister();
            Thread.sleep(100);
        }
    }
    
    
}
