package dk.drb.blacktiger.model;


/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> joins a conference room.
 */
public class ParticipantJoinEvent extends ConferenceEvent {

    private final Participant participant;
    
    public ParticipantJoinEvent(String roomNo, Participant participant) {
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
