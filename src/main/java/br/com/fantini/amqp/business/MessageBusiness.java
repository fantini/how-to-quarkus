package br.com.fantini.amqp.business;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import br.com.fantini.amqp.commons.Message;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageBusiness {
    /*
     * @Blocking: 
     *  - identifica que metodo ira realizar operacoes bloqueantes e permitira sua execucao em um worker separado
     * @ActivateRequestContext:
     *  - deve ser utilizado caso as operacoes envolvidades necessitem do contexto. Exemplo: acesso ao DB
     */
    @Incoming("messages-in")
    @Blocking
    public void processMessage(Message message) {
        System.out.println("Mensagem: " + message.getPayload());
    }

}
