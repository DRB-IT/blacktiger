package dk.drb.blacktiger.controller.rest;

import dk.drb.blacktiger.service.SystemService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static dk.drb.blacktiger.fixture.rest.SystemRestDataFixture.standardInfoAsJson;
import org.hamcrest.CoreMatchers;

import static org.hamcrest.CoreMatchers.*;


/**
 *
 * @author michael
 */
public class SystemControllerTest {
    
    MockMvc mockMvc;

    @InjectMocks
    SystemController controller;

    @Mock
    SystemService service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void thatInfoCanBeRetrieved() throws Exception {
        
        when(service.getFreeDiskSpace()).thenReturn(125000L);
        when(service.getTotalDiskSpace()).thenReturn(1000000L);
        when(service.getFreePhysicalMemorySize()).thenReturn(250000L);
        when(service.getTotalPhysicalMemorySize()).thenReturn(750000L);
        
        when(service.getNumberOfProcessors()).thenReturn(4);
        when(service.getSystemLoad()).thenReturn(25.0);
        when(service.getSystemLoadAverage()).thenReturn(20.0);
        
        String standardInfo = standardInfoAsJson();
        this.mockMvc.perform(get("/system/information")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}