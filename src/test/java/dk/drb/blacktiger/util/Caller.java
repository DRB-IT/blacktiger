package dk.drb.blacktiger.util;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.sip.syntaxencoding.SipUriSyntaxException;
import net.sourceforge.peers.sip.transport.SipRequest;

public class Caller {

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
        agent = new UserAgent(listener, cfg, new SilentSoundManager());
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

    public void sendDtmf(char digit) {
        agent.getMediaManager().sendDtmf(digit);
    }
}
