package dk.drb.blacktiger.model;


/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> joins a conference room.
 */
public class ConferenceJoinEvent extends ConferenceEvent {

    private final Participant participant;
    
    public ConferenceJoinEvent(String roomNo, Participant participant) {
        super(roomNo);
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    @Override
    public String getType() {
        return "Join";
    }

    
}
