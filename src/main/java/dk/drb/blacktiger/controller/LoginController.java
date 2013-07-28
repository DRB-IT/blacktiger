package dk.drb.blacktiger.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for login page.
 */
@Controller
public class LoginController {

    /**
     * Shows the login page.
     */
    @RequestMapping(value = "/login")
    public String showIndex() {
        return "login";
    }
}
