package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.controller.rest.ParticipantController;
import static dk.drb.blacktiger.fixture.rest.ParticipantRestDataFixture.*;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.service.ConferenceService;
import dk.drb.blacktiger.service.PhonebookService;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 *
 * @author michael
 */
public class ParticipantControllerTest {
    
    MockMvc mockMvc;

    @InjectMocks
    ParticipantController controller;

    @Mock
    ConferenceService service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void thatParticipantsCanBeListed() throws Exception {
        
        when(service.listParticipants(eq("H45-0000"))).thenReturn(standardParticipantsList());
        
        this.mockMvc.perform(get("/rooms/H45-0000/participants")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(standardParticipantListAsJson()));
    }
    
    @Test
    public void thatParticipantCanBeRetrieved() throws Exception {
        String id = "1";
        when(service.getParticipant(eq("H45-0000"), eq(id))).thenReturn(standardParticipant(id));
        
        this.mockMvc.perform(get("/rooms/H45-0000/participants/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(standardParticipantAsJson(id)));
    }
    
        
    @Test
    public void thatParticipantCanBeKicked() throws Exception {
        String id = "1";
        
        this.mockMvc.perform(delete("/rooms/H45-0000/participants/" + id))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
        
    @Test
    public void thatParticipantCanBeMuted() throws Exception {
        String id = "1";
        when(service.getParticipant(eq("H45-0000"), eq(id))).thenReturn(standardParticipant(id));
        
        this.mockMvc.perform(put("/rooms/H45-0000/participants/" + id)
                .contentType(MediaType.APPLICATION_JSON).content("{\"muted\":true}"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}