package dk.drb.blacktiger.sip;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;



public class TestCalls implements SipListener {

    private SipStack sipStack;
    private SipFactory sipFactory;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private HeaderFactory headerFactory;
    private SipProvider sipProvider;
    private long cseq;
    
    public void setUp() throws Exception
    {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "TextClient");
        properties.setProperty("javax.sip.IP_ADDRESS", "192.168.87.104");
        sipStack = sipFactory.createSipStack(properties);
        headerFactory = sipFactory.createHeaderFactory();
        addressFactory = sipFactory.createAddressFactory();
        messageFactory = sipFactory.createMessageFactory();
        sipProvider = sipStack.createSipProvider(sipStack.createListeningPoint("192.168.87.104", 5061, "udp"));
        sipProvider.addSipListener(this);
    }

    public void tearDown() throws Exception
    {
        //ua.dispose();
        //sipStack.dispose();
    }
    
    public void testCall() throws ParseException, InvalidArgumentException, TransactionUnavailableException, SipException {
        FromHeader fromHeader = headerFactory.createFromHeader(createAddress("L000000000", "192.168.50.2"), "12345");
        ToHeader toHeader = headerFactory.createToHeader(createAddress("+4522000000", "192.168.50.2"), "12345");
        
        SipURI requestURI = addressFactory.createSipURI("+4522000000", "192.168.50.2");
        ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");
        CallIdHeader callIdHeader = sipProvider.getNewCallId();
        MaxForwardsHeader maxForwards = this.headerFactory.createMaxForwardsHeader(70);
        ArrayList viaHeaders = new ArrayList();
        ViaHeader viaHeader = this.headerFactory.createViaHeader("192.68.50.2", 5060, "udp", null);
        viaHeaders.add(viaHeader);
        CSeqHeader cSeqHeader = this.headerFactory.createCSeqHeader(cseq, "REGISTER");
        
        Request request = messageFactory.createRequest(requestURI, Request.REGISTER, callIdHeader, cSeqHeader, fromHeader, toHeader, viaHeaders, maxForwards);
        ClientTransaction registerTid = sipProvider.getNewClientTransaction(request);
        registerTid.sendRequest();
    }

    @Override
    public void processRequest(RequestEvent re) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processResponse(ResponseEvent re) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processTimeout(TimeoutEvent te) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processIOException(IOExceptionEvent ioee) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent tte) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dte) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Address createAddress(String name, String host) throws ParseException {
        SipURI uri = addressFactory.createSipURI(name, host);
        return addressFactory.createAddress(uri);
    }

    public static void main(String[] args) throws Exception {
        TestCalls calls = new TestCalls();
        calls.setUp();
        calls.testCall();
    }
}
