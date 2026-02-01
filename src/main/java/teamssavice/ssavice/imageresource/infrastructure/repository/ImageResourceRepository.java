package teamssavice.ssavice.imageresource.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.imageresource.constants.ImageStatus;
import teamssavice.ssavice.imageresource.entity.ImageResource;

public interface ImageResourceRepository extends JpaRepository<ImageResource, Long> {

    Optional<ImageResource> findByTargetKey(String objectKey);

    @Query("SELECT i FROM ImageResource i WHERE i.sourceKey IN :sourceKeys")
    List<ImageResource> findAllBySourceKeyIn(@Param("sourceKeys") List<String> sourceKeys);

    Optional<ImageResource> findBySourceKey(String tempKey);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "UPDATE ImageResource i " +
            "SET i.status = :to " +
            "WHERE i.id = :id " +
            "AND i.status IN :from"
    )
    int updateStatusIfIn(@Param("id") Long id,
        @Param("to") ImageStatus to,
        @Param("from") List<ImageStatus> from);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "UPDATE ImageResource i " +
            "SET i.status = :to, " +
            "    i.isActive = :isActive " +
            "WHERE i.id = :id " +
            "AND i.status = :onlyWhen"
    )
    int updateStatusAndActiveWhen(@Param("id") Long id,
        @Param("to") ImageStatus to,
        @Param("isActive") boolean isActive,
        @Param("onlyWhen") ImageStatus onlyWhen);
}
