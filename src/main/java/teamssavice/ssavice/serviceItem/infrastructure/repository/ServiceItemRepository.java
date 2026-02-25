package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>, ServiceItemRepositoryCustom {

    @Query("SELECT s FROM ServiceItem s " +
        "JOIN FETCH s.address " +
        "LEFT JOIN FETCH s.thumbnailImageResource " +
        "WHERE s.company.id = :companyId " +
        "ORDER BY s.deadline DESC")
    List<ServiceItem> findTop5ByCompanyIdOrderByDeadlineDesc(Long companyId, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
            "LEFT JOIN FETCH s.thumbnailImageResource " +
            "WHERE s.company.id = :companyId")
    Page<ServiceItem> findAllByCompany_Id(Long companyId, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
            "LEFT JOIN FETCH s.thumbnailImageResource " +
            "WHERE s.company.id = :companyId " +
            "AND s.status = :status " +
            "AND s.deadline > :now")
    Page<ServiceItem> findAllByCompany_IdAndStatus(Long companyId, ServiceStatus status, LocalDateTime now, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
            "LEFT JOIN FETCH s.imageIds i " +
            "JOIN FETCH s.address a " +
            "WHERE s.id = :id")
    Optional<ServiceItem> findByIdWithAddressAndImageList(Long id);

    @Query("SELECT COUNT(s) FROM ServiceItem s " +
            "WHERE s.company.id = :companyId " +
            "AND s.status = :recruiting " +
            "AND s.deadline > :now")
    Long countRecruitingServiceItemsByCompanyId(Long companyId, ServiceStatus recruiting, LocalDateTime now);

    @Query("SELECT COUNT(s) FROM ServiceItem s " +
            "WHERE s.company.id = :companyId " +
            "AND s.status = :succeeded " +
            "AND s.endDate > :now")
    Long countSucceededServiceItemsByCompanyId(Long companyId, ServiceStatus succeeded, LocalDateTime now);

    @Query("SELECT s FROM ServiceItem s " +
            "LEFT JOIN FETCH s.thumbnailImageResource " +
            "WHERE s.id IN :serviceItemIds")
    List<ServiceItem> findAllByIdInWithThumbnail(List<Long> serviceItemIds);

    Long countAllByCompany_Id(Long companyId);
}
