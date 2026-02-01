package teamssavice.ssavice.imageresource.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.imageresource.entity.ImageResource;

public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {

    Optional<ImageResource> findByTargetKey(String objectKey);

    @Query("SELECT i FROM ImageResource i WHERE i.sourceKey IN :sourceKeys")
    List<ImageResource> findAllBySourceKeyIn(@Param("sourceKeys") List<String> sourceKeys);

    Optional<ImageResource> findBySourceKey(String tempKey);
}
