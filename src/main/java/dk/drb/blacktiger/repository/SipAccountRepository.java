package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.SipAccount;
import java.util.List;

/**
 *
 * @author michael
 */
public interface SipAccountRepository {
    public SipAccount findOneByKeyAndPhonenumber(String key, String phoneNumber);
    public boolean create(String hall, SipAccount account, String mailText);
    boolean sendPasswordEmail(String name, String phoneNumber, String email, String cityOfHall, String phoneNumberOfHall, String emailSubject, 
            String emailTextManager, String emailTextUser);
}
