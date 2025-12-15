package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPromotion;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPromotion.LvsDiscountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsPromotion
 * Xử lý truy vấn liên quan đến khuyến mãi
 */
@Repository
public interface LvsPromotionRepository extends JpaRepository<LvsPromotion, Integer> {

        // Tìm promotion theo code
        Optional<LvsPromotion> findByLvsCode(String lvsCode);

        // Kiểm tra promotion code đã tồn tại chưa
        boolean existsByLvsCode(String lvsCode);

        // Tìm promotion active
        List<LvsPromotion> findByLvsIsActiveTrue();

        Page<LvsPromotion> findByLvsIsActiveTrue(Pageable pageable);

        // Tìm promotion inactive
        List<LvsPromotion> findByLvsIsActiveFalse();

        Page<LvsPromotion> findByLvsIsActiveFalse(Pageable pageable);

        // Tìm promotion theo discount type
        List<LvsPromotion> findByLvsDiscountType(LvsDiscountType lvsDiscountType);

        Page<LvsPromotion> findByLvsDiscountType(LvsDiscountType lvsDiscountType, Pageable pageable);

        // Tìm promotion còn hiệu lực
        @Query("SELECT p FROM LvsPromotion p WHERE p.lvsIsActive = true AND p.lvsStartDate <= CURRENT_DATE AND p.lvsEndDate >= CURRENT_DATE")
        List<LvsPromotion> findValidPromotions();

        @Query("SELECT p FROM LvsPromotion p WHERE p.lvsIsActive = true AND p.lvsStartDate <= CURRENT_DATE AND p.lvsEndDate >= CURRENT_DATE")
        Page<LvsPromotion> findValidPromotions(Pageable pageable);

        // Tìm promotion hết hạn
        @Query("SELECT p FROM LvsPromotion p WHERE p.lvsEndDate < CURRENT_DATE")
        List<LvsPromotion> findExpiredPromotions();

        @Query("SELECT p FROM LvsPromotion p WHERE p.lvsEndDate < CURRENT_DATE")
        Page<LvsPromotion> findExpiredPromotions(Pageable pageable);

        // Tìm promotion sắp hết hạn
        // @Query("SELECT p FROM LvsPromotion p WHERE p.lvsEndDate BETWEEN CURRENT_DATE
        // AND CURRENT_DATE + 7")
        // List<LvsPromotion> findExpiringSoonPromotions();

        // Đếm promotion active
        Long countByLvsIsActiveTrue();

        // Đếm promotion inactive
        Long countByLvsIsActiveFalse();

        // Đếm promotion còn hiệu lực
        @Query("SELECT COUNT(p) FROM LvsPromotion p WHERE p.lvsIsActive = true AND p.lvsStartDate <= CURRENT_DATE AND p.lvsEndDate >= CURRENT_DATE")
        Long countValidPromotions();

        // Tăng used count
        @Modifying
        @Query("UPDATE LvsPromotion p SET p.lvsUsedCount = p.lvsUsedCount + 1 WHERE p.lvsPromotionId = :promotionId")
        void incrementUsedCount(@Param("promotionId") Integer promotionId);

        // Toggle active
        @Modifying
        @Query("UPDATE LvsPromotion p SET p.lvsIsActive = :active WHERE p.lvsPromotionId = :promotionId")
        void updateActiveStatus(@Param("promotionId") Integer promotionId, @Param("active") Boolean active);

        // Kiểm tra promotion còn hiệu lực không
        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM LvsPromotion p WHERE " +
                        "p.lvsCode = :code AND p.lvsIsActive = true AND " +
                        "p.lvsStartDate <= CURRENT_DATE AND p.lvsEndDate >= CURRENT_DATE AND " +
                        "(p.lvsUsageLimit IS NULL OR p.lvsUsedCount < p.lvsUsageLimit)")
        boolean isValidPromotion(@Param("code") String code);

        // Kiểm tra promotion có thể áp dụng cho order value
        @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM LvsPromotion p WHERE " +
                        "p.lvsCode = :code AND p.lvsIsActive = true AND " +
                        "p.lvsStartDate <= CURRENT_DATE AND p.lvsEndDate >= CURRENT_DATE AND " +
                        "(p.lvsUsageLimit IS NULL OR p.lvsUsedCount < p.lvsUsageLimit) AND " +
                        "(p.lvsMinOrderValue IS NULL OR p.lvsMinOrderValue <= :orderValue)")
        boolean isPromotionApplicable(@Param("code") String code, @Param("orderValue") Double orderValue);

        // Lấy promotion có hiệu lực cho order value
        @Query("SELECT p FROM LvsPromotion p WHERE " +
                        "p.lvsIsActive = true AND " +
                        "p.lvsStartDate <= CURRENT_DATE AND p.lvsEndDate >= CURRENT_DATE AND " +
                        "(p.lvsUsageLimit IS NULL OR p.lvsUsedCount < p.lvsUsageLimit) AND " +
                        "(p.lvsMinOrderValue IS NULL OR p.lvsMinOrderValue <= :orderValue) " +
                        "ORDER BY p.lvsDiscountValue DESC")
        List<LvsPromotion> findApplicablePromotions(@Param("orderValue") Double orderValue);

        // Tính discount amount
        @Query("SELECT CASE WHEN p.lvsDiscountType = 'PERCENT' THEN (:orderValue * p.lvsDiscountValue / 100) " +
                        "ELSE p.lvsDiscountValue END " +
                        "FROM LvsPromotion p WHERE p.lvsCode = :code")
        Double calculateDiscountAmount(@Param("code") String code, @Param("orderValue") Double orderValue);

        // Lấy promotion mới nhất
        List<LvsPromotion> findByOrderByLvsCreatedAtDesc();

        Page<LvsPromotion> findByOrderByLvsCreatedAtDesc(Pageable pageable);

        // Tìm kiếm promotion theo keyword
        @Query("SELECT p FROM LvsPromotion p WHERE " +
                        "LOWER(p.lvsCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<LvsPromotion> searchPromotions(@Param("keyword") String keyword, Pageable pageable);

        // Lấy promotion có usage limit
        List<LvsPromotion> findByLvsUsageLimitIsNotNull();

        // Lấy promotion đã sử dụng hết
        @Query("SELECT p FROM LvsPromotion p WHERE p.lvsUsageLimit IS NOT NULL AND p.lvsUsedCount >= p.lvsUsageLimit")
        List<LvsPromotion> findFullyUsedPromotions();

        // Tìm promotion theo isActive status (generic method)
        Page<LvsPromotion> findByLvsIsActive(Boolean isActive, Pageable pageable);
}
