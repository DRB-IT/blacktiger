package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestCancelEvent extends SparseParticipantEvent {

    public ParticipantCommentRequestCancelEvent(String roomNo, String channel) {
        super(roomNo, channel);
    }

    @Override
    public String getType() {
        return "CommentRequestCancel";
    }
    
}
