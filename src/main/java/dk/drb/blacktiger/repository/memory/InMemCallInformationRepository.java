package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.CallInformation;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class InMemCallInformationRepository implements CallInformationRepository, ConferenceEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(InMemCallInformationRepository.class);
    private ConferenceRoomRepository roomRepository;
    private Map<String, List<CallInformation>> callMap = new HashMap<>();
    private Map<String, Participant> participantMap = new HashMap<>();

    @Autowired
    public void setParticipantRepository(ConferenceRoomRepository roomRepository) {
        if(this.roomRepository != null) {
            this.roomRepository.removeEventListener(this);
        }
        
        this.roomRepository = roomRepository;
        this.roomRepository.addEventListener(this);
    }
    
    @Override
    public List<CallInformation> findByRoomNoAndPeriodAndDuration(String roomNo, Date start, Date end, int minimumDuration) {
        return findByRoomNoAndPeriodAndDurationAndNumbers(roomNo, start, end, minimumDuration, null);
    }

    @Override
    public List<CallInformation> findByRoomNoAndPeriodAndDurationAndNumbers(String roomNo, Date start, Date end, int minimumDuration, String[] numbers) {
        List<CallInformation> list = callMap.get(roomNo);
        List<CallInformation> result = new ArrayList<>();
        
        if(list == null) {
            list = new ArrayList<>();
        }
        
        if(numbers != null) {
            List<String> numberList = Arrays.asList(numbers);
            for(CallInformation ci : list) {
                if(numberList.contains(ci.getPhoneNumber())) {
                    result.add(ci);
                }
            }
        } else {
            result.addAll(list);
        }
        return result;
    }
    
    @Override
    public void onParticipantEvent(ConferenceEvent event) {
        if(event instanceof ParticipantJoinEvent) {
            ParticipantJoinEvent joinEvent = (ParticipantJoinEvent) event;
            participantMap.put(joinEvent.getParticipant().getCallerId(), joinEvent.getParticipant());
        }
        
        if(event instanceof ParticipantLeaveEvent) {
            ParticipantLeaveEvent leaveEvent = (ParticipantLeaveEvent) event;
            Participant participant = leaveEvent.getParticipant();
            if(participant != null) {
                int durationInSeconds = (int) ((System.currentTimeMillis() - participant.getDateJoined().getTime()) / 1000);
                CallInformation ci = new CallInformation(participant.getPhoneNumber(), participant.getName(), 1, durationInSeconds, 
                        participant.getDateJoined());
                
                List<CallInformation> list = callMap.get(event.getRoomNo());
                if(list==null) {
                    list = new ArrayList<>();
                    callMap.put(event.getRoomNo(), list);
                }
                list.add(ci);
            }
        }
    }

    @Override
    public void logAction(String caller, String callee, String action) {
        LOG.info("Caller={};callee={};action={}", new Object[]{caller, callee, action});
    }

    @Override
    public void logEvent(String hall, String caller, String activity) {
        LOG.info("Hall={};caller={};activity={}", new Object[]{hall, caller, activity});
    }
    
    
    
}
