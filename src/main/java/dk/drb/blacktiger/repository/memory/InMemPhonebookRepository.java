package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.repository.PhonebookRepository;
import java.util.HashMap;
import java.util.Map;

public class InMemPhonebookRepository implements PhonebookRepository {

    private Map<String, PhonebookEntry> phonebook = new HashMap<>();
    
    @Override
    public PhonebookEntry findByCallerId(String number) {
        return phonebook.get(number);
    }

    @Override
    public PhonebookEntry save(PhonebookEntry entity) {
        phonebook.put(entity.getNumber(), entity);
        return entity;
    }
    
}
