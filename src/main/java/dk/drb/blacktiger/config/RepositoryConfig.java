package dk.drb.blacktiger.config;

import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.repository.ConferenceRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.repository.UserRepository;
import dk.drb.blacktiger.repository.asterisk.AsteriskMeetMeRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcCallInformationRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcPhonebookRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcUserRepository;
import dk.drb.blacktiger.repository.memory.InMemCallInformationRepository;
import dk.drb.blacktiger.repository.memory.InMemConferenceRepository;
import dk.drb.blacktiger.repository.memory.InMemPhonebookRepository;
import dk.drb.blacktiger.repository.memory.InMemUserRepository;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.asteriskjava.live.AsteriskServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import({DatasourceConfig.class, AsteriskConfig.class})
public class RepositoryConfig {
    
    @Autowired
    Environment env;
    
    @Autowired
    private AsteriskServer asteriskServer;
    
    @Autowired
    @Qualifier(value = "callinfoDatasource")
    private DataSource callInfoDataSource;
    
    @Autowired
    @Qualifier(value = "asteriskDatasource")
    private DataSource asteriskDataSource;
    
    private boolean test;

    @PostConstruct
    public void init() {
        if ("true".equalsIgnoreCase(env.getProperty("test"))) {
            test = true;
        }
    }
    
    @Bean
    public CallInformationRepository callInformationRepository() {
        if(test) {
            return new InMemCallInformationRepository();
        } else {
            JdbcCallInformationRepository repository = new JdbcCallInformationRepository();
            repository.setDataSource(callInfoDataSource);
            return repository;
        }
    }

    @Bean
    public ConferenceRepository conferenceRepository() {
        if(test) {
            return new InMemConferenceRepository();
        } else {
            AsteriskMeetMeRepository repository = new AsteriskMeetMeRepository();
            repository.setAsteriskServer(asteriskServer);
            return repository;
        }
    }

    @Bean
    public PhonebookRepository phonebookRepository() {
        if(test) {
            return new InMemPhonebookRepository();
        } else {
            JdbcPhonebookRepository repository = new JdbcPhonebookRepository();
            repository.setDataSource(callInfoDataSource);
            return repository;
        }
    }

    @Bean
    public UserRepository userRepository() {
        if(test) {
            return new InMemUserRepository();
        } else {
            JdbcUserRepository repository = new JdbcUserRepository();
            repository.setDataSource(asteriskDataSource);
            return repository;
        }
    }
}
