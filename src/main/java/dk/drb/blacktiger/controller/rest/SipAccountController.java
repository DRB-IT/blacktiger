package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.controller.rest.model.SipAccountCreateRequest;
import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.service.SipAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller for administering SIP Users via REST.
 */
@Controller
public class SipAccountController {
    private static final Logger LOG = LoggerFactory.getLogger(SipAccountController.class);
    
    @Autowired
    SipAccountService service;
    
    @RequestMapping(value="/sipaccounts/{phoneNumber}", method = RequestMethod.GET)
    @ResponseBody
    public SipAccount listAccounts(@RequestParam String key, @PathVariable String phoneNumber) {
        LOG.debug("listAccounts requested [key={}; phoneNumber={}]", key, phoneNumber);
        return RestExceptionHandler.notNull(service.findOneByKeyAndPhonenumber(key, phoneNumber), "SipAccount not found.");
    }
    
    @RequestMapping(value="/sipaccounts", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void createAccount(@RequestBody SipAccountCreateRequest accountCreateRequest) {
        LOG.debug("createAccount requested [accountCreateRequest={}]", accountCreateRequest);
        boolean accepted = service.create(accountCreateRequest.getAccount(), accountCreateRequest.getMailText());
        if(!accepted) {
            throw new IllegalArgumentException("Request for new account was not accepted, - most likely because of illegal arguments.");
        }
    }
}
