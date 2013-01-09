/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.controller;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Javadoc
 */
@Controller
public class WelcomePageController {

    

    @RequestMapping(value = "/index")
    public String showIndex() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/data/rooms/" + auth.getName() + "1";
        }
        return "index";
    }

}
