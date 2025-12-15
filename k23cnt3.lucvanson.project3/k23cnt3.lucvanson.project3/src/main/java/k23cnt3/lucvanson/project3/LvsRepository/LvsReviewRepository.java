package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsReview
 * Xử lý truy vấn liên quan đến đánh giá
 */
@Repository
public interface LvsReviewRepository extends JpaRepository<LvsReview, Long> {

    // Tìm review theo project
    List<LvsReview> findByLvsProject_LvsProjectId(Long lvsProjectId);

    Page<LvsReview> findByLvsProject_LvsProjectId(Long lvsProjectId, Pageable pageable);

    // Tìm review theo user
    List<LvsReview> findByLvsUser_LvsUserId(Long lvsUserId);

    Page<LvsReview> findByLvsUser_LvsUserId(Long lvsUserId, Pageable pageable);

    // Tìm review theo project và user
    Optional<LvsReview> findByLvsProject_LvsProjectIdAndLvsUser_LvsUserId(Long lvsProjectId, Long lvsUserId);

    // Tìm review theo rating
    List<LvsReview> findByLvsRating(Integer lvsRating);

    Page<LvsReview> findByLvsRating(Integer lvsRating, Pageable pageable);

    // Tìm review theo approved
    List<LvsReview> findByLvsIsApprovedTrue();

    Page<LvsReview> findByLvsIsApprovedTrue(Pageable pageable);

    // Tìm review chưa approved
    List<LvsReview> findByLvsIsApprovedFalse();

    Page<LvsReview> findByLvsIsApprovedFalse(Pageable pageable);

    // Tìm review theo project và approved
    List<LvsReview> findByLvsProject_LvsProjectIdAndLvsIsApprovedTrue(Long lvsProjectId);

    Page<LvsReview> findByLvsProject_LvsProjectIdAndLvsIsApprovedTrue(Long lvsProjectId, Pageable pageable);

    // Đếm review theo project
    Long countByLvsProject_LvsProjectId(Long lvsProjectId);

    // Đếm review theo user
    Long countByLvsUser_LvsUserId(Long lvsUserId);

    // Đếm review theo rating
    Long countByLvsProject_LvsProjectIdAndLvsRating(Long lvsProjectId, Integer lvsRating);

    // Tính rating trung bình
    @Query("SELECT AVG(r.lvsRating) FROM LvsReview r WHERE r.lvsProject.lvsProjectId = :projectId AND r.lvsIsApproved = true")
    Double getAverageRating(@Param("projectId") Long projectId);

    // Đếm số review đã approved
    Long countByLvsProject_LvsProjectIdAndLvsIsApprovedTrue(Long lvsProjectId);

    // Lấy review mới nhất
    List<LvsReview> findByOrderByLvsCreatedAtDesc();

    Page<LvsReview> findByOrderByLvsCreatedAtDesc(Pageable pageable);

    // Lấy review rating cao nhất
    Page<LvsReview> findByOrderByLvsRatingDesc(Pageable pageable);

    // Lấy review nhiều like nhất
    Page<LvsReview> findByOrderByLvsLikeCountDesc(Pageable pageable);

    // Tăng like count
    @Modifying
    @Query("UPDATE LvsReview r SET r.lvsLikeCount = r.lvsLikeCount + 1 WHERE r.lvsReviewId = :reviewId")
    void incrementLikeCount(@Param("reviewId") Long reviewId);

    // Giảm like count
    @Modifying
    @Query("UPDATE LvsReview r SET r.lvsLikeCount = r.lvsLikeCount - 1 WHERE r.lvsReviewId = :reviewId")
    void decrementLikeCount(@Param("reviewId") Long reviewId);

    // Toggle approved
    @Modifying
    @Query("UPDATE LvsReview r SET r.lvsIsApproved = :approved WHERE r.lvsReviewId = :reviewId")
    void updateApprovedStatus(@Param("reviewId") Long reviewId, @Param("approved") Boolean approved);

    // Lấy distribution rating
    @Query("SELECT r.lvsRating, COUNT(r) FROM LvsReview r WHERE r.lvsProject.lvsProjectId = :projectId AND r.lvsIsApproved = true GROUP BY r.lvsRating ORDER BY r.lvsRating DESC")
    List<Object[]> getRatingDistribution(@Param("projectId") Long projectId);

    // Kiểm tra user đã review project chưa
    boolean existsByLvsProject_LvsProjectIdAndLvsUser_LvsUserId(Long lvsProjectId, Long lvsUserId);

    // Tìm review theo approved status (generic)
    Page<LvsReview> findByLvsIsApproved(Boolean isApproved, Pageable pageable);

    // Tính rating trung bình theo project ID (alias method)
    @Query("SELECT AVG(r.lvsRating) FROM LvsReview r WHERE r.lvsProject.lvsProjectId = :projectId AND r.lvsIsApproved = true")
    Double calculateAverageRatingByProjectId(@Param("projectId") Long projectId);

    // Lấy review có hình ảnh
    List<LvsReview> findByLvsImagesIsNotNull();

    // Kiểm tra user đã review project chưa (alias method with different parameter
    // order)
    default boolean existsByLvsUser_LvsUserIdAndLvsProject_LvsProjectId(Long userId, Long projectId) {
        return existsByLvsProject_LvsProjectIdAndLvsUser_LvsUserId(projectId, userId);
    }
}
