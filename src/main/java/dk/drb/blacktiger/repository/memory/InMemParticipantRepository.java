package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.User;
import dk.drb.blacktiger.repository.ParticipantRepository;
import dk.drb.blacktiger.repository.UserRepository;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * An in-memory implementation of ParticipantRepository for Test Scenarios.
 * It will create a host participant for each user found in <code>UserRepository</code>. Afterwards it will keep on adding non-host participants 
 * randomly.
 */
public class InMemParticipantRepository implements ParticipantRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InMemParticipantRepository.class);
    private Map<String, List<Participant>> participantMap = new HashMap<>();
    private List<ConferenceEventListener> eventListeners = new ArrayList<>();
    private int userCount;
    private final NumberFormat phoneNumberFormat = NumberFormat.getIntegerInstance();
    private UserRepository userRepository;
    
    @PostConstruct
    protected void init() {
        for(User user: userRepository.findAll()) {
            List<Participant> list = new ArrayList<>();
            String id = user.getUsername();
            list.add(new Participant(id, "Test Host for Kingdom Hall " + id, id, false, true, CallType.Sip, new Date()));
            participantMap.put(id, list);
        }
    }
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Scheduled(fixedDelay = 1000)
    public void addUser() {
        LOG.info("Adding user.");
        Random random = new Random();
        List<String> keys = new ArrayList<>(participantMap.keySet());
        String roomNo = keys.get(random.nextInt(keys.size()));
        String id = Integer.toString(userCount++);
        String number = "+45" + phoneNumberFormat.format(userCount);
        Participant p = new Participant(id, null, number, true, false, CallType.Phone, new Date());
        participantMap.get(roomNo).add(p);
        
        LOG.info("User added [id={}, room={}] ", id, roomNo);
        
        fireJoinEvent(roomNo, p);
    }
    
    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        if(participantMap.containsKey(roomNo)) {
            return Collections.unmodifiableList(participantMap.get(roomNo));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Participant findByRoomNoAndParticipantId(String roomNo, String participantId) {
        if(participantMap.containsKey(roomNo)) {
            for(Participant p : participantMap.get(roomNo)) {
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
        if(participantMap.containsKey(roomNo)) {
            Iterator<Participant> it = participantMap.get(roomNo).iterator();
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
        if(participantMap.containsKey(roomNo)) {
            for(Participant p : participantMap.get(roomNo)) {
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
        if(participantMap.containsKey(roomNo)) {
            for(Participant p : participantMap.get(roomNo)) {
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
