package br.com.fantini.sse.resource;

import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sse")
public class SSEResource {
    
    @Inject
    EventBus eventBus;

    @GET
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<String> streamMessages() {
        return eventBus.<String>consumer("messages").bodyStream().toMulti();
    }

    @GET
    @Path("/replay/{amount}")
    public Uni<Response> replay(@PathParam("amount") @DefaultValue("0") Integer amount) {

        for (;amount > 0; amount --)
            eventBus.send("messages", amount.toString());

        return Uni.createFrom().item(Response.accepted().build());
    }

}
