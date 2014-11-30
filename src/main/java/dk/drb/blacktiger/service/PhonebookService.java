package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.PhonebookEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

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
        String hall = SecurityContextHolder.getContext().getAuthentication().getName();
        
        PhonebookEntry entry = repository.findByCallerId(hall, phoneNumber);
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
        String hall = SecurityContextHolder.getContext().getAuthentication().getName();
        
        PhonebookEntry entry = repository.findByCallerId(hall, phoneNumber);
        if(entry != null) {
            entry = new PhonebookEntry(entry.getNumber(), name, entry.getCallType());
        } else {
            entry = new PhonebookEntry(phoneNumber, name, CallType.Sip);
        }
        repository.save(hall, entry);

    }
    
}
