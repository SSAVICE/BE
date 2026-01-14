package teamssavice.ssavice.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import teamssavice.ssavice.global.property.S3Properties;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final S3Properties properties;

    public ImageModel.PutPresignedUrl createPutPresignedUrl(String objectKey, ImageContentType contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .contentType(contentType.mimeType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(properties.putExpirationSecond()))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return ImageModel.PutPresignedUrl.from(presigned.url().toString(), objectKey);
    }

    public String generateGetPresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();

        GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(properties.getExpirationMin()))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presign).url().toString();
    }

    public void moveObject(String sourceKey, String targetKey, ImageContentType contentType) {
        CopyObjectRequest request = CopyObjectRequest.builder()
                .sourceBucket(properties.bucket())
                .sourceKey(sourceKey)
                .destinationBucket(properties.bucket())
                .destinationKey(targetKey)
                .contentType(contentType.mimeType())
                .build();

        s3Client.copyObject(request);

        deleteObject(sourceKey);
    }

    public void deleteObject(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.bucket())
                .key(objectKey)
                .build();

        s3Client.deleteObject(request);
    }
}
