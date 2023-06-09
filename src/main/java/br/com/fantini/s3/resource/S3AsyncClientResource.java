package br.com.fantini.s3.resource;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestMulti;
import org.reactivestreams.Publisher;

import br.com.fantini.s3.commons.CommonResource;
import br.com.fantini.s3.commons.FileObject;
import br.com.fantini.s3.commons.FormData;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Path("/async-s3")
public class S3AsyncClientResource extends CommonResource {
    
    @Inject
    S3AsyncClient s3;

    @Inject 
    S3Presigner s3Presigner;

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> uploadFile(FormData formData) throws Exception {

        if (formData.filename == null || formData.filename.isEmpty()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        if (formData.mimetype == null || formData.mimetype.isEmpty()) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).build());
        }

        return Uni.createFrom()
                .completionStage(() -> {
                    return s3.putObject(buildPutRequest(formData), AsyncRequestBody.fromFile(formData.data.filePath()));
                })
                .onItem().ignore().andSwitchTo(Uni.createFrom().item(Response.created(null).build()))
                .onFailure().recoverWithItem(th -> {
                    th.printStackTrace();
                    return Response.serverError().build();
                });
    }

    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public RestMulti<Buffer> downloadFile(String objectKey) {

        return RestMulti.fromUniResponse(Uni.createFrom()
                .completionStage(() -> s3.getObject(buildGetRequest(objectKey),
                        AsyncResponseTransformer.toPublisher())),
                response -> Multi.createFrom().safePublisher(AdaptersToFlow.publisher((Publisher<ByteBuffer>) response))
                        .map(S3AsyncClientResource::toBuffer),
                response -> Map.of("Content-Disposition", List.of("attachment;filename=" + objectKey), "Content-Type",
                        List.of(response.response().contentType())));
    }

    @GET
    @Path("/url/signed/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Uni<Response> signedUrl(String objectKey) {

        return Uni.createFrom().item(Response.ok(
            s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(buildGetRequest(objectKey))
                    .build()
            ).url()
        ).build());

    }

    @GET
    public Uni<List<FileObject>> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

        return Uni.createFrom().completionStage(() -> s3.listObjects(listRequest))
                .onItem().transform(result -> toFileItems(result));
    }

    private static Buffer toBuffer(ByteBuffer bytebuffer) {
        byte[] result = new byte[bytebuffer.remaining()];
        bytebuffer.get(result);
        return Buffer.buffer(result);
    }

    private List<FileObject> toFileItems(ListObjectsResponse objects) {
        return objects.contents().stream()
                .map(FileObject::from)
                .sorted(Comparator.comparing(FileObject::getObjectKey))
                .collect(Collectors.toList());
    }

}
