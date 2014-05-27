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
    
        
    public void sendPasswordEmail(String name, String phoneNumber, String email,  String cityOfHall, String phoneNumberOfHall) {
        String emailSubject = "Telesal i {%1}", 
                emailTextManager = "{%1} har aktiveret 'glemt kode' for salen med nummer {%4} som du er teknisk ansvarlig for. Han har telefon {%2} og e-mail {%3}.\\n\\nKoden er: {%5}\\n", 
                emailTextUser = "Du har aktiveret 'glemt kode' for salen med nummer {%4}. Koden er nu sendt i en e-mail til {%5}, som er den adresse der er registreret for den teknisk ansvarlige for denne sal. Han hedder {%3} og har telefon {%4}.\\n\\nBrugernavnet der skal bruges sammen med koden er {%2}.";
        boolean ok = sipAccountRepository.sendPasswordEmail(name, phoneNumber, email, cityOfHall, phoneNumberOfHall, emailSubject, emailTextManager, emailTextUser);
        if(!ok) {
            throw new UnknownError("An unknown error occured while sending password.");
        }
    }
}
