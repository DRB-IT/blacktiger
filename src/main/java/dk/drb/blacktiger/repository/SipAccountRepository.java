package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.SipAccount;
import java.util.List;

/**
 *
 * @author michael
 */
public interface SipAccountRepository {
    public SipAccount findOneByKeyAndPhonenumber(String key, String phoneNumber);
    public void save(String hall, SipAccount account);
}
