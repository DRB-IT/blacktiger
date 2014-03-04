package dk.drb.blacktiger.controller.rest.model;

/**
 * REST representation class for exceptions.
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
