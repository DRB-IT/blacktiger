package dk.drb.blacktiger.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author KROG
 */
public class SystemServiceTest {
    


    /**
     * Test of getTotalDiskSpace method, of class SystemService.
     */
    @Test
    public void testGetTotalDiskSpace() {
        System.out.println("getTotalDiskSpace");
        SystemService instance = new SystemService();
        long result = instance.getTotalDiskSpace();
        assertTrue(result > 0);
    }

    /**
     * Test of getFreeDiskSpace method, of class SystemService.
     */
    @Test
    public void testGetFreeDiskSpace() {
        System.out.println("getFreeDiskSpace");
        SystemService instance = new SystemService();
        long result = instance.getFreeDiskSpace();
        assertTrue(result > 0);
    }

    /**
     * Test of getTotalPhysicalMemorySize method, of class SystemService.
     */
    @Test
    public void testGetTotalPhysicalMemorySize() {
        System.out.println("getTotalPhysicalMemorySize");
        SystemService instance = new SystemService();
        long result = instance.getTotalPhysicalMemorySize();
        assertTrue(result > 0);
    }

    /**
     * Test of getFreePhysicalMemorySize method, of class SystemService.
     */
    @Test
    public void testGetFreePhysicalMemorySize() {
        System.out.println("getFreePhysicalMemorySize");
        SystemService instance = new SystemService();
        long result = instance.getFreePhysicalMemorySize();
        assertTrue(result > 0);
    }

    /**
     * Test of getNumberOfProcessors method, of class SystemService.
     */
    @Test
    public void testGetNumberOfProcessors() {
        System.out.println("getNumberOfProcessors");
        SystemService instance = new SystemService();
        int result = instance.getNumberOfProcessors();
        assertTrue(result > 0);
    }

    /**
     * Test of getSystemLoad method, of class SystemService.
     */
    @Test
    public void testGetSystemLoad() {
        System.out.println("getSystemLoad");
        SystemService instance = new SystemService();
        
        for(int i=0;i<100000;i++) {
            Math.sqrt(i);
            Thread.yield();
        }
        
        double result = instance.getSystemLoad();
        //assertTrue(result != 0);
    }

    /**
     * Test of getSystemLoadAverage method, of class SystemService.
     */
    @Test
    public void testGetSystemLoadAverage() {
        System.out.println("getSystemLoadAverage");
        SystemService instance = new SystemService();
        
        for(int i=0;i<10000;i++) {
            Math.sqrt(i);
        }
        
        double result = instance.getSystemLoadAverage();
        assertTrue(result != 0);
    }
    
}
