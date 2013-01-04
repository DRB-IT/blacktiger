/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.service;

import java.util.List;
import dk.drb.blacktiger.model.Participant;
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
    User getUser(String username, String password);
    
    /**
     * Retrieves a list of participants in  a room.
     * @param roomNo The room number.
     * @return The list of participants.
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
}
