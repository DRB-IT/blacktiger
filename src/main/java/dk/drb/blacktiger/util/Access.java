package dk.drb.blacktiger.util;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author michael
 */
public class Access {
    
    private static final Logger LOG = LoggerFactory.getLogger(Access.class);
    
    /**
     * Checks whether current user has access to a given room number.
     */
    public static void checkRoomAccess(String roomNo) {
        if(!hasRole("ROOMACCESS_" + roomNo) && !hasRole("ADMIN")) {
            throw new AccessDeniedException("Not authorized to access room " + roomNo);
        }
    }
    
        /**
     * Checks whether current user holds a specific role.
     */
    public static boolean hasRole(String role) {
        LOG.debug("Checking if current user has role '{}'", role);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated()) {
            for(GrantedAuthority ga : auth.getAuthorities()) {
                if(ga.getAuthority().startsWith("ROLE_") && ga.getAuthority().substring(5).equals(role)) {
                    return true;
                }
            }
        }
        LOG.debug("User does not have role. [auth={}]", auth);
        return false;
    }
    

    public static List<String> getAccessibleRooms() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String prefix = "ROLE_ROOMACCESS_";
        List<String> list = new ArrayList<>();
        LOG.debug("Retrieving the accessible rooms for user ({})", auth);
        if(auth!=null && auth.isAuthenticated()) {
            for(GrantedAuthority ga : auth.getAuthorities()) {
                String role = ga.getAuthority();
                if(role.startsWith(prefix)) {
                    list.add(role.substring(prefix.length()));
                }
            }
        }
        return list;
    }

}
