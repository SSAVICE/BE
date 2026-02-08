package teamssavice.ssavice.imageresource.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageStatus;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImageWriteServiceTest {

    @InjectMocks
    private ImageWriteService imageWriteService;

    @Mock
    private ImageResourceRepository imageResourceRepository;

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("이미지 리소스를 저장하고 반환한다")
        void 이미지_저장_성공() {
            // given
            String targetKey = "profile/origin/1/uuid.jpg";
            String sourceKey = "temp/profile/1/uuid.jpg";
            ImagePath path = ImagePath.profile;
            ImageContentType contentType = ImageContentType.JPEG;

            ImageResource expected = ImageResource.builder()
                    .targetKey(targetKey)
                    .sourceKey(sourceKey)
                    .path(path)
                    .contentType(contentType.mimeType())
                    .build();

            given(imageResourceRepository.save(any(ImageResource.class))).willReturn(expected);

            // when
            ImageResource result = imageWriteService.save(targetKey, sourceKey, path, contentType);

            // then
            assertThat(result.getTargetKey()).isEqualTo(targetKey);
            assertThat(result.getSourceKey()).isEqualTo(sourceKey);
            assertThat(result.getPath()).isEqualTo(path);
            assertThat(result.getContentType()).isEqualTo(contentType.mimeType());
            verify(imageResourceRepository).save(any(ImageResource.class));
        }
    }

    @Nested
    @DisplayName("updateStatusToProcessing")
    class UpdateStatusToProcessing {

        @Test
        @DisplayName("이미지 상태를 PROCESSING으로 변경한다")
        void 상태_PROCESSING_변경_성공() {
            // given
            Long id = 1L;
            ImageResource image = ImageResource.builder()
                    .targetKey("target").sourceKey("source").path(ImagePath.profile).contentType("image/jpeg").build();
            given(imageResourceRepository.findById(id)).willReturn(Optional.of(image));

            // when
            imageWriteService.updateStatusToProcessing(id);

            // then
            assertThat(image.getStatus()).isEqualTo(ImageStatus.PROCESSING);
        }

        @Test
        @DisplayName("존재하지 않는 이미지 ID로 호출하면 EntityNotFoundException을 던진다")
        void 미존재_이미지_예외() {
            // given
            Long id = 999L;
            given(imageResourceRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> imageWriteService.updateStatusToProcessing(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateStatusToDone")
    class UpdateStatusToDone {

        @Test
        @DisplayName("이미지 상태를 DONE으로 변경한다")
        void 상태_DONE_변경_성공() {
            // given
            Long id = 1L;
            ImageResource image = ImageResource.builder()
                    .targetKey("target").sourceKey("source").path(ImagePath.profile).contentType("image/jpeg").build();
            given(imageResourceRepository.findById(id)).willReturn(Optional.of(image));

            // when
            imageWriteService.updateStatusToDone(id);

            // then
            assertThat(image.getStatus()).isEqualTo(ImageStatus.DONE);
        }

        @Test
        @DisplayName("존재하지 않는 이미지 ID로 호출하면 EntityNotFoundException을 던진다")
        void 미존재_이미지_예외() {
            // given
            Long id = 999L;
            given(imageResourceRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> imageWriteService.updateStatusToDone(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateStatusToFailed")
    class UpdateStatusToFailed {

        @Test
        @DisplayName("이미지 상태를 FAILED로 변경한다")
        void 상태_FAILED_변경_성공() {
            // given
            Long id = 1L;
            ImageResource image = ImageResource.builder()
                    .targetKey("target").sourceKey("source").path(ImagePath.profile).contentType("image/jpeg").build();
            given(imageResourceRepository.findById(id)).willReturn(Optional.of(image));

            // when
            imageWriteService.updateStatusToFailed(id);

            // then
            assertThat(image.getStatus()).isEqualTo(ImageStatus.FAILED);
        }

        @Test
        @DisplayName("존재하지 않는 이미지 ID로 호출하면 EntityNotFoundException을 던진다")
        void 미존재_이미지_예외() {
            // given
            Long id = 999L;
            given(imageResourceRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> imageWriteService.updateStatusToFailed(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("changeMetaDataToThumb")
    class ChangeMetaDataToThumb {

        @Test
        @DisplayName("썸네일 키로 메타데이터를 변경한다")
        void 썸네일_메타데이터_변경_성공() {
            // given
            String originKey = "profile/origin/1/image.jpg";
            String thumbKey = "profile/thumbnail/1/image.jpg";
            ImageResource image = ImageResource.builder()
                    .targetKey(originKey).sourceKey("source").path(ImagePath.profile).contentType("image/jpeg").build();
            given(imageResourceRepository.findByTargetKey(originKey)).willReturn(Optional.of(image));

            // when
            imageWriteService.changeMetaDataToThumb(originKey, thumbKey);

            // then
            assertThat(image.getTargetKey()).isEqualTo(thumbKey);
            assertThat(image.getSourceKey()).isEqualTo(originKey);
        }

        @Test
        @DisplayName("이미지를 찾지 못하면 로그만 남기고 예외를 던지지 않는다")
        void 이미지_미존재_시_로그만_남김() {
            // given
            String originKey = "nonexistent";
            String thumbKey = "thumb";
            given(imageResourceRepository.findByTargetKey(originKey)).willReturn(Optional.empty());

            // when & then (예외 없이 정상 종료)
            imageWriteService.changeMetaDataToThumb(originKey, thumbKey);
        }
    }
}
