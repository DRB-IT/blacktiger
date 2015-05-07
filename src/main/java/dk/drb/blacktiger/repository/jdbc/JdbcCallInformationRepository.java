package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.model.CallInformation;
import dk.drb.blacktiger.util.IpPhoneNumber;
import dk.drb.blacktiger.util.PhoneNumber;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.util.Assert;

/**
 *
 */
public class JdbcCallInformationRepository implements CallInformationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcCallInformationRepository.class);
    private JdbcTemplate jdbcTemplate;
    private CallInformationMapper mapper = new CallInformationMapper();
    private String encryptionKey;

    private class CallInformationMapper implements RowMapper<CallInformation> {

        @Override
        public CallInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
            String phoneNumber = rs.getString("phoneNumber");
            if(IpPhoneNumber.isIpPhoneNumber(phoneNumber)) {
                phoneNumber = IpPhoneNumber.normalize(phoneNumber);
            } else if(PhoneNumber.isPhoneNumber(phoneNumber, "DK")) {
                phoneNumber = PhoneNumber.normalize(phoneNumber, "DK");
            }
            return new CallInformation(phoneNumber, null, rs.getInt("numberOfCalls"),
                    rs.getInt("totalDuration"), rs.getTimestamp("firstCallTimestamp"));
        }
    }
    
    private class ActionLogSP extends StoredProcedure {
        
        public ActionLogSP(JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, "WRITE_CALL_LOG");
            declareParameter(new SqlParameter("caller", Types.VARCHAR));
            declareParameter(new SqlParameter("callee", Types.VARCHAR));
            declareParameter(new SqlParameter("action", Types.VARCHAR));
            declareParameter(new SqlParameter("key", Types.VARCHAR));
            compile();
        }

        public void execute(String caller, String callee, String action) {
            LOG.debug("Executing ActionLogSP [caller={};callee={};action={}]", new Object[]{caller, callee, action});
            Map<String, Object> params = new HashMap<>();
            params.put("caller", caller);
            params.put("callee", callee);
            params.put("action", action);
            params.put("key", encryptionKey);
            execute(params);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    @PostConstruct
    protected void init() {
        Assert.notNull(encryptionKey, "Encryption key not set.");
    }

    @Override
    public List<CallInformation> findByRoomNoAndPeriodAndDuration(String roomNo, Date start, Date end, int minimumDuration) {
        String roomSearch = "%" + roomNo;
        String sql = "SELECT count(*) as numberOfCalls,sum(billsec) as totalDuration, min(calldate) as firstCallTimeStamp,src as phoneNumber "
                + "FROM cdr where calldate > ? and calldate < ? and dcontext LIKE ? group by src having totalDuration > ? ORDER BY firstCallTimeStamp";
        return this.jdbcTemplate.query(sql, new Object[]{start, end, roomSearch, minimumDuration}, mapper);
    }

    @Override
    public List<CallInformation> findByRoomNoAndPeriodAndDurationAndNumbers(String roomNo, Date start, Date end, int minimumDuration, String[] numbers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logAction(String caller, String callee, String action) {
        ActionLogSP sp = new ActionLogSP(jdbcTemplate);
        try {
            sp.execute(caller, callee, action);
        } catch(Exception ex) {
            LOG.error("Error while logging action.", ex);
        }
    }
    
    
    
    
    
}
