package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.PhonebookEntry;

/**
 *
 */
public interface PhonebookRepository {
    
    PhonebookEntry findByCallerId(String hallCalling, String number);
    PhonebookEntry save(String hallCalling, PhonebookEntry entry);
}
