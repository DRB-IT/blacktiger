package dk.drb.blacktiger.model;

/**
 * A <code>ParticipantEvent</code> for when a <code>Participant</code> leaves a conference room.
 */
public class ParticipantLeaveEvent extends ConferenceEvent {

    private final String participantId;
    
    public ParticipantLeaveEvent(String roomNo, String participantId) {
        super(roomNo);
        this.participantId = participantId;
    }

    public String getParticipantId() {
        return participantId;
    }

    @Override
    public String getType() {
        return "Leave";
    }

    
}
