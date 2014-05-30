package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.controller.rest.model.ResourceNotFoundException;
import dk.drb.blacktiger.controller.rest.model.SendPasswordRequest;
import dk.drb.blacktiger.controller.rest.model.UserPresentation;
import dk.drb.blacktiger.service.SystemService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for retrieving system information as well as authentication information.
 */
@Controller
public class SystemController {
    
        private static final Logger LOG = LoggerFactory.getLogger(PhonebookController.class);
    
    @Autowired
    private SystemService service;
    
    @RequestMapping(value = "/system/information", headers = "Accept=application/json")
    @ResponseBody
    public Map getInformation() {
        LOG.debug("Got request system information.");
       Map map = new HashMap();
       Map loadMap = new HashMap();
       Map averageCpuLoadMap = new HashMap();
       
       map.put("cores", service.getNumberOfProcessors());
       map.put("load", loadMap);
       map.put("averageCpuLoad", averageCpuLoadMap);
       
       loadMap.put("disk", percentageOf(service.getFreeDiskSpace(), service.getTotalDiskSpace()));
       loadMap.put("memory", percentageOf(service.getFreePhysicalMemorySize(), service.getTotalPhysicalMemorySize()));
       loadMap.put("cpu", service.getSystemLoad());
       loadMap.put("net", 0.0);
       
       averageCpuLoadMap.put("oneMinute", service.getSystemLoadAverage());
       averageCpuLoadMap.put("fiveMinutes", 0.0);
       averageCpuLoadMap.put("tenMinutes", 0.0);
       
       return map;
    }
    
    @RequestMapping(value = "/system/authenticate", produces = "application/json")
    @ResponseBody
    public UserPresentation authenticate() {
        return UserPresentation.from(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @RequestMapping(value = "/system/passwordRequests", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public void requstPassword(@RequestBody SendPasswordRequest request) {
        boolean ok = service.sendPasswordEmail(request.getName(), request.getPhoneNumber(), request.getEmail(), request.getCityOfHall(), request.getPhoneNumberOfHall());
        if(!ok) {
            throw new ResourceNotFoundException("No hall found using the specified input.");
        }
    }
    
    private double percentageOf(double minor, double major) {
        return (minor / major) * 100;
    }
    
}
