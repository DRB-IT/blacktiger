package dk.drb.blacktiger.controller;


import dk.drb.blacktiger.security.SystemUserDetailsManager;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for landing page.
 */
@Controller
public class WelcomePageController {

    @RequestMapping(value = "/")
    public String showIndex() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/app.html";
        }
        return "landingpage";
    }
    
    @RequestMapping("/assets/js/blacktiger-service-*")
    public String serveServiceFile() {
        return "blacktiger-service";
    } 
    
    @RequestMapping("/rooms")
    @ResponseBody
    public String[] getRooms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> rooms = new ArrayList<String>();
        
        for(GrantedAuthority authority : auth.getAuthorities()) {
            if(authority.getAuthority().startsWith(SystemUserDetailsManager.ROLE_ROOMACCESS_PREFIX)) {
                rooms.add(authority.getAuthority().substring(SystemUserDetailsManager.ROLE_ROOMACCESS_PREFIX.length()));
            }
        }
        
        return rooms.toArray(new String[0]);
    }

}
