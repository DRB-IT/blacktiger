package dk.drb.blacktiger.config;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration for JDBC Datasources.
 */
@Configuration
public class DatasourceConfig {

    @Autowired
    private Environment env;

    private BoneCPConfig getDefaulBoneCpConfig() {
        BoneCPConfig config = new BoneCPConfig();
        config.setIdleConnectionTestPeriod(60, TimeUnit.MINUTES);
        config.setIdleMaxAgeInMinutes(240);
        config.setMaxConnectionsPerPartition(30);
        config.setMinConnectionsPerPartition(5);
        config.setPartitionCount(2);
        config.setAcquireIncrement(5);
        config.setStatementsCacheSize(100);
        config.setReleaseHelperThreads(3);
        return config;
    }
    
    @Bean(name = "asteriskDatasource")
    public DataSource asteriskDataSource() {
        BoneCPDataSource source = new BoneCPDataSource(getDefaulBoneCpConfig());
        source.setDriverClass(env.getProperty("asteriskjdbc.driverClassName"));
        source.setJdbcUrl(env.getProperty("asteriskjdbc.url"));
        source.setUsername(env.getProperty("asteriskjdbc.username"));
        source.setPassword(env.getProperty("asteriskjdbc.password"));
        return source;
    }
}
