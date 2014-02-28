package dk.drb.blacktiger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:blacktiger.properties","file:${user.home}/blacktiger.properties"})
@Import({ServiceConfig.class, WebsocketConfig.class, ControllerConfig.class})
@ImportResource("classpath:springsecurity.xml")
public class AppConfig {

    
}
