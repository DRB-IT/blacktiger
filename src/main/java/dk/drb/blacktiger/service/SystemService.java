package dk.drb.blacktiger.service;

import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;
import org.springframework.security.access.annotation.Secured;

/**
 *
 * @author KROG
 */
public class SystemService {
    
    private static final OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final File file = File.listRoots()[0];
    
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
}
