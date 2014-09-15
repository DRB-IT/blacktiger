package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ConferenceStartEvent;
import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.repository.ContactRepository;
import dk.drb.blacktiger.repository.RoomInfoRepository;
import dk.drb.blacktiger.util.Access;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 *
 */
public class ConferenceService {
 
    private static final Logger LOG = LoggerFactory.getLogger(ConferenceService.class);
    private PhonebookRepository phonebookRepository;
    private ConferenceRoomRepository roomRepository;
    private ContactRepository contactRepository;
    private RoomInfoRepository roomInfoRepository;
    private CallInformationRepository callInformationRepository;
    private Map<ConferenceEventListener, ConferenceEventListenerWrapper> listenerMap = new WeakHashMap<>();
    
    private class ConferenceEventListenerWrapper implements ConferenceEventListener {

        private ConferenceEventListener wrapped;

        public ConferenceEventListenerWrapper(ConferenceEventListener wrapped) {
            this.wrapped = wrapped;
        }
        
        @Override
        public void onParticipantEvent(ConferenceEvent event) {
            if(event instanceof ParticipantJoinEvent) {
                LOG.debug("Decorating event with phonebook information.");
                ParticipantJoinEvent joinEvent = (ParticipantJoinEvent) event;
                Participant p = decorateWithPhonebookInformation(joinEvent.getRoomNo(), joinEvent.getParticipant());
                event = new ParticipantJoinEvent(joinEvent.getRoomNo(), p);
                doActionLog(p, joinEvent.getRoomNo(), "call");
            }
            if(event instanceof ParticipantLeaveEvent) {
                ParticipantLeaveEvent leaveEvent = (ParticipantLeaveEvent) event;
                String room = event.getRoomNo();
                Participant p = decorateWithPhonebookInformation(room, leaveEvent.getParticipant());
                doActionLog(p, room, "hangup");
            }
            
            if(event instanceof ConferenceStartEvent) {
                ConferenceStartEvent startEvent = (ConferenceStartEvent) event;
                decorateRoom(startEvent.getRoom());
            }
            
            wrapped.onParticipantEvent(event);
        }
        
    }
    
    @PostConstruct
    protected void init() {
        Assert.notNull(phonebookRepository, "PhonebookRepository must be specified. Was null.");
        Assert.notNull(contactRepository, "ContactRepository must be specified. Was null.");
        Assert.notNull(roomRepository, "RoomRepository must be specified. Was null.");
        Assert.notNull(roomInfoRepository, "RoomInfoRepository must be specified. Was null.");
        Assert.notNull(callInformationRepository, "CallInformationRepository must be specified. Was null.");
    }

    private void doActionLog(Participant p, String roomNo, String action) {
        if(p != null) {
            Room room = roomInfoRepository.findById(roomNo);
            callInformationRepository.logAction(p.getPhoneNumber(), room.getPhoneNumber(), action);
        }
    }
    
    @Autowired
    public void setPhonebookRepository(PhonebookRepository phonebookRepository) {
        this.phonebookRepository = phonebookRepository;
    }

