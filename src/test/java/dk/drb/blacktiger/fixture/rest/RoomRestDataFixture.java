package dk.drb.blacktiger.fixture.rest;

import dk.drb.blacktiger.model.Room;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class RoomRestDataFixture {
    
    public static Room standardRoom(String id) {
        Room room = new Room();
        room.setId(id);
        room.setDisplayName("Room " + id);
        return room;
    }
    
    public static List<Room> standardListOfRooms(String ... ids) {
        List<Room> rooms = new ArrayList<Room>();
        for(String id :ids) {
            rooms.add(standardRoom(id));
        }
        return rooms;
    }
    
    public static String standardRoomAsJson(String id) {
        return "{\"id\":\"" + id + "\",\"displayName\":\"Room " + id + "\",\"participants\":[],\"contact\":{\"name\":null,\"email\":null,\"phoneNumber\":null,\"comment\":null}}";
    }
    
    public static String standardListOfRoomsAsJson(String ... ids) {
        String value = "[";
        int count = 0;
        for(String id: ids) {
            if(count>0) {
                value+=",";
            }
            value+=standardRoomAsJson(id);
            count++;
        }
        value+="]";
        return value;
    }
}
