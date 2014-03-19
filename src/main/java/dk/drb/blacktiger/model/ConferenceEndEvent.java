package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ConferenceEndEvent extends ConferenceEvent {

    public ConferenceEndEvent(String roomNo) {
        super(roomNo);
    }

    
    @Override
    public String getType() {
        return "ConferenceEnd";
    }
    
}
