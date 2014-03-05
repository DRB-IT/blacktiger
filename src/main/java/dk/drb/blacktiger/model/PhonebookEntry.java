package dk.drb.blacktiger.model;

import org.apache.commons.lang.builder.ToStringBuilder;

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
