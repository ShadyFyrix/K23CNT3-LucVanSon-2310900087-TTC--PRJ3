package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho entity LvsComment
 * Xử lý truy vấn liên quan đến bình luận
 */
@Repository
public interface LvsCommentRepository extends JpaRepository<LvsComment, Long> {

    // Tìm comment theo post
    List<LvsComment> findByLvsPost_LvsPostId(Long lvsPostId);

    Page<LvsComment> findByLvsPost_LvsPostId(Long lvsPostId, Pageable pageable);

    // Tìm comment theo user
    List<LvsComment> findByLvsUser_LvsUserId(Long lvsUserId);

    Page<LvsComment> findByLvsUser_LvsUserId(Long lvsUserId, Pageable pageable);

    // Tìm comment theo parent
    List<LvsComment> findByLvsParent_LvsCommentId(Long lvsParentId);

    Page<LvsComment> findByLvsParent_LvsCommentId(Long lvsParentId, Pageable pageable);

    // Tìm comment không có parent (comment gốc)
    List<LvsComment> findByLvsParentIsNull();

    Page<LvsComment> findByLvsParentIsNull(Pageable pageable);

    // Tìm comment theo post và không có parent
    List<LvsComment> findByLvsPost_LvsPostIdAndLvsParentIsNull(Long lvsPostId);

    Page<LvsComment> findByLvsPost_LvsPostIdAndLvsParentIsNull(Long lvsPostId, Pageable pageable);

    // Tìm comment theo approved
    List<LvsComment> findByLvsIsApprovedTrue();

    Page<LvsComment> findByLvsIsApprovedTrue(Pageable pageable);

    // Tìm comment chưa approved
    List<LvsComment> findByLvsIsApprovedFalse();

    Page<LvsComment> findByLvsIsApprovedFalse(Pageable pageable);

    // Đếm comment theo post
    Long countByLvsPost_LvsPostId(Long lvsPostId);

    // Đếm comment theo user
    Long countByLvsUser_LvsUserId(Long lvsUserId);

    // Đếm reply theo comment
    Long countByLvsParent_LvsCommentId(Long lvsParentId);

    // Lấy comment mới nhất
    List<LvsComment> findByOrderByLvsCreatedAtDesc();

    Page<LvsComment> findByOrderByLvsCreatedAtDesc(Pageable pageable);

    // Lấy comment nhiều like nhất
    Page<LvsComment> findByOrderByLvsLikeCountDesc(Pageable pageable);

    // Tăng like count
    @Modifying
    @Query("UPDATE LvsComment c SET c.lvsLikeCount = c.lvsLikeCount + 1 WHERE c.lvsCommentId = :commentId")
    void incrementLikeCount(@Param("commentId") Long commentId);

    // Giảm like count
    @Modifying
    @Query("UPDATE LvsComment c SET c.lvsLikeCount = c.lvsLikeCount - 1 WHERE c.lvsCommentId = :commentId")
    void decrementLikeCount(@Param("commentId") Long commentId);

    // Xóa comment theo post
    @Modifying
    @Query("DELETE FROM LvsComment c WHERE c.lvsPost.lvsPostId = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    // Tìm comment theo isApproved (generic method)
    Page<LvsComment> findByLvsIsApproved(Boolean isApproved, Pageable pageable);

    // Tìm replies cho user (comments mà parent của nó thuộc về user)
    Page<LvsComment> findByLvsParent_LvsUser_LvsUserId(Long userId, Pageable pageable);

    // Toggle approved
    @Modifying
    @Query("UPDATE LvsComment c SET c.lvsIsApproved = :approved WHERE c.lvsCommentId = :commentId")
    void updateApprovedStatus(@Param("commentId") Long commentId, @Param("approved") Boolean approved);

    // Lấy reply cho comment của user
    @Query("SELECT c FROM LvsComment c WHERE c.lvsParent.lvsUser.lvsUserId = :userId AND c.lvsUser.lvsUserId != :userId")
    Page<LvsComment> findRepliesToUserComments(@Param("userId") Long userId, Pageable pageable);

    // Lấy tất cả reply của comment
    @Query("SELECT c FROM LvsComment c WHERE c.lvsParent.lvsCommentId = :parentId ORDER BY c.lvsCreatedAt ASC")
    List<LvsComment> findAllReplies(@Param("parentId") Long parentId);

    // Đếm tổng số comment
    @Query("SELECT COUNT(c) FROM LvsComment c")
    Long countAllComments();

    // Đếm comment đã chỉnh sửa
    Long countByLvsIsEditedTrue();
}