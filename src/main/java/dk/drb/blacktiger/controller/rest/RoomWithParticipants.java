package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.model.Contact;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.Room;
import java.util.List;

public class RoomWithParticipants {

    private Room wrapped;
    private List<Participant> participants;

    public RoomWithParticipants(Room wrapped, List<Participant> participants) {
        this.wrapped = wrapped;
        this.participants = participants;
    }

    public String getCity() {
        return wrapped.getCity();
    }

    public Contact getContact() {
        return wrapped.getContact();
    }

    public String getCountryCallingCode() {
        return wrapped.getCountryCallingCode();
    }

    public String getHallNumber() {
        return wrapped.getHallNumber();
    }

    public String getId() {
        return wrapped.getId();
    }

    public String getName() {
        return wrapped.getName();
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getPhoneNumber() {
        return wrapped.getPhoneNumber();
    }

    public String getPostalCode() {
        return wrapped.getPostalCode();
    }

}
