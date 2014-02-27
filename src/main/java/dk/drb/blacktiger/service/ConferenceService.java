package dk.drb.blacktiger.service;

import dk.drb.blacktiger.repository.ParticipantRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.util.Access;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 */
public class ConferenceService {
 
    private static final Logger LOG = LoggerFactory.getLogger(ConferenceService.class);
    private ParticipantRepository repository;
    private PhonebookRepository phonebookRepository;

    @Autowired
    public void setRepository(ParticipantRepository repository) {
        this.repository = repository;
    }
    
    @Autowired
    public void setPhonebookRepository(PhonebookRepository phonebookRepository) {
        this.phonebookRepository = phonebookRepository;
    }

    public List<Room> listRooms() {
        return null;
    }
    
    public Room getRoom(String room) {
        Access.checkRoomAccess(room);
        return null;
    }
    
    /**
     * Retrieves a list of participants in  a room.
     * @param roomNo The room number.
     * @return The list of participants or an empty list if room does not exist.
     */
    public List<Participant> listParticipants(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        Access.checkRoomAccess(roomNo);
        return decorateWithPhonebookInformation(repository.findByRoomNo(roomNo));
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
        return decorateWithPhonebookInformation(repository.findByRoomNoAndParticipantId(roomNo, participantId));
    }

    /**
     * Kick a participant from a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void kickParticipant(String roomNo, String participantId) {
        Access.checkRoomAccess(roomNo);
        repository.kickParticipant(roomNo, participantId);
    }

    /**
     * Mutes a participant in a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void muteParticipant(String roomNo, String participantId) {
        Access.checkRoomAccess(roomNo);
        repository.muteParticipant(roomNo, participantId);
    }

    /**
     * Unmutes a participant.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void unmuteParticipant(String roomNo, String participantId) {
        Access.checkRoomAccess(roomNo);
        repository.unmuteParticipant(roomNo, participantId);
    }

    public void addEventListener(ConferenceEventListener listener) {
        repository.addEventListener(listener);
    }

    public void removeEventListener(ConferenceEventListener listener) {
        repository.removeEventListener(listener);
    }
    
    
    

    
    private List<Participant> decorateWithPhonebookInformation(List<Participant> participants) {
        for(Participant p : participants) {
            decorateWithPhonebookInformation(p);
        }
        return participants;
    }
    
    private Participant decorateWithPhonebookInformation(Participant participant) {
        PhonebookEntry entry = phonebookRepository.findByNumber(participant.getPhoneNumber());
        if(entry != null) {
            participant.setName(entry.getName());
        }
        return participant;
    }
    
}
