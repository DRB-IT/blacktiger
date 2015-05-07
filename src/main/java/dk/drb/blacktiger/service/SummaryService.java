package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.ConferenceEndEvent;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.ConferenceStartEvent;
import dk.drb.blacktiger.model.ParticipantEvent;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.ParticipantMuteEvent;
import dk.drb.blacktiger.model.ParticipantUnmuteEvent;
import dk.drb.blacktiger.model.Summary;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

public class SummaryService {

    public static final String GLOBAL_IDENTIFIER = "all";

    private static final Logger LOG = LoggerFactory.getLogger(SummaryService.class);
    private static final Pattern roomPattern = Pattern.compile("([a-zA-Z0-9]+)-.*");
    private final Set<String> openMicsChannelSet = new HashSet<>();
    private final Map<String, Summary> summaryMap = new HashMap<>();
    private Summary globalSummary;

    private ConferenceService conferenceService;

    @Autowired
    public void setConferenceService(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostConstruct
    protected void init() {
        globalSummary = new Summary();
        summaryMap.put(GLOBAL_IDENTIFIER, globalSummary);
        conferenceService.addEventListener(new ConferenceEventListener() {

            @Override
            public void onParticipantEvent(ConferenceEvent event) {
                LOG.debug("Conference event recieved for summary. [class={}]", event.getClass());
                if (event instanceof ConferenceStartEvent) {
                    adjustHalls(event.getRoomNo(), 1);
                    LOG.debug("Conference added to summary.");
                }

                if (event instanceof ConferenceEndEvent) {
                    adjustHalls(event.getRoomNo(), -1);
                    LOG.debug("Conference removed from summary.");
                }

                if (event instanceof ParticipantMuteEvent) {
                    openMicsChannelSet.remove(((ParticipantMuteEvent)event).getChannel());
                    adjustOpenMicrophones(event.getRoomNo(), -1);
                    LOG.debug("Open microphone removed from summary.");
                }

                if (event instanceof ParticipantUnmuteEvent) {
                    openMicsChannelSet.add(((ParticipantUnmuteEvent)event).getChannel());
                    adjustOpenMicrophones(event.getRoomNo(), 1);
                    LOG.debug("Open microphone added to summary.");
                }

                if (event instanceof ParticipantEvent) {
                    ParticipantEvent pEvent = (ParticipantEvent) event;

                    if (event instanceof ParticipantJoinEvent) {
                        adjustParticipants(pEvent.getRoomNo(), 1, pEvent.getParticipant().getType());
                        LOG.debug("Participant added to summary.");
                    }

                    if (event instanceof ParticipantLeaveEvent) {
                        if(openMicsChannelSet.contains(((ParticipantLeaveEvent)event).getParticipant().getChannel())) {
                            adjustOpenMicrophones(event.getRoomNo(), -1);
                        }
                        adjustParticipants(pEvent.getRoomNo(), -1, pEvent.getParticipant().getType());
                        LOG.debug("Participant removed from summary.");
                    }

                }
                clean(event.getRoomNo());
            }
        });
    }

    private String identifierFromRoom(String roomNo) {
        Matcher matcher = roomPattern.matcher(roomNo);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            LOG.warn("Roomno does not match pattern of room numbers and identifier cannot be retrieved.");
            return null;
        }
    }

    private Summary summaryFromRoom(String room) {
        Summary regionalSummary = null;

        String identifier = identifierFromRoom(room);
        if (identifier != null) {
            regionalSummary = summaryMap.get(identifier);

            if (regionalSummary == null) {
                regionalSummary = new Summary();
                summaryMap.put(identifier, regionalSummary);
            }
        }
        return regionalSummary;
    }

    private void clean(String room) {
        Summary regionalSummary = summaryFromRoom(room);
        if (regionalSummary != null && regionalSummary.getHalls() == 0 && regionalSummary.getParticipants() == 0) {
            summaryMap.remove(identifierFromRoom(room));
        }
    }

    private void adjustHalls(String room, int value) {
        globalSummary.adjustHalls(value);
        Summary regionalSummary = summaryFromRoom(room);
        if (regionalSummary != null) {
            regionalSummary.adjustHalls(value);
        }
    }

    private void adjustParticipants(String room, int value, CallType type) {
        if (type == CallType.Hall || type == CallType.Unknown) {
            return;
        }

        globalSummary.adjustParticipants(value);

        switch (type) {
            case Phone:
                globalSummary.adjustParticipantsViaPhone(value);
                break;
            case Sip:
                globalSummary.adjustParticipantsViaSip(value);
                break;
        }

        Summary regionalSummary = summaryFromRoom(room);
        if (regionalSummary != null) {
            regionalSummary.adjustParticipants(value);

            switch (type) {
                case Phone:
                    regionalSummary.adjustParticipantsViaPhone(value);
                    break;
                case Sip:
                    regionalSummary.adjustParticipantsViaSip(value);
                    break;
            }
        }
    }

    private void adjustOpenMicrophones(String room, int value) {
        globalSummary.adjustOpenMicrophones(value);
        Summary regionalSummary = summaryFromRoom(room);
        if (regionalSummary != null) {
            regionalSummary.adjustOpenMicrophones(value);
        }
    }

    @Secured("ROLE_ADMIN")
    public Map<String, Summary> getSummary() {
        return Collections.unmodifiableMap(summaryMap);
    }
    
    public Summary getGlobalSummary() {
        return summaryMap.get(GLOBAL_IDENTIFIER);
    }

}
