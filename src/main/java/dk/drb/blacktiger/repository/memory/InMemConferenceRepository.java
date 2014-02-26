package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ConferenceJoinEvent;
import dk.drb.blacktiger.model.ConferenceLeaveEvent;
import dk.drb.blacktiger.repository.ConferenceRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class InMemConferenceRepository implements ConferenceRepository {

    private static final String ROOM_ID = "09991";
    private static final Logger LOG = LoggerFactory.getLogger(InMemConferenceRepository.class);
    private List<Participant> participants = new ArrayList<>();
    private List<ConferenceEventListener> eventListeners = new ArrayList<>();
    private int userCount;
    
    public InMemConferenceRepository() {
    
        participants.add(new Participant("0999", "Brønderslev", "IP-0999", false, true, new Date()));
        for(int i=0;i<10;i++) {
            addUser();
        }
    }
    
    @Scheduled(fixedDelay = 16000)
    public void addUser() {
        LOG.info("Adding user.");
        String id = Integer.toString(userCount++);
        String number = "+453314144" + userCount;
        Participant p = new Participant(id, null, number, true, false, new Date());
        participants.add(p);
        
        LOG.info("User add [id={}]", id);
        
        fireJoinEvent(p);
    }
    
    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        if(ROOM_ID.equals(roomNo)) {
            return Collections.unmodifiableList(participants);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Participant findByRoomNoAndParticipantId(String roomNo, String participantId) {
        if(ROOM_ID.equals(roomNo)) {
            for(Participant p : participants) {
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
        if(ROOM_ID.equals(roomNo)) {
            Iterator<Participant> it = participants.iterator();
            while(it.hasNext()) {
                Participant p = it.next();
                if(participantId.equals(p.getUserId())) {
                    it.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void muteParticipant(String roomNo, String participantId) {
        LOG.info("Muting user. [id={}]", participantId);
        if(ROOM_ID.equals(roomNo)) {
            for(Participant p : participants) {
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
        if(ROOM_ID.equals(roomNo)) {
            for(Participant p : participants) {
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
    
    private void fireJoinEvent(Participant p) {
        for(ConferenceEventListener l : eventListeners) {
            l.onParticipantEvent(new ConferenceJoinEvent(ROOM_ID, p));
        }
    }
    
     private void fireLeaveEvent(String userId) {
        for(ConferenceEventListener l : eventListeners) {
            l.onParticipantEvent(new ConferenceLeaveEvent(ROOM_ID, userId));
        }
    }
    
}
