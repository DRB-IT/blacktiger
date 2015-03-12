package dk.drb.blacktiger.model;


public class Summary {

    private int halls;
    private int participants;
    private int participantsViaPhone;
    private int participantsViaSip;
    private int openMicrophones;

    public int getHalls() {
        return halls;
    }
    
    public void adjustHalls(int value) {
        halls = Math.max(0, halls + value);
    }

    public int getParticipants() {
        return participants;
    }
    
    public void adjustParticipants(int value) {
        participants = Math.max(0, participants + value);
    }

    public int getParticipantsViaPhone() {
        return participantsViaPhone;
    }

    public void adjustParticipantsViaPhone(int value) {
        participantsViaPhone = Math.max(0, participantsViaPhone + value);
    }

    public int getParticipantsViaSip() {
        return participantsViaSip;
    }

    public void adjustParticipantsViaSip(int value) {
        participantsViaSip = Math.max(0, participantsViaSip + value);
    }

    public int getOpenMicrophones() {
        return openMicrophones;
    }
    
    public void adjustOpenMicrophones(int value) {
        openMicrophones = Math.max(0, openMicrophones + value);
    }
    
}
