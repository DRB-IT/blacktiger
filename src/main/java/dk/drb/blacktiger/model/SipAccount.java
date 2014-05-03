package dk.drb.blacktiger.model;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author michael
 */
public class SipAccount {

    private String name;
    private String email;
    private String phoneNumber;
    private String sipId;
    private String sipPass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSipId() {
        return sipId;
    }

    public void setSipId(String sipId) {
        this.sipId = sipId;
    }

    public String getSipPass() {
        return sipPass;
    }

    public void setSipPass(String sipPass) {
        this.sipPass = sipPass;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
