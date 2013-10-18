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
        //TODO Implement
        return new ArrayList<CallInformation>();
    }
    
}
