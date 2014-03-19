package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ConferenceStartEvent extends ConferenceEvent {

    public ConferenceStartEvent(String roomNo) {
        super(roomNo);
    }

    @Override
    public String getType() {
        return "ConferenceStart";
    }
    
    
}
