package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface cho quản lý bình luận
 * Xử lý viết, chỉnh sửa, xóa, duyệt bình luận
 */
public interface LvsCommentService {

        // Lấy bình luận theo ID
        LvsComment lvsGetCommentById(Long lvsCommentId);

        // Lấy tất cả bình luận
        Page<LvsComment> lvsGetAllComments(Pageable lvsPageable);

        // Lấy bình luận theo bài viết
        Page<LvsComment> lvsGetCommentsByPost(Long lvsPostId, Pageable lvsPageable);

        // Lấy bình luận theo user
        Page<LvsComment> lvsGetCommentsByUser(Long lvsUserId, Pageable lvsPageable);

        // Lấy bình luận theo approval
        Page<LvsComment> lvsGetCommentsByApproval(Boolean lvsIsApproved, Pageable lvsPageable);

        // Lấy reply cho bình luận
        Page<LvsComment> lvsGetRepliesToUser(Long lvsUserId, Pageable lvsPageable);

        // Lưu bình luận
        LvsComment lvsSaveComment(LvsComment lvsComment);

        // Cập nhật bình luận
        LvsComment lvsUpdateComment(Long commentId, String newContent);

        // Xóa bình luận
        void lvsDeleteComment(Long lvsCommentId);

        // Xóa bình luận với lý do
        void lvsDeleteComment(Long lvsCommentId, String lvsReason);

        // Duyệt bình luận
        LvsComment lvsApproveComment(Long lvsCommentId);

        // Ẩn bình luận
        LvsComment lvsHideComment(Long lvsCommentId, String lvsReason);

        // Thích bình luận
        boolean lvsLikeComment(Long lvsCommentId, Long lvsUserId);

        // Báo cáo bình luận
        void lvsReportComment(Long lvsCommentId, Long lvsReporterId, String lvsReason);

        // ==================== IMAGE METHODS ====================

        // Lưu comment với ảnh
        LvsComment lvsSaveCommentWithImages(LvsComment lvsComment,
                        org.springframework.web.multipart.MultipartFile[] images)
                        throws java.io.IOException;

        // Thêm ảnh vào comment
        void lvsAddImagesToComment(Long lvsCommentId, org.springframework.web.multipart.MultipartFile[] images)
                        throws java.io.IOException;

        // Xóa ảnh của comment
        Long lvsDeleteCommentImage(Long imageId);

        // Lấy ảnh của comment
        List<k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage> lvsGetCommentImages(Long lvsCommentId);
}