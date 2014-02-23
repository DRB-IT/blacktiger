package dk.drb.blacktiger.repository.asterisk;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ConferenceJoinEvent;
import dk.drb.blacktiger.model.ConferenceLeaveEvent;
import dk.drb.blacktiger.util.IpPhoneNumber;
import dk.drb.blacktiger.util.PhoneNumber;
import java.util.ArrayList;
import java.util.List;
import org.asteriskjava.live.MeetMeRoom;
import org.asteriskjava.live.MeetMeUser;
import org.asteriskjava.manager.event.ManagerEvent;
import org.asteriskjava.manager.event.MeetMeJoinEvent;
import org.asteriskjava.manager.event.MeetMeLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This implementation of ConferenceRepository uses the MeetMe conference in Asterisk.
 */
public class AsteriskMeetMeRepository extends AbstractAsteriskConferenceRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AsteriskMeetMeRepository.class);

    @Override
    public void onManagerEvent(ManagerEvent event) {
        if (event instanceof MeetMeJoinEvent) {
            String roomNo = ((MeetMeJoinEvent) event).getMeetMe();
            Integer index = ((MeetMeJoinEvent) event).getUserNum();
            AsteriskMeetMeRepository.this.fireEvent(new ConferenceJoinEvent(roomNo, index.toString()));
        }
        if (event instanceof MeetMeLeaveEvent) {
            String roomNo = ((MeetMeLeaveEvent) event).getMeetMe();
            Integer index = ((MeetMeLeaveEvent) event).getUserNum();
            AsteriskMeetMeRepository.this.fireEvent(new ConferenceLeaveEvent(roomNo, index.toString()));
        }
    }

    @Override
    public List<Participant> findByRoomNo(String roomNo) {
        LOG.debug("Listing participants. [room={}]", roomNo);
        MeetMeRoom room = asteriskServer.getMeetMeRoom(roomNo);
        List<Participant> result = new ArrayList<Participant>();

        for (MeetMeUser mmu : room.getUsers()) {
            result.add(participantFromMeetMeUser(mmu));
        }
        return result;
    }

    @Override
    public Participant findByRoomNoAndParticipantId(String roomNo, String participantId) {
        LOG.debug("Retrieving participant. [room={};participant={}]", roomNo, participantId);
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        return mmu == null ? null : participantFromMeetMeUser(mmu);
    }

    @Override
    public void kickParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        if (mmu != null) {
            mmu.kick();
        }
    }

    @Override
    public void muteParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        if (mmu != null) {
            mmu.mute();
        }
    }

    @Override
    public void unmuteParticipant(String roomNo, String participantId) {
        MeetMeUser mmu = getMeetMeUser(roomNo, participantId);
        if (mmu != null) {
            mmu.unmute();
        }
    }

    @Override
    public void removeEventListener(ConferenceEventListener listener) {
        if (listener != null) {
            eventListeners.remove(listener);
        }
    }

    private MeetMeUser getMeetMeUser(String roomNo, String participantId) {
        MeetMeRoom room = asteriskServer.getMeetMeRoom(roomNo);
        Integer id = Integer.parseInt(participantId);
        for (MeetMeUser mmu : room.getUsers()) {
            if (mmu.getUserNumber().equals(id)) {
                return mmu;
            }
        }
        return null;
    }

    private Participant participantFromMeetMeUser(MeetMeUser user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String number = user.getChannel().getCallerId().getNumber();
        boolean host = false;

        if (auth != null && number.equalsIgnoreCase(auth.getName())) {
            host = true;
        }

        String phoneNumber = user.getChannel().getCallerId().getNumber();
        String name = user.getChannel().getCallerId().getName();

        if (IpPhoneNumber.isIpPhoneNumber(phoneNumber)) {
            phoneNumber = IpPhoneNumber.normalize(phoneNumber);
        } else if (PhoneNumber.isPhoneNumber(name, "DK")) {
            phoneNumber = PhoneNumber.normalize(phoneNumber, "DK");
        }

        return new Participant(user.getUserNumber().toString(), name, phoneNumber, user.isMuted(), host, user.getDateJoined());
    }
}
