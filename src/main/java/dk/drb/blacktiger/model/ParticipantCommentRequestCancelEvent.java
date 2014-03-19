package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestCancelEvent extends SparseParticipantEvent {

    public ParticipantCommentRequestCancelEvent(String roomNo, String callerId) {
        super(roomNo, callerId);
    }

    @Override
    public String getType() {
        return "CommentRequestCancel";
    }
    
}
