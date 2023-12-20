package br.com.fantini.mailer.resource;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/mail")                                                          
public class MailResource {

    @Inject Mailer mailer;                                              

    @GET                                                                
    @Blocking                                                           
    public void sendEmail() {
        mailer.send(
                Mail.withText("email-test@io",                     
                    "Ahoy from Quarkus",
                    "A simple email sent from a Quarkus application."
                )
        );
    }

}
