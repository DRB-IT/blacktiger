package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public class ConferenceStartEvent extends ConferenceEvent {

    private Room room;
    
    public ConferenceStartEvent(Room room) {
        super(room.getId());
        this.room = room;
    }

    @Override
    public String getType() {
        return "ConferenceStart";
    }

    public Room getRoom() {
        return room;
    }
    
}
