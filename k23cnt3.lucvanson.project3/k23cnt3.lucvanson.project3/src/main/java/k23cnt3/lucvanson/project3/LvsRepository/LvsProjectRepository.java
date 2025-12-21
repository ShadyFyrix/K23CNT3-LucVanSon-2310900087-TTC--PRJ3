package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsEntity.LvsProject.LvsProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho entity LvsProject
 * Xử lý truy vấn liên quan đến dự án
 */
@Repository
public interface LvsProjectRepository extends JpaRepository<LvsProject, Long> {

        // Tìm dự án theo status
        List<LvsProject> findByLvsStatus(LvsProjectStatus lvsStatus);

        Page<LvsProject> findByLvsStatus(LvsProjectStatus lvsStatus, Pageable pageable);

        // Tìm dự án theo user
        List<LvsProject> findByLvsUser_LvsUserId(Long lvsUserId);

        Page<LvsProject> findByLvsUser_LvsUserId(Long lvsUserId, Pageable pageable);

        // Tìm dự án theo category
        List<LvsProject> findByLvsCategory_LvsCategoryId(Integer lvsCategoryId);

        Page<LvsProject> findByLvsCategory_LvsCategoryId(Integer lvsCategoryId, Pageable pageable);

        // Tìm dự án theo user và status
        List<LvsProject> findByLvsUser_LvsUserIdAndLvsStatus(Long lvsUserId, LvsProjectStatus lvsStatus);

        Page<LvsProject> findByLvsUser_LvsUserIdAndLvsStatus(Long lvsUserId, LvsProjectStatus lvsStatus,
                        Pageable pageable);

        // Tìm dự án theo category và status
        List<LvsProject> findByLvsCategory_LvsCategoryIdAndLvsStatus(Integer lvsCategoryId, LvsProjectStatus lvsStatus);

        Page<LvsProject> findByLvsCategory_LvsCategoryIdAndLvsStatus(Integer lvsCategoryId, LvsProjectStatus lvsStatus,
                        Pageable pageable);

        // Tìm dự án featured
        List<LvsProject> findByLvsIsFeaturedTrue();

        Page<LvsProject> findByLvsIsFeaturedTrue(Pageable pageable);

        // Tìm dự án đã được approved
        List<LvsProject> findByLvsIsApprovedTrue();

        Page<LvsProject> findByLvsIsApprovedTrue(Pageable pageable);

        // Tìm dự án với rating cao hơn
        List<LvsProject> findByLvsRatingGreaterThanEqual(Double minRating);

        Page<LvsProject> findByLvsRatingGreaterThanEqual(Double minRating, Pageable pageable);

        // Tìm dự án trong khoảng giá
        List<LvsProject> findByLvsPriceBetween(Double minPrice, Double maxPrice);

        Page<LvsProject> findByLvsPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

        // Tìm kiếm dự án theo keyword
        @Query("SELECT p FROM LvsProject p WHERE " +
                        "LOWER(p.lvsProjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsTags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<LvsProject> searchProjects(@Param("keyword") String keyword, Pageable pageable);

        // Tìm kiếm dự án theo keyword và status
        @Query("SELECT p FROM LvsProject p WHERE p.lvsStatus = :status AND " +
                        "(LOWER(p.lvsProjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<LvsProject> searchProjectsByStatus(@Param("keyword") String keyword,
                        @Param("status") LvsProjectStatus status,
                        Pageable pageable);

        // Đếm dự án theo status
        Long countByLvsStatus(LvsProjectStatus lvsStatus);

        // Đếm dự án theo category
        Long countByLvsCategory_LvsCategoryId(Integer lvsCategoryId);

        // Đếm dự án theo user
        Long countByLvsUser_LvsUserId(Long lvsUserId);

        // Đếm dự án trong khoảng thời gian
        Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Lấy dự án mới nhất
        List<LvsProject> findByLvsStatusOrderByLvsCreatedAtDesc(LvsProjectStatus lvsStatus);

        Page<LvsProject> findByLvsStatusOrderByLvsCreatedAtDesc(LvsProjectStatus lvsStatus, Pageable pageable);

        // Lấy dự án phổ biến nhất (theo view count)
        Page<LvsProject> findByLvsStatusOrderByLvsViewCountDesc(LvsProjectStatus lvsStatus, Pageable pageable);

        // Lấy dự án bán chạy nhất (theo purchase count)
        Page<LvsProject> findByLvsStatusOrderByLvsPurchaseCountDesc(LvsProjectStatus lvsStatus, Pageable pageable);

        // Lấy dự án rating cao nhất
        Page<LvsProject> findByLvsStatusOrderByLvsRatingDesc(LvsProjectStatus lvsStatus, Pageable pageable);

        // Tăng view count
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsViewCount = p.lvsViewCount + 1 WHERE p.lvsProjectId = :projectId")
        void incrementViewCount(@Param("projectId") Long projectId);

        // Tăng purchase count
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsPurchaseCount = p.lvsPurchaseCount + 1 WHERE p.lvsProjectId = :projectId")
        void incrementPurchaseCount(@Param("projectId") Long projectId);

        // Tăng download count
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsDownloadCount = p.lvsDownloadCount + 1 WHERE p.lvsProjectId = :projectId")
        void incrementDownloadCount(@Param("projectId") Long projectId);

        // Cập nhật rating
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsRating = :rating, p.lvsReviewCount = :reviewCount WHERE p.lvsProjectId = :projectId")
        void updateRating(@Param("projectId") Long projectId, @Param("rating") Double rating,
                        @Param("reviewCount") Integer reviewCount);

        // Toggle featured
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsIsFeatured = :featured WHERE p.lvsProjectId = :projectId")
        void updateFeaturedStatus(@Param("projectId") Long projectId, @Param("featured") Boolean featured);

        // Toggle approved
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsIsApproved = :approved WHERE p.lvsProjectId = :projectId")
        void updateApprovedStatus(@Param("projectId") Long projectId, @Param("approved") Boolean approved);

        // Cập nhật status
        @Modifying
        @Query("UPDATE LvsProject p SET p.lvsStatus = :status WHERE p.lvsProjectId = :projectId")
        void updateStatus(@Param("projectId") Long projectId, @Param("status") LvsProjectStatus status);

        // Tìm dự án theo tags
        @Query("SELECT p FROM LvsProject p WHERE p.lvsTags LIKE %:tag%")
        Page<LvsProject> findByTag(@Param("tag") String tag, Pageable pageable);

        // Lấy tổng số dự án
        @Query("SELECT COUNT(p) FROM LvsProject p")
        Long countAllProjects();

        // Lấy tổng doanh thu từ tất cả dự án
        @Query("SELECT SUM(p.lvsPrice * p.lvsPurchaseCount) FROM LvsProject p")
        Double getTotalRevenue();

        // Lấy dự án đã mua bởi user
        @Query("SELECT DISTINCT p FROM LvsProject p JOIN LvsOrderItem oi ON p.lvsProjectId = oi.lvsProject.lvsProjectId "
                        +
                        "JOIN LvsOrder o ON oi.lvsOrder.lvsOrderId = o.lvsOrderId " +
                        "WHERE o.lvsBuyer.lvsUserId = :userId AND o.lvsStatus = 'COMPLETED'")
        List<LvsProject> findPurchasedProjectsByUser(@Param("userId") Long userId);

        // Lấy tất cả dự án với category eager loading (for order creation form)
        @Query("SELECT DISTINCT p FROM LvsProject p LEFT JOIN FETCH p.lvsCategory LEFT JOIN FETCH p.lvsUser")
        Page<LvsProject> findAllWithCategoryAndUser(Pageable pageable);
}