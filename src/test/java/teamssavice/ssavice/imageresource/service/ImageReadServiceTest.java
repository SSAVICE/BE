package teamssavice.ssavice.imageresource.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ImageReadServiceTest {

    @InjectMocks
    private ImageReadService imageReadService;

    @Mock
    private ImageResourceRepository imageResourceRepository;

    private ImageResource createImageResource(String targetKey, String sourceKey) {
        return ImageResource.builder()
                .targetKey(targetKey)
                .sourceKey(sourceKey)
                .path(ImagePath.profile)
                .contentType("image/jpeg")
                .build();
    }

    @Nested
    @DisplayName("findBySourceKey")
    class FindBySourceKey {

        @Test
        @DisplayName("sourceKey로 이미지를 조회한다")
        void sourceKey로_조회_성공() {
            // given
            String sourceKey = "temp/profile/1/uuid.jpg";
            ImageResource image = createImageResource("target", sourceKey);
            given(imageResourceRepository.findBySourceKey(sourceKey)).willReturn(Optional.of(image));

            // when
            ImageResource result = imageReadService.findBySourceKey(sourceKey);

            // then
            assertThat(result.getSourceKey()).isEqualTo(sourceKey);
        }

        @Test
        @DisplayName("존재하지 않는 sourceKey로 조회하면 EntityNotFoundException을 던진다")
        void sourceKey_미존재_시_예외() {
            // given
            String sourceKey = "nonexistent";
            given(imageResourceRepository.findBySourceKey(sourceKey)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> imageReadService.findBySourceKey(sourceKey))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAllBySourceKeyIn")
    class FindAllBySourceKeyIn {

        @Test
        @DisplayName("여러 sourceKey로 이미지 목록을 조회한다")
        void 여러_sourceKey로_조회_성공() {
            // given
            List<String> keys = List.of("key1", "key2");
            List<ImageResource> images = List.of(
                    createImageResource("t1", "key1"),
                    createImageResource("t2", "key2")
            );
            given(imageResourceRepository.findAllBySourceKeyIn(keys)).willReturn(images);

            // when
            List<ImageResource> result = imageReadService.findAllBySourceKeyIn(keys);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 리스트를 전달하면 빈 결과를 반환한다")
        void 빈_리스트_조회() {
            // given
            given(imageResourceRepository.findAllBySourceKeyIn(List.of())).willReturn(List.of());

            // when
            List<ImageResource> result = imageReadService.findAllBySourceKeyIn(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllById")
    class FindAllById {

        @Test
        @DisplayName("여러 ID로 이미지 목록을 조회한다")
        void ID_목록으로_조회_성공() {
            // given
            List<Long> ids = List.of(1L, 2L);
            List<ImageResource> images = List.of(
                    createImageResource("t1", "s1"),
                    createImageResource("t2", "s2")
            );
            given(imageResourceRepository.findAllById(ids)).willReturn(images);

            // when
            List<ImageResource> result = imageReadService.findAllById(ids);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findByTargetKey")
    class FindByTargetKey {

        @Test
        @DisplayName("targetKey로 이미지를 조회한다")
        void targetKey로_조회_성공() {
            // given
            String targetKey = "profile/origin/1/uuid.jpg";
            ImageResource image = createImageResource(targetKey, "source");
            given(imageResourceRepository.findByTargetKey(targetKey)).willReturn(Optional.of(image));

            // when
            ImageResource result = imageReadService.findByTargetKey(targetKey);

            // then
            assertThat(result.getTargetKey()).isEqualTo(targetKey);
        }

        @Test
        @DisplayName("존재하지 않는 targetKey로 조회하면 EntityNotFoundException을 던진다")
        void targetKey_미존재_시_예외() {
            // given
            String targetKey = "nonexistent";
            given(imageResourceRepository.findByTargetKey(targetKey)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> imageReadService.findByTargetKey(targetKey))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("ID로 이미지를 조회한다")
        void ID로_조회_성공() {
            // given
            Long id = 1L;
            ImageResource image = createImageResource("target", "source");
            given(imageResourceRepository.findById(id)).willReturn(Optional.of(image));

            // when
            ImageResource result = imageReadService.findById(id);

            // then
            assertThat(result.getTargetKey()).isEqualTo("target");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 EntityNotFoundException을 던진다")
        void ID_미존재_시_예외() {
            // given
            Long id = 999L;
            given(imageResourceRepository.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> imageReadService.findById(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
