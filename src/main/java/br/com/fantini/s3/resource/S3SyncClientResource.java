package br.com.fantini.s3.resource;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import br.com.fantini.s3.CommonResource;
import br.com.fantini.s3.FileObject;
import br.com.fantini.s3.FormData;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Path("/s3")
public class S3SyncClientResource extends CommonResource {
    
    @Inject
    S3Client s3;

    @Inject 
    S3Presigner s3Presigner;

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormData formData) throws Exception {

        if (formData.filename == null || formData.filename.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        
        if (formData.mimetype == null || formData.mimetype.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        PutObjectResponse putResponse = s3.putObject(buildPutRequest(formData),
                RequestBody.fromFile(formData.data.filePath()));
        if (putResponse != null) {
            return Response.ok().status(Status.CREATED).build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(String objectKey) {
        ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(buildGetRequest(objectKey));
        ResponseBuilder response = Response.ok(objectBytes.asUtf8String());
        response.header("Content-Disposition", "attachment;filename=" + objectKey);
        response.header("Content-Type", objectBytes.response().contentType());
        return response.build();
    }

    @GET
    @Path("/url/signed/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response signedUrl(String objectKey) {

        return Response.ok(
            s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(buildGetRequest(objectKey))
                    .build()
            ).url()
        ).build();
        
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileObject> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();

        //HEAD S3 objects to get metadata
        return s3.listObjects(listRequest).contents().stream()
                .map(FileObject::from)
                .sorted(Comparator.comparing(FileObject::getObjectKey))
                .collect(Collectors.toList());
    }

}
