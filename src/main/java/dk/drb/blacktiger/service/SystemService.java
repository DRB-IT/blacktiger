package dk.drb.blacktiger.service;

import com.sun.management.OperatingSystemMXBean;
import dk.drb.blacktiger.controller.rest.model.SendPasswordRequest;
import dk.drb.blacktiger.repository.SipAccountRepository;
import java.io.File;
import java.lang.management.ManagementFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

/**
 *
 * @author KROG
 */
public class SystemService {
    
    private static final OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final File file = File.listRoots()[0];
    
    @Autowired
    private SipAccountRepository sipAccountRepository;
    
    @Secured("ROLE_ADMIN")
    public long getTotalDiskSpace() {
        return file.getTotalSpace();
    }
    
    @Secured("ROLE_ADMIN")
    public long getFreeDiskSpace() {
        return file.getFreeSpace();
    }
    
    @Secured("ROLE_ADMIN")
    /**
     * Returns the total amount of physical memory in bytes.
     */
    public long getTotalPhysicalMemorySize() {
        return osmxb.getTotalPhysicalMemorySize();
    }
    
    @Secured("ROLE_ADMIN")
    /**
     * Returns the amount of free physical memory in bytes.
     */
    public long getFreePhysicalMemorySize() {
        return osmxb.getFreePhysicalMemorySize();
    }    
    
    @Secured("ROLE_ADMIN")
    public int getNumberOfProcessors() {
        return osmxb.getAvailableProcessors();
    }
    
    @Secured("ROLE_ADMIN")
    public double getSystemLoad() {
        return osmxb.getSystemCpuLoad();
    }
    
    @Secured("ROLE_ADMIN")
    public double getSystemLoadAverage() {
        return osmxb.getSystemLoadAverage();
    }
    
        
    public boolean sendPasswordEmail(SendPasswordRequest request) {
        //The following 3 if's is a fallback from when the client originally did'nt deliver the texts.
        if(request.getEmailSubject() == null) {
            request.setEmailSubject("Telesal in {%1}");
        }
        
        if(request.getEmailTextManager() == null) {
            request.setEmailTextManager("{%1} has activated 'forgot password' for the hall using phone number {%4}, which you are technically responsible for. He has mobile phone number is {%2} and his e-mail is {%3}. For security reasons, the password been sent to you only, not to him. You must contact him to check if he should have the password from you.\\n\\nThe password is: {%5}\\n");
        }
        
        if(request.getEmailTextUser() == null) {
            request.setEmailTextUser("You have activated 'forgot password' for the hall using phone number {%1}. The password has been sent to {%5}, which is the e-mail address which was entered earlier for the technical responsible for this hall. His name is {%3} and his phone number is {%4}.\\n\\nThe user ID which must be used together with the password is {%2}.");
        }

        return sipAccountRepository.sendPasswordEmail(request.getName(), request.getPhoneNumber(), request.getEmail(), request.getCityOfHall(), 
                request.getPhoneNumberOfHall(), request.getEmailSubject(), request.getEmailTextManager(), request.getEmailTextUser());

    }
}
