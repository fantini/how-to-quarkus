package br.com.fantini.amqp.resource;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import br.com.fantini.amqp.commons.Message;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/message")
public class MessageResource {
    
    @Inject
    @Channel("messages-out")
    Emitter<Message> emitter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response send(Message message) {
        emitter.send(message);
        return Response.accepted().build();
    }

    @Incoming("messages-in")
    public void receive(JsonObject json) {
        System.out.println(json.mapTo(Message.class).getPayload());
    }

}
