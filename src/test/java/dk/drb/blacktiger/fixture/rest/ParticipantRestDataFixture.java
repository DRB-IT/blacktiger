package dk.drb.blacktiger.fixture.rest;

import dk.drb.blacktiger.model.CallType;
import dk.drb.blacktiger.model.Participant;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author michael
 */
public class ParticipantRestDataFixture {
    
    private static Date standardDate() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.parse("2014-1-1");
        } catch (ParseException ex) {
            return null;
        }
    }
    
    public static Participant standardParticipant(String id) {
        return new Participant("1", "John Doe", "+4512345678", true, false, CallType.Sip, standardDate());
    }
    
    public static List<Participant> standardParticipantsList() {
        List<Participant> list = new ArrayList<Participant>();
        list.add(standardParticipant("1"));
        return list;
    }
    
    public static String standardParticipantListAsJson() {
        return "[" + standardParticipantAsJson("1") + "]";
    }
    
    public static String standardParticipantAsJson(String id) {
        return "{\"userId\":\"" + id + "\",\"muted\":true,\"phoneNumber\":\"+4512345678\",\"dateJoined\":1388530800000,\"name\":\"John Doe\",\"type\":\"Sip\",\"host\":false}";
    }
}
