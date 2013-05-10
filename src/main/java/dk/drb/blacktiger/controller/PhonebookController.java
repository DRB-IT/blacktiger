/*
 * Copyright by Apaq 2011-2013
 */

package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.service.IBlackTigerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Javadoc
 */
@Controller
public class PhonebookController {

    private static final Logger LOG = LoggerFactory.getLogger(PhonebookController.class);
    
    @Autowired
    private IBlackTigerService service;
    
    @RequestMapping(value = "/phonebook/{number}", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public int updatePhonebookEntryAsJson(@PathVariable final String number, @RequestBody String name) {
        LOG.debug("Updating phonebook entry. [number={}; name={}]", number, name);
        service.updatePhonebookEntry(number, name);
        return 1;
    }
    
    @RequestMapping(value = "/phonebook/{number}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public String getPhonebookEntryAsJson(@PathVariable final String number) {
        LOG.debug("Retrieving phonebook entry. [number={}]", number);
        return service.getPhonebookEntry(number);
    }
}
