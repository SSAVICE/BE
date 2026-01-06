package teamssavice.ssavice.imageresource.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.imageresource.constants.ImagePath;

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
    private String objectKey;

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

    public void deActivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}
