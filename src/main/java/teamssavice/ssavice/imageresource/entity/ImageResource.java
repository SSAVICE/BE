package teamssavice.ssavice.imageresource.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageStatus;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class ImageResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String targetKey;

    @NotNull
    @Column(nullable = false, unique = true)
    private String sourceKey;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ImagePath path;

    @NotNull
    @Column(nullable = false)
    private String contentType;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = false;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private ImageStatus status = ImageStatus.PENDING;

    public void deActivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    /**
     * 썸네일 생성 완료 → 메타데이터 확정
     */
    public void confirmAsThumbnail(String newObjectKey) {
        this.objectKey = newObjectKey;
    }
}
