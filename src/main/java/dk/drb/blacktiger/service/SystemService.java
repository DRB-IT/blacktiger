package dk.drb.blacktiger.service;

import com.sun.management.OperatingSystemMXBean;
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
    public long getTotalPhysicalMemorySize() {
        return osmxb.getTotalPhysicalMemorySize();
    }
    
    @Secured("ROLE_ADMIN")
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
    
        
    public boolean sendPasswordEmail(String name, String phoneNumber, String email,  String cityOfHall, String phoneNumberOfHall) {
        String emailSubject = "Telesal i {%1}", 
                emailTextManager = "{%1} har aktiveret 'glemt kodeord' for salen med nummer {%4}, som du er teknisk ansvarlig for. Hans mobilnummer er {%2} og hans e-mail er {%3}. Af sikkerhedshensyn er kodeordet kun sendt til dig, ikke til ham. Du skal kontakte ham for at finde ud af om han skal have udleveret kodeordet af dig.\\n\\nDet glemte kodeord er: {%5}\\n", 
                            // EN: "{%1} has activated 'forgot password' for the hall using phone number {%4}, which you are technically responsible for. He has mobile phone number is {%2} and his e-mail is {%3}. For security reasons, the password been sent to you only, not to him. You must contact him to check if he should have the password from you.\\n\\nThe password is: {%5}\\n"
                emailTextUser = "Du har aktiveret 'glemt kodeord' for salen med nummer {%1}. Koden er nu sendt til {%5}, som er den e-mailadresse der tidligere er oplyst for den teknisk ansvarlige for denne sal. Han hedder {%3} og har telefon {%4}.\\n\\nBrugernavnet der skal bruges sammen med koden er {%2}.";
                            // EN: "You have activated 'forgot password' for the hall using phone number {%1}. The password has been sent to {%5}, which is the e-mail address which was entered earlier for the technical responsible for this hall. His name is {%3} and his phone number is {%4}.\\n\\nThe user ID which must be used together with the password is {%2}."
        return sipAccountRepository.sendPasswordEmail(name, phoneNumber, email, cityOfHall, phoneNumberOfHall, emailSubject, emailTextManager, emailTextUser);

    }
}
