package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ParticipantCommentRequestEvent extends SparseParticipantEvent {

    public ParticipantCommentRequestEvent(String roomNo, String channel) {
        super(roomNo, channel);
    }

    @Override
    public String getType() {
        return "CommentRequest";
    }
    
}
