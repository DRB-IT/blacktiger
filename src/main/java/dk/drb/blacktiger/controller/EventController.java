package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.model.ParticipantEvent;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author michael
 */
@Controller
public class EventController {
    
    @RequestMapping(value="/events", method = RequestMethod.GET)
    @ResponseBody
    public List<ParticipantEvent> getEvents(@RequestParam(required = false) long since, @RequestParam String[] rooms) {
        return null;
    }
}
