package dk.drb.blacktiger.service;

import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.util.Access;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class ConferenceService {
 
    private static final Logger LOG = LoggerFactory.getLogger(ConferenceService.class);
    private PhonebookRepository phonebookRepository;
    private ConferenceRoomRepository roomRepository;

    @Autowired
    public void setPhonebookRepository(PhonebookRepository phonebookRepository) {
        this.phonebookRepository = phonebookRepository;
    }

    @Autowired
    public void setRoomRepository(ConferenceRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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
        
        //Decorate rooms with contact and displayname
        return rooms;
    }
    
    public Room getRoom(String room) {
        Access.checkRoomAccess(room);
        return roomRepository.findOne(room);
    }
    
    /**
     * Retrieves a list of participants in  a room.
     * @param roomNo The room number.
     * @return The list of participants or an empty list if room does not exist.
     */
    public List<Participant> listParticipants(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        Access.checkRoomAccess(roomNo);
        return decorateWithPhonebookInformation(roomRepository.findByRoomNo(roomNo));
    }

    /**
     * Retrieves a specific participant in a room. 
     * @param roomNo The room number
     * @param participantId The participant id.
     * @return The participant or null if no match found.
     */
    
    public Participant getParticipant(String roomNo, String participantId) {
        LOG.debug("Retrieving participant. [room={};participant={}]", roomNo, participantId);
        Access.checkRoomAccess(roomNo);
        return decorateWithPhonebookInformation(roomRepository.findByRoomNoAndParticipantId(roomNo, participantId));
    }

    /**
     * Kick a participant from a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void kickParticipant(String roomNo, String participantId) {
        Access.checkRoomAccess(roomNo);
        roomRepository.kickParticipant(roomNo, participantId);
    }

    /**
     * Mutes a participant in a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void muteParticipant(String roomNo, String participantId) {
        Access.checkRoomAccess(roomNo);
        roomRepository.muteParticipant(roomNo, participantId);
    }

    /**
     * Unmutes a participant.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void unmuteParticipant(String roomNo, String participantId) {
        Access.checkRoomAccess(roomNo);
        roomRepository.unmuteParticipant(roomNo, participantId);
    }

    public void addEventListener(ConferenceEventListener listener) {
        roomRepository.addEventListener(listener);
    }

    public void removeEventListener(ConferenceEventListener listener) {
        roomRepository.removeEventListener(listener);
    }
    
    private List<Participant> decorateWithPhonebookInformation(List<Participant> participants) {
        for(Participant p : participants) {
            decorateWithPhonebookInformation(p);
        }
        return participants;
    }
    
    private Participant decorateWithPhonebookInformation(Participant participant) {
        PhonebookEntry entry = phonebookRepository.findByCallerId(participant.getCallerId());
        if(entry != null) {
            participant.setPhoneNumber(entry.getNumber());
            participant.setName(entry.getName());
        }
        return participant;
    }
    
}
