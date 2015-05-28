package dk.drb.blacktiger.util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class LotsOfCalls {

    public static void main(String[] args) throws Exception {
        List<Caller> callers = new ArrayList<>();
        
        for(int i=0;i<10;i++) {
            Caller caller = new Caller("L000000000", "12345", "sip:+4522000000@192.168.50.2");
            caller.register();
            caller.call();
            callers.add(caller);
            Thread.sleep(100);
        }
        
        Thread.sleep(10000);
        
        for(Caller caller : callers) {
            caller.hangup();
            Thread.sleep(50);
        }
        
        System.exit(0);
    }
}
