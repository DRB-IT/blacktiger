package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.PhonebookEntry;
import org.springframework.data.repository.CrudRepository;

/**
 *
 */
public interface PhonebookRepository extends CrudRepository<PhonebookEntry, Integer> {
    
    PhonebookEntry findByNumber(String number) ;
    void deleteByNumber(String number);
}
