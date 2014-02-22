package dk.drb.blacktiger.controller;

import static dk.drb.blacktiger.fixture.rest.SystemRestDataFixture.standardInfoAsJson;
import dk.drb.blacktiger.service.PhonebookService;
import dk.drb.blacktiger.service.SystemService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
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
public class PhonebookControllerTest {
    
    MockMvc mockMvc;

    @InjectMocks
    PhonebookController controller;

    @Mock
    PhonebookService service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void thatEntryCanBeRetrieved() throws Exception {
        
        when(service.getPhonebookEntry("+1911")).thenReturn("Emergency");
        
        this.mockMvc.perform(get("/phonebook/+1911")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("\"Emergency\""));
    }
    
    @Test
    public void thatEntryCanBeStored() throws Exception {
        
        this.mockMvc.perform(put("/phonebook/+1911").content("\"Emergency\"")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    public void thatEntryCanDeleted() throws Exception {
        
        this.mockMvc.perform(delete("/phonebook/+1911"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}