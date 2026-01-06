package teamssavice.ssavice.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
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
                .tagging("is_active=false")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(properties.putExpirationSecond()))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
        return ImageModel.PutPresignedUrl.from(presigned.url().toString(), objectKey);
    }

    public void updateIsActiveTag(String objectKey, boolean isActive) {
        try {
            PutObjectTaggingRequest request = PutObjectTaggingRequest.builder()
                    .bucket(properties.bucket())
                    .key(objectKey)
                    .tagging(Tagging.builder()
                            .tagSet(Tag.builder().key("is_active").value(Boolean.toString(isActive)).build())
                            .build())
                    .build();

            s3Client.putObjectTagging(request);
        } catch (S3Exception ignored) {
        }
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
}
