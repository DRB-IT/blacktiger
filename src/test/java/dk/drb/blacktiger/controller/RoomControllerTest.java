/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.User;
import dk.drb.blacktiger.service.BlackTigerEventListener;
import dk.drb.blacktiger.service.IBlackTigerService;
import dk.drb.blacktiger.service.ParticipantEvent;
import dk.drb.blacktiger.service.ParticipantJoinEvent;
import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import static org.junit.Assert.*;

/**
 *
 */
public class RoomControllerTest {
    
    private class MockService implements IBlackTigerService {

        private List<BlackTigerEventListener> eventListeners = new ArrayList<BlackTigerEventListener>();
        
        @Override
        public User getUser(String username) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Participant> listParticipants(String roomNo) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Participant getParticipant(String roomNo, String participantId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void kickParticipant(String roomNo, String participantId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void muteParticipant(String roomNo, String participantId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void unmuteParticipant(String roomNo, String participantId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addEventListener(BlackTigerEventListener listener) {
            eventListeners.add(listener);
        }

        @Override
        public void removeEventListener(BlackTigerEventListener listener) {
            eventListeners.remove(listener);
        }
        
        public void fireJoinEvent(String room, String participantId) {
            ParticipantEvent event = new ParticipantJoinEvent(room, participantId);
            for(BlackTigerEventListener el : eventListeners) {
                el.onParticipantEvent(event);
            }
        }
        
    }

    

    /**
     * Test of listenForChange method, of class RoomController.
     */
    @Test
    @Ignore
    public void testListenForChange() {
        System.out.println("listenForChange");
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockService service = new MockService();
        String roomNo = "123";
        RoomController instance = new RoomController(service);
        instance.init();
        instance.listenForChange(request, response, roomNo);
        
        service.fireJoinEvent(roomNo, "321");
        
    }

    
}
