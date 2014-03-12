package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.Room;
import java.io.IOException;
import java.util.List;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.action.ConfbridgeListRoomsAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.event.ConfbridgeEndEvent;
import org.asteriskjava.manager.event.ConfbridgeListRoomsEvent;
import org.asteriskjava.manager.event.ConfbridgeStartEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.internal.ResponseEventsImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author michael
 */
public class AsteriskConfbridgeRepositoryTest {
    
    private AsteriskServer server;
    private ManagerConnection managerConnection;
    private AsteriskConfbridgeRepository repo;
    private ManagerEventListener listener;
    
    private Answer<Void> answerAddEventListener() {
        return new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                listener = (ManagerEventListener) invocation.getArguments()[0];
                return null;
            }
        };
    }
    
    private Answer<ResponseEvents> answerRooms() {
        return new Answer<ResponseEvents>() {

            @Override
            public ResponseEvents answer(InvocationOnMock invocation) throws Throwable {
                ResponseEventsImpl events = new ResponseEventsImpl();
                
                ConfbridgeListRoomsEvent e = new ConfbridgeListRoomsEvent(this);
                e.setConference("H45-0000");
                events.addEvent(e);
                
                return events;
            }
        };
    }
    
    @Before
    public void init() throws Exception {
        repo = new AsteriskConfbridgeRepository();
        
        managerConnection = mock(ManagerConnection.class);
        doAnswer(answerAddEventListener()).when(managerConnection).addEventListener(any(ManagerEventListener.class));
        when(managerConnection.sendEventGeneratingAction(any(ConfbridgeListRoomsAction.class), anyLong())).then(answerRooms());
        
        server = mock(AsteriskServer.class);
        when(server.getManagerConnection()).thenReturn(managerConnection);
        
        repo.setAsteriskServer(server);
    }
    
    @Test
    public void ifRoomsAreInitiallyRead() {
        assertEquals(1, repo.findAll().size());
    }
    
    @Test
    public void ifRoomStartEventsAreHandled() {
        ConfbridgeStartEvent e = new ConfbridgeStartEvent(this);
        e.setConference("H45-0001");
        listener.onManagerEvent(e);
        assertEquals(2, repo.findAll().size());
    }
    
    @Test
    public void ifRoomEndEventsAreHandled() {
        ConfbridgeEndEvent e = new ConfbridgeEndEvent(this);
        e.setConference("H45-0000");
        listener.onManagerEvent(e);
        assertEquals(0, repo.findAll().size());
    }
    
}
