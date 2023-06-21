package br.com.fantini.amqp.business;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import br.com.fantini.amqp.commons.Message;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageBusiness {
    
    @Incoming("messages-in")
    public void processMessage(Message message) {
        System.out.println(message.getPayload());
    }

}
