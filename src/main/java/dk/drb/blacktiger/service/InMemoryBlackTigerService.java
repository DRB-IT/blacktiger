/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.CallInformation;
import dk.drb.blacktiger.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Javadoc
 */
public class InMemoryBlackTigerService implements IBlackTigerService {

    private static final String ROOM_ID = "09991";
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryBlackTigerService.class);
    
    private List<User> users = new ArrayList<User>();
    private List<Participant> participants = new ArrayList<Participant>();
    private List<BlackTigerEventListener> eventListeners = new ArrayList<BlackTigerEventListener>();
    private Map<String, String> phonebook = new HashMap<String, String>();
    private int userCount;
    
    public InMemoryBlackTigerService() {
        users.add(new User("0999", "123"));
        
        participants.add(new Participant("0999", "Brønderslev", "IP-0999", false, true, new Date()));
        for(int i=0;i<10;i++) {
            addUser();
        }
    }

    
    @Override
    public User getUser(String username) {
        for(User u : users) {
            if(username.equals(u.getUsername())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public List<Participant> listParticipants(String roomNo) {
        if(ROOM_ID.equals(roomNo)) {
            List<Participant> decorated = new ArrayList<Participant>();
            for(Participant participant: participants) {
                decorated.add(decorateWithName(participant));
            }
            return Collections.unmodifiableList(decorated);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Participant getParticipant(String roomNo, String participantId) {
        if(ROOM_ID.equals(roomNo)) {
            for(Participant p : participants) {
                if(participantId.equals(p.getUserId())) {
                    return decorateWithName(p);
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
    public void addEventListener(BlackTigerEventListener listener) {
        if (listener != null) {
            eventListeners.add(listener);
        }
    }

    @Override
    public void removeEventListener(BlackTigerEventListener listener) {
        if (listener != null) {
            eventListeners.remove(listener);
        }
    }
    
    @Scheduled(fixedDelay = 160000)
    public void addUser() {
        LOG.info("Adding user.");
        String id = Integer.toString(userCount++);
        String number = "3314144" + userCount;
        participants.add(new Participant(id, null, number, true, false, new Date()));
        
        LOG.info("User add [id={}]", id);
        
        fireJoinEvent(id);
    }
    
    @Scheduled(fixedDelay = 180000)
    public void removeRandomUser() {
        LOG.info("Removing random user.");
        Random r = new Random();
        Participant p = participants.get(r.nextInt(participants.size()));
        if(!p.isHost()) {
            kickParticipant(ROOM_ID, p.getUserId());
            LOG.info("User removed [id={}]", p.getUserId());
        } else {
            LOG.info("Did not remove because the random user was the host.", p.getUserId());
        }
        
        
        fireLeaveEvent(p.getUserId());
    }

    @Override
    public List<CallInformation> getReport(Date start, Date end, int minimumDuration) {
        List<CallInformation> calls = new ArrayList<CallInformation>();
        
        String phoneNumber1 = "22736623";
        String phoneNumber2 = "51923192";
        
        String name1 = getPhonebookEntry(phoneNumber1);
        String name2 = getPhonebookEntry(phoneNumber2);
        
        CallInformation call1 = new CallInformation(phoneNumber1, name1, 1, 12321, new Date());
        CallInformation call2 = new CallInformation(phoneNumber2, name2, 4, 234324, new Date());
        
        calls.add(call1);
        calls.add(call2);
        
        return calls;
    }

    @Override
    public String getPhonebookEntry(String phoneNumber) {
        return phonebook.get(phoneNumber);
    }
    
    @Override
    public void updatePhonebookEntry(String phoneNumber, String name) {
        phonebook.put(phoneNumber, name);
    }
    
    private void fireJoinEvent(String userId) {
        for(BlackTigerEventListener l : eventListeners) {
            l.onParticipantEvent(new ParticipantJoinEvent(ROOM_ID, userId));
        }
    }
    
     private void fireLeaveEvent(String userId) {
        for(BlackTigerEventListener l : eventListeners) {
            l.onParticipantEvent(new ParticipantLeaveEvent(ROOM_ID, userId));
        }
    }
     
     private Participant decorateWithName(Participant p) {
         if(p.isHost()) {
             return p;
         }
         return new Participant(p.getUserId(), getPhonebookEntry(p.getPhoneNumber()), p.getPhoneNumber(), p.isMuted(), p.isHost(), p.getDateJoined());
     }
    
}
