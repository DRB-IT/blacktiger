package dk.drb.blacktiger.config;

import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.repository.ContactRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.repository.RoomInfoRepository;
import dk.drb.blacktiger.repository.SipAccountRepository;
import dk.drb.blacktiger.repository.asterisk.Asterisk11ConfbridgeRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcPhonebookRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcCallInformationRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcContactRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcRoomInfoRepository;
import dk.drb.blacktiger.repository.jdbc.JdbcSipAccountRepository;
import dk.drb.blacktiger.repository.memory.InMemCallInformationRepository;
import dk.drb.blacktiger.repository.memory.InMemConferenceRoomRepository;
import dk.drb.blacktiger.repository.memory.InMemPhonebookRepository;
import dk.drb.blacktiger.repository.memory.InMemSipAccountRepository;
import dk.drb.blacktiger.repository.memory.InMemoryContactRepository;
import dk.drb.blacktiger.repository.memory.InMemoryRoomInfoRepository;
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

/**
 * Configuration for Repository classes.
 */
@Configuration
@Import({DatasourceConfig.class, AsteriskConfig.class})
@EnableScheduling
public class RepositoryConfig {
    
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryConfig.class);
    
    @Autowired
    Environment env;
    
    @Autowired
    @Qualifier(value = "asteriskDatasource")
    private DataSource asteriskDataSource;
    
    @Autowired
    private AsteriskServer asteriskServer;
    
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
            String encryptionKey = env.getProperty("encryptionKey");
            JdbcCallInformationRepository repository = new JdbcCallInformationRepository();
            repository.setDataSource(asteriskDataSource);
            repository.setEncryptionKey(encryptionKey);
            return repository;
        }
    }

    @Bean
    public PhonebookRepository phonebookRepository() {
        if(test) {
            LOG.info("** USING InMemPhonebookRepository FOR TEST **");
            return new InMemPhonebookRepository();
        } else {
            String encryptionKey = env.getProperty("encryptionKey");
            JdbcPhonebookRepository repo = new JdbcPhonebookRepository();
            repo.setDataSource(asteriskDataSource);
            repo.setEncryptionKey(encryptionKey);
            LOG.info("Creating JdbcPhonebookRepository instance [datasource={};encryptionKey={}]", asteriskDataSource != null, encryptionKey != null);
            return repo;
        }
    }
    
    @Bean 
    public ConferenceRoomRepository conferenceRoomRepository() {
        if(test) {
            LOG.info("** USING InMemConferenceRoomRepository FOR TEST **");
            return new InMemConferenceRoomRepository();
        } else {
            Asterisk11ConfbridgeRepository repo = new Asterisk11ConfbridgeRepository();
            asteriskServer.initialize();
            repo.setAsteriskServer(asteriskServer);
            return repo;
        }
    }
    
    @Bean
    public SipAccountRepository sipAccountRepository() {
        if(test) {
            LOG.info("** USING InMemSipAccountRepository FOR TEST **");
            return new InMemSipAccountRepository();
        } else {
            String encryptionKey = env.getProperty("encryptionKey");
            JdbcSipAccountRepository repo = new JdbcSipAccountRepository();
            repo.setDataSource(asteriskDataSource);
            repo.setEncryptionKey(encryptionKey);
            LOG.info("Creating JdbcSipAccountRepository instance [datasource={};encryptionKey={}]", asteriskDataSource != null, encryptionKey != null);
            return repo;
        }
    }
    
    @Bean
    public ContactRepository contactRepository() {
        if(test) {
            LOG.info("** Using ContactRepository FOR TEST **");
            return new InMemoryContactRepository();
        } else {
            String encryptionKey = env.getProperty("encryptionKey");
            JdbcContactRepository repo = new JdbcContactRepository();
            repo.setDataSource(asteriskDataSource);
            repo.setEncryptionKey(encryptionKey);
            LOG.info("Creating JdbcContactRepository instance [datasource={};encryptionKey={}]", asteriskDataSource != null, encryptionKey != null);
            return repo;
        }
    }
    
    @Bean
    public RoomInfoRepository roomInfoRepository() {
        if(test) {
            LOG.info("** Using RoomInfoRepository FOR TEST **");
            return new InMemoryRoomInfoRepository();
        } else {
            String encryptionKey = env.getProperty("encryptionKey");
            JdbcRoomInfoRepository repo = new JdbcRoomInfoRepository();
            repo.setDataSource(asteriskDataSource);
            repo.setEncryptionKey(encryptionKey);
            LOG.info("Creating JdbcRoomInfoRepository instance [datasource={};encryptionKey={}]", asteriskDataSource != null, encryptionKey != null);
            return repo;
        }
    }
}
