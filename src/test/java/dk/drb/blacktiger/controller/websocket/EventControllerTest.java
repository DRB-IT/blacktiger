package dk.drb.blacktiger.controller.websocket;

import dk.drb.blacktiger.controller.rest.TestChannelInterceptor;
import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import dk.drb.blacktiger.config.WebsocketConfig;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.service.ConferenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
/**
 *
 * @author michael
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebsocketConfig.class})
public class EventControllerTest {
    static {
        System.setProperty("test", "true");
    }
    @Autowired private AbstractSubscribableChannel clientInboundChannel;

	@Autowired private AbstractSubscribableChannel clientOutboundChannel;

	@Autowired private AbstractSubscribableChannel brokerChannel;

	private TestChannelInterceptor clientOutboundChannelInterceptor;

	private TestChannelInterceptor brokerChannelInterceptor;
        private ConferenceService service;
        @Autowired private EventController controller;
        private ConferenceEventListener eventListener;

	@Before
	public void setUp() throws Exception {

		this.brokerChannelInterceptor = new TestChannelInterceptor(false);
		this.clientOutboundChannelInterceptor = new TestChannelInterceptor(false);

		this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
		this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
                
                service = Mockito.mock(ConferenceService.class);
                Mockito.doAnswer(eventListenerAnswer()).when(service).addEventListener(Mockito.any(ConferenceEventListener.class));
                controller.setService(service);
	}

        private Answer<Void> eventListenerAnswer() {
            return new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    eventListener = (ConferenceEventListener) invocation.getArguments()[0];
                    return null;
                }
            };
        }

	@Test
	public void getEvents() throws Exception {

                StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setSubscriptionId("0");
		headers.setDestination("/queue/events/H45-1234");
		headers.setSessionId("0");
		//Message<byte[]> message = MessageBuilder.withPayload("\"\"".getBytes()).setHeaders(headers).build();
                Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

                this.clientOutboundChannelInterceptor.setIncludedDestinations("/queue/events/H45-1234");
                this.clientOutboundChannelInterceptor.startRecording();
                this.clientInboundChannel.send(message);

                final List<Message> replies = new ArrayList<>();
                Thread t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            Message<?> reply = EventControllerTest.this.clientOutboundChannelInterceptor.awaitMessage(5);
                            replies.add(reply);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(EventControllerTest.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                t.start();
                Thread.sleep(1000);
                
                // Send event
                Participant p = new Participant();
                p.setCallerId("123");
                eventListener.onParticipantEvent(new ParticipantLeaveEvent("H45-1234", p));
                
                t.join();
                
		Message<?> reply = replies.get(replies.size()-1);
		assertNotNull("No reply recieved", reply);

		StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
		assertEquals("0", replyHeaders.getSessionId());
		assertEquals("0", replyHeaders.getSubscriptionId());
		assertTrue("Destination should start with /events/ but was " + replyHeaders.getDestination()+ ".", replyHeaders.getDestination().startsWith("/queue/events/"));

		String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
                assertEquals("{\"roomNo\":\"H45-1234\",\"participant\":{\"callerId\":\"123\",\"channel\":null,\"muted\":false,\"phoneNumber\":null,\"dateJoined\":null,\"name\":null,\"type\":null,\"host\":false},\"type\":\"Leave\"}", json);
	}
}