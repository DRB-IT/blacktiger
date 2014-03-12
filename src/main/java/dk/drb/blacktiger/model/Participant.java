package dk.drb.blacktiger.model;

import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Defines a Participant in a conference room.
 */
public class Participant {

    private String userId;
    private boolean muted;
    private String phoneNumber;
    private Date dateJoined;
    private String name;
    private CallType type;
    private boolean host;

    public Participant() {
    }

    public Participant(String userId, String name, String phoneNumber, boolean muted, boolean host, CallType type, Date dateJoined) {
        this.userId = userId;
        this.muted = muted;
        this.type = type;
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

    public CallType getType() {
        return type;
    }

    public void setType(CallType type) {
        this.type = type;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"dateJoined"});
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Participant other = (Participant) obj;
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (this.muted != other.muted) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.host != other.host) {
            return false;
        }
        return true;
    }
    
    
}
