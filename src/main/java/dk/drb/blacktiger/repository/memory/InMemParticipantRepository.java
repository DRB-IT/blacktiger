package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.repository.ParticipantRepository;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class InMemParticipantRepository implements ParticipantRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InMemParticipantRepository.class);
    private Map<String, List<Participant>> participants = new HashMap<>();
    private List<ConferenceEventListener> eventListeners = new ArrayList<>();
    private int userCount;
    private final NumberFormat roomNumberFormat = NumberFormat.getIntegerInstance();
    private final NumberFormat phoneNumberFormat = NumberFormat.getIntegerInstance();
    
    public InMemParticipantRepository() {
        
        roomNumberFormat.setMinimumIntegerDigits(4);
        roomNumberFormat.setGroupingUsed(false);
        phoneNumberFormat.setMinimumIntegerDigits(8);
        phoneNumberFormat.setGroupingUsed(false);
        
        for(int i=0;i<1000;i++) {
            List<Participant> list = new ArrayList<>();
            String id = "H45-" + roomNumberFormat.format(i);
            list.add(new Participant(id, "Test Rigssal 1", id, false, true, new Date()));
            participants.put(id, list);
            
        }
    }
    
    @Scheduled(fixedDelay = 1000)
    public void addUser() {
        LOG.info("Adding user.");
        String id = Integer.toString(userCount++);
        String number = "+45" + phoneNumberFormat.format(userCount);
        String roomNo = "H45-" + roomNumberFormat.format(userCount % 1000);
        Participant p = new Participant(id, null, number, true, false, new Date());
        participants.get(roomNo).add(p);
        
        LOG.info("User add [id={}]", id);
        
        fireJoinEvent(roomNo, p);
    }
    
    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        if(participants.containsKey(roomNo)) {
            return Collections.unmodifiableList(participants.get(roomNo));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Participant findByRoomNoAndParticipantId(String roomNo, String participantId) {
        if(participants.containsKey(roomNo)) {
            for(Participant p : participants.get(roomNo)) {
                if(participantId.equals(p.getUserId())) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void kickParticipant(String roomNo, String participantId) {
        LOG.info("Kicking user [id={}]", participantId);
        if(participants.containsKey(roomNo)) {
            Iterator<Participant> it = participants.get(roomNo).iterator();
            while(it.hasNext()) {
                Participant p = it.next();
                if(participantId.equals(p.getUserId())) {
                    it.remove();
                    fireLeaveEvent(roomNo, p.getUserId());
                    break;
                }
            }
        }
    }

    @Override
    public void muteParticipant(String roomNo, String participantId) {
        LOG.info("Muting user. [id={}]", participantId);
        if(participants.containsKey(roomNo)) {
            for(Participant p : participants.get(roomNo)) {
                if(participantId.equals(p.getUserId())) {
                    try {
                        Field f = p.getClass().getDeclaredField("muted");
                        f.setAccessible(true);
                        f.setBoolean(p, true);
                        LOG.info("User muted. [id={}; muted={}]", participantId, p.isMuted());
                    } catch (Exception ex) {
                        LOG.error("Unable to mute.", ex);
                    }
                }
            }
        }
    }

    @Override
    public void unmuteParticipant(String roomNo, String participantId) {
        LOG.info("Unmuting user. [id={}]", participantId);
        if(participants.containsKey(roomNo)) {
            for(Participant p : participants.get(roomNo)) {
                if(participantId.equals(p.getUserId())) {
                    try {
                        Field f = p.getClass().getDeclaredField("muted");
                        f.setAccessible(true);
                        f.setBoolean(p, false);
                        LOG.info("User unmuted. [id={}; muted={}]", participantId, p.isMuted());
        
                    } catch (Exception ex) {
                        LOG.error("Unable to mute.", ex);
                    }
                }
            }
        }
    }

    @Override
    public void addEventListener(ConferenceEventListener listener) {
        if (listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    @Override
    public void removeEventListener(ConferenceEventListener listener) {
        if (listener != null) {
            eventListeners.remove(listener);
        }
    }
    
    private void fireJoinEvent(String roomNo, Participant p) {
        for(ConferenceEventListener l : eventListeners) {
            l.onParticipantEvent(new ParticipantJoinEvent(roomNo, p));
        }
    }
    
     private void fireLeaveEvent(String roomNo, String userId) {
        for(ConferenceEventListener l : eventListeners) {
            l.onParticipantEvent(new ParticipantLeaveEvent(roomNo, userId));
        }
    }
    
}
