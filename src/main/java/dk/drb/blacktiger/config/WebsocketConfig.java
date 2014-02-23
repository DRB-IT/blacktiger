package dk.drb.blacktiger.config;

import dk.drb.blacktiger.util.EventConverter;
import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 *
 * @author michael
 */
@Configuration
@EnableScheduling
@ComponentScan(
		basePackages="dk.drb.blacktiger.controller",
		excludeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION, value = Configuration.class)
)
@Import({ServiceConfig.class})
@EnableWebSocketMessageBroker
public class WebsocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry r) {
        r.addEndpoint("/queue").withSockJS(); // WebSocket URL prefix
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) { 
        registration.taskExecutor().corePoolSize(4).maxPoolSize(10);
        
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new EventConverter());
        return true;
    }
    
    

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //registry.enableSimpleBroker("/events"); // destination prefix
        registry.setApplicationDestinationPrefixes("/events");
    }
}
