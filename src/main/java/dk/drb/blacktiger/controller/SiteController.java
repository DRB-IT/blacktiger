/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.controller;

import java.util.List;

import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.service.IBlackTigerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Javadoc
 */
@Controller
public class SiteController {

    @Autowired 
    private IBlackTigerService service;
    
    @RequestMapping("/rooms/{roomNo}")
    public ModelAndView showRoom(@PathVariable final String roomNo) {
        final List<Participant> list = service.listParticipants(roomNo);
        return new ModelAndView("room", "participants", list);
    }
}
