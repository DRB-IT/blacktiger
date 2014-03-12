package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author michael
 */
public class InMemCallInformationRepositoryTest {

    private ConferenceRoomRepository confRepo;
    private ConferenceEventListener eventListener;
    private InMemCallInformationRepository instance;
    
    @Before
    public void setUp() throws Exception {
        confRepo = Mockito.mock(ConferenceRoomRepository.class);
        Mockito.doAnswer(eventListenerAnswer()).when(confRepo).addEventListener(Mockito.any(ConferenceEventListener.class));
        
        instance = new InMemCallInformationRepository();
        instance.setParticipantRepository(confRepo);
    }

    private Answer<Void> eventListenerAnswer() {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                eventListener = (ConferenceEventListener) invocation.getArguments()[0];
                return null;
            }
        };
    }

    /**
     * Test of findByRoomNoAndPeriodAndDurationAndNumbers method, of class InMemCallInformationRepository.
     */
    @Test
    public void testFindByRoomNoAndPeriodAndDurationAndNumbers() {
        System.out.println("findByRoomNoAndPeriodAndDurationAndNumbers");
        String roomNo = "H45-0000";
        Date start = null;
        Date end = null;
        int minimumDuration = 0;
        Participant participant = new Participant("1", "name", "+4512345678", true, false, CallType.Phone, new Date());
        String[] numbers = {participant.getPhoneNumber()};
        List result = instance.findByRoomNoAndPeriodAndDurationAndNumbers(roomNo, start, end, minimumDuration, numbers);
        assertEquals(0, result.size());
        
        eventListener.onParticipantEvent(new ParticipantJoinEvent(roomNo, participant));
        result = instance.findByRoomNoAndPeriodAndDurationAndNumbers(roomNo, start, end, minimumDuration, numbers);
        assertEquals(0, result.size());
        
        eventListener.onParticipantEvent(new ParticipantLeaveEvent(roomNo, participant.getUserId()));
        result = instance.findByRoomNoAndPeriodAndDurationAndNumbers(roomNo, start, end, minimumDuration, numbers);
        assertEquals(1, result.size());
    }

}