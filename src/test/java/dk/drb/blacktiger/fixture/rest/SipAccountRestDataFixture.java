package dk.drb.blacktiger.fixture.rest;

import dk.drb.blacktiger.model.SipAccount;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
public class SipAccountRestDataFixture {
    
    public static List<SipAccount> standardList() {
        SipAccount account = new SipAccount();
        account.setName("John Doe");
        account.setEmail("john@doe.dk");
        account.setPhoneNumber("+4512345678");
        List<SipAccount> list = new ArrayList<SipAccount>();
        list.add(account);
        return list;
    }
    
    public static String standardAccountAsJson() {
        return "{\n" +
            "    \"phoneNumber\": \"+4512121212\",\n" +
            "    \"name\": \"John Doe\",\n" +
            "    \"email\": \"john@doe.com\"\n" +
            "}";
    }
}
