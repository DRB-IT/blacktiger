package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.service.ConferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "/rooms/{roomNo}/{participantId}", headers = "Accept=application/json")
    @ResponseBody
    public Participant getParticipant(@PathVariable final String roomNo, @PathVariable final String participantId) {
        LOG.debug("Got JSON request for participant in room [room={};participant={}].", roomNo, participantId);
        return service.getParticipant(roomNo, participantId);
    }

    @RequestMapping(value = "/rooms/{roomNo}/{participantId}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    @ResponseBody
    public int kickParticipant(@PathVariable final String roomNo, @PathVariable final String participantId) {
        LOG.debug("Kicking participant from room [room={};participant={}].", roomNo, participantId);
        service.kickParticipant(roomNo, participantId);
        return 1;
    }

    @RequestMapping(value = "/rooms/{roomNo}/{participantId}/mute", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public int muteParticipant(@PathVariable final String roomNo, @PathVariable final String participantId, @RequestBody boolean muted) {
        LOG.debug("Muting participant in room [room={};participant={}].", roomNo, participantId);
        if(muted) {
            service.muteParticipant(roomNo, participantId);
        } else {
            service.unmuteParticipant(roomNo, participantId);
        }
        return 1;
    }

}
