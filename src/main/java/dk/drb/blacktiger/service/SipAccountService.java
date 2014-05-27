package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

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
    
    public SipAccount findOneByKeyAndPhonenumber(String key, String phoneNumber) {
        return sipAccountRepository.findOneByKeyAndPhonenumber(key, phoneNumber);
    }
    
    @Secured("ROLE_USER")
    public boolean create(SipAccount account, String mailText) {
        String hall = SecurityContextHolder.getContext().getAuthentication().getName();
        return sipAccountRepository.create(hall, account, mailText);
    }

}
