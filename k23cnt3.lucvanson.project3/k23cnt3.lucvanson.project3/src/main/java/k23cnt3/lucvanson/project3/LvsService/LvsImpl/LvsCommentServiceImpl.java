package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsComment;
import k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage;
import k23cnt3.lucvanson.project3.LvsRepository.LvsCommentRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsCommentImageRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsCommentService;
import k23cnt3.lucvanson.project3.LvsService.LvsFileUploadService;
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
    private final LvsCommentImageRepository lvsCommentImageRepository;
    private final LvsFileUploadService lvsFileUploadService;

    /**
     * Lấy bình luận theo ID
     * 
     * @param lvsCommentId ID bình luận
     * @return Bình luận tìm thấy
     */
    @Override
    public LvsComment lvsGetCommentById(Long lvsCommentId) {
        return lvsCommentRepository.findById(lvsCommentId).orElse(null);
    }

    /**
     * Lấy tất cả bình luận với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetAllComments(Pageable lvsPageable) {
        return lvsCommentRepository.findAll(lvsPageable);
    }

    /**
     * Lấy bình luận theo bài viết
     * 
     * @param lvsPostId   ID bài viết
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetCommentsByPost(Long lvsPostId, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsPost_LvsPostId(lvsPostId, lvsPageable);
    }

    /**
     * Lấy bình luận theo user
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetCommentsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy bình luận theo approval
     * 
     * @param lvsIsApproved Trạng thái duyệt
     * @param lvsPageable   Thông tin phân trang
     * @return Trang bình luận
     */
    @Override
    public Page<LvsComment> lvsGetCommentsByApproval(Boolean lvsIsApproved, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsIsApproved(lvsIsApproved, lvsPageable);
    }

    /**
     * Lấy reply cho bình luận
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang reply
     */
    @Override
    public Page<LvsComment> lvsGetRepliesToUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsCommentRepository.findByLvsParent_LvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lưu bình luận
     * 
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
     * 
     * @param commentId  ID bình luận
     * @param newContent Nội dung mới
     * @return Bình luận đã cập nhật
     */
    @Override
    public LvsComment lvsUpdateComment(Long commentId, String newContent) {
        LvsComment comment = lvsCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setLvsContent(newContent);
        comment.setLvsIsEdited(true);
        comment.setLvsUpdatedAt(LocalDateTime.now());
        return lvsCommentRepository.save(comment);
    }

    /**
     * Xóa bình luận
     * 
     * @param lvsCommentId ID bình luận
     */
    @Override
    public void lvsDeleteComment(Long lvsCommentId) {
        lvsCommentRepository.deleteById(lvsCommentId);
    }

    /**
     * Xóa bình luận với lý do - HARD DELETE
     * 
     * @param commentId ID bình luận
     * @param reason    Lý do xóa
     */
    @Override
    public void lvsDeleteComment(Long commentId, String reason) {
        LvsComment comment = lvsCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // HARD DELETE: Delete all images first
        List<LvsCommentImage> images = lvsCommentImageRepository
                .findByLvsComment_LvsCommentIdOrderByLvsImageOrderAsc(commentId);
        if (!images.isEmpty()) {
            for (LvsCommentImage img : images) {
                // Delete physical file using service
                lvsFileUploadService.lvsDeleteFile(img.getLvsImageUrl());
            }
            // Delete from database
            lvsCommentImageRepository.deleteAll(images);
        }

        // Delete comment from database
        lvsCommentRepository.delete(comment);
        System.out.println("Hard deleted comment ID: " + commentId);
    }

    /**
     * Duyệt bình luận
     * 
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
     * 
     * @param lvsCommentId ID bình luận
     * @param lvsReason    Lý do ẩn
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
     * 
     * @param lvsCommentId ID bình luận
     * @param lvsUserId    ID người dùng
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
     * 
     * @param lvsCommentId  ID bình luận
     * @param lvsReporterId ID người báo cáo
     * @param lvsReason     Lý do báo cáo
     */
    @Override
    public void lvsReportComment(Long lvsCommentId, Long lvsReporterId, String lvsReason) {
        // TODO: Tạo báo cáo cho bình luận
        System.out.println("Báo cáo bình luận " + lvsCommentId + " bởi user " + lvsReporterId + ": " + lvsReason);
    }

    // ==================== IMAGE METHODS ====================

    /**
     * Lưu comment với ảnh
     */
    @Override
    public LvsComment lvsSaveCommentWithImages(LvsComment lvsComment,
            org.springframework.web.multipart.MultipartFile[] images) throws java.io.IOException {
        // Lưu comment trước
        LvsComment savedComment = lvsSaveComment(lvsComment);

        // Nếu có ảnh, lưu ảnh
        if (images != null && images.length > 0) {
            lvsAddImagesToComment(savedComment.getLvsCommentId(), images);
        }

        return savedComment;
    }

    /**
     * Thêm ảnh vào comment
     */
    @Override
    public void lvsAddImagesToComment(Long lvsCommentId, org.springframework.web.multipart.MultipartFile[] images)
            throws java.io.IOException {
        LvsComment comment = lvsGetCommentById(lvsCommentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }

        // Giới hạn tối đa 5 ảnh cho comment
        int currentImageCount = comment.getLvsImages() != null ? comment.getLvsImages().size() : 0;
        int maxImages = 5;

        if (currentImageCount >= maxImages) {
            throw new RuntimeException("Comment đã đạt giới hạn " + maxImages + " ảnh");
        }

        // Upload từng ảnh
        int order = currentImageCount;
        for (org.springframework.web.multipart.MultipartFile file : images) {
            if (file != null && !file.isEmpty()) {
                if (order >= maxImages) {
                    break; // Dừng nếu đã đủ 5 ảnh
                }

                // Upload file
                String imageUrl = lvsFileUploadService.lvsSaveFile(file, "comments");

                // Tạo CommentImage entity
                k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage commentImage = new k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage();
                commentImage.setLvsComment(comment);
                commentImage.setLvsImageUrl(imageUrl);
                commentImage.setLvsImageOrder(order++);
                commentImage.setLvsCreatedAt(java.time.LocalDateTime.now());

                lvsCommentImageRepository.save(commentImage);
            }
        }
    }

    /**
     * Xóa ảnh của comment
     */
    @Override
    public Long lvsDeleteCommentImage(Long imageId) {
        k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage commentImage = lvsCommentImageRepository.findById(imageId)
                .orElse(null);
        if (commentImage != null) {
            Long commentId = commentImage.getLvsComment().getLvsCommentId();

            // Xóa file
            lvsFileUploadService.lvsDeleteFile(commentImage.getLvsImageUrl());

            // Xóa record
            lvsCommentImageRepository.delete(commentImage);

            return commentId;
        }
        return null;
    }

    /**
     * Lấy ảnh của comment
     */
    @Override
    public java.util.List<k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage> lvsGetCommentImages(Long lvsCommentId) {
        return lvsCommentImageRepository.findByLvsComment_LvsCommentIdOrderByLvsImageOrderAsc(lvsCommentId);
    }
}