package br.gov.pr.celepar.cotap.httpclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey="file-manager-api")
public interface FileManagerClient {
    
    @GET
    @Produces(value = MediaType.APPLICATION_OCTET_STREAM)
    public Response download();
    
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@MultipartForm MultipartFormDataOutput data);
}