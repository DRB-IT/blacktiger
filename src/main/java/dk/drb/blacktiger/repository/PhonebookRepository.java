package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.PhonebookEntry;

/**
 *
 */
public interface PhonebookRepository {
    
    PhonebookEntry findByNumber(String number) ;
    PhonebookEntry save(PhonebookEntry entry);
}
