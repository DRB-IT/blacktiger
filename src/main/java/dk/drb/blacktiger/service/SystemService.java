package dk.drb.blacktiger.service;

import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;

/**
 *
 * @author KROG
 */
public class SystemService {
    
    private static final OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final File file = File.listRoots()[0];
    
    public long getTotalDiskSpace() {
        return file.getTotalSpace();
    }
    
    public long getFreeDiskSpace() {
        return file.getFreeSpace();
    }
    
    public long getTotalPhysicalMemorySize() {
        return osmxb.getTotalPhysicalMemorySize();
    }
    
    public long getFreePhysicalMemorySize() {
        return osmxb.getFreePhysicalMemorySize();
    }    
    
    public int getNumberOfProcessors() {
        return osmxb.getAvailableProcessors();
    }
    
    public double getSystemLoad() {
        return osmxb.getSystemCpuLoad();
    }
    
    public double getSystemLoadAverage() {
        return osmxb.getSystemLoadAverage();
    }
}
