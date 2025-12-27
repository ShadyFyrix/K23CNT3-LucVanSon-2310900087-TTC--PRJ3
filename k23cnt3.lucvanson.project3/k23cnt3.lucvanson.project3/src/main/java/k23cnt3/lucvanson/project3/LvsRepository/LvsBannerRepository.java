package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsBanner;
import k23cnt3.lucvanson.project3.LvsEntity.LvsBanner.LvsBannerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LvsBannerRepository - Repository cho quản lý banner
 */
@Repository
public interface LvsBannerRepository extends JpaRepository<LvsBanner, Long> {

    /**
     * Lấy tất cả banner đang active, sắp xếp theo display order
     */
    @Query("SELECT b FROM LvsBanner b WHERE b.lvsIsActive = true " +
            "AND (b.lvsStartDate IS NULL OR b.lvsStartDate <= :now) " +
            "AND (b.lvsEndDate IS NULL OR b.lvsEndDate >= :now) " +
            "ORDER BY b.lvsDisplayOrder ASC")
    List<LvsBanner> findActiveBanners(LocalDateTime now);

    /**
     * Lấy banner theo loại và đang active
     */
    @Query("SELECT b FROM LvsBanner b WHERE b.lvsIsActive = true " +
            "AND b.lvsType = :type " +
            "AND (b.lvsStartDate IS NULL OR b.lvsStartDate <= :now) " +
            "AND (b.lvsEndDate IS NULL OR b.lvsEndDate >= :now) " +
            "ORDER BY b.lvsDisplayOrder ASC")
    List<LvsBanner> findActiveBannersByType(LvsBannerType type, LocalDateTime now);

    /**
     * Lấy tất cả banner (cho admin)
     */
    List<LvsBanner> findAllByOrderByLvsDisplayOrderAsc();
}
