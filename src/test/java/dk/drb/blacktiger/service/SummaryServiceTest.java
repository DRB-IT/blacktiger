package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.ConferenceEndEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ConferenceStartEvent;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.ParticipantMuteEvent;
import dk.drb.blacktiger.model.ParticipantUnmuteEvent;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.model.Summary;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import java.util.Date;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author michael
 */
public class SummaryServiceTest {
    
    public SummaryServiceTest() {
    }
    
    
    private final Answer<Void> answerAddEventListener = new Answer<Void>() {

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            eventListener = (ConferenceEventListener) invocation.getArguments()[0];
            return null;
        }
            
    };
            
    @Before
    public void setUp() {
        ConferenceRoomRepository conferenceRepo = Mockito.mock(ConferenceRoomRepository.class);
        Mockito.doAnswer(answerAddEventListener).when(conferenceRepo).addEventListener(Mockito.isA(ConferenceEventListener.class));
        
        summaryService.setConferenceRepository(conferenceRepo);
        summaryService.init();
    }
    
    private SummaryService summaryService = new SummaryService();
    private ConferenceEventListener eventListener;
    
    /**
     * Test of getSummary method, of class SummaryService.
     */
    @Test
    public void testGetSummary() {
        System.out.println("getSummary");
        Room roomDK = new Room("H45-1234", "Danish", null, null, null, null);
        Room roomFO = new Room("H298-1234", "Faroese", null, null, null, null);
        Room roomUS = new Room("H1-1234", "Nanpa", null, null, null, null);
        Participant participantDK = new Participant("SIP/channel001", "+4512345678", "John Doe", "+4512345678", true, false, CallType.Sip, new Date());
        Participant participantFO = new Participant("SIP/channel002", "+29812345678", "John Doe", "+29812345678", true, false, CallType.Phone, new Date());
        
        Map<String, Summary> map = summaryService.getSummary();
        
        assertNull(map.get("H45"));
        assertNull(map.get("H298"));
        assertNull(map.get("H1"));
        assertNotNull(map.get(SummaryService.GLOBAL_IDENTIFIER));
        
        eventListener.onParticipantEvent(new ConferenceStartEvent(roomDK));
        eventListener.onParticipantEvent(new ConferenceStartEvent(roomFO));
        eventListener.onParticipantEvent(new ConferenceStartEvent(roomUS));
        map = summaryService.getSummary();
        assertEquals(1, map.get("H45").getHalls());
        assertEquals(1, map.get("H298").getHalls());
        assertEquals(1, map.get("H1").getHalls());
        assertEquals(3, map.get(SummaryService.GLOBAL_IDENTIFIER).getHalls());
        
        eventListener.onParticipantEvent(new ParticipantJoinEvent(roomDK.getId(), participantDK));
        eventListener.onParticipantEvent(new ParticipantUnmuteEvent(roomDK.getId(), participantDK.getChannel()));
        map = summaryService.getSummary();
        assertEquals(1, map.get("H45").getParticipants());
        assertEquals(1, map.get("H45").getParticipantsViaSip());
        assertEquals(0, map.get("H45").getParticipantsViaPhone());
        assertEquals(1, map.get("H45").getOpenMicrophones());
        assertEquals(0, map.get("H298").getParticipants());
        assertEquals(0, map.get("H298").getOpenMicrophones());
        assertEquals(0, map.get("H1").getParticipants());
        assertEquals(0, map.get("H1").getOpenMicrophones());
        assertEquals(1, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipants());
        assertEquals(1, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipantsViaSip());
        assertEquals(0, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipantsViaPhone());
        assertEquals(1, map.get(SummaryService.GLOBAL_IDENTIFIER).getOpenMicrophones());
        
        eventListener.onParticipantEvent(new ParticipantJoinEvent(roomFO.getId(), participantFO));
        eventListener.onParticipantEvent(new ParticipantUnmuteEvent(roomFO.getId(), participantFO.getChannel()));
        map = summaryService.getSummary();
        assertEquals(1, map.get("H45").getParticipants());
        assertEquals(1, map.get("H45").getParticipantsViaSip());
        assertEquals(0, map.get("H45").getParticipantsViaPhone());
        assertEquals(1, map.get("H45").getOpenMicrophones());
        assertEquals(1, map.get("H298").getParticipants());
        assertEquals(0, map.get("H298").getParticipantsViaSip());
        assertEquals(1, map.get("H298").getParticipantsViaPhone());
        assertEquals(1, map.get("H298").getOpenMicrophones());
        assertEquals(0, map.get("H1").getParticipants());
        assertEquals(0, map.get("H1").getOpenMicrophones());
        assertEquals(2, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipants());
        assertEquals(1, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipantsViaSip());
        assertEquals(1, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipantsViaPhone());
        assertEquals(2, map.get(SummaryService.GLOBAL_IDENTIFIER).getOpenMicrophones());
        
        eventListener.onParticipantEvent(new ParticipantMuteEvent(roomDK.getId(), participantDK.getChannel()));
        eventListener.onParticipantEvent(new ParticipantLeaveEvent(roomDK.getId(), participantDK));
        eventListener.onParticipantEvent(new ParticipantLeaveEvent(roomFO.getId(), participantFO));
        eventListener.onParticipantEvent(new ConferenceEndEvent(roomDK.getId()));
        eventListener.onParticipantEvent(new ConferenceEndEvent(roomFO.getId()));
        eventListener.onParticipantEvent(new ConferenceEndEvent(roomUS.getId()));
        map = summaryService.getSummary();
        assertNull(map.get("H45"));
        assertNull(map.get("H298"));
        assertNull(map.get("H1"));
        assertNotNull(map.get(SummaryService.GLOBAL_IDENTIFIER));
        assertEquals(0, map.get(SummaryService.GLOBAL_IDENTIFIER).getHalls());
        assertEquals(0, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipants());
        assertEquals(0, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipantsViaSip());
        assertEquals(0, map.get(SummaryService.GLOBAL_IDENTIFIER).getParticipantsViaPhone());
        assertEquals(0, map.get(SummaryService.GLOBAL_IDENTIFIER).getOpenMicrophones());
        
    }
    
}
