package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.service.SystemService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;    
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author michael
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
       
       loadMap.put("disk", (service.getFreeDiskSpace() / service.getTotalDiskSpace()) * 100);
       loadMap.put("memory", (service.getFreePhysicalMemorySize() / service.getTotalPhysicalMemorySize()) * 100);
       loadMap.put("cpu", service.getSystemLoad());
       loadMap.put("net", 0.0);
       
       averageCpuLoadMap.put("oneMinute", service.getSystemLoadAverage());
       averageCpuLoadMap.put("fiveMinutes", 0.0);
       averageCpuLoadMap.put("tenMinutes", 0.0);
       
       return map;
    }
    
}
