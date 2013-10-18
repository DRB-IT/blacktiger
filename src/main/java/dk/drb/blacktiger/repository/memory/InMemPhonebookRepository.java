package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.repository.PhonebookRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemPhonebookRepository implements PhonebookRepository {

    private Map<String, PhonebookEntry> phonebook = new HashMap<String, PhonebookEntry>();
    
    @Override
    public PhonebookEntry findByNumber(String number) {
        return phonebook.get(number);
    }

    @Override
    public void deleteByNumber(String number) {
        phonebook.put(number, null);
    }

    @Override
    public <S extends PhonebookEntry> S save(S entity) {
        phonebook.put(entity.getNumber(), entity);
        return entity;
    }

    @Override
    public <S extends PhonebookEntry> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PhonebookEntry findOne(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean exists(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<PhonebookEntry> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<PhonebookEntry> findAll(Iterable<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(PhonebookEntry entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Iterable<? extends PhonebookEntry> entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
