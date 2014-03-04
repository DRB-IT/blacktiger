package dk.drb.blacktiger.config;

import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration related to Asterisk Communications.
 */
@Configuration
public class AsteriskConfig {

    @Autowired
    Environment env;
    
    @Bean
    public AsteriskServer asteriskServer() {
        String host = env.getProperty("asterisk.host");
        String user = env.getProperty("asterisk.username");
        String pass = env.getProperty("asterisk.password");
        return new DefaultAsteriskServer(host, user, pass);
    }
}
