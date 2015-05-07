package dk.drb.blacktiger.config;

import dk.drb.blacktiger.repository.asterisk.ConfbridgeListEvent;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration related to Asterisk Communications.
 */
@Configuration
public class AsteriskConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AsteriskConfig.class);
    
    @Autowired
    Environment env;
    
    @Bean
    public AsteriskServer asteriskServer() {
        String host = env.getProperty("asterisk.host");
        String user = env.getProperty("asterisk.username");
        String pass = env.getProperty("asterisk.password");
        LOG.info("Creating server connection [host={};user={}]", host, user);
        DefaultAsteriskServer server = new DefaultAsteriskServer(host, user, pass);
        server.getManagerConnection().registerUserEventClass(ConfbridgeListEvent.class);
        return server;
    }
}
