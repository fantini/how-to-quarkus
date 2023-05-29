package br.gov.pr.celepar.cotap.httpclient;

import java.io.InputStream;

import org.apache.commons.io.input.CountingInputStream;
import org.junit.jupiter.api.Test;

import br.gov.pr.celepar.cotap.resource.FileManagerResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
public class FileManagerClientTest {

    @Inject
    FileManagerResource resource;

    @Test
    void testStreamEndpoint() {
        io.restassured.response.Response response = RestAssured.given()
            .when().get("/file")
            .andReturn();

        String fileName = response.getHeader("filename");
        System.out.println(fileName);

        try {
            try (
                InputStream in = (InputStream)response.getBody().asInputStream();
                CountingInputStream cin = new CountingInputStream(in)                
            ) {

                System.out.println(cin.getCount());

                RestAssured.given()
                    .header(new Header("Content-Type", MediaType.MULTIPART_FORM_DATA.toString()))
                    .multiPart("arquivo", "teste.pdf", cin, MediaType.APPLICATION_OCTET_STREAM)
                    .when().post("/file");
                
                System.out.println(cin.getCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}