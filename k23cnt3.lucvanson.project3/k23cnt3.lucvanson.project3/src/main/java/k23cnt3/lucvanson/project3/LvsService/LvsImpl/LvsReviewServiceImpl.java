package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsReview;
import k23cnt3.lucvanson.project3.LvsRepository.LvsReviewRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation cho quản lý đánh giá
 * Xử lý viết, chỉnh sửa, xóa, duyệt đánh giá
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsReviewServiceImpl implements LvsReviewService {

    private final LvsReviewRepository lvsReviewRepository;

    /**
     * Lấy đánh giá theo ID
     * @param lvsReviewId ID đánh giá
     * @return Đánh giá tìm thấy
     */
    @Override
    public LvsReview lvsGetReviewById(Long lvsReviewId) {
        return lvsReviewRepository.findById(lvsReviewId).orElse(null);
    }

    /**
     * Lấy tất cả đánh giá với phân trang
     * @param lvsPageable Thông tin phân trang
     * @return Trang đánh giá
     */
    @Override
    public Page<LvsReview> lvsGetAllReviews(Pageable lvsPageable) {
        return lvsReviewRepository.findAll(lvsPageable);
    }

    /**
     * Lấy đánh giá theo dự án
     * @param lvsProjectId ID dự án
     * @param lvsPageable Thông tin phân trang
     * @return Trang đánh giá
     */
    @Override
    public Page<LvsReview> lvsGetReviewsByProject(Long lvsProjectId, Pageable lvsPageable) {
        return lvsReviewRepository.findByLvsProject_LvsProjectId(lvsProjectId, lvsPageable);
    }

    /**
     * Lấy đánh giá theo user
     * @param lvsUserId ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang đánh giá
     */
    @Override
    public Page<LvsReview> lvsGetReviewsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsReviewRepository.findByLvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy đánh giá theo approval
     * @param lvsIsApproved Trạng thái duyệt
     * @param lvsPageable Thông tin phân trang
     * @return Trang đánh giá
     */
    @Override
    public Page<LvsReview> lvsGetReviewsByApproval(Boolean lvsIsApproved, Pageable lvsPageable) {
        return lvsReviewRepository.findByLvsIsApproved(lvsIsApproved, lvsPageable);
    }

    /**
     * Lưu đánh giá
     * @param lvsReview Thông tin đánh giá
     * @return Đánh giá đã lưu
     */
    @Override
    public LvsReview lvsSaveReview(LvsReview lvsReview) {
        lvsReview.setLvsCreatedAt(LocalDateTime.now());
        lvsReview.setLvsUpdatedAt(LocalDateTime.now());
        return lvsReviewRepository.save(lvsReview);
    }

    /**
     * Cập nhật đánh giá
     * @param lvsReview Thông tin đánh giá cập nhật
     * @return Đánh giá đã cập nhật
     */
    @Override
    public LvsReview lvsUpdateReview(LvsReview lvsReview) {
        LvsReview lvsExistingReview = lvsGetReviewById(lvsReview.getLvsReviewId());
        if (lvsExistingReview != null) {
            lvsExistingReview.setLvsRating(lvsReview.getLvsRating());
            lvsExistingReview.setLvsTitle(lvsReview.getLvsTitle());
            lvsExistingReview.setLvsContent(lvsReview.getLvsContent());
            lvsExistingReview.setLvsImages(lvsReview.getLvsImages());
            lvsExistingReview.setLvsUpdatedAt(LocalDateTime.now());
            return lvsReviewRepository.save(lvsExistingReview);
        }
        return null;
    }

    /**
     * Xóa đánh giá
     * @param lvsReviewId ID đánh giá
     */
    @Override
    public void lvsDeleteReview(Long lvsReviewId) {
        lvsReviewRepository.deleteById(lvsReviewId);
    }

    /**
     * Xóa đánh giá với lý do
     * @param lvsReviewId ID đánh giá
     * @param lvsReason Lý do xóa
     */
    @Override
    public void lvsDeleteReview(Long lvsReviewId, String lvsReason) {
        LvsReview lvsReview = lvsGetReviewById(lvsReviewId);
        if (lvsReview != null) {
            // Thay vì xóa cứng, đánh dấu content
            lvsReview.setLvsContent("[Đã xóa: " + lvsReason + "]");
            lvsReview.setLvsIsApproved(false);
            lvsReview.setLvsUpdatedAt(LocalDateTime.now());
            lvsReviewRepository.save(lvsReview);
        }
    }

    /**
     * Duyệt đánh giá
     * @param lvsReviewId ID đánh giá
     * @return Đánh giá đã duyệt
     */
    @Override
    public LvsReview lvsApproveReview(Long lvsReviewId) {
        LvsReview lvsReview = lvsGetReviewById(lvsReviewId);
        if (lvsReview != null) {
            lvsReview.setLvsIsApproved(true);
            lvsReview.setLvsUpdatedAt(LocalDateTime.now());
            return lvsReviewRepository.save(lvsReview);
        }
        return null;
    }

    /**
     * Ẩn đánh giá
     * @param lvsReviewId ID đánh giá
     * @param lvsReason Lý do ẩn
     * @return Đánh giá đã ẩn
     */
    @Override
    public LvsReview lvsHideReview(Long lvsReviewId, String lvsReason) {
        LvsReview lvsReview = lvsGetReviewById(lvsReviewId);
        if (lvsReview != null) {
            lvsReview.setLvsIsApproved(false);
            lvsReview.setLvsUpdatedAt(LocalDateTime.now());
            return lvsReviewRepository.save(lvsReview);
        }
        return null;
    }

    /**
     * Thích đánh giá
     * @param lvsReviewId ID đánh giá
     * @param lvsUserId ID người dùng
     * @return true nếu thành công
     */
    @Override
    public boolean lvsLikeReview(Long lvsReviewId, Long lvsUserId) {
        LvsReview lvsReview = lvsGetReviewById(lvsReviewId);
        if (lvsReview != null) {
            // TODO: Kiểm tra user đã like chưa
            lvsReview.setLvsLikeCount(lvsReview.getLvsLikeCount() + 1);
            lvsReviewRepository.save(lvsReview);
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra user đã đánh giá chưa
     * @param lvsUserId ID người dùng
     * @param lvsProjectId ID dự án
     * @return true nếu đã đánh giá
     */
    @Override
    public boolean lvsHasUserReviewed(Long lvsUserId, Long lvsProjectId) {
        return lvsReviewRepository.existsByLvsUser_LvsUserIdAndLvsProject_LvsProjectId(lvsUserId, lvsProjectId);
    }

    /**
     * Lấy rating trung bình
     * @param lvsProjectId ID dự án
     * @return Rating trung bình
     */
    @Override
    public Double lvsGetAverageRating(Long lvsProjectId) {
        return lvsReviewRepository.calculateAverageRatingByProjectId(lvsProjectId);
    }

    /**
     * Lấy số lượng đánh giá theo rating
     * @param lvsProjectId ID dự án
     * @return Map phân bố rating
     */
    @Override
    public Map<Integer, Long> lvsGetRatingDistribution(Long lvsProjectId) {
        Map<Integer, Long> lvsDistribution = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            Long lvsCount = lvsReviewRepository.countByLvsProject_LvsProjectIdAndLvsRating(lvsProjectId, i);
            lvsDistribution.put(i, lvsCount);
        }

        return lvsDistribution;
    }

    /**
     * Báo cáo đánh giá
     * @param lvsReviewId ID đánh giá
     * @param lvsReporterId ID người báo cáo
     * @param lvsReason Lý do báo cáo
     */
    @Override
    public void lvsReportReview(Long lvsReviewId, Long lvsReporterId, String lvsReason) {
        // TODO: Tạo báo cáo cho đánh giá
        System.out.println("Báo cáo đánh giá " + lvsReviewId + " bởi user " + lvsReporterId + ": " + lvsReason);
    }
}