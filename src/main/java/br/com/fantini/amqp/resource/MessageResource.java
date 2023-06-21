package br.com.fantini.amqp.resource;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;

import br.com.fantini.amqp.commons.Message;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/message")
public class MessageResource {
    
    @Inject
    @Channel("messages-out")
    MutinyEmitter<Message> emitter;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseStatus(StatusCode.ACCEPTED)
    public Uni<Void> send(Message message) {
        return emitter.send(message);
    }

}
