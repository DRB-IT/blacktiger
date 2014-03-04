package dk.drb.blacktiger.config;

import dk.drb.blacktiger.service.CallInformationService;
import dk.drb.blacktiger.service.ConferenceService;
import dk.drb.blacktiger.service.PhonebookService;
import dk.drb.blacktiger.service.SipAccountService;
import dk.drb.blacktiger.service.SystemService;
import dk.drb.blacktiger.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for Service classes.
 */
@Configuration
@Import({RepositoryConfig.class})
public class ServiceConfig {
    
    @Bean
    public CallInformationService callInformationService() {
        return new CallInformationService();
    }

    @Bean
    public ConferenceService conferenceService() {
        return new ConferenceService();
    }

    @Bean
    public PhonebookService phonebookService() {
        return new PhonebookService();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }
    
    @Bean SipAccountService sipAccountService() {
        return new SipAccountService();
    }
    
    @Bean SystemService systemService() {
        return new SystemService();
    }
    
}
