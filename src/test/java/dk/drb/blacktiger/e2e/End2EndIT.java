package dk.drb.blacktiger.e2e;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import dk.apaq.peers.JavaConfig;
import dk.apaq.peers.sip.core.useragent.UserAgent;
import dk.apaq.peers.sip.transport.SipRequest;
import dk.apaq.peers.sip.transport.SipResponse;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class End2EndIT {
    
    /*private BaseSipListener listener = new BaseSipListener() {

        @Override
        public void registerSuccessful(SipResponse sipResponse) {
            System.out.println("We got registered. ");
        }

        @Override
        public void remoteHangup(SipRequest sipRequest) {
            System.out.println("Was remotely hanged up. ");
        }

        @Override
        public void registering(SipRequest sipRequest) {
            System.out.println("Is registering. ");
        }

        @Override
        public void registerFailed(SipResponse sipResponse) {
            System.out.println("Register failed. " + sipResponse.getReasonPhrase());
        }
        
        
        
    };
    
    @Test
    public void ifEnd2EndTestWorksAsExpected() throws Exception {
        List<UserAgent> agents = new ArrayList<>();
        
        JavaConfig config = new JavaConfig();
        config.setDomain("192.168.50.2");
        config.setSipPort(5060);
        config.setUserPart("#00000000");
        config.setPassword("1234567890");
        config.setLocalInetAddress(InetAddress.getLocalHost());
        UserAgent agent = new UserAgent(listener, config);
        SipRequest request =  agent.getUac().register();
        agent.getUac().invite("sip:H45-0000@192.168.50.2", null);
        
        Thread.sleep(10000);
        agent.getUac().unregister();
    }*/
}
