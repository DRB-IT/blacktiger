package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.controller.rest.model.ResourceNotFoundException;
import java.util.List;

import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.service.ConferenceService;
import groovy.util.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for conference rooms.
 */
@Controller
public class RoomController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);
    private final ConferenceService service;
    
    
    /**
     * Constructor for new instance of RoomController.
     *
     * @param service The service which is to be used by the RoomController.
     */
    @Autowired
    public RoomController(ConferenceService service) {
        this.service = service;
    }

    @RequestMapping("/rooms")
    @ResponseBody
    @Secured("ROLE_USER")
    public List<Room> getRooms() {
        LOG.debug("Got request for all rooms.");
        return service.listRooms();
    }
    
    @RequestMapping(value = "/rooms/{roomNo}", headers = "Accept=application/json")
    @ResponseBody
    public Room get(@PathVariable final String roomNo) {
        LOG.debug("Got request for specific room '{}'.", roomNo);
        return RestExceptionHandler.notNull(service.getRoom(roomNo), "Room does not exist.");
    }

    @RequestMapping(value = "/rooms/{roomNo}", headers = "Accept=application/json", method = RequestMethod.PUT)
    @ResponseBody
    public void save(@PathVariable final String roomNo, @RequestBody Room room) {
        LOG.debug("Persisting room {}.", room);
        service.saveRoom(room);
    }
}
