package dk.drb.blacktiger.service;

import dk.drb.blacktiger.repository.CallInformationRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import dk.drb.blacktiger.model.CallInformation;
import dk.drb.blacktiger.model.PhonebookEntry;
import dk.drb.blacktiger.util.Access;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CallInformationService {

    private CallInformationRepository repository;
    private PhonebookRepository phonebookRepository;

    @Autowired
    public void setRepository(CallInformationRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setPhonebookRepository(PhonebookRepository phonebookRepository) {
        this.phonebookRepository = phonebookRepository;
    }
    
    /**
     * Retrieves a list of archived calls.
     * @param start The start timestamp for the list.
     * @param end The end timestamp for the list.
     * @param minimumDuration The minimum duration in seconds for each call to include.
     * @return The list of archived calls.
     */
    public List<CallInformation> getReport(String roomNo, Date start, Date end, int minimumDuration) {
        Access.checkRoomAccess(roomNo);
        List<CallInformation> list = repository.findByRoomNoAndPeriodAndDuration(roomNo, start, end, minimumDuration);
        for(CallInformation info : list) {
            PhonebookEntry entry = phonebookRepository.findByNumber(info.getPhoneNumber());
            if(entry != null) {
                info.setName(entry.getName());
            }
        }
        return list;
    }
}
