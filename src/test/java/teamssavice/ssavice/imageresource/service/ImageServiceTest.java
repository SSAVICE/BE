package teamssavice.ssavice.imageresource.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageVariant;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.dto.ImageCommand;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.s3.S3ObjectKeyGenerator;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.event.ServiceItemThumbnailUpdatedEvent;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3ObjectKeyGenerator s3ObjectKeyGenerator;

    @Mock
    private ImageWriteService imageWriteService;

    @Mock
    private ImageReadService imageReadService;

    @Mock
    private ServiceItemReadService serviceItemReadService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Nested
    @DisplayName("updateImage")
    class UpdateImage {

        @Test
        @DisplayName("이미지 업로드 시 tempKey와 originKey를 생성하고 presigned URL을 반환한다")
        void 이미지_업로드_성공() {
            // given
            Long id = 1L;
            ImagePath path = ImagePath.profile;
            ImageContentType contentType = ImageContentType.JPEG;
            String tempKey = "temp/profile/1/uuid.jpg";
            String originKey = "profile/origin/1/uuid.jpg";
            ImageModel.PutPresignedUrl expected = ImageModel.PutPresignedUrl.from("https://presigned-url", tempKey);
            ImageResource savedEntity = ImageResource.builder()
                    .targetKey(originKey).sourceKey(tempKey).path(path).contentType(contentType.mimeType()).build();

            given(s3ObjectKeyGenerator.tempGenerator(path, id, contentType)).willReturn(tempKey);
            given(s3ObjectKeyGenerator.originGenerator(path, ImageVariant.origin, id, contentType)).willReturn(originKey);
            given(imageWriteService.save(originKey, tempKey, path, contentType)).willReturn(savedEntity);
            given(s3Service.createPutPresignedUrl(tempKey, contentType)).willReturn(expected);

            // when
            ImageModel.PutPresignedUrl result = imageService.updateImage(id, path, contentType);

            // then
            assertThat(result.uploadUrl()).isEqualTo("https://presigned-url");
            assertThat(result.objectKey()).isEqualTo(tempKey);
            verify(imageWriteService).save(originKey, tempKey, path, contentType);
        }
    }

    @Nested
    @DisplayName("updateImages")
    class UpdateImages {

        @Test
        @DisplayName("여러 이미지를 업로드하면 각각의 presigned URL 리스트를 반환한다")
        void 여러_이미지_업로드_성공() {
            // given
            ImageCommand.PutPresignedUrls command = ImageCommand.PutPresignedUrls.builder()
                    .companyId(1L)
                    .path(ImagePath.company)
                    .add(List.of(ImageContentType.JPEG, ImageContentType.PNG))
                    .build();

            given(s3ObjectKeyGenerator.tempGenerator(any(), anyLong(), any())).willReturn("tempKey");
            given(s3ObjectKeyGenerator.originGenerator(any(), any(), anyLong(), any())).willReturn("originKey");
            given(imageWriteService.save(anyString(), anyString(), any(), any()))
                    .willReturn(ImageResource.builder().targetKey("t").sourceKey("s").path(ImagePath.company).contentType("image/jpeg").build());
            given(s3Service.createPutPresignedUrl(anyString(), any()))
                    .willReturn(ImageModel.PutPresignedUrl.from("url1", "key1"))
                    .willReturn(ImageModel.PutPresignedUrl.from("url2", "key2"));

            // when
            List<ImageModel.PutPresignedUrl> result = imageService.updateImages(command);

            // then
            assertThat(result).hasSize(2);
            verify(imageWriteService, times(2)).save(anyString(), anyString(), any(), any());
        }

        @Test
        @DisplayName("빈 contentType 리스트를 전달하면 빈 리스트를 반환한다")
        void 빈_리스트_요청_시_빈_결과() {
            // given
            ImageCommand.PutPresignedUrls command = ImageCommand.PutPresignedUrls.builder()
                    .companyId(1L)
                    .path(ImagePath.company)
                    .add(List.of())
                    .build();

            // when
            List<ImageModel.PutPresignedUrl> result = imageService.updateImages(command);

            // then
            assertThat(result).isEmpty();
            verify(imageWriteService, never()).save(anyString(), anyString(), any(), any());
        }
    }

    @Nested
    @DisplayName("deActivateImages")
    class DeActivateImages {

        @Test
        @DisplayName("이미지 ID 리스트로 이미지를 비활성화한다")
        void 이미지_비활성화_성공() {
            // given
            List<Long> imageIds = List.of(1L, 2L);
            ImageResource image1 = ImageResource.builder().targetKey("k1").sourceKey("s1").path(ImagePath.profile).contentType("image/jpeg").build();
            ImageResource image2 = ImageResource.builder().targetKey("k2").sourceKey("s2").path(ImagePath.profile).contentType("image/png").build();
            image1.activate();
            image2.activate();
            given(imageReadService.findAllById(imageIds)).willReturn(List.of(image1, image2));

            // when
            imageService.deActivateImages(imageIds);

            // then
            assertThat(image1.isActive()).isFalse();
            assertThat(image2.isActive()).isFalse();
        }

        @Test
        @DisplayName("null을 전달하면 아무 작업도 하지 않는다")
        void null_전달_시_무시() {
            // when
            imageService.deActivateImages(null);

            // then
            verify(imageReadService, never()).findAllById(any());
        }

        @Test
        @DisplayName("빈 리스트를 전달하면 아무 작업도 하지 않는다")
        void 빈_리스트_전달_시_무시() {
            // when
            imageService.deActivateImages(List.of());

            // then
            verify(imageReadService, never()).findAllById(any());
        }
    }

    @Nested
    @DisplayName("handleImageMove")
    class HandleImageMove {

        @Test
        @DisplayName("S3 복사 성공 시 상태를 DONE으로 변경한다")
        void S3_복사_성공() {
            // given
            Long imageResourceId = 1L;
            String sourceKey = "temp/image.jpg";
            String targetKey = "profile/origin/1/image.jpg";
            ImageContentType contentType = ImageContentType.JPEG;

            // when
            imageService.handleImageMove(imageResourceId, sourceKey, targetKey, contentType);

            // then
            verify(s3Service).copyObject(sourceKey, targetKey, contentType);
            verify(imageWriteService).updateStatusToDone(imageResourceId);
            verify(imageWriteService, never()).updateStatusToFailed(any());
        }

        @Test
        @DisplayName("S3 복사 실패 시 상태를 FAILED로 변경하고 예외를 다시 던진다")
        void S3_복사_실패() {
            // given
            Long imageResourceId = 1L;
            String sourceKey = "temp/image.jpg";
            String targetKey = "profile/origin/1/image.jpg";
            ImageContentType contentType = ImageContentType.JPEG;

            S3Exception s3Exception = (S3Exception) S3Exception.builder()
                    .message("S3 copy failed")
                    .build();

            doThrow(s3Exception)
                    .when(s3Service).copyObject(sourceKey, targetKey, contentType);

            // when & then
            assertThatThrownBy(() -> imageService.handleImageMove(imageResourceId, sourceKey, targetKey, contentType))
                    .isInstanceOf(S3Exception.class);

            verify(imageWriteService).updateStatusToFailed(imageResourceId);
            verify(imageWriteService, never()).updateStatusToDone(any());
        }
    }

    @Nested
    @DisplayName("changeMetaDataToThumbnail")
    class ChangeMetaDataToThumbnail {

        private final String originKey = "profile/origin/1/image.jpg";
        private final String thumbKey = "profile/thumbnail/1/image.jpg";
        private ServiceItem serviceItem;

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
            serviceItem = ServiceItemFixture.base(null);
            ReflectionTestUtils.setField(serviceItem, "id", 1L);
            given(serviceItemReadService.findByThumbnailImageResourceSourceKey(originKey)).willReturn(serviceItem);
        }

        @Test
        @DisplayName("썸네일 메타데이터를 변경한다")
        void 썸네일_메타데이터_변경_성공() {
            // when
            imageService.changeMetaDataToThumbnail(originKey, thumbKey);

            // then
            verify(imageWriteService).changeMetaDataToThumb(originKey, thumbKey);
        }

        @Test
        @DisplayName("썸네일 메타데이터 변경 시 썸네일 업데이트 이벤트가 발행된다")
        void 썸네일_이벤트_발행() {
            // when
            imageService.changeMetaDataToThumbnail(originKey, thumbKey);

            // then
            ArgumentCaptor<ServiceItemThumbnailUpdatedEvent> captor =
                    ArgumentCaptor.forClass(ServiceItemThumbnailUpdatedEvent.class);
            verify(applicationEventPublisher).publishEvent(captor.capture());
            assertThat(captor.getValue().serviceItemId()).isEqualTo(1L);
            assertThat(captor.getValue().thumbKey()).isEqualTo(thumbKey);
        }
    }
}
