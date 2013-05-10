/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.controller;


import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Javadoc
 */
@Controller
public class WelcomePageController {

    

    @RequestMapping(value = "/")
    public String showIndex() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/rooms/" + auth.getName() + "1";
        }
        return "landingpage";
    }

}
