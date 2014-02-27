package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.User;
import dk.drb.blacktiger.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public class InMemUserRepository implements UserRepository {

    private List<User> users = new ArrayList<User>();

    public InMemUserRepository() {
        users.add(new User("H45-0000", "123"));
        users.add(new User("H45-0001", "123"));
        users.add(new User("H45-0002", "123"));
        users.add(new User("H45-0003", "123"));
        users.add(new User("H45-0004", "123"));
        users.add(new User("H45-0005", "123"));
        users.add(new User("H45-0006", "123"));
        users.add(new User("H45-0007", "123"));
        users.add(new User("H45-0008", "123"));
        users.add(new User("H45-0009", "123"));
        users.add(new User("admin", "123"));
    }
    
    
    @Override
    public User findByUsername(String username) {
        for(User u : users) {
            if(username.equals(u.getUsername())) {
                return u;
            }
        }
        return null;
    }

    @Override
    public <S extends User> S save(S entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <S extends User> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User findOne(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean exists(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<User> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<User> findAll(Iterable<Integer> ids) {
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
    public void delete(User entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Iterable<? extends User> entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
