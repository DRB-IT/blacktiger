package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ConferenceEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.event.ManagerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author michael
 */
public abstract class AbstractAsteriskConferenceRepository implements ManagerEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAsteriskConferenceRepository.class);
    protected List<ConferenceEventListener> eventListeners = new ArrayList<>();
    protected Timer eventTimer = new Timer();
    protected AsteriskServer asteriskServer;
    private ManagerEventListener managerEventListener;

    protected void setManagerEventListener(ManagerEventListener managerEventListener) {
        this.managerEventListener = managerEventListener;
    }
    
    @Override
    public abstract void onManagerEvent(ManagerEvent event);
    
    protected void fireEvent(final ConferenceEvent event) {
        LOG.debug("Firering conferenceevent to {} listeners. [event={}]", eventListeners.size(), event);
        for (ConferenceEventListener listener : eventListeners) {
            listener.onParticipantEvent(event);
        }
        
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
