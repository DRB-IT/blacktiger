package dk.drb.blacktiger.model;

import java.util.Date;

/**
 * Defines information about calls for a specific number for use in reports.
 */
public class CallInformation {

    private String phoneNumber;
    private String name;
    private int numberOfCalls;
    private int totalDuration;
    private Date firstCallTimestamp;

    public CallInformation(String phoneNumber, String name, int numberOfCalls, int totalDuration, Date firstCallTimestamp) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.numberOfCalls = numberOfCalls;
        this.totalDuration = totalDuration;
        this.firstCallTimestamp = firstCallTimestamp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public Date getFirstCallTimestamp() {
        return firstCallTimestamp;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfCalls(int numberOfCalls) {
        this.numberOfCalls = numberOfCalls;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setFirstCallTimestamp(Date firstCallTimestamp) {
        this.firstCallTimestamp = firstCallTimestamp;
    }
    
    

}
