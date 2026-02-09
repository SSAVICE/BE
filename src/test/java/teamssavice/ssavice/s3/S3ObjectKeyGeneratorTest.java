package teamssavice.ssavice.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageVariant;

import static org.assertj.core.api.Assertions.assertThat;

class S3ObjectKeyGeneratorTest {

    private final S3ObjectKeyGenerator generator = new S3ObjectKeyGenerator();

    @Test
    @DisplayName("temp 키는 temp/{path}/{id}/{uuid}.{ext} 형식으로 생성된다")
    void tempKey_형식_검증() {
        // given
        ImagePath path = ImagePath.profile;
        Long id = 1L;
        ImageContentType contentType = ImageContentType.JPEG;

        // when
        String result = generator.tempGenerator(path, id, contentType);

        // then
        assertThat(result).startsWith("temp/profile/1/");
        assertThat(result).endsWith(".jpg");
    }

    @Test
    @DisplayName("origin 키는 {path}/{variant}/{userId}/{uuid}.{ext} 형식으로 생성된다")
    void originKey_형식_검증() {
        // given
        ImagePath path = ImagePath.company;
        ImageVariant variant = ImageVariant.origin;
        Long userId = 5L;
        ImageContentType contentType = ImageContentType.PNG;

        // when
        String result = generator.originGenerator(path, variant, userId, contentType);

        // then
        assertThat(result).startsWith("company/origin/5/");
        assertThat(result).endsWith(".png");
    }

    @Test
    @DisplayName("동일한 파라미터로 호출해도 UUID가 달라 매번 다른 키를 생성한다")
    void 매번_다른_키_생성() {
        // given
        ImagePath path = ImagePath.profile;
        Long id = 1L;
        ImageContentType contentType = ImageContentType.JPEG;

        // when
        String key1 = generator.tempGenerator(path, id, contentType);
        String key2 = generator.tempGenerator(path, id, contentType);

        // then
        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    @DisplayName("WEBP 확장자를 가진 키를 생성한다")
    void WEBP_확장자_키_생성() {
        // given
        ImageContentType contentType = ImageContentType.WEBP;

        // when
        String result = generator.tempGenerator(ImagePath.serviceItem, 3L, contentType);

        // then
        assertThat(result).startsWith("temp/serviceItem/3/");
        assertThat(result).endsWith(".webp");
    }
}
