package dk.drb.blacktiger.service;

import dk.drb.blacktiger.repository.UserRepository;
import dk.drb.blacktiger.model.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author krog
 */
public class UserService {
    
    private UserRepository repository;
    
    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
    
    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }
    
}
