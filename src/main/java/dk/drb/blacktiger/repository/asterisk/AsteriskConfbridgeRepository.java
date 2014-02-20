package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.util.IpPhoneNumber;
import dk.drb.blacktiger.util.PhoneNumber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.asteriskjava.manager.EventTimeoutException;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.ConfbridgeKickAction;
import org.asteriskjava.manager.action.ConfbridgeListAction;
import org.asteriskjava.manager.action.ConfbridgeListRoomsAction;
import org.asteriskjava.manager.action.ConfbridgeMuteAction;
import org.asteriskjava.manager.action.ConfbridgeUnmuteAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.event.ConfbridgeJoinEvent;
import org.asteriskjava.manager.event.ConfbridgeLeaveEvent;
import org.asteriskjava.manager.event.ConfbridgeListEvent;
import org.asteriskjava.manager.event.ConfbridgeListRoomsEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.ResponseEvent;
import org.asteriskjava.manager.response.ManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This implementation of ConferenceRepository uses the MeetMe conference in Asterisk.
 */
public class AsteriskConfbridgeRepository extends AbstractAsteriskConferenceRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AsteriskConfbridgeRepository.class);

    @Override
    public void onManagerEvent(ManagerEvent event) {
        if (event instanceof ConfbridgeJoinEvent) {
            String roomNo = ((ConfbridgeJoinEvent) event).getConference();
            String id = ((ConfbridgeJoinEvent) event).getChannel();
            AsteriskConfbridgeRepository.this.fireEvent(new ParticipantJoinEvent(roomNo, id));
        }
        if (event instanceof ConfbridgeLeaveEvent) {
            String roomNo = ((ConfbridgeLeaveEvent) event).getConference();
            String id = ((ConfbridgeJoinEvent) event).getChannel();
            AsteriskConfbridgeRepository.this.fireEvent(new ParticipantLeaveEvent(roomNo, id));
        }
    }

    public List findRooms() {
        ResponseEvents events = sendAction(new ConfbridgeListRoomsAction());
        
        List result = new ArrayList();
        
        for (ResponseEvent event : events.getEvents()) {
            if(event instanceof ConfbridgeListRoomsEvent) {
                ConfbridgeListRoomsEvent roomsEvent = (ConfbridgeListRoomsEvent) event;
                result.add(roomsEvent.getConference());
            }
        }
        return result;
    }
    
    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        
        ResponseEvents events = sendAction(new ConfbridgeListAction(roomNo));
        List<Participant> result = new ArrayList<Participant>();

        for (ResponseEvent event : events.getEvents()) {
            result.add(participantFromEvent((ConfbridgeListEvent)event));
        }
        return result;
    }

    @Override
    public Participant findByRoomNoAndParticipantId(String roomNo, String participantId) {
        LOG.debug("Retrieving participant. [room={};participant={}]", roomNo, participantId);
        List<Participant> participants = findByRoomNo(roomNo);
        for(Participant p : participants) {
            if(participantId.equals(p.getUserId())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void kickParticipant(String roomNo, String participantId) {
        ManagerResponse response = sendAction(new ConfbridgeKickAction(roomNo, participantId));
    }

    @Override
    public void muteParticipant(String roomNo, String participantId) {
        ManagerResponse response = sendAction(new ConfbridgeMuteAction(roomNo, participantId));
    }

    @Override
    public void unmuteParticipant(String roomNo, String participantId) {
        ManagerResponse response = sendAction(new ConfbridgeUnmuteAction(roomNo, participantId));
    }

    @Override
    public void removeEventListener(ConferenceEventListener listener) {
        if (listener != null) {
            eventListeners.remove(listener);
        }
    }

    private Participant participantFromEvent(ConfbridgeListEvent event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String number = event.getCallerIDnum();
        boolean host = false;

        if (auth != null && number.equalsIgnoreCase(auth.getName())) {
            host = true;
        }

        String phoneNumber = event.getCallerIDnum();
        String name = event.getCallerIdName();

        if (IpPhoneNumber.isIpPhoneNumber(phoneNumber)) {
            phoneNumber = IpPhoneNumber.normalize(phoneNumber);
        } else if (PhoneNumber.isPhoneNumber(name, "DK")) {
            phoneNumber = PhoneNumber.normalize(phoneNumber, "DK");
        }

        return new Participant(event.getCallerIDnum(), name, phoneNumber, false, host, event.getDateReceived());
    }
    
    private ManagerResponse sendAction(ManagerAction action) {
        try {
            return asteriskServer.getManagerConnection().sendAction(action);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalStateException ex) {
            throw new RuntimeException(ex);
        } catch (TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private ResponseEvents sendAction(EventGeneratingAction action) {
        try {
            return asteriskServer.getManagerConnection().sendEventGeneratingAction(action, 100);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (EventTimeoutException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalStateException ex) {
            throw new RuntimeException(ex);
        }
    }
}
