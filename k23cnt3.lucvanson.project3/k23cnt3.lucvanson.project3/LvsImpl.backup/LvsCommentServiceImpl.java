package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsComment;
import k23cnt3.lucvanson.project3.LvsRepository.LvsCommentRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation cho quản lý bình luận
 * Xử lý viết, chỉnh sửa, xóa, duyệt bình luận
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsCommentServiceImpl implements LvsCommentService {

    private final LvsCommentRepository lvsCommentRepository;

    /**
     * Lấy bình luận theo ID
     * @param lvsCommentId ID bình luận
     * @return Bình luận tìm thấy
     */
    @Override
    public LvsComment lvsGetCommentById(Long lvsCommentId) {
        return lvsCommentRepository.findById(lvsCommentId).orElse(null);
    }

    /**
     * Lấy tất cả bình luận với phân trang
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetAllComments(Pageable lvsPageable) {
        return lvsCommentRepository.findAll(lvsPageable);
    }

    /**
     * Lấy bình luận theo bài viết
     * @param lvsPostId ID bài viết
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetCommentsByPost(Long lvsPostId, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsPost_LvsPostId(lvsPostId, lvsPageable);
    }

    /**
     * Lấy bình luận theo user
     * @param lvsUserId ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetCommentsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy bình luận theo approval
     * @param lvsIsApproved Trạng thái duyệt
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetCommentsByApproval(Boolean lvsIsApproved, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsIsApproved(lvsIsApproved, lvsPageable);
    }

    /**
     * Lấy reply cho bình luận
     * @param lvsUserId ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang reply
     */
    @Override
    public Page<LvsComment> lvsGetRepliesToUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsParent_LvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lưu bình luận
     * @param lvsComment Thông tin bình luận
     * @return Bình luận đã lưu
     */
    @Override
    public LvsComment lvsSaveComment(LvsComment lvsComment) {
        lvsComment.setLvsCreatedAt(LocalDateTime.now());
        lvsComment.setLvsUpdatedAt(LocalDateTime.now());
        return lvsCommentRepository.save(lvsComment);
    }

    /**
     * Cập nhật bình luận
     * @param lvsComment Thông tin bình luận cập nhật
     * @return Bình luận đã cập nhật
     */
    @Override
    public LvsComment lvsUpdateComment(LvsComment lvsComment) {
        LvsComment lvsExistingComment = lvsGetCommentById(lvsComment.getLvsCommentId());
        if (lvsExistingComment != null) {
            lvsExistingComment.setLvsContent(lvsComment.getLvsContent());
            lvsExistingComment.setLvsUpdatedAt(LocalDateTime.now());
            lvsExistingComment.setLvsIsEdited(true);
            return lvsCommentRepository.save(lvsExistingComment);
        }
        return null;
    }

    /**
     * Xóa bình luận
     * @param lvsCommentId ID bình luận
     */
    @Override
    public void lvsDeleteComment(Long lvsCommentId) {
        lvsCommentRepository.deleteById(lvsCommentId);
    }

    /**
     * Xóa bình luận với lý do
     * @param lvsCommentId ID bình luận
     * @param lvsReason Lý do xóa
     */
    @Override
    public void lvsDeleteComment(Long lvsCommentId, String lvsReason) {
        LvsComment lvsComment = lvsGetCommentById(lvsCommentId);
        if (lvsComment != null) {
            // Thay vì xóa cứng, đánh dấu content
            lvsComment.setLvsContent("[Đã xóa: " + lvsReason + "]");
            lvsComment.setLvsUpdatedAt(LocalDateTime.now());
            lvsCommentRepository.save(lvsComment);
        }
    }

    /**
     * Duyệt bình luận
     * @param lvsCommentId ID bình luận
     * @return Bình luận đã duyệt
     */
    @Override
    public LvsComment lvsApproveComment(Long lvsCommentId) {
        LvsComment lvsComment = lvsGetCommentById(lvsCommentId);
        if (lvsComment != null) {
            lvsComment.setLvsIsApproved(true);
            lvsComment.setLvsUpdatedAt(LocalDateTime.now());
            return lvsCommentRepository.save(lvsComment);
        }
        return null;
    }

    /**
     * Ẩn bình luận
     * @param lvsCommentId ID bình luận
     * @param lvsReason Lý do ẩn
     * @return Bình luận đã ẩn
     */
    @Override
    public LvsComment lvsHideComment(Long lvsCommentId, String lvsReason) {
        LvsComment lvsComment = lvsGetCommentById(lvsCommentId);
        if (lvsComment != null) {
            lvsComment.setLvsIsApproved(false);
            lvsComment.setLvsUpdatedAt(LocalDateTime.now());
            return lvsCommentRepository.save(lvsComment);
        }
        return null;
    }

    /**
     * Thích bình luận
     * @param lvsCommentId ID bình luận
     * @param lvsUserId ID người dùng
     * @return true nếu thành công
     */
    @Override
    public boolean lvsLikeComment(Long lvsCommentId, Long lvsUserId) {
        LvsComment lvsComment = lvsGetCommentById(lvsCommentId);
        if (lvsComment != null) {
            // TODO: Kiểm tra user đã like chưa
            lvsComment.setLvsLikeCount(lvsComment.getLvsLikeCount() + 1);
            lvsCommentRepository.save(lvsComment);
            return true;
        }
        return false;
    }

    /**
     * Báo cáo bình luận
     * @param lvsCommentId ID bình luận
     * @param lvsReporterId ID người báo cáo
     * @param lvsReason Lý do báo cáo
     */
    @Override
    public void lvsReportComment(Long lvsCommentId, Long lvsReporterId, String lvsReason) {
        // TODO: Tạo báo cáo cho bình luận
        System.out.println("Báo cáo bình luận " + lvsCommentId + " bởi user " + lvsReporterId + ": " + lvsReason);
    }
}