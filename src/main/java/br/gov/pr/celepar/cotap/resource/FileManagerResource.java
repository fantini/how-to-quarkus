package br.gov.pr.celepar.cotap.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/file")
public class FileManagerResource {
    
    @GET
    @Produces(value = MediaType.APPLICATION_OCTET_STREAM)
    public Response download() {
        try {
            return Response.ok(new FileInputStream(new File("C:/Users/luizr/Downloads/teste.pdf")))
                .header("Content-Disposition", "attachment;filename=teste.pdf")
                .header("filename", "teste.pdf")
                .build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@MultipartForm MultipartFormDataInput data) {
        try {
            FileUtils.copyInputStreamToFile((InputStream)data.getFormDataMap().get("arquivo").get(0).getBody(), new File("C:/Users/luizr/Downloads/"+UUID.randomUUID()+".pdf"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Response.ok().build();
    }
}