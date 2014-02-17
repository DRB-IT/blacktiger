package dk.drb.blacktiger.controller;

import java.util.ArrayList;
import java.util.List;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.security.SystemUserDetailsManager;
import dk.drb.blacktiger.service.ConferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String[] getRooms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> rooms = new ArrayList<String>();
        
        for(GrantedAuthority authority : auth.getAuthorities()) {
            if(authority.getAuthority().startsWith(SystemUserDetailsManager.ROLE_ROOMACCESS_PREFIX)) {
                rooms.add(authority.getAuthority().substring(SystemUserDetailsManager.ROLE_ROOMACCESS_PREFIX.length()));
            }
        }
        
        return rooms.toArray(new String[0]);
    }
    
    @RequestMapping(value = "/rooms/{roomNo}", headers = "Accept=application/json")
    @ResponseBody
    public List<Participant> get(@PathVariable final String roomNo) {
        LOG.debug("Got JSON request for room '{}'.", roomNo);
        return service.listParticipants(roomNo);
    }

}
