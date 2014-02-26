package dk.drb.blacktiger.controller.rest;

/**
 *
 * @author michael
 */
public class RestError {
    
    private String message;

    public RestError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    
}
