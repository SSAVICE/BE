package teamssavice.ssavice.imageresource.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.imageresource.entity.ImageResource;

import java.util.Optional;

public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {

    public Optional<ImageResource> findByObjectKey(String objectKey);
}
