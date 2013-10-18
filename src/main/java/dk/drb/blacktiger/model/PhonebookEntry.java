package dk.drb.blacktiger.model;

import org.springframework.data.domain.Persistable;

/**
 *
 */
public class PhonebookEntry implements Persistable<Integer>{
    
    private Integer id;
    private String number;
    private String name;

    public PhonebookEntry() {
    }

    public PhonebookEntry(String number, String name) {
        this.number = number;
        this.name = name;
    }
        
    public PhonebookEntry(Integer id, String number, String name) {
        this.id = id;
        this.number = number;
        this.name = name;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
    
    
}
