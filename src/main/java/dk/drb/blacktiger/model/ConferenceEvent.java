package dk.drb.blacktiger.model;

import dk.drb.blacktiger.service.*;

/**
 * Base abastract class for partipant events used by the <code>IBlackTigerService</code>.
 */
public abstract class ConferenceEvent {

    private final String roomNo;
    
    public ConferenceEvent(String roomNo) {
        this.roomNo = roomNo;
    }
    
    
    public String getRoomNo() {
        return roomNo;
    }

    public abstract String getType();
    
}
