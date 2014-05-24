package dk.drb.blacktiger.controller.rest.model;

import dk.drb.blacktiger.model.SipAccount;


public class SipAccountCreateRequest {

    private SipAccount account;
    private String mailText;

    public SipAccount getAccount() {
        return account;
    }

    public void setAccount(SipAccount account) {
        this.account = account;
    }

    public String getMailText() {
        return mailText;
    }

    public void setMailText(String mailText) {
        this.mailText = mailText;
    }
    
    
}
