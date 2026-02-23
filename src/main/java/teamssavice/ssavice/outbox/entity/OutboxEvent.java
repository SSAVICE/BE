package teamssavice.ssavice.outbox.entity;

import jakarta.persistence.*;
import lombok.*;
import software.amazon.awssdk.annotations.NotNull;
import teamssavice.ssavice.outbox.constants.EventType;

import java.time.LocalDateTime;

@Entity
// 지금은 이렇게 걸어놨지만 어차피 인덱스는 나중에 DB에서 관리할거임 - DB에서 걸면 삭제해도 OK
@Table(indexes = {
        @Index(name = "idx_outbox_published", columnList = "published, createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지금은 서비스 아이템만 아웃박스 패턴을 쓰고 있으니까  type 가 필요없지만 만약 새로운 엔티티에 대해서도 걸어야된다면 Type 을 추가해주어야 한다.
//    @Column(nullable = false)
//    private String aggregateType;

    @NotNull
    @Column(nullable = false)
    private Long aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Builder.Default
    @Column(nullable = false)
    private boolean published = false;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public void markPublished() {
        this.published = true;
    }
}