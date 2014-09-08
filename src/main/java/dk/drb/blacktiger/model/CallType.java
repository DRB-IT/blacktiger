package dk.drb.blacktiger.model;

/**
 *
 * @author michael
 */
public enum CallType {
    Sip, Phone, Hall;
    
    public static CallType fromCode(String code) {
        //H=hall, P=phone, C=computer,
        switch(code) {
            case "H":
                return CallType.Hall;
            case "P":
                return CallType.Phone;
            case "C":
                return CallType.Sip;
            default:
                return null;
        }
        
        
    }
}
