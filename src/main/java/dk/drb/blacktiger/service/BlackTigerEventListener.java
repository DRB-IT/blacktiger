package dk.drb.blacktiger.service;

/**
 * Interface for event listeners BlackTigerService events.
 */
public interface BlackTigerEventListener {
    
    /**
     * Fired when a Participance event is triggered.
     * @param event The event.
     */
    void onParticipantEvent(ParticipantEvent event);
    
}
