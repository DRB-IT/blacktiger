package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.service.ConferenceService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for serving participants in a conference room via REST.
 */
@Controller
public class ParticipantController {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantController.class);
    private final ConferenceService service;
    
    /**
     * Constructor for new instance of ParticipantController.
     *
     * @param service The service which is to be used by the ParticipantController.
     */
    @Autowired
    public ParticipantController(ConferenceService service) {
        this.service = service;
    }
    
    @RequestMapping(value = "/rooms/{roomNo}/participants", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public List<Participant> listParticipants(@PathVariable final String roomNo) {
        LOG.debug("Got request for participants in room [room={}].", roomNo);
        return service.listParticipants(roomNo);
    }
    
    @RequestMapping(value = "/rooms/{roomNo}/participants/{channel}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public Participant getParticipant(@PathVariable final String roomNo, @PathVariable final String channel) {
        LOG.debug("Got request for participant in room [room={};participant={}].", roomNo, channel);
        return service.getParticipant(roomNo, channel);
    }

    @RequestMapping(value = "/rooms/{roomNo}/participants/{channel}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void kickParticipant(@PathVariable final String roomNo, @PathVariable final String channel) {
        LOG.debug("Kicking participant from room [room={};participant={}].", roomNo, channel);
        service.kickParticipant(roomNo, channel);
    }

    @RequestMapping(value = "/rooms/{roomNo}/participants/{channel}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public void saveParticipant(@PathVariable final String roomNo, @PathVariable final String channel, @RequestBody Participant participant) {
        LOG.debug("Persisting participant in room [room={};participant={}].", roomNo, channel);
        if(participant.isMuted()) {
            service.muteParticipant(roomNo, channel);
        } else {
            service.unmuteParticipant(roomNo, channel);
        }
    }

}
