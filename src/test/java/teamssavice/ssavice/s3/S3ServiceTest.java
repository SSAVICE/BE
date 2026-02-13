package teamssavice.ssavice.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.global.exception.ImageSizeException;
import teamssavice.ssavice.global.property.S3Properties;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.dto.S3Command;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @InjectMocks
    private S3Service s3Service;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties properties;

    @Nested
    @DisplayName("createPutPresignedUrl")
    class CreatePutPresignedUrl {

        @Test
        @DisplayName("PUT presigned URL을 생성하여 반환한다")
        void PUT_presigned_URL_생성_성공() throws MalformedURLException {
            // given
            String objectKey = "temp/profile/1/uuid.jpg";
            ImageContentType contentType = ImageContentType.JPEG;
            URL presignedUrl = URI.create("https://s3.amazonaws.com/bucket/temp/profile/1/uuid.jpg").toURL();

            given(properties.bucket()).willReturn("test-bucket");
            given(properties.putExpirationSecond()).willReturn(120L);

            PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
            given(presignedRequest.url()).willReturn(presignedUrl);
            given(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).willReturn(presignedRequest);

            // when
            ImageModel.PutPresignedUrl result = s3Service.createPutPresignedUrl(objectKey, contentType);

            // then
            assertThat(result.uploadUrl()).contains("s3.amazonaws.com");
            assertThat(result.objectKey()).isEqualTo(objectKey);
        }
    }

    @Nested
    @DisplayName("generateGetPresignedUrl")
    class GenerateGetPresignedUrl {

        @Test
        @DisplayName("GET presigned URL을 생성하여 반환한다")
        void GET_presigned_URL_생성_성공() throws MalformedURLException {
            // given
            String objectKey = "profile/origin/1/uuid.jpg";
            URL presignedUrl = URI.create("https://s3.amazonaws.com/bucket/profile/origin/1/uuid.jpg").toURL();

            given(properties.bucket()).willReturn("test-bucket");
            given(properties.getExpirationMin()).willReturn(5L);

            PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
            given(presignedRequest.url()).willReturn(presignedUrl);
            given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(presignedRequest);

            // when
            String result = s3Service.generateGetPresignedUrl(objectKey);

            // then
            assertThat(result).contains("s3.amazonaws.com");
        }
    }

    @Nested
    @DisplayName("copyObject")
    class CopyObject {

        @Test
        @DisplayName("S3 객체를 복사한다")
        void S3_객체_복사_성공() {
            // given
            String sourceKey = "temp/image.jpg";
            String targetKey = "profile/origin/1/image.jpg";
            ImageContentType contentType = ImageContentType.JPEG;

            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.copyObject(any(CopyObjectRequest.class)))
                    .willReturn(CopyObjectResponse.builder().build());

            // when
            s3Service.copyObject(sourceKey, targetKey, contentType);

            // then
            verify(s3Client).copyObject(any(CopyObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("deleteObject")
    class DeleteObject {

        @Test
        @DisplayName("S3 객체를 삭제한다")
        void S3_객체_삭제_성공() {
            // given
            String objectKey = "temp/image.jpg";
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .willReturn(DeleteObjectResponse.builder().build());

            // when
            s3Service.deleteObject(objectKey);

            // then
            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        @DisplayName("이미 삭제된 객체를 삭제해도 예외를 던지지 않는다")
        void 이미_삭제된_객체_멱등() {
            // given
            String objectKey = "nonexistent.jpg";
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .willThrow(NoSuchKeyException.builder().message("Not found").build());

            // when & then (예외 없이 정상 종료)
            s3Service.deleteObject(objectKey);
        }
    }

    @Nested
    @DisplayName("head")
    class Head {

        @Test
        @DisplayName("S3 객체의 메타데이터를 조회한다")
        void 메타데이터_조회_성공() {
            // given
            String key = "temp/image.jpg";
            HeadObjectResponse response = HeadObjectResponse.builder().contentLength(1024L).build();
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(response);

            // when
            HeadObjectResponse result = s3Service.head(key);

            // then
            assertThat(result.contentLength()).isEqualTo(1024L);
        }

        @Test
        @DisplayName("존재하지 않는 객체를 조회하면 EntityNotFoundException을 던진다")
        void 미존재_객체_조회_예외() {
            // given
            String key = "nonexistent.jpg";
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.headObject(any(HeadObjectRequest.class)))
                    .willThrow(NoSuchKeyException.builder().message("Not found").build());

            // when & then
            assertThatThrownBy(() -> s3Service.head(key))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateMaxSizeOrDelete")
    class ValidateMaxSizeOrDelete {

        @Test
        @DisplayName("이미지 용량이 제한 내이면 정상 통과한다")
        void 용량_제한_내_통과() {
            // given
            HeadObjectResponse head = HeadObjectResponse.builder().contentLength(1024L).build();
            org.springframework.test.util.ReflectionTestUtils.setField(s3Service, "maxUploadBytes", 5242880L);

            // when & then (예외 없이 정상 종료)
            s3Service.validateMaxSizeOrDelete(head, "key");
        }

        @Test
        @DisplayName("이미지 용량이 초과하면 삭제 후 ImageSizeException을 던진다")
        void 용량_초과_시_삭제_후_예외() {
            // given
            HeadObjectResponse head = HeadObjectResponse.builder().contentLength(10_000_000L).build();
            org.springframework.test.util.ReflectionTestUtils.setField(s3Service, "maxUploadBytes", 5242880L);
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .willReturn(DeleteObjectResponse.builder().build());

            // when & then
            assertThatThrownBy(() -> s3Service.validateMaxSizeOrDelete(head, "key"))
                    .isInstanceOf(ImageSizeException.class);

            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("validateAllTempImagesOrDeleteAll")
    class ValidateAllTempImagesOrDeleteAll {

        @Test
        @DisplayName("모든 이미지가 유효하면 정상 통과한다")
        void 모든_이미지_유효() {
            // given
            List<String> keys = List.of("key1", "key2");
            S3Command.ValidateKeys command = S3Command.ValidateKeys.builder().objectKeys(keys).build();
            HeadObjectResponse head = HeadObjectResponse.builder().contentLength(1024L).build();

            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(head);
            org.springframework.test.util.ReflectionTestUtils.setField(s3Service, "maxUploadBytes", 5242880L);

            // when & then (예외 없이 정상 종료)
            s3Service.validateAllTempImagesOrDeleteAll(command);
        }

        @Test
        @DisplayName("하나라도 유효하지 않으면 전체 삭제 후 예외를 던진다")
        void 하나_실패_시_전체_삭제() {
            // given
            List<String> keys = List.of("key1", "key2");
            S3Command.ValidateKeys command = S3Command.ValidateKeys.builder().objectKeys(keys).build();

            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.headObject(any(HeadObjectRequest.class)))
                    .willThrow(NoSuchKeyException.builder().message("Not found").build());

            // when & then
            assertThatThrownBy(() -> s3Service.validateAllTempImagesOrDeleteAll(command))
                    .isInstanceOf(EntityNotFoundException.class);

            // 전체 삭제 검증 (key1, key2 모두 삭제)
            verify(s3Client, atLeast(1)).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        @DisplayName("빈 키 리스트인 경우 정상 통과한다")
        void 빈_키_리스트_통과() {
            // given
            List<String> keys = List.of();
            S3Command.ValidateKeys command = S3Command.ValidateKeys.builder().objectKeys(keys).build();

            // when & then (예외 없이 정상 종료)
            s3Service.validateAllTempImagesOrDeleteAll(command);

            // headObject 호출 없음
            verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
        }

        @Test
        @DisplayName("두 번째 키에서 실패하는 경우 전체 삭제 후 예외를 던진다")
        void 두_번째_키_실패_시_전체_삭제() {
            // given
            List<String> keys = List.of("key1", "key2", "key3");
            S3Command.ValidateKeys command = S3Command.ValidateKeys.builder().objectKeys(keys).build();

            HeadObjectResponse validHead = HeadObjectResponse.builder().contentLength(1024L).build();
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.headObject(any(HeadObjectRequest.class)))
                    .willReturn(validHead) // 첫 번째 키: 성공
                    .willThrow(NoSuchKeyException.builder().message("Not found").build()); // 두 번째 키: 실패
            org.springframework.test.util.ReflectionTestUtils.setField(s3Service, "maxUploadBytes", 5242880L);

            // when & then
            assertThatThrownBy(() -> s3Service.validateAllTempImagesOrDeleteAll(command))
                    .isInstanceOf(EntityNotFoundException.class);

            // 전체 삭제 검증 (key1, key2, key3 모두 삭제)
            verify(s3Client, times(3)).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        @DisplayName("이미지 사이즈 초과로 실패하는 경우 전체 삭제 후 ImageSizeException을 던진다")
        void 사이즈_초과_시_전체_삭제() {
            // given
            List<String> keys = List.of("key1", "key2");
            S3Command.ValidateKeys command = S3Command.ValidateKeys.builder().objectKeys(keys).build();

            HeadObjectResponse oversizedHead = HeadObjectResponse.builder().contentLength(10_000_000L).build();
            given(properties.bucket()).willReturn("test-bucket");
            given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(oversizedHead);
            given(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                    .willReturn(DeleteObjectResponse.builder().build());
            org.springframework.test.util.ReflectionTestUtils.setField(s3Service, "maxUploadBytes", 5242880L);

            // when & then
            assertThatThrownBy(() -> s3Service.validateAllTempImagesOrDeleteAll(command))
                    .isInstanceOf(ImageSizeException.class);

            // 전체 삭제 검증 (초과한 key1 삭제 + 전체 key1, key2 삭제 = 총 3회)
            verify(s3Client, times(3)).deleteObject(any(DeleteObjectRequest.class));
        }
    }
}
