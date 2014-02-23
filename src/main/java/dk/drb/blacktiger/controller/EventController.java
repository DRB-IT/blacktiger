package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.service.ConferenceService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author michael
 */
@Controller
public class EventController {

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;
    private ConferenceService service;
    
    private class EventHandler implements ConferenceEventListener {

        @Override
        public void onParticipantEvent(ConferenceEvent event) {
            messagingTemplate.convertAndSend("/events/" + event.getRoomNo(), event);
        }
    }

    @Autowired
    public void setService(ConferenceService service) {
        this.service = service;
        service.addEventListener(new EventHandler());
        
    }

    @SubscribeMapping("/{room}")
    @Secured("ROLE_USER")
    public void subscribeEventsFor(@DestinationVariable("room") String room) {
        // Check if user can subscribe to the room
        System.out.println("room: " + room);
    }
}
