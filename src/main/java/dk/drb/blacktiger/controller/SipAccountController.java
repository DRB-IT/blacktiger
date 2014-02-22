package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.model.SipAccount;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author michael
 */
@Controller
public class SipAccountController {
    
    @RequestMapping(value="/sipaccounts", method = RequestMethod.GET)
    @ResponseBody
    public List<SipAccount> listAccounts(@PathVariable String key, @PathVariable String phoneNumber) {
        return null;
    }
    
    @RequestMapping(value="/sipaccounts", method = RequestMethod.POST)
    @ResponseBody
    public void createAccount(@RequestBody SipAccount account) {
        
    }
}
