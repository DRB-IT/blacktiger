package dk.drb.blacktiger.util;

import java.text.NumberFormat;

/**
 *
 * @author michael
 */
public class GenerateConf {
    
    public static void main(String[] args) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setMinimumIntegerDigits(4);
        nf.setGroupingUsed(false);
        
        for(int i=0;i<1000;i++) {
            String id = nf.format(i);

            System.out.println("exten=>+450000" + id + ",1,Macro(CallConf,H45-" + id + ",da)");
        }
    }
}
