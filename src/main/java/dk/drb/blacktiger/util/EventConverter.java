/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.drb.blacktiger.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ParticipantEvent;
import dk.drb.blacktiger.model.ParticipantJoinEvent;
import dk.drb.blacktiger.model.ParticipantLeaveEvent;
import dk.drb.blacktiger.model.SparseParticipantEvent;
import java.io.IOException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

/**
 *
 * @author michael
 */
public class EventConverter extends MappingJackson2MessageConverter {

    public EventConverter() {
        SimpleModule m = new SimpleModule("EventModule");
        m.addSerializer(new JsonSerializer<ConferenceEvent>() {

            @Override
            public void serialize(ConferenceEvent value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeStartObject();
                jgen.writeStringField("roomNo", value.getRoomNo());
                
                if(value instanceof ParticipantEvent) {
                    jgen.writeObjectField("participant", ((ParticipantEvent)value).getParticipant());
                }
                
                if(value instanceof SparseParticipantEvent) {
                    jgen.writeStringField("callerId", ((SparseParticipantEvent)value).getCallerId());
                }
                
                jgen.writeStringField("type", value.getType());
                jgen.writeEndObject();
            }

            @Override
            public Class<ConferenceEvent> handledType() {
                return ConferenceEvent.class;
            }
            
        });
        
        getObjectMapper().registerModule(m);
        
    }

    @Override
    protected boolean canConvertTo(Object payload, MessageHeaders headers) {
        return payload instanceof ConferenceEvent;
    }

    @Override
    protected boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
        return false;
    }
    
    
     
}
