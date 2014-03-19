package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.util.List;

/**
 *
 * @author michael
 */
public class JdbcSipAccountRepository implements SipAccountRepository {

    @Override
    public List<SipAccount> findByKeyAndPhonenumber(String key, String phoneNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(SipAccount account) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
