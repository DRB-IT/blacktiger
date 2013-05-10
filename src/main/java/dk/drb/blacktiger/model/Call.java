/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.model;

import java.util.Date;

/**
 * Javadoc
 */
public class Call {

    private String phoneNumber;
    private String name;
    private int numberOfCalls;
    private int totalDuration;
    private Date firstCallTimestamp;

    public Call(String phoneNumber, String name, int numberOfCalls, int totalDuration, Date firstCallTimestamp) {
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

}
