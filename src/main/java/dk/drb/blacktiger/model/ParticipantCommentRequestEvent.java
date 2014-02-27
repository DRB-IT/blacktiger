package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestEvent extends ConferenceEvent {

    private String participantId;
    
    public ParticipantCommentRequestEvent(String roomNo, String participantId) {
        super(roomNo);
        this.participantId = participantId;
    }

    public String getParticipantId() {
        return participantId;
    }

    @Override
    public String getType() {
        return "CommentRequest";
    }
    
}
