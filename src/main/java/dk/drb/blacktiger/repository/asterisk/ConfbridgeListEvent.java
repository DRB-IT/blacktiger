package dk.drb.blacktiger.repository.asterisk;


public class ConfbridgeListEvent extends org.asteriskjava.manager.event.ConfbridgeListEvent {

    private boolean muted;
    
    public ConfbridgeListEvent(Object source) {
        super(source);
    }
    
    public void setMuted(String value) {
        this.muted = "Yes".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value) || "True".equalsIgnoreCase(value);
    }
    
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

}
