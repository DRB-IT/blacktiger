/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.Participant;

/**
 * Javadoc
 */
public abstract class ParticipantEvent {

    private final String roomNo;
    private final String participantId;

    public ParticipantEvent(String roomNo, String participantId) {
        this.roomNo = roomNo;
        this.participantId = participantId;
    }
    
    
    public String getRoomNo() {
        return roomNo;
    }

    public String getParticipantId() {
        return participantId;
    }
    
    
    
    
}
