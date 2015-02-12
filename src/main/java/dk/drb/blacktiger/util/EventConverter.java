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
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import dk.drb.blacktiger.model.ConferenceEvent;
import dk.drb.blacktiger.model.ConferenceStartEvent;
import dk.drb.blacktiger.model.Participant;
import dk.drb.blacktiger.model.ParticipantEvent;
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

    private final JsonSerializer<ConferenceEvent> eventSerializer = new JsonSerializer<ConferenceEvent>() {
        @Override
        public void serialize(ConferenceEvent value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeStringField("roomNo", value.getRoomNo());

            if (value instanceof ParticipantEvent) {
                jgen.writeObjectField("participant", ((ParticipantEvent) value).getParticipant());
            }

            if (value instanceof SparseParticipantEvent) {
                jgen.writeStringField("channel", ((SparseParticipantEvent) value).getChannel());
            }

            if (value instanceof ConferenceStartEvent) {
                jgen.writeObjectField("room", ((ConferenceStartEvent) value).getRoom());
            }

            jgen.writeStringField("type", value.getType());
            jgen.writeEndObject();
        }

        @Override
        public Class<ConferenceEvent> handledType() {
            return ConferenceEvent.class;
        }
    };
    
    private final JsonSerializer<Participant> participantSerializer = new JsonSerializer<Participant>() {
        ISO8601DateFormat df = new ISO8601DateFormat();
        @Override
        public void serialize(Participant value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            String dateJoined = value.getDateJoined() == null ? null : df.format(value.getDateJoined());
            String type = value.getType() == null ? null : value.getType().name();
            jgen.writeStartObject();
            jgen.writeStringField("callerId", value.getCallerId());
            jgen.writeStringField("channel", value.getChannel());
            jgen.writeBooleanField("muted", value.isMuted());
            jgen.writeStringField("phoneNumber", value.getPhoneNumber());
            jgen.writeStringField("dateJoined", dateJoined);
            jgen.writeStringField("name", value.getName());
            jgen.writeStringField("type", type);
            jgen.writeBooleanField("host", value.isHost());
            jgen.writeEndObject();
        }

        @Override
        public Class<Participant> handledType() {
            return Participant.class;
        }
    };

    public EventConverter() {
        SimpleModule m = new SimpleModule("EventModule");
        m.addSerializer(eventSerializer);
        m.addSerializer(participantSerializer);

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
