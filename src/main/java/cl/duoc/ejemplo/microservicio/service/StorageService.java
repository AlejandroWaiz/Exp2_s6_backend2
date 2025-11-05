package cl.duoc.ejemplo.microservicio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client s3; // lo autoconfigura spring-cloud-aws-starter-s3

    @Value("${app.s3.bucket}")
    private String bucket;

    public void upload(String folder, String filename, byte[] content) {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(folder + "/" + filename)
                        .build(),
                RequestBody.fromBytes(content)
        );
    }

    public byte[] download(String folder, String filename) {
        return s3.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(folder + "/" + filename)
                        .build()
        ).asByteArray();
    }

    public void delete(String folder, String filename) {
        s3.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(folder + "/" + filename)
                        .build()
        );
    }
}
