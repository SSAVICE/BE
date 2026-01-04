package teamssavice.ssavice.image.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.image.entity.ImageResource;

public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {
}
