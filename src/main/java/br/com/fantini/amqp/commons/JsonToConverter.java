package br.com.fantini.amqp.commons;

import java.lang.reflect.Type;

import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.MessageConverter;
import io.smallrye.reactive.messaging.amqp.IncomingAmqpMetadata;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
public class JsonToConverter implements MessageConverter {

    @Override
    public boolean canConvert(Message<?> in, Type target) {
        return in.getMetadata(IncomingAmqpMetadata.class)
            .map(meta -> meta.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON) && target instanceof Class)
            .orElse(false);
    }

    @Override
    public Message<?> convert(Message<?> in, Type target) {
        return in.withPayload(((JsonObject)in.getPayload()).mapTo((Class<?>) target));
    }
    
}
