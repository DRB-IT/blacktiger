package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.SipAccount;
import java.util.List;
import org.springframework.security.access.annotation.Secured;

/**
 *
 * @author michael
 */
public class SipAccountService {

    public List<SipAccount> findByKeyAndPhonenumber(String key, String phoneNumber) {
        return null;
    }
    
    @Secured("ROLE_USER")
    public void save(SipAccount account) {
        
    }
}
