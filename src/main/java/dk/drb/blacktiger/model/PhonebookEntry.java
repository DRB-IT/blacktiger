package dk.drb.blacktiger.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 */
public class PhonebookEntry {

    private String number;
    private String name;
    private CallType callType;

    public PhonebookEntry(String number, String name, CallType callType) {
        this.number = number;
        this.name = name;
        this.callType = callType;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public CallType getCallType() {
        return callType;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
