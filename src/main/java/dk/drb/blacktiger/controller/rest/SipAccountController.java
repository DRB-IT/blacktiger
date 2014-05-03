package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.model.SipAccount;
import dk.drb.blacktiger.service.SipAccountService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for administering SIP Users via REST.
 */
@Controller
public class SipAccountController {
    
    @Autowired
    SipAccountService service;
    
    @RequestMapping(value="/sipaccounts/{phoneNumber}", method = RequestMethod.GET)
    @ResponseBody
    public SipAccount listAccounts(@RequestParam String key, @PathVariable String phoneNumber) {
        return service.findOneByKeyAndPhonenumber(key, phoneNumber);
    }
    
    @RequestMapping(value="/sipaccounts", method = RequestMethod.POST)
    @ResponseBody
    public void createAccount(@RequestBody SipAccount account) {
        service.save(account);
    }
}
