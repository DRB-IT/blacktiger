package dk.drb.blacktiger.controller.rest;

/**
 *
 * @author michael
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
