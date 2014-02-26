package dk.drb.blacktiger.controller.websocket;

import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

/**
 *
 * @author michael
 */
@Controller
public class EventController {

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;
    private ConferenceService service;
    private EventHandler eventHandler = new EventHandler();
    
    private class EventHandler implements ConferenceEventListener {

        @Override
        public void onParticipantEvent(ConferenceEvent event) {
            messagingTemplate.convertAndSend("/events/" + event.getRoomNo(), event);
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
        
    }

    /*@SubscribeMapping("/{room}")
    public void subscribeEventsFor(@DestinationVariable("room") String room) {
        // Check if user can subscribe to the room
        System.out.println("room: " + room);
    }*/
}
