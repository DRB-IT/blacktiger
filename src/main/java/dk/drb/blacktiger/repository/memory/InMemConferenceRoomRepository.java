package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.model.User;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
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
 * An in-memory implementation of ConferenceRoomRepository for use in test scenarios.
 */
public class InMemConferenceRoomRepository implements ConferenceRoomRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InMemConferenceRoomRepository.class);
    private List<Room> rooms = new ArrayList<>();
    private UserRepository userRepository;
    private Map<String, List<Participant>> participantMap = new HashMap<>();
    private List<ConferenceEventListener> eventListeners = new ArrayList<>();
    private int userCount;
    private final NumberFormat phoneNumberFormat = NumberFormat.getIntegerInstance();

    @PostConstruct
    protected void init() {
        for(User user : userRepository.findAll()) {
            rooms.add(new Room(user.getUsername(), "Test Kingdom Hall " + user.getUsername()));
        }
        
        phoneNumberFormat.setMinimumIntegerDigits(8);
        phoneNumberFormat.setGroupingUsed(false);
        
        for (User user : userRepository.findAll()) {
            List<Participant> list = new ArrayList<>();
            String id = user.getUsername();
            list.add(new Participant(id, "Test Host for Kingdom Hall " + id, id, false, true, CallType.Sip, new Date()));
            participantMap.put(id, list);
        }
    }
    
    @Scheduled(fixedDelay = 50)
    public void maintain() {
        LOG.debug("Adding user.");
        Random random = new Random();
        List<String> keys = new ArrayList<>(participantMap.keySet());
        String roomNo = keys.get(random.nextInt(keys.size()));
        List<Participant> participantsInRoom = participantMap.get(roomNo);

        int votesForJoin = random.nextInt(Math.max(1, 15 - participantsInRoom.size()));
        int votesForLeave = random.nextInt(Math.max(1, participantsInRoom.size() - 5));
        boolean join = votesForJoin >= votesForLeave;;

        if (join) {
            String id = Integer.toString(userCount++);
            String number = "+45" + phoneNumberFormat.format(userCount);
            Participant p = new Participant(id, null, number, true, false, CallType.Phone, new Date());
            participantMap.get(roomNo).add(p);

            LOG.debug("User added [id={}, room={}] ", id, roomNo);

            fireJoinEvent(roomNo, p);
        } else {
            int toRemove = random.nextInt(participantsInRoom.size());
            if (!participantsInRoom.get(toRemove).isHost()) {
                Participant p = participantsInRoom.remove(toRemove);
                LOG.debug("User removed [id={}, room={}] ", p.getCallerId(), roomNo);
                fireLeaveEvent(roomNo, p.getCallerId());
            }
        }
    }
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public List<Room> findAll() {
        return Collections.unmodifiableList(rooms);
    }

    @Override
    public List<Room> findAllByIds(List<String> ids) {
        List<Room> list = new ArrayList<>();
        for(Room room : rooms) {
            if(ids.contains(room.getId())) {
                list.add(room);
            }
        }
        return list;
    }

    @Override
    public Room findOne(String id) {
        for(Room room : rooms) {
            if(room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        if (participantMap.containsKey(roomNo)) {
            return Collections.unmodifiableList(participantMap.get(roomNo));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Participant findByRoomNoAndChannel(String roomNo, String callerId) {
        if (participantMap.containsKey(roomNo)) {
            for (Participant p : participantMap.get(roomNo)) {
                if (callerId.equals(p.getCallerId())) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void kickParticipant(String roomNo, String callerId) {
        LOG.info("Kicking user [id={}]", callerId);
        if (participantMap.containsKey(roomNo)) {
            Iterator<Participant> it = participantMap.get(roomNo).iterator();
            while (it.hasNext()) {
                Participant p = it.next();
                if (callerId.equals(p.getCallerId())) {
                    it.remove();
                    fireLeaveEvent(roomNo, p.getCallerId());
                    break;
                }
            }
        }
    }

    @Override
    public void muteParticipant(String roomNo, String callerId) {
        LOG.info("Muting user. [id={}]", callerId);
        if (participantMap.containsKey(roomNo)) {
            for (Participant p : participantMap.get(roomNo)) {
                if (callerId.equals(p.getCallerId())) {
                    try {
                        Field f = p.getClass().getDeclaredField("muted");
                        f.setAccessible(true);
                        f.setBoolean(p, true);
                        LOG.info("User muted. [id={}; muted={}]", callerId, p.isMuted());
                    } catch (Exception ex) {
                        LOG.error("Unable to mute.", ex);
                    }
                }
            }
        }
    }

    @Override
    public void unmuteParticipant(String roomNo, String callerId) {
        LOG.info("Unmuting user. [id={}]", callerId);
        if (participantMap.containsKey(roomNo)) {
            for (Participant p : participantMap.get(roomNo)) {
                if (callerId.equals(p.getCallerId())) {
                    try {
                        Field f = p.getClass().getDeclaredField("muted");
                        f.setAccessible(true);
                        f.setBoolean(p, false);
                        LOG.info("User unmuted. [id={}; muted={}]", callerId, p.isMuted());

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
        for (ConferenceEventListener l : eventListeners) {
            l.onParticipantEvent(new ParticipantJoinEvent(roomNo, p));
        }
    }

    private void fireLeaveEvent(String roomNo, String userId) {
        for (ConferenceEventListener l : eventListeners) {
            l.onParticipantEvent(new ParticipantLeaveEvent(roomNo, userId));
        }
    }
    
}
