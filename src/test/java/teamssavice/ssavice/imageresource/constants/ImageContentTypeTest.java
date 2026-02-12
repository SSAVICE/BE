package teamssavice.ssavice.imageresource.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import teamssavice.ssavice.global.exception.UnsupportedImageContentTypeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageContentTypeTest {

    @Nested
    @DisplayName("from")
    class From {

        @Test
        @DisplayName("image/jpeg 입력 시 JPEG를 반환한다")
        void JPEG_반환() {
            assertThat(ImageContentType.from("image/jpeg")).isEqualTo(ImageContentType.JPEG);
        }

        @Test
        @DisplayName("image/png 입력 시 PNG를 반환한다")
        void PNG_반환() {
            assertThat(ImageContentType.from("image/png")).isEqualTo(ImageContentType.PNG);
        }

        @Test
        @DisplayName("image/webp 입력 시 WEBP를 반환한다")
        void WEBP_반환() {
            assertThat(ImageContentType.from("image/webp")).isEqualTo(ImageContentType.WEBP);
        }

        @Test
        @DisplayName("대소문자가 섞여있어도 정상 파싱한다")
        void 대소문자_혼합() {
            assertThat(ImageContentType.from("Image/JPEG")).isEqualTo(ImageContentType.JPEG);
        }

        @ParameterizedTest
        @ValueSource(strings = {"text/plain", "application/json", "image/gif", "image/bmp"})
        @DisplayName("지원하지 않는 content type 입력 시 UnsupportedImageContentTypeException을 던진다")
        void 지원하지_않는_타입_예외(String unsupported) {
            assertThatThrownBy(() -> ImageContentType.from(unsupported))
                    .isInstanceOf(UnsupportedImageContentTypeException.class);
        }
    }

    @Nested
    @DisplayName("allowedMimeTypes")
    class AllowedMimeTypes {

        @Test
        @DisplayName("허용된 MIME 타입 목록을 반환한다")
        void 허용_MIME_타입_목록() {
            // when
            String[] allowed = ImageContentType.allowedMimeTypes();

            // then
            assertThat(allowed).containsExactlyInAnyOrder("image/jpeg", "image/png", "image/webp");
        }
    }

    @Nested
    @DisplayName("mimeType / extension")
    class MimeTypeAndExtension {

        @Test
        @DisplayName("JPEG의 mimeType은 image/jpeg이다")
        void JPEG_mimeType() {
            assertThat(ImageContentType.JPEG.mimeType()).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("JPEG의 extension은 .jpg이다")
        void JPEG_extension() {
            assertThat(ImageContentType.JPEG.extension()).isEqualTo(".jpg");
        }

        @Test
        @DisplayName("PNG의 extension은 .png이다")
        void PNG_extension() {
            assertThat(ImageContentType.PNG.extension()).isEqualTo(".png");
        }

        @Test
        @DisplayName("WEBP의 extension은 .webp이다")
        void WEBP_extension() {
            assertThat(ImageContentType.WEBP.extension()).isEqualTo(".webp");
        }
    }
}
