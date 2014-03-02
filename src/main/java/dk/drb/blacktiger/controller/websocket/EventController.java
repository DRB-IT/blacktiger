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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.annotation.Secured;
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

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;
    private ConferenceService service;
    private EventHandler eventHandler = new EventHandler();
    
    private class EventHandler implements ConferenceEventListener {

        @Override
        public void onParticipantEvent(ConferenceEvent event) {
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
        
    }

    @SubscribeMapping("/{room}")
    public List<ParticipantJoinEvent> subscribeEventsFor(@DestinationVariable("room") String roomId, Principal principal) {
        // Start out by sending join events for all in the room.
        List<ParticipantJoinEvent> events = new ArrayList<>();
        if(principal instanceof Authentication) {
            Authentication auth = (Authentication) principal;
            Authentication oldAuth = SecurityContextHolder.getContext().getAuthentication();
            SecurityContextHolder.getContext().setAuthentication(auth);
            if(Access.hasRole("ADMIN") || Access.getAccessibleRooms().contains(roomId)) {

                if("*".equals(roomId)) {
                    // Send all participants
                    for(Room room : service.listRooms()) {
                        addEvents(events, room.getId());
                    }
                } else {
                    addEvents(events, roomId);
                }
            }
            SecurityContextHolder.getContext().setAuthentication(oldAuth);
        }
        return events;
    }
    
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
    
    private void addEvents(List<ParticipantJoinEvent> events, String roomId) {
        for(Participant p : service.listParticipants(roomId)) {
            events.add(new ParticipantJoinEvent(roomId, p));
        }
    }
}
