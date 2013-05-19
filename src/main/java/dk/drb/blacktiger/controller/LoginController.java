
package dk.drb.blacktiger.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for login page.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login")
    public String showIndex() {
        return "login";
    }
}
