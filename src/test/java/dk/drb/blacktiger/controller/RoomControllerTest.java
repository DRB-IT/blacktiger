/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.controller;

import dk.drb.blacktiger.service.ConferenceService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static dk.drb.blacktiger.fixture.rest.RoomRestDataFixture.*;

/**
 *
 */
public class RoomControllerTest {

    MockMvc mockMvc;

    @InjectMocks
    RoomController controller;

    @Mock
    ConferenceService service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @Test
    public void thatRoomsCanBeRetrieved() throws Exception {
        String[] ids = {"1", "2"};
        when(service.listRooms()).thenReturn(standardListOfRooms(ids));

        this.mockMvc.perform(get("/rooms")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(standardListOfRoomsAsJson(ids)));
    }
    
    @Test
    public void thatARoomCanBeRetrieved() throws Exception {
        when(service.getRoom(eq("1"))).thenReturn(standardRoom("1"));

        this.mockMvc.perform(get("/rooms/1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(standardRoomAsJson("1")));
    }

}
