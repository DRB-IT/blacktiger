package dk.drb.blacktiger.model;

import dk.drb.blacktiger.service.*;

/**
 * Base abastract class for partipant events used by the <code>IBlackTigerService</code>.
 */
public abstract class ConferenceEvent {

    private final String roomNo;
    private final String participantId;

    public ConferenceEvent(String roomNo, String participantId) {
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
