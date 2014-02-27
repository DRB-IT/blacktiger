package dk.drb.blacktiger.service;

import dk.drb.blacktiger.model.ConferenceEventListener;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.Room;
import dk.drb.blacktiger.repository.ConferenceRoomRepository;
import dk.drb.blacktiger.repository.ParticipantRepository;
import dk.drb.blacktiger.repository.PhonebookRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author michael
 */
public class ConferenceServiceTest {
    
    private List<Room> rooms;
    private ConferenceRoomRepository conferenceRoomRepository;
    private ConferenceService service;
    
    private Answer<List<Room>> answerSubselectedRooms() {
        return new Answer<List<Room>>() {

            @Override
            public List<Room> answer(InvocationOnMock invocation) throws Throwable {
                List<String> ids = (List<String>) invocation.getArguments()[0];
                List<Room> result = new ArrayList<>();
                for(Room room : rooms) {
                    if(ids.contains(room.getId())) {
                        result.add(room);
                    }
                }
                return result;
            }
        };
    }
    
    @Before
    public void init() {
        rooms = new ArrayList<>();
        rooms.add(new Room("H45-0000", "Number 1"));
        rooms.add(new Room("H45-0001", "Number 2"));
        rooms.add(new Room("H45-0002", "Number 3"));
        rooms.add(new Room("H45-0003", "Number 4"));
     
        conferenceRoomRepository = Mockito.mock(ConferenceRoomRepository.class);
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(rooms);
        Mockito.when(conferenceRoomRepository.findAllByIds(Mockito.anyList())).then(answerSubselectedRooms());
 
        service = new ConferenceService();
        service.setRoomRepository(conferenceRoomRepository);
    }
    
    @Test
    public void ifAdminCanRetrieveAll() {
        Collection<? extends GrantedAuthority> auths = Arrays.asList((GrantedAuthority)new SimpleGrantedAuthority("ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("john", "doe", auths));
        
        List<Room> result = service.listRooms();
        assertEquals(4, result.size());
    }
    
    @Test
    public void ifNormalUserCanOnlyRetrieveASubset() {
        Collection<? extends GrantedAuthority> auths = Arrays.asList(
                (GrantedAuthority)new SimpleGrantedAuthority("ROLE_ROOMACCESS_H45-0000"),
                (GrantedAuthority)new SimpleGrantedAuthority("ROLE_ROOMACCESS_H45-0001"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("john", "doe", auths));
        
        List<Room> result = service.listRooms();
        assertEquals(2, result.size());
    }
}