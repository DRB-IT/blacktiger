package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.service.PhonebookService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author michael
 */
@Controller
public class SystemController {
    
        private static final Logger LOG = LoggerFactory.getLogger(PhonebookController.class);
    
    //@Autowired
    //private SystemService service;
    
    @RequestMapping(value = "/system/information", headers = "Accept=application/json")
    @ResponseBody
    public List<Participant> listParticipants(@PathVariable final String roomNo) {
        LOG.debug("Got request for participants in room [room={}].", roomNo);
        return null;//service.listParticipants(roomNo);
    }
}
