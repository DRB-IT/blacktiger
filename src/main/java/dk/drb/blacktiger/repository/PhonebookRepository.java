package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.PhonebookEntry;

/**
 *
 */
public interface PhonebookRepository {
    
    PhonebookEntry findByCallerId(String number);
    PhonebookEntry save(PhonebookEntry entry);
}
