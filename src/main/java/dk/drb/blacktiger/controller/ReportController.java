/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for editing reports.
 */
@Controller
public class ReportController {

    @RequestMapping("/reports/{roomNo}")
    public ModelAndView showReport(@PathVariable String roomNo) {
        return new ModelAndView("report");
    }
    
    @RequestMapping("/reports")
    public String redirectToReport(@PathVariable String roomNo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/reports/" + auth.getName() + "1";
        } else {
            return "redirect:/";
        }
    }
    
}
