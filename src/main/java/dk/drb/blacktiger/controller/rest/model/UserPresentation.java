package dk.drb.blacktiger.controller.rest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author michael
 */
public class UserPresentation {
    
    private final String name;
    private final List roles;

    public UserPresentation(String name, List roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public List getRoles() {
        return roles;
    }
    
    public static UserPresentation from(Authentication authentication) {
        if(authentication == null) {
            return new UserPresentation("anonymous", Collections.EMPTY_LIST);
        } else {
            List<String> roles = new ArrayList<>();
            for(GrantedAuthority role : authentication.getAuthorities()) {
                roles.add(role.getAuthority());
            }
            return new UserPresentation(authentication.getName(), roles);
        }
    }
}
