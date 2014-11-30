package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.model.PhonebookUpdateEvent;

/**
 *
 */
public interface PhonebookRepository {
    public interface PhonebookEventListener {
        void onUpdate(PhonebookUpdateEvent event);
    }
    
    public void addEventListener(PhonebookEventListener eventListener);
    public void removeEventListener(PhonebookEventListener eventListener);
    
    PhonebookEntry findByCallerId(String hallCalling, String number);
    PhonebookEntry save(String hallCalling, PhonebookEntry entry);
}
