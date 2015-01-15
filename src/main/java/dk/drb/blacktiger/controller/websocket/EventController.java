package dk.drb.blacktiger.controller.websocket;

import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.service.ConferenceService;
import dk.drb.blacktiger.util.Access;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
@Controller
public class EventController {

    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);
    
    @Autowired
    private MessageSendingOperations<String> messagingTemplate;
    private ConferenceService service;
    private EventHandler eventHandler = new EventHandler();
    
    private class EventHandler implements ConferenceEventListener {

        @Override
        public void onParticipantEvent(ConferenceEvent event) {
            LOG.info("Sending event to queue [queue={};type={}]", event.getRoomNo(), event.getType());
            messagingTemplate.convertAndSend("/queue/events/" + event.getRoomNo(), event);
        }
    }
    
    @Autowired
    public void setService(ConferenceService service) {
        Assert.notNull(service, "service cannot be null.");
        
        if(this.service != null) {
            this.service.removeEventListener(eventHandler);
        }
        
        this.service = service;
        this.service.addEventListener(eventHandler);
        LOG.info("EventController initialized.");
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }

}
