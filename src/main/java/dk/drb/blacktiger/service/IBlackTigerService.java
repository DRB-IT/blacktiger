/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.service;

import java.util.Date;
import java.util.List;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.Call;
import dk.drb.blacktiger.model.User;

/**
 *
 * @author michael
 */
public interface IBlackTigerService {
    /**
     * Retrieves a user by a username and password.
     * @param username The username
     * @param password The password
     * @return User or null of user not found.
     */
    User getUser(String username);
    
    /**
     * Retrieves a list of participants in  a room.
     * @param roomNo The room number.
     * @return The list of participants or an empty list if room does not exist.
     */
    List<Participant> listParticipants(String roomNo);
    
    /**
     * Retrieves a specific participant in a room. 
     * @param roomNo The room number
     * @param participantId The participant id.
     * @return The participant or null if no match found.
     */
    Participant getParticipant(String roomNo, String participantId);
    
    /**
     * Kick a participant from a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    void kickParticipant(String roomNo, String participantId);
   
    /**
     * Retrieves an entry from the phonebook.
     * @param phoneNumber The number to retrieve for.
     * @return The name from the phonebook or null if not available.
     */
    String getPhonebookEntry(String phoneNumber);
    
    /**
     * Sets a name for a given phonenumber in the phonebook.
     * @param phoneNumber The number to change the name for.
     * @param name The new name for the entry.
     */
    void updatePhonebookEntry(String phoneNumber, String name) ;
    
    /**
     * Retrieves a list of archived calls.
     * @param start The start timestamp for the list.
     * @param end The end timestamp for the list.
     * @param minimumDuration The minimum duration in seconds for each call to include.
     * @return The list of archived calls.
     */
    List<Call> getReport(Date start, Date end, int minimumDuration);
    
    /**
     * Mutes a participant in a room.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    void muteParticipant(String roomNo, String participantId);
    
    
    
    /**
     * Unmutes a participant.
     * @param roomNo The room number.
     * @param participantId  The participant id.
     */
    void unmuteParticipant(String roomNo, String participantId);
    
    void addEventListener(BlackTigerEventListener listener);
    void removeEventListener(BlackTigerEventListener listener);
    
}
