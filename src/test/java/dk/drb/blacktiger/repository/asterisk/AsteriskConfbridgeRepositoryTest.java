package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEndEvent;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ConferenceStartEvent;
import dk.drb.blacktiger.model.ParticipantCommentRequestCancelEvent;
import dk.drb.blacktiger.model.ParticipantCommentRequestEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.action.ConfbridgeKickAction;
import org.asteriskjava.manager.action.ConfbridgeListAction;
import org.asteriskjava.manager.action.ConfbridgeListRoomsAction;
import org.asteriskjava.manager.action.ConfbridgeMuteAction;
import org.asteriskjava.manager.action.ConfbridgeUnmuteAction;
import org.asteriskjava.manager.event.ConfbridgeEndEvent;
import org.asteriskjava.manager.event.ConfbridgeLeaveEvent;
import org.asteriskjava.manager.event.ConfbridgeListEvent;
import org.asteriskjava.manager.event.ConfbridgeListRoomsEvent;
import org.asteriskjava.manager.event.ConfbridgeStartEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.internal.ResponseEventsImpl;
import org.asteriskjava.manager.response.ManagerResponse;
import org.junit.Before;
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
    
    private Answer<ResponseEvents> answerParticipants() {
        return new Answer<ResponseEvents>() {

            @Override
            public ResponseEvents answer(InvocationOnMock invocation) throws Throwable {
                ResponseEventsImpl events = new ResponseEventsImpl();
                NumberFormat nf = new DecimalFormat("00000000");
                for(int i=0;i<10;i++) {
                    ConfbridgeListEvent e = new ConfbridgeListEvent(this);
                    e.setConference("H45-0000");
                    e.setCallerIDnum("#" + nf.format(i));
                    e.setCallerIdName("John Doe");
                    e.setChannel("SIP/#" + nf.format(i));
                    events.addEvent(e);
                }
                return events;
            }
        };
    }
    
    private Answer<ManagerResponse> answerKick() {
        return new Answer<ManagerResponse>() {

            @Override
            public ManagerResponse answer(InvocationOnMock invocation) throws Throwable {
                ConfbridgeLeaveEvent e = new ConfbridgeLeaveEvent(this);
                e.setConference("H45-0000");
                e.setCallerIdNum("0");
                e.setChannel("SIP/#00000000");
                e.setCallerIdName("John Doe");
                listener.onManagerEvent(e);
                
                return new ManagerResponse();
            }
        };
    }
    
    private Answer<ManagerResponse> answerOK() {
        return new Answer<ManagerResponse>() {

            @Override
            public ManagerResponse answer(InvocationOnMock invocation) throws Throwable {
                return new ManagerResponse();
            }
        };
    }
    
    @Before
    public void init() throws Exception {
        repo = new AsteriskConfbridgeRepository();
        
        managerConnection = mock(ManagerConnection.class);
        doAnswer(answerAddEventListener()).when(managerConnection).addEventListener(any(ManagerEventListener.class));
        when(managerConnection.sendEventGeneratingAction(isA(ConfbridgeListAction.class), anyLong())).then(answerParticipants());
        when(managerConnection.sendEventGeneratingAction(isA(ConfbridgeListRoomsAction.class), anyLong())).then(answerRooms());
        when(managerConnection.sendAction(isA(ConfbridgeKickAction.class))).then(answerKick());
        when(managerConnection.sendAction(isA(ConfbridgeMuteAction.class))).then(answerOK());
        when(managerConnection.sendAction(isA(ConfbridgeUnmuteAction.class))).then(answerOK());
        
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
        repo.handleEventQueue();
        assertEquals(2, repo.findAll().size());
    }
    
    @Test
    public void ifRoomEndEventsAreHandled() {
        ConfbridgeEndEvent e = new ConfbridgeEndEvent(this);
        e.setConference("H45-0000");
        listener.onManagerEvent(e);
        repo.handleEventQueue();
        assertEquals(0, repo.findAll().size());
    }
    
    @Test
    public void ifParticipantsCanBeRetreived() {
        assertEquals(10, repo.findByRoomNo("H45-0000").size());
    }
    
    @Test
    public void ifOneParticipantCanBeRetreived() {
        assertNotNull(repo.findByRoomNoAndChannel("H45-0000", "SIP___#00000000"));
    }
    
    @Test
    public void ifUserCanBeKickedAndEmitsLeaveEvent() {
        repo.kickParticipant("H45-0000", "SIP/#00000000");
        repo.handleEventQueue();
        assertEquals(9, repo.findByRoomNo("H45-0000").size());
    }
    
    @Test
    public void ifUserLeavingEmitsLeaveEvent() {
        ConfbridgeLeaveEvent e = new ConfbridgeLeaveEvent(this);
        e.setCallerIdName("John Doe");
        e.setCallerIdNum("0");
        e.setChannel("SIP/#00000000");
        e.setConference("H45-0000");
        listener.onManagerEvent(e);
        repo.handleEventQueue();
        assertEquals(9, repo.findByRoomNo("H45-0000").size());
    }
    
    @Test
    public void ifUserCanHaveMutenessChanged() {
        String roomId = "H45-0000";
        String channel = "SIP___#00000000";
        
        // They are initially muted
        assertEquals(true, repo.findByRoomNoAndChannel(roomId, channel).isMuted());
        
        // So when we mute them they should stay muted
        repo.muteParticipant(roomId, channel);
        assertEquals(true, repo.findByRoomNoAndChannel(roomId, channel).isMuted());
        
        // But we should also be able to unmute them
        repo.unmuteParticipant(roomId, channel);
        assertEquals(false, repo.findByRoomNoAndChannel(roomId, channel).isMuted());
        
        // and re-mute them
        repo.muteParticipant(roomId, channel);
        assertEquals(true, repo.findByRoomNoAndChannel(roomId, channel).isMuted());
        
        
    }
    
    
    @Test
    public void ifCommentRequestEventIsHandled() {
        final List<ConferenceEvent> conferenceEvents = new ArrayList<>();
        
        repo.addEventListener(new ConferenceEventListener() {

            @Override
            public void onParticipantEvent(ConferenceEvent event) {
                conferenceEvents.add(event);
            }
        });
        
        DtmfEvent event = new DtmfEvent(this);
        event.setBegin(false);
        event.setEnd(true);
        event.setDigit("1");
        event.setChannel("SIP/#00000000");
        listener.onManagerEvent(event);
        repo.handleEventQueue();
        
        ConferenceEvent lastEvent = conferenceEvents.get(conferenceEvents.size()-1);
        assertTrue(lastEvent instanceof ParticipantCommentRequestEvent);
        
        ParticipantCommentRequestEvent commentRequestEvent = (ParticipantCommentRequestEvent) lastEvent;
        assertEquals("H45-0000", commentRequestEvent.getRoomNo());
        assertEquals("SIP___#00000000", commentRequestEvent.getChannel());
        
    }
    
    @Test
    public void ifCommentRequestCancelEventIsHandled() {
        final List<ConferenceEvent> conferenceEvents = new ArrayList<>();
        
        repo.addEventListener(new ConferenceEventListener() {

            @Override
            public void onParticipantEvent(ConferenceEvent event) {
                conferenceEvents.add(event);
            }
        });
        
        DtmfEvent event = new DtmfEvent(this);
        event.setBegin(false);
        event.setEnd(true);
        event.setDigit("0");
        event.setChannel("SIP/#00000000");
        listener.onManagerEvent(event);
        repo.handleEventQueue();
        
        ConferenceEvent lastEvent = conferenceEvents.get(conferenceEvents.size()-1);
        assertTrue(lastEvent instanceof ParticipantCommentRequestCancelEvent);
        
        ParticipantCommentRequestCancelEvent commentRequestEvent = (ParticipantCommentRequestCancelEvent) lastEvent;
        assertEquals("H45-0000", commentRequestEvent.getRoomNo());
        assertEquals("SIP___#00000000", commentRequestEvent.getChannel());
    }
    
    @Test
    public void ifConferenceStartEventIsHandled() {
        final List<ConferenceEvent> conferenceEvents = new ArrayList<>();
        
        repo.addEventListener(new ConferenceEventListener() {

            @Override
            public void onParticipantEvent(ConferenceEvent event) {
                conferenceEvents.add(event);
            }
        });
        
        ConfbridgeStartEvent event = new ConfbridgeStartEvent(this);
        event.setConference("H45-0001");
        listener.onManagerEvent(event);
        repo.handleEventQueue();
        
        assertEquals(2, repo.findAll().size());
        
        ConferenceEvent lastEvent = conferenceEvents.get(conferenceEvents.size()-1);
        assertTrue(lastEvent instanceof ConferenceStartEvent);
        
        ConferenceStartEvent confEvent = (ConferenceStartEvent) lastEvent;
        assertEquals("H45-0001", confEvent.getRoomNo());
    }
    
    @Test
    public void ifConferenceEndEventIsHandled() {
        final List<ConferenceEvent> conferenceEvents = new ArrayList<>();
        
        repo.addEventListener(new ConferenceEventListener() {

            @Override
            public void onParticipantEvent(ConferenceEvent event) {
                conferenceEvents.add(event);
            }
        });
        
        ConfbridgeEndEvent event = new ConfbridgeEndEvent(this);
        event.setConference("H45-0000");
        listener.onManagerEvent(event);
        repo.handleEventQueue();
        
        assertEquals(0, repo.findAll().size());
        assertNull(repo.findOne("H45-0000"));
        
        ConferenceEvent lastEvent = conferenceEvents.get(conferenceEvents.size()-1);
        assertTrue(lastEvent instanceof ConferenceEndEvent);
        
        ConferenceEndEvent confEvent = (ConferenceEndEvent) lastEvent;
        assertEquals("H45-0000", confEvent.getRoomNo());
    }
}
