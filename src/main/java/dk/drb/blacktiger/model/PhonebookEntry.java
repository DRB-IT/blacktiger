package dk.drb.blacktiger.model;

import org.springframework.data.domain.Persistable;

/**
 *
 */
public class PhonebookEntry {
    
    private String number;
    private String name;
        
    public PhonebookEntry(String number, String name) {
        this.number = number;
        this.name = name;
    }


    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    
    
}
