package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.ConferenceEndEvent;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceStartEvent;
import dk.drb.blacktiger.model.ParticipantCommentRequestCancelEvent;
import dk.drb.blacktiger.model.ParticipantCommentRequestEvent;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.util.IpPhoneNumber;
import dk.drb.blacktiger.util.PhoneNumber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.manager.EventTimeoutException;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.AbstractManagerAction;
import org.asteriskjava.manager.action.ConfbridgeKickAction;
import org.asteriskjava.manager.action.ConfbridgeListAction;
import org.asteriskjava.manager.action.ConfbridgeListRoomsAction;
import org.asteriskjava.manager.action.ConfbridgeMuteAction;
import org.asteriskjava.manager.action.ConfbridgeUnmuteAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.event.ConfbridgeEndEvent;
import org.asteriskjava.manager.event.ConfbridgeJoinEvent;
import org.asteriskjava.manager.event.ConfbridgeLeaveEvent;
import org.asteriskjava.manager.event.ConfbridgeListEvent;
import org.asteriskjava.manager.event.ConfbridgeListRoomsEvent;
import org.asteriskjava.manager.event.ConfbridgeStartEvent;
import org.asteriskjava.manager.event.DtmfEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.ResponseEvent;
import org.asteriskjava.manager.response.ManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * This implementation of ConferenceRepository uses the Confbridge conference in Asterisk.
 */
