package teamssavice.ssavice.s3;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.global.exception.ImageSizeException;
import teamssavice.ssavice.global.property.S3Properties;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.dto.S3Command;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final S3Properties properties;
    @Value("${image.upload.max-bytes}")
    private long maxUploadBytes;

    public ImageModel.PutPresignedUrl createPutPresignedUrl(String objectKey,
        ImageContentType contentType) {
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
        copyObject(sourceKey, targetKey, contentType);
        deleteObject(sourceKey);
    }

    public void copyObject(String sourceKey, String targetKey, ImageContentType contentType) {
        CopyObjectRequest request = CopyObjectRequest.builder()
            .sourceBucket(properties.bucket())
            .sourceKey(sourceKey)
            .destinationBucket(properties.bucket())
            .destinationKey(targetKey)
            .contentType(contentType.mimeType())
            .build();

        s3Client.copyObject(request);
    }

    public void deleteObject(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(properties.bucket())
            .key(objectKey)
            .build();

        s3Client.deleteObject(request);
    }

    public void validateTempImageOrDelete(String key) {
        try {
            validateTempImage(key); // head + size check
        } catch (ImageSizeException e) {
            // 정책: 사이즈 초과면 temp 정리
            deleteObject(key);
            throw e;
        }
    }

    public void validateTempImage(String key) {
        HeadObjectResponse head = head(key);
        validateMaxSize(head);
    }

    public HeadObjectResponse head(String key) {
        try {
            return s3Client.headObject(HeadObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .build());
        } catch (NoSuchKeyException e) {
            throw new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    public void validateMaxSize(HeadObjectResponse head) {
        if (head.contentLength() > maxUploadBytes) {
            throw new ImageSizeException(
                ErrorCode.IMAGE_TOO_LARGE,
                head.contentLength(),
                maxUploadBytes
            );
        }
    }

    public void validateAllTempImagesOrDeleteAll(S3Command.ValidateKeys command) {
        List<String> keys = command.objectKeys();
        // 1) 전부 검증
        try {
            for (String key : keys) {
                HeadObjectResponse head = head(key);     // 없으면 EntityNotFoundException
                validateMaxSize(head);                   // 크면 ImageSizeException
            }
        } catch (RuntimeException e) {
            // 2) 하나라도 실패하면 전부 삭제
            deleteAllObject(keys);
            throw e;
        }
    }

    private void deleteAllObject(List<String> keys) {
        for (String key : keys) {
            deleteObject(key);
        }
    }


}
