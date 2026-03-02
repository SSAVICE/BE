package teamssavice.ssavice.kafka.event;

import lombok.Builder;
import teamssavice.ssavice.kafka.MessageType;
import teamssavice.ssavice.kafka.RoomType;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;

public class KafkaEvent {

    @Builder
    public record Join(
            MessageType messageType,
            RoomType roomType,
            String roomId,
            String roomName,
            Long sender,
            String message,
            LocalDateTime createdAt
    ) {
        public static KafkaEvent.Join createEvent(ServiceItem item, Long sender) {
            return Join.builder()
                    .messageType(MessageType.CREATE)
                    .roomType(RoomType.GROUP)
                    .roomId(String.valueOf(item.getId()))
                    .roomName(item.getTitle())
                    .sender(sender)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        public static KafkaEvent.Join joinEvent(ServiceItem item, Long sender) {
            return Join.builder()
                    .messageType(MessageType.JOIN)
                    .roomType(RoomType.GROUP)
                    .roomId(String.valueOf(item.getId()))
                    .sender(sender)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        public static KafkaEvent.Join leaveEvent(ServiceItem item, Long sender) {
            return Join.builder()
                    .messageType(MessageType.LEAVE)
                    .roomType(RoomType.GROUP)
                    .roomId(String.valueOf(item.getId()))
                    .sender(sender)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }
}