public class AsteriskConfbridgeRepository extends AbstractAsteriskConferenceRepository implements ConferenceRoomRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AsteriskConfbridgeRepository.class);
    private static final String DIGIT_COMMENT_REQUEST = "1";
    private static final String DIGIT_COMMENT_REQUEST_CANCEL = "0";

    private final Map<String, Room> roomMap = new HashMap<>();
    private final Map<String, List<Participant>> participantMap = new HashMap<>();
    private final Map<String, String> channelRoomMap = new HashMap<>();
    private final Map<String, String> channelCallerIdMap = new HashMap<>();
    private final Queue<ManagerEvent> managerEvents = new LinkedList<>();

    public AsteriskConfbridgeRepository() {
        setManagerEventListener(this);
    }

    @Override
    public void onManagerEvent(final ManagerEvent event) {
        try {
            managerEvents.add(event);
        } catch (IllegalStateException ex) {
            LOG.error("Unable to add event to queue.", ex);
        }

    }

    @Scheduled(fixedDelay = 50)
    protected void handleEventQueue() {
        ManagerEvent event = null;
        while ((event = managerEvents.poll()) != null) {
            LOG.debug("Handling event from queue. [event={}]", event);
            if (event instanceof ConfbridgeJoinEvent) {
                onConfbridgeJoinEvent((ConfbridgeJoinEvent) event);
            }
            if (event instanceof ConfbridgeLeaveEvent) {
                onConfbridgeLeaveEvent((ConfbridgeLeaveEvent) event);
            }
            if (event instanceof ConfbridgeStartEvent) {
                onConfbridgeStart((ConfbridgeStartEvent) event);
            }

            if (event instanceof ConfbridgeEndEvent) {
                onConfbridgeEnd((ConfbridgeEndEvent) event);
            }

            if (event instanceof DtmfEvent) {
                onDtmfEvent((DtmfEvent) event);
            }
        }
    }

    private void onDtmfEvent(DtmfEvent event) {
        if (event.isEnd() && (event.getDigit().equals(DIGIT_COMMENT_REQUEST) || event.getDigit().equals(DIGIT_COMMENT_REQUEST_CANCEL))) {
            // A DTMF event has been received. We need to retrieve roomId and callerId. 
            String roomId = channelRoomMap.get(event.getChannel());
            String callerId = channelCallerIdMap.get(event.getChannel());

            ConferenceEvent ce = null;
            switch (event.getDigit()) {
                case DIGIT_COMMENT_REQUEST:
                    ce = new ParticipantCommentRequestEvent(roomId, callerId);
                    break;
                case DIGIT_COMMENT_REQUEST_CANCEL:
                    ce = new ParticipantCommentRequestCancelEvent(roomId, callerId);
                    break;
            }

            AsteriskConfbridgeRepository.this.fireEvent(ce);
        }
    }

    private void onConfbridgeJoinEvent(ConfbridgeJoinEvent event) {
        String roomNo = event.getConference();
        Participant p = participantFromEvent(event);
        getParticipantListSynced(roomNo).add(p);
        channelCallerIdMap.put(event.getChannel(), event.getCallerIdNum());
        channelRoomMap.put(event.getChannel(), event.getConference());
        AsteriskConfbridgeRepository.this.fireEvent(new ParticipantJoinEvent(roomNo, p));
    }

    private void onConfbridgeLeaveEvent(ConfbridgeLeaveEvent event) {
        Participant toRemove = null;
        String roomNo = event.getConference();
        Participant p = participantFromEvent(event);
        List<Participant> list = getParticipantListSynced(roomNo);

        for (Participant current : list) {
            if (p.getCallerId().equals(current.getCallerId())) {
                toRemove = current;
                break;
            }
        }

        if (toRemove != null) {
            list.remove(toRemove);
        }

        channelCallerIdMap.remove(event.getChannel());
        channelRoomMap.remove(event.getChannel());

        AsteriskConfbridgeRepository.this.fireEvent(new ParticipantLeaveEvent(roomNo, p.getCallerId()));
    }

    private void onConfbridgeStart(ConfbridgeStartEvent e) {
        LOG.debug("Handling ConfbridgeStartEvent.");
        Room room = new Room(e.getConference());
        roomMap.put(e.getConference(), room);

        AsteriskConfbridgeRepository.this.fireEvent(new ConferenceStartEvent(e.getConference()));

    }

    private void onConfbridgeEnd(ConfbridgeEndEvent e) {
        LOG.debug("Handling ConfbridgeEndEvent.");
        roomMap.remove(e.getConference());

        AsteriskConfbridgeRepository.this.fireEvent(new ConferenceEndEvent(e.getConference()));
    }

    private synchronized List<Participant> getParticipantListSynced(String roomId) {
        List<Participant> participants = participantMap.get(roomId);
        if (participants == null) {
            participants = readParticipantsFromServer(roomId);
            participantMap.put(roomId, participants);
        }
        return participants;
    }

    @Override
    public void setAsteriskServer(AsteriskServer asteriskServer) {
        super.setAsteriskServer(asteriskServer);

        LOG.info("Asterisk Server specified. Reading rooms from server.");

        roomMap.clear();
        for (Room room : readRoomsFromServer()) {
            roomMap.put(room.getId(), room);
            getParticipantListSynced(room.getId());
        }
    }

    /**
     * Reads rooms directly from the asterisk server.
     */
    private List<Room> readRoomsFromServer() {
        ResponseEvents events = sendAction(new ConfbridgeListRoomsAction());

        List<Room> result = new ArrayList();

        for (ResponseEvent event : events.getEvents()) {
            if (event instanceof ConfbridgeListRoomsEvent) {
                ConfbridgeListRoomsEvent roomsEvent = (ConfbridgeListRoomsEvent) event;
                result.add(new Room(roomsEvent.getConference()));
            }
        }
        return result;
    }

    private List<Participant> readParticipantsFromServer(String roomId) {
        ResponseEvents events = sendAction(new ConfbridgeListAction(roomId));
        List<Participant> result = new ArrayList<>();

        for (ResponseEvent event : events.getEvents()) {
            if (event instanceof ConfbridgeListEvent) {
                ConfbridgeListEvent confbridgeListEvent = (ConfbridgeListEvent) event;
                Participant p = participantFromEvent(confbridgeListEvent);
                result.add(p);
                channelCallerIdMap.put(confbridgeListEvent.getChannel(), confbridgeListEvent.getCallerIDnum());
                channelRoomMap.put(confbridgeListEvent.getChannel(), confbridgeListEvent.getConference());
            }
        }
        return result;
    }

    @Override
    public Room findOne(String id) {
        return roomMap.get(id);
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList(roomMap.values());
    }

    @Override
    public List<Room> findAllByIds(List<String> ids) {
        List<Room> rooms = new ArrayList<>();
        for (String id : ids) {
            Room room = roomMap.get(id);
            if (room != null) {
                rooms.add(room);
            }
        }
        return rooms;
    }

    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        return getParticipantListSynced(roomNo);
    }

    @Override
    public Participant findByRoomNoAndParticipantId(String roomNo, String callerId) {
        LOG.debug("Retrieving participant. [room={};participant={}]", roomNo, callerId);
        List<Participant> participants = findByRoomNo(roomNo);
        for (Participant p : participants) {
            if (callerId.equals(p.getCallerId())) {
                return p;
            }
        }
        return null;
    }

    @Override
    public void kickParticipant(String roomNo, String callerId) {
        ManagerResponse response = sendAction(new ConfbridgeKickAction(roomNo, callerId));
    }

    @Override
    public void muteParticipant(String roomNo, String callerId) {
        setMutenessOfParticipant(roomNo, callerId, true);
    }

    @Override
    public void unmuteParticipant(String roomNo, String callerId) {
        setMutenessOfParticipant(roomNo, callerId, false);
    }

    @Override
    public void removeEventListener(ConferenceEventListener listener) {
        if (listener != null) {
            eventListeners.remove(listener);
        }
    }

    private void setMutenessOfParticipant(String roomId, String callerId, boolean value) {
        // Because Asterisk 11 does not send events when muteness changes, we have to keep that info here ourselves.
        // That also means that we have no idea if other parties mutes a user
        AbstractManagerAction a = value == true ? new ConfbridgeMuteAction(roomId, callerId) : new ConfbridgeUnmuteAction(roomId, callerId);
        sendAction(a);

        for (Participant p : getParticipantListSynced(roomId)) {
            if (callerId.equals(p.getCallerId())) {
                p.setMuted(value);
                break;
            }
        }
    }

    private Participant participantFromEvent(ConfbridgeListEvent event) {
        return participantFromEventData(event.getConference(), event.getCallerIDnum(), event.getCallerIdName(), event.getDateReceived());
    }

    private Participant participantFromEvent(ConfbridgeJoinEvent event) {
        return participantFromEventData(event.getConference(), event.getCallerIdNum(), event.getCallerIdName(), event.getDateReceived());
    }

    private Participant participantFromEvent(ConfbridgeLeaveEvent event) {
        return participantFromEventData(event.getConference(), event.getCallerIdNum(), event.getCallerIdName(), event.getDateReceived());
    }

    private Participant participantFromEventData(String conference, String callerIdNum, String callerIdName, Date dateReceived) {
        boolean host = false;
        CallType callType = CallType.Sip;

        if (callerIdNum.equals(conference)) {
            host = true;
        }

        boolean muted = !host;
        String phoneNumber = callerIdNum;
        String name = callerIdName;

        if (IpPhoneNumber.isIpPhoneNumber(phoneNumber)) {
            phoneNumber = IpPhoneNumber.normalize(phoneNumber);
        } else if (PhoneNumber.isPhoneNumber(name, "DK")) {
            phoneNumber = PhoneNumber.normalize(phoneNumber, "DK");
            callType = CallType.Phone;
        }

        return new Participant(callerIdNum, name, phoneNumber, muted, host, callType, dateReceived);
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
            return asteriskServer.getManagerConnection().sendEventGeneratingAction(action, 1000);
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
