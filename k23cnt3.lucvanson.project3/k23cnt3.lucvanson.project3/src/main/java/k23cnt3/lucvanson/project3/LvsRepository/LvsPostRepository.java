package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPost;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostType;
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
 * Repository interface cho entity LvsPost
 * Xử lý truy vấn liên quan đến bài viết
 */
@Repository
public interface LvsPostRepository extends JpaRepository<LvsPost, Long> {

        // Tìm bài viết theo status
        List<LvsPost> findByLvsStatus(LvsPostStatus lvsStatus);

        Page<LvsPost> findByLvsStatus(LvsPostStatus lvsStatus, Pageable pageable);

        // Tìm bài viết theo type
        List<LvsPost> findByLvsType(LvsPostType lvsType);

        Page<LvsPost> findByLvsType(LvsPostType lvsType, Pageable pageable);

        // Tìm bài viết theo user
        List<LvsPost> findByLvsUser_LvsUserId(Long lvsUserId);

        Page<LvsPost> findByLvsUser_LvsUserId(Long lvsUserId, Pageable pageable);

        // Tìm bài viết theo project
        List<LvsPost> findByLvsProject_LvsProjectId(Long lvsProjectId);

        Page<LvsPost> findByLvsProject_LvsProjectId(Long lvsProjectId, Pageable pageable);

        // Tìm bài viết theo status và type
        List<LvsPost> findByLvsStatusAndLvsType(LvsPostStatus lvsStatus, LvsPostType lvsType);

        Page<LvsPost> findByLvsStatusAndLvsType(LvsPostStatus lvsStatus, LvsPostType lvsType, Pageable pageable);

        // Tìm bài viết pinned
        List<LvsPost> findByLvsIsPinnedTrue();

        Page<LvsPost> findByLvsIsPinnedTrue(Pageable pageable);

        // Tìm bài viết approved
        List<LvsPost> findByLvsIsApprovedTrue();

        Page<LvsPost> findByLvsIsApprovedTrue(Pageable pageable);

        // Tìm kiếm bài viết theo keyword
        @Query("SELECT p FROM LvsPost p WHERE " +
                        "LOWER(p.lvsTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsTags) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<LvsPost> searchPosts(@Param("keyword") String keyword, Pageable pageable);

        // Tìm kiếm bài viết theo keyword và status
        @Query("SELECT p FROM LvsPost p WHERE p.lvsStatus = :status AND " +
                        "(LOWER(p.lvsTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(p.lvsContent) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<LvsPost> searchPostsByStatus(@Param("keyword") String keyword,
                        @Param("status") LvsPostStatus status,
                        Pageable pageable);

        // Đếm bài viết theo status
        Long countByLvsStatus(LvsPostStatus lvsStatus);

        // Đếm bài viết theo type
        Long countByLvsType(LvsPostType lvsType);

        // Đếm bài viết theo user
        Long countByLvsUser_LvsUserId(Long lvsUserId);

        // Đếm bài viết trong khoảng thời gian
        Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Đếm bài viết của user trong khoảng thời gian (for title calculation)
        Long countByLvsUser_LvsUserIdAndLvsCreatedAtBetween(Long lvsUserId, LocalDateTime startDate,
                        LocalDateTime endDate);

        // Lấy bài viết mới nhất
        List<LvsPost> findByLvsStatusOrderByLvsCreatedAtDesc(LvsPostStatus lvsStatus);

        Page<LvsPost> findByLvsStatusOrderByLvsCreatedAtDesc(LvsPostStatus lvsStatus, Pageable pageable);

        // Lấy bài viết phổ biến nhất (theo view count)
        Page<LvsPost> findByLvsStatusOrderByLvsViewCountDesc(LvsPostStatus lvsStatus, Pageable pageable);

        // Lấy bài viết nhiều like nhất
        Page<LvsPost> findByLvsStatusOrderByLvsLikeCountDesc(LvsPostStatus lvsStatus, Pageable pageable);

        // Lấy bài viết nhiều comment nhất
        Page<LvsPost> findByLvsStatusOrderByLvsCommentCountDesc(LvsPostStatus lvsStatus, Pageable pageable);

        // Tăng view count
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsViewCount = p.lvsViewCount + 1 WHERE p.lvsPostId = :postId")
        void incrementViewCount(@Param("postId") Long postId);

        // Tăng like count
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsLikeCount = p.lvsLikeCount + 1 WHERE p.lvsPostId = :postId")
        void incrementLikeCount(@Param("postId") Long postId);

        // Giảm like count
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsLikeCount = p.lvsLikeCount - 1 WHERE p.lvsPostId = :postId")
        void decrementLikeCount(@Param("postId") Long postId);

        // Tăng comment count
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsCommentCount = p.lvsCommentCount + 1 WHERE p.lvsPostId = :postId")
        void incrementCommentCount(@Param("postId") Long postId);

        // Giảm comment count
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsCommentCount = p.lvsCommentCount - 1 WHERE p.lvsPostId = :postId")
        void decrementCommentCount(@Param("postId") Long postId);

        // Tăng share count
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsShareCount = p.lvsShareCount + 1 WHERE p.lvsPostId = :postId")
        void incrementShareCount(@Param("postId") Long postId);

        // Toggle pinned
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsIsPinned = :pinned WHERE p.lvsPostId = :postId")
        void updatePinnedStatus(@Param("postId") Long postId, @Param("pinned") Boolean pinned);

        // Toggle approved
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsIsApproved = :approved WHERE p.lvsPostId = :postId")
        void updateApprovedStatus(@Param("postId") Long postId, @Param("approved") Boolean approved);

        // Cập nhật status
        @Modifying
        @Query("UPDATE LvsPost p SET p.lvsStatus = :status WHERE p.lvsPostId = :postId")
        void updateStatus(@Param("postId") Long postId, @Param("status") LvsPostStatus status);

        // Tìm bài viết theo tags
        @Query("SELECT p FROM LvsPost p WHERE p.lvsTags LIKE %:tag%")
        Page<LvsPost> findByTag(@Param("tag") String tag, Pageable pageable);

        // Lấy bài viết có project
        List<LvsPost> findByLvsProjectIsNotNull();

        Page<LvsPost> findByLvsProjectIsNotNull(Pageable pageable);

        // Lấy bài viết không có project
        List<LvsPost> findByLvsProjectIsNull();

        Page<LvsPost> findByLvsProjectIsNull(Pageable pageable);

        // Lấy bài viết theo user và status
        Page<LvsPost> findByLvsUser_LvsUserIdAndLvsStatus(Long userId, LvsPostStatus status, Pageable pageable);

        // Đếm tổng số bài viết
        @Query("SELECT COUNT(p) FROM LvsPost p")
        Long countAllPosts();

        // Lấy tổng view count
        @Query("SELECT SUM(p.lvsViewCount) FROM LvsPost p")
        Long getTotalViewCount();

        // Tìm kiếm theo title hoặc content (Spring auto-generated)
        Page<LvsPost> findByLvsTitleContainingOrLvsContentContaining(String title, String content, Pageable pageable);

        // Lấy tất cả bài viết sắp xếp theo ngày tạo giảm dần
        Page<LvsPost> findAllByOrderByLvsCreatedAtDesc(Pageable pageable);

        // Lấy tất cả bài viết sắp xếp theo lượt xem giảm dần
        Page<LvsPost> findAllByOrderByLvsViewCountDesc(Pageable pageable);
}
