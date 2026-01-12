package teamssavice.ssavice.imageresource.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.imageresource.entity.ImageResource;

import java.util.List;
import java.util.Optional;

public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {

    Optional<ImageResource> findByObjectKey(String objectKey);

    @Query("SELECT i FROM ImageResource i WHERE i.tempKey IN :tempKeys")
    List<ImageResource> findAllByTempKeyIn(@Param("tempKeys") List<String> tempKeys);

    Optional<ImageResource> findByTempKey(String tempKey);
}
