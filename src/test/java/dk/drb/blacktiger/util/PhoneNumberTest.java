package dk.drb.blacktiger.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class PhoneNumberTest {
    
    
    @Test
    public void testDanishValid() {
        String number = "51923192";
        String region = "DK";
        String expResult = "+4551923192";
        String result = PhoneNumber.normalize(number, region);
        assertEquals(expResult, result);
        
    }
    
    @Test
    public void testDanishValid2() {
        String number = "4551923192";
        String region = "DK";
        String expResult = "+4551923192";
        String result = PhoneNumber.normalize(number, region);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testDanishValid3() {
        String number = "+4551923192";
        String region = "DK";
        String expResult = "+4551923192";
        String result = PhoneNumber.normalize(number, region);
        assertEquals(expResult, result);
    }
    



}