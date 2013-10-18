package dk.drb.blacktiger.repository;

import dk.drb.blacktiger.model.CallInformation;
import java.util.Date;
import java.util.List;

/**
 * Repository for accessing statistics abouts calls made.
 */
public interface CallInformationRepository  {
    
    List<CallInformation> findByRoomNoAndPeriodAndDuration(String roomNo, Date start, Date end, int minimumDuration);
}
