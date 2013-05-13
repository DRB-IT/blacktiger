package dk.drb.blacktiger.security;

import java.util.ArrayList;
import java.util.List;

import dk.drb.blacktiger.service.IBlackTigerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * A UserDetailsService implementation for the users available via the IBlackTigerService.<br>
 * <br>
 * The UserDetails retrieved with be given 2 roles:<br>
 * - ROLE_USER<br>
 * - ROLE_ROOMACCESS_<username>1<br>
 * <br>
 * In other words for the user '1234' the roles will be: ROLE_USER & ROLE_ROOMACCESS_12341
 */
public class SystemUserDetailsManager implements UserDetailsService {

    @Autowired
    private IBlackTigerService service;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        dk.drb.blacktiger.model.User user = service.getUser(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found. [username=" + username + "]");
        }

        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        authList.add(new SimpleGrantedAuthority("ROLE_ROOMACCESS_" + username + "1"));
        authList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, authList);
    }

}
