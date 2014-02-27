package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.manager.EventTimeoutException;
import org.asteriskjava.manager.ResponseEvents;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.ConfbridgeListRoomsAction;
import org.asteriskjava.manager.action.EventGeneratingAction;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.event.ConfbridgeListRoomsEvent;
import org.asteriskjava.manager.event.ResponseEvent;
import org.asteriskjava.manager.response.ManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This implementation of ConferenceRepository uses the MeetMe conference in Asterisk.
 */
public class AsteriskConfbridgeRoomsRepository implements ConferenceRoomRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AsteriskConfbridgeRoomsRepository.class);
    private AsteriskServer asteriskServer;

    @Autowired
    public void setAsteriskServer(AsteriskServer asteriskServer) {
        this.asteriskServer = asteriskServer;
    }
    
    public List findRooms() {
        ResponseEvents events = sendAction(new ConfbridgeListRoomsAction());
        
        List result = new ArrayList();
        
        for (ResponseEvent event : events.getEvents()) {
            if(event instanceof ConfbridgeListRoomsEvent) {
                ConfbridgeListRoomsEvent roomsEvent = (ConfbridgeListRoomsEvent) event;
                result.add(roomsEvent.getConference());
            }
        }
        return result;
    }

    @Override
    public Room findOne(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> findAllByIds(List<String> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    public void save(Room room) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    private ManagerResponse sendAction(ManagerAction action) {
        try {
            return asteriskServer.getManagerConnection().sendAction(action);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalStateException ex) {
            throw new RuntimeException(ex);
        } catch (TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private ResponseEvents sendAction(EventGeneratingAction action) {
        try {
            return asteriskServer.getManagerConnection().sendEventGeneratingAction(action, 100);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (EventTimeoutException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalStateException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
