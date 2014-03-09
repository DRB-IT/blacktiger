package dk.drb.blacktiger.util;

import java.text.NumberFormat;

/**
 *
 * @author michael
 */
public class GenerateConf {
    
    public static void main(String[] args) {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setMinimumIntegerDigits(8);
        nf.setGroupingUsed(false);
        
        for(int i=0;i<10000;i++) {
            String id = nf.format(i);

            System.out.println("call create_hall(\"45\",\"8654\",\"Bryrup\",\"\",\"+4588701234\",\"Peter A. F. æøå\",\"paf@test.æøå\",\"+4511223344\",\"Brugerkommentar æøå\",\"Admin-kommentar æøå\",\"Admin\",@key); ");
            System.out.println();
        }
    }
}
