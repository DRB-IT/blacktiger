package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.Room;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.sip.core.useragent.BaseSipListener;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.sip.syntaxencoding.SipUriSyntaxException;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class AsteriskConfbridgeRepositoryIT {
    
    private class SipListener extends BaseSipListener {

        Boolean registered = null;
        Boolean pickedUp = null;
                
        @Override
        public void registerFailed(SipResponse sipResponse) {
            registered = false;
        }

        @Override
        public void registerSuccessful(SipResponse sipResponse) {
            registered = true;
        }

        @Override
        public void calleePickup(SipResponse sipResponse) {
            pickedUp = true;
        }

        @Override
        public void error(SipResponse sipResponse) {
            registered = false;
            pickedUp = false;
        }
    }
    
    private class Caller {
        
        private String username;
        private String callDestination;
        private SipListener listener = new SipListener();
        private SipRequest sipRequest;
        private UserAgent agent;
        
        public Caller(String username, String password, String callDestination) throws UnknownHostException, SocketException {
            this.username = username;
            this.callDestination = callDestination;
            
            JavaConfig cfg = new JavaConfig();
            cfg.setDomain("192.168.50.2");
            cfg.setLocalInetAddress(InetAddress.getByName("192.168.50.1"));
            cfg.setUserPart(username);
            cfg.setPassword(password);
            agent = new UserAgent(listener, cfg);
        }
        
        public void register() throws SipUriSyntaxException {
            agent.getUac().register();
        }
        
        public void call() throws SipUriSyntaxException {
            sipRequest = agent.getUac().invite(callDestination, null);
        }
        
        public void hangup() throws SipUriSyntaxException {
            agent.getUac().terminate(sipRequest);
        }
        
        public void unregister() throws SipUriSyntaxException {
            agent.getUac().unregister();
            agent.close();
        }
    }
    
    @Before
    public void setUp() {
        asteriskServer = new DefaultAsteriskServer("192.168.50.2", "vagrant", "vagrant");
        asteriskServer.initialize();
        repository = new AsteriskConfbridgeRepository();
        repository.setAsteriskServer(asteriskServer);
    }
    
    private AsteriskServer asteriskServer;
    private AsteriskConfbridgeRepository repository;
    
    @Test
    public void testListRooms() throws Exception {
        Caller caller = null;
        
        try {
        List<Room> rooms = repository.findAll();
        assertEquals(0, rooms.size());
        
        caller = new Caller("H45-0002", "12345", "sip:+4500000002@192.168.50.2");
        caller.register();
        caller.call();
            
        Thread.sleep(2000);
        
        repository.handleEventQueue();
        
        rooms = repository.findAll();
        assertEquals(1, rooms.size());
        
        Room room = repository.findOne(rooms.get(0).getId());
        assertNotNull(room);
        
        } catch(Throwable t) {
            if(caller != null) {
                caller.hangup();
                Thread.sleep(200);
                caller.unregister();
                Thread.sleep(100);
            }
        }
        

    }

    
    
}
