package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public List<SipAccount> findByKeyAndPhonenumber(String key, String phoneNumber) {
        List<SipAccount> accounts = new ArrayList<>();
        SipAccount account = accountMap.get(key);
        if(account!=null) {
            accounts.add(account);
        }
        return accounts;
    }

    @Override
    public void save(SipAccount account) {
        String id = UUID.randomUUID().toString();
        LOG.info("Saving sipaccount. [id={}, account={}", id, account);
        accountMap.put(id, account);
    }
    
}
