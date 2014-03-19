package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author krog
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    
    User findByUsername(String username);
}
