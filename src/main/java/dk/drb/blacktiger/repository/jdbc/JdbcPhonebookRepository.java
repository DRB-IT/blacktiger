package dk.drb.blacktiger.repository.jdbc;

import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.PhonebookEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Repository for handling Phonebook data stored in Asterisk database.
 * Data is access via the given JdbcTemplate.
 */
public class JdbcPhonebookRepository implements PhonebookRepository {

    private static final String TABLE_NAME = "ConfNames";
    private static final String KEY_COLUMN = "id";
    private JdbcTemplate jdbcTemplate;
    private PhonebookEntryMapper mapper = new PhonebookEntryMapper();
    private SimpleJdbcInsert jdbcInsert;
    
    private class PhonebookEntryMapper implements RowMapper<PhonebookEntry> {

        @Override
        public PhonebookEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PhonebookEntry(rs.getInt("id"), rs.getString("phonenumber"), rs.getString("name"));
        }
        
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(TABLE_NAME).usingGeneratedKeyColumns(KEY_COLUMN);
    }
    
    @Override
    public PhonebookEntry findOne(Integer id) {
        try {
            return this.jdbcTemplate.queryForObject("select * from " + TABLE_NAME + " where id=?", new Object[]{id}, mapper);
        
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }
    }
    
    @Override
    public PhonebookEntry findByNumber(String number) {
        try {
            return this.jdbcTemplate.queryForObject("select * from " + TABLE_NAME + " where phonenumber=?", new Object[]{number}, mapper);
        
        } catch(EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public <S extends PhonebookEntry> S save(S entity) {
        Assert.hasLength(entity.getName(), "Name must not be an empty string or null.");
        Assert.hasLength(entity.getNumber(), "Number must not be an empty string or null.");
        
        String sql;
        Integer id;
        
        if(entity.isNew()) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("name", entity.getName());
            data.put("phonenumber", entity.getNumber());
            id = jdbcInsert.execute(data);

        } else {
            id = entity.getId();
            sql = "update " + TABLE_NAME + " set name=?, phonenumber=? where id = ?";
            this.jdbcTemplate.update(sql, new Object[]{entity.getName(), entity.getNumber(), id});
        }
        
        return (S) findOne(id);
    }

    @Override
    public <S extends PhonebookEntry> Iterable<S> save(Iterable<S> entities) {
        List<PhonebookEntry> savedEntries = new ArrayList<PhonebookEntry>();
        for(PhonebookEntry entry : entities) {
            savedEntries.add(save(entry));
        }
        return (Iterable<S>) savedEntries;
    }

    @Override
    public boolean exists(Integer id) {
        return findOne(id) != null;
    }

    @Override
    public Iterable<PhonebookEntry> findAll() {
        return this.jdbcTemplate.query("select * from " + TABLE_NAME, mapper);
    }

    @Override
    public Iterable<PhonebookEntry> findAll(Iterable<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long count() {
        return this.jdbcTemplate.queryForLong("select count(*) from " + TABLE_NAME);
    }

    @Override
    public void delete(Integer id) {
        this.jdbcTemplate.update("delete from " + TABLE_NAME + " where id=?", id);
    }

    @Override
    public void delete(PhonebookEntry entity) {
        delete(entity.getId());
    }

    @Override
    public void delete(Iterable<? extends PhonebookEntry> entities) {
        for(PhonebookEntry entry : entities) {
            delete(entry);
        }
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from " + TABLE_NAME);
    }

    @Override
    public void deleteByNumber(String number) {
        this.jdbcTemplate.update("delete from " + TABLE_NAME + " where phonenumber=?", number);
    }
    
    
    
    
}
