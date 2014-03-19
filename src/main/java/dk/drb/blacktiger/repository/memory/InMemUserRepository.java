package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.User;
import dk.drb.blacktiger.repository.UserRepository;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemUserRepository implements UserRepository {

    private List<User> users = new ArrayList<User>();
    private NumberFormat nf = NumberFormat.getInstance();

    public InMemUserRepository() {
        nf.setMinimumIntegerDigits(4);
        nf.setGroupingUsed(false);
        
        for(int i=0;i<1000;i++) {
            users.add(new User("H45-" + nf.format(i), "123"));
        }
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
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public <S extends User> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public User findOne(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean exists(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<User> findAll() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public Iterable<User> findAll(Iterable<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); 
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
