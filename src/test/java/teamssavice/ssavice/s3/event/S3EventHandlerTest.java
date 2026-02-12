package teamssavice.ssavice.s3.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.s3.S3Service;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class S3EventHandlerTest {

    @InjectMocks
    private S3EventHandler s3EventHandler;

    @Mock
    private ImageService imageService;

    @Mock
    private S3Service s3Service;

    @Nested
    @DisplayName("moveImageEventListener")
    class MoveImageEventListener {

        @Test
        @DisplayName("Move 이벤트 수신 시 이미지 이동을 처리한다")
        void 이미지_이동_이벤트_처리_성공() {
            // given
            S3EventDto.Move event = S3EventDto.Move.builder()
                    .imageResourceId(1L)
                    .sourceKey("temp/image.jpg")
                    .targetKey("profile/origin/1/image.jpg")
                    .contentType(ImageContentType.JPEG)
                    .build();

            // when
            s3EventHandler.moveImageEventListener(event);

            // then
            verify(imageService).handleImageMove(1L, "temp/image.jpg", "profile/origin/1/image.jpg", ImageContentType.JPEG);
        }
    }

    @Nested
    @DisplayName("deleteImageEventListener")
    class DeleteImageEventListener {

        @Test
        @DisplayName("Delete 이벤트 수신 시 S3 객체를 삭제한다")
        void S3_객체_삭제_이벤트_처리() {
            // given
            S3EventDto.Delete event = S3EventDto.Delete.builder()
                    .targetKey("profile/origin/1/image.jpg")
                    .build();

            // when
            s3EventHandler.deleteImageEventListener(event);

            // then
            verify(s3Service).deleteObject("profile/origin/1/image.jpg");
        }
    }
}
