package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPost;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý bài viết
 */
public interface LvsPostService {

    // Lấy bài viết theo ID
    LvsPost lvsGetPostById(Long lvsPostId);

    // Lấy tất cả bài viết với phân trang
    Page<LvsPost> lvsGetAllPosts(Pageable lvsPageable);

    // Lấy bài viết đã publish
    Page<LvsPost> lvsGetAllPublishedPosts(Pageable lvsPageable);

    // Tìm kiếm bài viết
    Page<LvsPost> lvsSearchPosts(String lvsKeyword, Pageable lvsPageable);

    // Lấy bài viết theo type
    Page<LvsPost> lvsGetPostsByType(String lvsType, Pageable lvsPageable);

    // Lấy bài viết theo status
    Page<LvsPost> lvsGetPostsByStatus(String lvsStatus, Pageable lvsPageable);

    // Lấy bài viết theo type và status
    Page<LvsPost> lvsGetPostsByStatusAndType(String lvsStatus, String lvsType, Pageable lvsPageable);

    // Lấy bài viết theo người đăng
    Page<LvsPost> lvsGetPostsByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy bài viết theo người đăng và trạng thái
    Page<LvsPost> lvsGetPostsByUserAndStatus(Long lvsUserId, LvsPostStatus lvsStatus, Pageable lvsPageable);

    // Lấy bài viết mới nhất
    List<LvsPost> lvsGetNewestPosts(Pageable lvsPageable);

    // Lấy bài viết phổ biến nhất
    List<LvsPost> lvsGetPopularPosts(Pageable lvsPageable);

    // Lấy bài viết được ghim
    List<LvsPost> lvsGetPinnedPosts();

    // Lưu bài viết
    LvsPost lvsSavePost(LvsPost lvsPost);

    // Cập nhật bài viết
    LvsPost lvsUpdatePost(LvsPost lvsPost);

    // Xóa bài viết
    void lvsDeletePost(Long lvsPostId);

    // Xóa bài viết với lý do
    void lvsDeletePost(Long lvsPostId, String lvsReason);

    // Duyệt bài viết
    LvsPost lvsApprovePost(Long lvsPostId);

    // Ẩn bài viết
    LvsPost lvsHidePost(Long lvsPostId, String lvsReason);

    // Hiển thị bài viết
    LvsPost lvsShowPost(Long lvsPostId);

    // Ghim bài viết
    LvsPost lvsPinPost(Long lvsPostId);

    // Bỏ ghim bài viết
    LvsPost lvsUnpinPost(Long lvsPostId);

    // Thích bài viết
    boolean lvsLikePost(Long lvsPostId, Long lvsUserId);

    // Tăng lượt xem
    void lvsIncrementViewCount(Long lvsPostId);

    // Tăng lượt comment
    void lvsIncrementCommentCount(Long lvsPostId);

    // Tăng lượt share
    void lvsIncrementShareCount(Long lvsPostId);

    // Đếm tổng số bài viết
    Long lvsCountTotalPosts();

    // Đếm số bài viết theo trạng thái
    Long lvsCountPostsByStatus(LvsPostStatus lvsStatus);

    // Đếm số bài viết mới trong khoảng thời gian
    Long lvsCountNewPosts(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy thống kê bài viết
    Map<String, Long> lvsGetPostStats();

    // ==================== IMAGE METHODS ====================

    // Lưu bài viết kèm ảnh (tối đa 50 ảnh)
    LvsPost lvsSavePostWithImages(LvsPost lvsPost, List<MultipartFile> images) throws Exception;

    // Thêm ảnh vào bài viết đã tồn tại
    void lvsAddImagesToPost(Long lvsPostId, List<MultipartFile> images) throws Exception;

    // Xóa một ảnh của bài viết
    void lvsDeletePostImage(Long lvsImageId);

    // Lấy danh sách ảnh của bài viết
    List<LvsPostImage> lvsGetPostImages(Long lvsPostId);
}