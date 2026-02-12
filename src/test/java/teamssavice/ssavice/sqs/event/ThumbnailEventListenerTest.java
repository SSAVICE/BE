package teamssavice.ssavice.sqs.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.imageresource.service.ImageService;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThumbnailEventListenerTest {

    @InjectMocks
    private ThumbnailEventListener thumbnailEventListener;

    @Mock
    private ImageService imageService;

    @Test
    @DisplayName("DONE 상태의 썸네일 이벤트를 수신하면 메타데이터를 변경한다")
    void DONE_상태_이벤트_처리_성공() {
        // given
        SqsEventDto.ThumbnailEvent event = SqsEventDto.ThumbnailEvent.builder()
                .status("DONE")
                .bucket("test-bucket")
                .originKey("profile/origin/1/image.jpg")
                .thumbKey("profile/thumbnail/1/image.jpg")
                .root("profile")
                .ownerId(1L)
                .ts(Instant.now())
                .build();

        // when
        thumbnailEventListener.alarmListener(event);

        // then
        verify(imageService).changeMetaDataToThumbnail("profile/origin/1/image.jpg", "profile/thumbnail/1/image.jpg");
    }

    @Test
    @DisplayName("FAILED 상태의 썸네일 이벤트를 수신하면 메타데이터를 변경하지 않는다")
    void FAILED_상태_이벤트_무시() {
        // given
        SqsEventDto.ThumbnailEvent event = SqsEventDto.ThumbnailEvent.builder()
                .status("FAILED")
                .bucket("test-bucket")
                .originKey("profile/origin/1/image.jpg")
                .thumbKey("profile/thumbnail/1/image.jpg")
                .root("profile")
                .ownerId(1L)
                .ts(Instant.now())
                .build();

        // when
        thumbnailEventListener.alarmListener(event);

        // then
        verify(imageService, never()).changeMetaDataToThumbnail(anyString(), anyString());
    }

    @Test
    @DisplayName("알 수 없는 상태의 이벤트를 수신하면 메타데이터를 변경하지 않는다")
    void 알수없는_상태_이벤트_무시() {
        // given
        SqsEventDto.ThumbnailEvent event = SqsEventDto.ThumbnailEvent.builder()
                .status("UNKNOWN")
                .bucket("test-bucket")
                .originKey("origin")
                .thumbKey("thumb")
                .root("root")
                .ownerId(1L)
                .ts(Instant.now())
                .build();

        // when
        thumbnailEventListener.alarmListener(event);

        // then
        verify(imageService, never()).changeMetaDataToThumbnail(anyString(), anyString());
    }
}
