package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ConferenceEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.event.ManagerEvent;

/**
 *
 * @author michael
 */
public abstract class AbstractAsteriskConferenceRepository implements ManagerEventListener {
    protected List<ConferenceEventListener> eventListeners = new ArrayList<ConferenceEventListener>();
    protected Timer eventTimer = new Timer();
    protected AsteriskServer asteriskServer;
    private static final int DEFAULT_EVENT_IDLE_TIME = 500;
    private ManagerEventListener managerEventListener;

    protected void setManagerEventListener(ManagerEventListener managerEventListener) {
        this.managerEventListener = managerEventListener;
    }
    
    public abstract void onManagerEvent(ManagerEvent event);
    
    protected void fireEvent(final ConferenceEvent event) {
        // These events are actually fired some time before they may actually be fullfilled as the asterisk server. 
        // Wait a little before sending them along
        eventTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                for (ConferenceEventListener listener : eventListeners) {
                    listener.onParticipantEvent(event);
                }
            }
        }, DEFAULT_EVENT_IDLE_TIME);
        
    }
    
    
    public void addEventListener(ConferenceEventListener listener) {
        if (listener != null) {
            eventListeners.add(listener);
        }
    }

    public void setAsteriskServer(AsteriskServer asteriskServer) {
        if (this.asteriskServer != null) {
            this.asteriskServer.getManagerConnection().removeEventListener(managerEventListener);
        }
        this.asteriskServer = asteriskServer;
        this.asteriskServer.getManagerConnection().addEventListener(managerEventListener);
    }
    
}
