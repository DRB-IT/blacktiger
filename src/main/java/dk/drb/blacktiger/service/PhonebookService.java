package dk.drb.blacktiger.service;

import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.PhonebookEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

/**
 *
 */
public class PhonebookService {

    private PhonebookRepository repository;

    @Autowired
    public void setRepository(PhonebookRepository repository) {
        this.repository = repository;
    }
    
    
    /**
     * Retrieves an name from the phonebook.
     * @param phoneNumber The number to retrieve name for.
     * @return The name from the phonebook or null if not available.
     */
    @Secured("ROLE_USER")
    public String getPhonebookEntry(String phoneNumber) {
        PhonebookEntry entry = repository.findByCallerId(phoneNumber);
        if(entry != null) {
            return entry.getName();
        } else {
            return null;
        }
    }
    
    /**
     * Sets a name for a given phonenumber in the phonebook.
     * @param phoneNumber The number to change the name for.
     * @param name The new name for the entry.
     */
    @Secured("ROLE_USER")
    public void updatePhonebookEntry(String phoneNumber, String name) {
        PhonebookEntry entry = repository.findByCallerId(phoneNumber);
        if(entry != null) {
            entry = new PhonebookEntry(entry.getNumber(), name);
        } else {
            entry = new PhonebookEntry(phoneNumber, name);
        }
        repository.save(entry);
    }
    
}
