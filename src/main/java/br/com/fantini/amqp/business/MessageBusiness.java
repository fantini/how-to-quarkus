package br.com.fantini.amqp.business;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import br.com.fantini.amqp.commons.Message;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageBusiness {
    
    @Incoming("messages-in")
    public void processMessage(JsonObject json) {
        System.out.println(json.mapTo(Message.class).getPayload());
    }

}
