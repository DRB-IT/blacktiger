package dk.drb.blacktiger.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author michael
 */
@ControllerAdvice
public class RestExceptionHandler {
    
    @ResponseStatus(HttpStatus.FORBIDDEN) 
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public RestError handleSecurityException(AccessDeniedException ex) {
        return new RestError(ex.getMessage());
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND) 
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public RestError handleSecurityException(ResourceNotFoundException ex) {
        return new RestError(ex.getMessage());
    }
}
