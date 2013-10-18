package dk.drb.blacktiger.model;

import dk.drb.blacktiger.service.*;

/**
 * Interface for event listeners BlackTigerService events.
 */
public interface ConferenceEventListener {
    
    /**
     * Fired when a ParticipantEvent is triggered.
     * @param event The event.
     */
    void onParticipantEvent(ParticipantEvent event);
    
}
