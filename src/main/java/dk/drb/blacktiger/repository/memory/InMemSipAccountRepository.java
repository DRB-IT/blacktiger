package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael
 */
public class InMemSipAccountRepository implements SipAccountRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InMemSipAccountRepository.class);
    private Map<String, SipAccount> accountMap = new HashMap<>();
    
    @Override
    public SipAccount findOneByKeyAndPhonenumber(String key, String phoneNumber) {
        return accountMap.get(key);
    }

    @Override
    public boolean create(String hall, SipAccount account, String mailText) {
        String id = UUID.randomUUID().toString();
        LOG.info("Saving sipaccount. [id={}, account={}", id, account);
        accountMap.put(id, account);
        return true;
    }

    @Override
    public boolean sendPasswordEmail(String name, String phoneNumber, String email, String cityOfHall, String phoneNumberOfHall, String emailSubject, String emailTextManager, String emailTextUser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
