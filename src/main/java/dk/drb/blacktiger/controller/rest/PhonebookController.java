package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.controller.rest.model.ResourceNotFoundException;
import dk.drb.blacktiger.service.PhonebookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for administering Phonebook entries.
 */
@Controller
public class PhonebookController {

    private static final Logger LOG = LoggerFactory.getLogger(PhonebookController.class);
    
    @Autowired
    private PhonebookService service;
    
    /**
     * Updates a phonebook entry taking the requestbody as new name.
     * @return JSON Object with the value '1'.
     */
    @RequestMapping(value = "/phonebook/{number}", method = RequestMethod.PUT)
    @ResponseBody
    public void update(@PathVariable final String number, @RequestBody String name) {
        LOG.info("Updating phonebook entry. [number={}; name={}]", number, name);
        service.updatePhonebookEntry(number, name);
    }
    
    
    /**
     * 
     * @param number Retrieves a number from the phone book.
     * @return JSON Object with the number.
     */
    @RequestMapping(value = "/phonebook/{number}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public String get(@PathVariable final String number) {
        LOG.debug("Retrieving phonebook entry. [number={}]", number);
        String value = service.getPhonebookEntry(number);
        if(value == null) {
            throw new ResourceNotFoundException("Phonenumber(" + number + ") not in phonebook.");
        }
        return value;
    }
}
