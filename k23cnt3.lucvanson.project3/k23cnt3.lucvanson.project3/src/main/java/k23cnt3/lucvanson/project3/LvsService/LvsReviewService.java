package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý đánh giá
 * Xử lý viết, chỉnh sửa, xóa, duyệt đánh giá
 */
public interface LvsReviewService {

    // Lấy đánh giá theo ID
    LvsReview lvsGetReviewById(Long lvsReviewId);

    // Lấy tất cả đánh giá
    Page<LvsReview> lvsGetAllReviews(Pageable lvsPageable);

    // Lấy đánh giá theo dự án
    Page<LvsReview> lvsGetReviewsByProject(Long lvsProjectId, Pageable lvsPageable);

    // Lấy đánh giá theo user
    Page<LvsReview> lvsGetReviewsByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy đánh giá theo approval
    Page<LvsReview> lvsGetReviewsByApproval(Boolean lvsIsApproved, Pageable lvsPageable);

    // Lưu đánh giá
    LvsReview lvsSaveReview(LvsReview lvsReview);

    // Cập nhật đánh giá
    LvsReview lvsUpdateReview(LvsReview lvsReview);

    // Xóa đánh giá
    void lvsDeleteReview(Long lvsReviewId);

    // Xóa đánh giá với lý do
    void lvsDeleteReview(Long lvsReviewId, String lvsReason);

    // Duyệt đánh giá
    LvsReview lvsApproveReview(Long lvsReviewId);

    // Ẩn đánh giá
    LvsReview lvsHideReview(Long lvsReviewId, String lvsReason);

    // Thích đánh giá
    boolean lvsLikeReview(Long lvsReviewId, Long lvsUserId);

    // Kiểm tra user đã đánh giá chưa
    boolean lvsHasUserReviewed(Long lvsUserId, Long lvsProjectId);

    // Lấy rating trung bình
    Double lvsGetAverageRating(Long lvsProjectId);

    // Lấy số lượng đánh giá theo rating
    Map<Integer, Long> lvsGetRatingDistribution(Long lvsProjectId);

    // Báo cáo đánh giá
    void lvsReportReview(Long lvsReviewId, Long lvsReporterId, String lvsReason);
}