package dk.drb.blacktiger.repository.memory;

import dk.drb.blacktiger.model.CallInformation;
import dk.drb.blacktiger.repository.CallInformationRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class InMemCallInformationRepository implements CallInformationRepository {

    @Override
    public List<CallInformation> findByRoomNoAndPeriodAndDuration(String roomNo, Date start, Date end, int minimumDuration) {
        CallInformation ci = new CallInformation("+4551923171", "Hannah Krog", 2, 1123, new Date());
        CallInformation ci2 = new CallInformation("+4551923192", "Michael Krog", 4, 3123, new Date());
        
        List<CallInformation> list = new ArrayList<CallInformation>();
        list.add(ci);
        list.add(ci2);
        return list;
    }
    
}
