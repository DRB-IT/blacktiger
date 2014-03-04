package dk.drb.blacktiger.controller.rest.model;

/**
 * Exception used when resources are not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
