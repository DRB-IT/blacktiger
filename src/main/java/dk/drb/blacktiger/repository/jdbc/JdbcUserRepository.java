package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.repository.UserRepository;
import dk.drb.blacktiger.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 *
 * @author krog
 */
public class JdbcUserRepository implements UserRepository {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcUserRepository.class);
    private static final String TABLE_NAME = "sip";
    private JdbcTemplate jdbcTemplate;
    private UserMapper mapper = new UserMapper();
    
    private class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getInt("id"), rs.getString("id"), rs.getString("data"));
        }
        
    }

    
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    @Override
    public User findOne(Integer id) {
        try {
            return this.jdbcTemplate.queryForObject("select * from " + TABLE_NAME + " where id=? and keyword = 'secret'", new Object[]{id}, mapper);
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try {
            int id = Integer.parseInt(username);
            return findOne(id);
        } catch(NumberFormatException ex) {
            LOG.info("Unable to find user by a name which cannot be parsed to number.", ex);
            return null;
        }
        
    }
    
            
    
    @Override
    public <S extends User> S save(S entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <S extends User> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean exists(Integer id) {
        return findOne(id) != null;
    }

    @Override
    public Iterable<User> findAll() {
        return this.jdbcTemplate.query("select * from " + TABLE_NAME, mapper);
    }

    @Override
    public Iterable<User> findAll(Iterable<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public long count() {
        return this.jdbcTemplate.queryForLong("select count(*) from " + TABLE_NAME);
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(User entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Iterable<? extends User> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
