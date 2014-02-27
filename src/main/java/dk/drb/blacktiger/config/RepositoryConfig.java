package dk.drb.blacktiger.config;

import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.repository.ParticipantRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.repository.UserRepository;
import dk.drb.blacktiger.repository.asterisk.AsteriskMeetMeRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcCallInformationRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcPhonebookRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcUserRepository;
import dk.drb.blacktiger.repository.memory.InMemCallInformationRepository;
import dk.drb.blacktiger.repository.memory.InMemConferenceRoomRepository;
import dk.drb.blacktiger.repository.memory.InMemParticipantRepository;
import dk.drb.blacktiger.repository.memory.InMemPhonebookRepository;
import dk.drb.blacktiger.repository.memory.InMemUserRepository;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.asteriskjava.live.AsteriskServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({DatasourceConfig.class, AsteriskConfig.class})
@EnableScheduling
public class RepositoryConfig {
    
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryConfig.class);
    
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
            LOG.info("** RUNNING IN TEST MODE **");
            test = true;
        }
    }
    
    @Bean
    public CallInformationRepository callInformationRepository() {
        if(test) {
            LOG.info("** USING InMemCallInformationRepository FOR TEST **");
            return new InMemCallInformationRepository();
        } else {
            JdbcCallInformationRepository repository = new JdbcCallInformationRepository();
            repository.setDataSource(callInfoDataSource);
            return repository;
        }
    }

    @Bean
    public ParticipantRepository conferenceRepository() {
        if(test) {
            LOG.info("** USING InMemConferenceRepository FOR TEST **");
            return new InMemParticipantRepository();
        } else {
            AsteriskMeetMeRepository repository = new AsteriskMeetMeRepository();
            repository.setAsteriskServer(asteriskServer);
            return repository;
        }
    }

    @Bean
    public PhonebookRepository phonebookRepository() {
        if(test) {
            LOG.info("** USING InMemPhonebookRepository FOR TEST **");
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
            LOG.info("** USING InMemUserRepository FOR TEST **");
            return new InMemUserRepository();
        } else {
            JdbcUserRepository repository = new JdbcUserRepository();
            repository.setDataSource(asteriskDataSource);
            return repository;
        }
    }
    
    @Bean ConferenceRoomRepository conferenceRoomRepository() {
        if(test) {
            LOG.info("** USING InMemConferenceRoomRepository FOR TEST **");
            return new InMemConferenceRoomRepository();
        } else {
            return null;
        }
    }
}
