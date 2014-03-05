package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

/**
 *
 * @author michael
 */
public class SipAccountService {

    private SipAccountRepository sipAccountRepository;

    @Autowired
    public void setSipAccountRepository(SipAccountRepository sipAccountRepository) {
        this.sipAccountRepository = sipAccountRepository;
    }
    
    public List<SipAccount> findByKeyAndPhonenumber(String key, String phoneNumber) {
        return sipAccountRepository.findByKeyAndPhonenumber(key, phoneNumber);
    }
    
    @Secured("ROLE_USER")
    public void save(SipAccount account) {
        sipAccountRepository.save(account);
    }
}
