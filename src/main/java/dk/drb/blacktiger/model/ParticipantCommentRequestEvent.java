package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestEvent extends SparseParticipantEvent {

    public ParticipantCommentRequestEvent(String roomNo, String callerId) {
        super(roomNo, callerId);
    }

    @Override
    public String getType() {
        return "CommentRequest";
    }
    
}
