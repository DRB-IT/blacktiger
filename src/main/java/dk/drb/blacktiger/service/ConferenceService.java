package dk.drb.blacktiger.service;

import dk.drb.blacktiger.repository.ConferenceRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.model.Room;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 */
public class ConferenceService {
 
    private static final Logger LOG = LoggerFactory.getLogger(ConferenceService.class);
    private ConferenceRepository repository;
    private PhonebookRepository phonebookRepository;

    @Autowired
    public void setRepository(ConferenceRepository repository) {
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
        return null;
    }
    
    /**
     * Retrieves a list of participants in  a room.
     * @param roomNo The room number.
     * @return The list of participants or an empty list if room does not exist.
     */
    public List<Participant> listParticipants(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        checkRoomAccess(roomNo);
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
        checkRoomAccess(roomNo);
        return decorateWithPhonebookInformation(repository.findByRoomNoAndParticipantId(roomNo, participantId));
    }

    /**
     * Kick a participant from a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void kickParticipant(String roomNo, String participantId) {
        checkRoomAccess(roomNo);
        repository.kickParticipant(roomNo, participantId);
    }

    /**
     * Mutes a participant in a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void muteParticipant(String roomNo, String participantId) {
        checkRoomAccess(roomNo);
        repository.muteParticipant(roomNo, participantId);
    }

    /**
     * Unmutes a participant.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    public void unmuteParticipant(String roomNo, String participantId) {
        checkRoomAccess(roomNo);
        repository.unmuteParticipant(roomNo, participantId);
    }

    public void addEventListener(ConferenceEventListener listener) {
        repository.addEventListener(listener);
    }

    public void removeEventListener(ConferenceEventListener listener) {
        repository.removeEventListener(listener);
    }
    
    /**
     * Checks whether current user has access to a given room number.
     */
    private void checkRoomAccess(String roomNo) {
        if(!hasRole("ROOMACCESS_" + roomNo)) {
            throw new SecurityException("Not authorized to access room " + roomNo);
        }
    }
    
    /**
     * Checks whether current user hols a specific role.
     */
    private boolean hasRole(String role) {
        LOG.debug("Checking if current user has role '{}'", role);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated()) {
            for(GrantedAuthority ga : auth.getAuthorities()) {
                if(ga.getAuthority().startsWith("ROLE_") && ga.getAuthority().substring(5).equals(role)) {
                    return true;
                }
            }
        }
        LOG.debug("User does not have role. [auth={}]", auth);
        return false;
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
