package dk.drb.blacktiger.model;

import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Defines a Participant in a conference room.
 */
public class Participant {

    private String callerId;
    private String channel;
    private boolean muted;
    private String phoneNumber;
    private Date dateJoined;
    private String name;
    private CallType type;
    private boolean host;

    public Participant() {
    }
    
    public Participant(String channel, String callerId, String name, boolean muted, boolean host, CallType type, Date dateJoined) {
        this(callerId, channel, name, null, muted, host, type, dateJoined);
    }

    public Participant(String channel, String callerId, String name, String phoneNumber, boolean muted, boolean host, CallType type, Date dateJoined) {
        this.callerId = callerId;
        this.channel = channel;
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
    
    public long getMillisSinceJoin() {
        return dateJoined == null ? 0 : System.currentTimeMillis() - dateJoined.getTime();
    }

    /**
     * The id of the caller as retrieved from Asterisk.
     * @return The callerId, fx. SIP id, phonenumber etc..
     */
    public String getCallerId() {
        return this.callerId;
    }

    /**
     * The channel of the caller as retrieved from Asterisk.
     * @return The channel.
     */
    public String getChannel() {
        return channel;
    }
    
    /**
     * Whether the participant is muted.
     * @return True/False
     */
    public boolean isMuted() {
        return this.muted;
    }

    /**
     * The type of call.
     * @return Sip/Phone/Hall/Unknown
     */
    public CallType getType() {
        return type;
    }

    public void setType(CallType type) {
        this.type = type;
    }

    /**
     * Whether the participant is host of the meeting.
     * @return True/False
     */
    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    /**
     * The phonenumber of the participant.
     * This may be decorated compared to what was originally retrieved from Asterisk depending on whether information about the participant was found in the phonebook. 
     * @return The phone number.
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Name of participant as found in the phonebook.
     * @return The name or null.
     */
    public String getName() {
        return name;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
        if(!(obj instanceof Participant)) {
            return false;
        }
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    
}