    @Autowired
    public void setRoomRepository(ConferenceRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Autowired
    public void setContactRepository(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Autowired
    public void setRoomInfoRepository(RoomInfoRepository roomInfoRepository) {
        this.roomInfoRepository = roomInfoRepository;
    }

    @Autowired
    public void setCallInformationRepository(CallInformationRepository callInformationRepository) {
        this.callInformationRepository = callInformationRepository;
    }

    public List<Room> listRooms() {
        List<Room> rooms;
        if(Access.hasRole("ADMIN")) {
            rooms = roomRepository.findAll();
        } else {
            rooms = new ArrayList<>();
            for(String roomId:Access.getAccessibleRooms()) {
                rooms.add(new Room(roomId));
            }
        }
        
        return decorateRooms(rooms);
    }
    
    public Room getRoom(String roomId) {
        Access.checkRoomAccess(roomId);
        Room room = roomRepository.findOne(roomId);
        if(room != null) {
            decorateRoom(room);
        }
        return room;
    }
    
    /**
     * Saves contact information for the room. It does not save any other information.
     */
    public void saveRoom(Room room) {
        Access.checkRoomAccess(room.getId());
        contactRepository.save(room.getId(), room.getContact());
    }
    
    /**
     * Retrieves a list of participants in  a room.
     * @param roomNo The room number.
     * @return The list of participants or an empty list if room does not exist.
     */
    public List<Participant> listParticipants(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        Access.checkRoomAccess(roomNo);
        String hall = SecurityContextHolder.getContext().getAuthentication().getName();
        return decorateWithPhonebookInformation(hall, roomRepository.findByRoomNo(roomNo));
    }

    /**
     * Retrieves a specific participant in a room. 
     * @param roomNo The room number
     * @param channel The channel.
     * @return The participant or null if no match found.
     */
    
    public Participant getParticipant(String roomNo, String channel) {
        LOG.debug("Retrieving participant. [room={};participant={}]", roomNo, channel);
        Access.checkRoomAccess(roomNo);
        String hall = SecurityContextHolder.getContext().getAuthentication().getName();
        return decorateWithPhonebookInformation(hall, roomRepository.findByRoomNoAndChannel(roomNo, channel));
    }

    /**
     * Kick a participant from a room.
     * @param roomNo The room number.
     * @param channel  The channel.
     */
    public void kickParticipant(String roomNo, String channel) {
        Access.checkRoomAccess(roomNo);
        roomRepository.kickParticipant(roomNo, channel);
    }

    /**
     * Mutes a participant in a room.
     * @param roomNo The room number.
     * @param channel  The channel.
     */
    public void muteParticipant(String roomNo, String channel) {
        Access.checkRoomAccess(roomNo);
        roomRepository.muteParticipant(roomNo, channel);
    }

    /**
     * Unmutes a participant.
     * @param roomNo The room number.
     * @param channel  The channel.
     */
    public void unmuteParticipant(String roomNo, String channel) {
        Access.checkRoomAccess(roomNo);
        roomRepository.unmuteParticipant(roomNo, channel);
    }

    public void addEventListener(ConferenceEventListener listener) {
        ConferenceEventListenerWrapper wrapped = new ConferenceEventListenerWrapper(listener);
        listenerMap.put(listener, wrapped);
        roomRepository.addEventListener(wrapped);
    }

    public void removeEventListener(ConferenceEventListener listener) {
        ConferenceEventListenerWrapper wrapped = listenerMap.get(listener);
        if(wrapped != null) {
            roomRepository.removeEventListener(listener);
            listenerMap.remove(listener);
        }
    }
    
    private List<Participant> decorateWithPhonebookInformation(String hall, List<Participant> participants) {
        for(Participant p : participants) {
            decorateWithPhonebookInformation(hall, p);
        }
        return participants;
    }
    
    private Participant decorateWithPhonebookInformation(String hall, Participant participant) {
        PhonebookEntry entry = phonebookRepository.findByCallerId(hall, participant.getCallerId());
        if(entry != null) {
            participant.setPhoneNumber(entry.getNumber());
            participant.setName(entry.getName());
            participant.setType(entry.getCallType());
        } else {
            participant.setType(CallType.Unknown);
        }
        return participant;
    }
    
    private List<Room> decorateRooms(List<Room> rooms) {
        for(Room room : rooms) {
            decorateRoom(room);
        }return rooms;
    }
    
    private Room decorateRoom(Room room) {
        if(room != null) {
            Room roomInfo = roomInfoRepository.findById(room.getId());
            if(roomInfo == null) {
                // If no room found then just make an empty one to merge in.
                roomInfo = new Room();
            }
            
            Contact contact = contactRepository.findByRoomId(room.getId());
            roomInfo.setContact(contact);
            
            room.mergeIn(roomInfo);
        }
        return room;
    }
    
}
