package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public abstract class ParticipantEvent extends ConferenceEvent {
    
    private final Participant participant;

    public ParticipantEvent(String roomNo, Participant participant) {
        super(roomNo);
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }
    
}
