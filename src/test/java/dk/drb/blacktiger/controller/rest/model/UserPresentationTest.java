package dk.drb.blacktiger.controller.rest.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserPresentationTest {
    
    @Test
    public void testBeanPattern() {
        System.out.println("getName");
        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserPresentation instance = UserPresentation.from(new UsernamePasswordAuthenticationToken("john", "doe", auths));
        
        assertEquals("john", instance.getName());
        assertEquals("ROLE_USER", instance.getRoles().get(0));
    }

   
}