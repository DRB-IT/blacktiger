/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.model;

import java.util.Date;

/**
 * Javadoc
 */
public class Participant {
    private String userId;
    private boolean muted;
    private boolean host;
    private String phoneNumber;
    private Date dateJoined;
    private String name;

    public Participant(String userId, String name, String phoneNumber, boolean muted, boolean host, Date dateJoined) {
        this.userId = userId;
        this.muted = muted;
        this.host = host;
        this.phoneNumber = phoneNumber;
        this.dateJoined = dateJoined;
        this.name = name;
    }
    
    public Date getDateJoined() {
        return dateJoined;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public boolean isMuted() {
        return this.muted;
    }

    public boolean isHost() {
        return host;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getName() {
        return name;
    }
    
}
