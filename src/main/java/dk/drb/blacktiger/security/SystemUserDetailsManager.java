/*
 * Copyright by Apaq 2011-2013
 */

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
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * Javadoc
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
        return new User(user.getUsername(), user.getPassword(), true, true, true, true, authList);
    }

}
