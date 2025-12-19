package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPost;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage;
import k23cnt3.lucvanson.project3.LvsRepository.LvsPostRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsPostImageRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsPostService;
import k23cnt3.lucvanson.project3.LvsService.LvsFileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation cho quản lý bài viết
 * Xử lý CRUD bài viết, duyệt, ẩn, tìm kiếm
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsPostServiceImpl implements LvsPostService {

    private final LvsPostRepository lvsPostRepository;
    private final LvsPostImageRepository lvsPostImageRepository;
    private final LvsFileUploadService lvsFileUploadService;

    /**
     * Lấy bài viết theo ID
     * 
     * @param lvsPostId ID bài viết
     * @return Bài viết tìm thấy
     */
    @Override
    public LvsPost lvsGetPostById(Long lvsPostId) {
        return lvsPostRepository.findById(lvsPostId).orElse(null);
    }

    /**
     * Lấy tất cả bài viết với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết
     */
    @Override
    public Page<LvsPost> lvsGetAllPosts(Pageable lvsPageable) {
        return lvsPostRepository.findAll(lvsPageable);
    }

    /**
     * Lấy bài viết đã publish
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết
     */
    @Override
    public Page<LvsPost> lvsGetAllPublishedPosts(Pageable lvsPageable) {
        return lvsPostRepository.findByLvsStatus(LvsPostStatus.PUBLISHED, lvsPageable);
    }

    /**
     * Tìm kiếm bài viết theo keyword
     * 
     * @param lvsKeyword  Từ khóa tìm kiếm
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết tìm thấy
     */
    @Override
    public Page<LvsPost> lvsSearchPosts(String lvsKeyword, Pageable lvsPageable) {
        return lvsPostRepository.findByLvsTitleContainingOrLvsContentContaining(
                lvsKeyword, lvsKeyword, lvsPageable);
    }

    /**
     * Lấy bài viết theo type
     * 
     * @param lvsType     Loại bài viết
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết
     */
    @Override
    public Page<LvsPost> lvsGetPostsByType(String lvsType, Pageable lvsPageable) {
        LvsPostType lvsPostType = LvsPostType.valueOf(lvsType.toUpperCase());
        return lvsPostRepository.findByLvsType(lvsPostType, lvsPageable);
    }

    /**
     * Lấy bài viết theo status
     * 
     * @param lvsStatus   Trạng thái bài viết
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết
     */
    @Override
    public Page<LvsPost> lvsGetPostsByStatus(String lvsStatus, Pageable lvsPageable) {
        LvsPostStatus lvsPostStatus = LvsPostStatus.valueOf(lvsStatus.toUpperCase());
        return lvsPostRepository.findByLvsStatus(lvsPostStatus, lvsPageable);
    }

    /**
     * Lấy bài viết theo type và status
     * 
     * @param lvsStatus   Trạng thái bài viết
     * @param lvsType     Loại bài viết
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết
     */
    @Override
    public Page<LvsPost> lvsGetPostsByStatusAndType(String lvsStatus, String lvsType, Pageable lvsPageable) {
        LvsPostStatus lvsPostStatus = LvsPostStatus.valueOf(lvsStatus.toUpperCase());
        LvsPostType lvsPostType = LvsPostType.valueOf(lvsType.toUpperCase());
        return lvsPostRepository.findByLvsStatusAndLvsType(lvsPostStatus, lvsPostType, lvsPageable);
    }

    /**
     * Lấy bài viết theo người đăng
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang bài viết
     */
    @Override
    public Page<LvsPost> lvsGetPostsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsPostRepository.findByLvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy bài viết mới nhất
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Danh sách bài viết mới nhất
     */
    @Override
    public List<LvsPost> lvsGetNewestPosts(Pageable lvsPageable) {
        return lvsPostRepository.findAllByOrderByLvsCreatedAtDesc(lvsPageable).getContent();
    }

    /**
     * Lấy bài viết phổ biến nhất
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Danh sách bài viết phổ biến
     */
    @Override
    public List<LvsPost> lvsGetPopularPosts(Pageable lvsPageable) {
        return lvsPostRepository.findAllByOrderByLvsViewCountDesc(lvsPageable).getContent();
    }

    /**
     * Lấy bài viết được ghim
     * 
     * @return Danh sách bài viết được ghim
     */
    @Override
    public List<LvsPost> lvsGetPinnedPosts() {
        return lvsPostRepository.findByLvsIsPinnedTrue();
    }

    /**
     * Lưu bài viết
     * 
     * @param lvsPost Thông tin bài viết
     * @return Bài viết đã lưu
     */
    @Override
    public LvsPost lvsSavePost(LvsPost lvsPost) {
        lvsPost.setLvsCreatedAt(LocalDateTime.now());
        lvsPost.setLvsUpdatedAt(LocalDateTime.now());
        return lvsPostRepository.save(lvsPost);
    }

    /**
     * Cập nhật bài viết
     * 
     * @param lvsPost Thông tin bài viết cập nhật
     * @return Bài viết đã cập nhật
     */
    @Override
    public LvsPost lvsUpdatePost(LvsPost lvsPost) {
        LvsPost lvsExistingPost = lvsGetPostById(lvsPost.getLvsPostId());
        if (lvsExistingPost != null) {
            lvsExistingPost.setLvsTitle(lvsPost.getLvsTitle());
            lvsExistingPost.setLvsContent(lvsPost.getLvsContent());
            lvsExistingPost.setLvsType(lvsPost.getLvsType());
            lvsExistingPost.setLvsStatus(lvsPost.getLvsStatus());
            lvsExistingPost.setLvsTags(lvsPost.getLvsTags());
            lvsExistingPost.setLvsUpdatedAt(LocalDateTime.now());
            // TODO: Add setLvsIsEdited method to LvsPost entity
            // lvsExistingPost.setLvsIsEdited(true);
            return lvsPostRepository.save(lvsExistingPost);
        }
        return null;
    }

    /**
     * Xóa bài viết
     * 
     * @param lvsPostId ID bài viết
     */
    @Override
    public void lvsDeletePost(Long lvsPostId) {
        lvsPostRepository.deleteById(lvsPostId);
    }

    /**
     * Xóa bài viết với lý do
     * 
     * @param lvsPostId ID bài viết
     * @param lvsReason Lý do xóa
     */
    @Override
    public void lvsDeletePost(Long lvsPostId, String lvsReason) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsStatus(LvsPostStatus.DELETED);
            lvsPost.setLvsUpdatedAt(LocalDateTime.now());
            lvsPostRepository.save(lvsPost);
        }
    }

    /**
     * Duyệt bài viết
     * 
     * @param lvsPostId ID bài viết
     * @return Bài viết đã duyệt
     */
    @Override
    public LvsPost lvsApprovePost(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsStatus(LvsPostStatus.PUBLISHED);
            lvsPost.setLvsIsApproved(true);
            lvsPost.setLvsUpdatedAt(LocalDateTime.now());
            return lvsPostRepository.save(lvsPost);
        }
        return null;
    }

    /**
     * Ẩn bài viết
     * 
     * @param lvsPostId ID bài viết
     * @param lvsReason Lý do ẩn
     * @return Bài viết đã ẩn
     */
    @Override
    public LvsPost lvsHidePost(Long lvsPostId, String lvsReason) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsStatus(LvsPostStatus.HIDDEN);
            lvsPost.setLvsUpdatedAt(LocalDateTime.now());
            return lvsPostRepository.save(lvsPost);
        }
        return null;
    }

    /**
     * Hiển thị bài viết
     * 
     * @param lvsPostId ID bài viết
     * @return Bài viết đã hiển thị
     */
    @Override
    public LvsPost lvsShowPost(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsStatus(LvsPostStatus.PUBLISHED);
            lvsPost.setLvsUpdatedAt(LocalDateTime.now());
            return lvsPostRepository.save(lvsPost);
        }
        return null;
    }

    /**
     * Ghim bài viết
     * 
     * @param lvsPostId ID bài viết
     * @return Bài viết đã ghim
     */
    @Override
    public LvsPost lvsPinPost(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsIsPinned(true);
            lvsPost.setLvsUpdatedAt(LocalDateTime.now());
            return lvsPostRepository.save(lvsPost);
        }
        return null;
    }

    /**
     * Bỏ ghim bài viết
     * 
     * @param lvsPostId ID bài viết
     * @return Bài viết đã bỏ ghim
     */
    @Override
    public LvsPost lvsUnpinPost(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsIsPinned(false);
            lvsPost.setLvsUpdatedAt(LocalDateTime.now());
            return lvsPostRepository.save(lvsPost);
        }
        return null;
    }

    /**
     * Thích bài viết
     * 
     * @param lvsPostId ID bài viết
     * @param lvsUserId ID người dùng
     * @return true nếu thành công
     */
    @Override
    public boolean lvsLikePost(Long lvsPostId, Long lvsUserId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            // TODO: Kiểm tra user đã like chưa
            lvsPost.setLvsLikeCount(lvsPost.getLvsLikeCount() + 1);
            lvsPostRepository.save(lvsPost);
            return true;
        }
        return false;
    }

    /**
     * Tăng lượt xem
     * 
     * @param lvsPostId ID bài viết
     */
    @Override
    public void lvsIncrementViewCount(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsViewCount(lvsPost.getLvsViewCount() + 1);
            lvsPostRepository.save(lvsPost);
        }
    }

    /**
     * Tăng lượt comment
     * 
     * @param lvsPostId ID bài viết
     */
    @Override
    public void lvsIncrementCommentCount(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsCommentCount(lvsPost.getLvsCommentCount() + 1);
            lvsPostRepository.save(lvsPost);
        }
    }

    /**
     * Tăng lượt share
     * 
     * @param lvsPostId ID bài viết
     */
    @Override
    public void lvsIncrementShareCount(Long lvsPostId) {
        LvsPost lvsPost = lvsGetPostById(lvsPostId);
        if (lvsPost != null) {
            lvsPost.setLvsShareCount(lvsPost.getLvsShareCount() + 1);
            lvsPostRepository.save(lvsPost);
        }
    }

    /**
     * Đếm tổng số bài viết
     * 
     * @return Tổng số bài viết
     */
    @Override
    public Long lvsCountTotalPosts() {
        return lvsPostRepository.count();
    }

    /**
     * Đếm số bài viết theo trạng thái
     * 
     * @param lvsStatus Trạng thái cần đếm
     * @return Số bài viết
     */
    @Override
    public Long lvsCountPostsByStatus(LvsPostStatus lvsStatus) {
        return lvsPostRepository.countByLvsStatus(lvsStatus);
    }

    /**
     * Đếm số bài viết mới trong khoảng thời gian
     * 
     * @param lvsStartDate Ngày bắt đầu
     * @param lvsEndDate   Ngày kết thúc
     * @return Số bài viết mới
     */
    @Override
    public Long lvsCountNewPosts(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        return lvsPostRepository.countByLvsCreatedAtBetween(
                lvsStartDate.atStartOfDay(), lvsEndDate.atTime(23, 59, 59));
    }

    /**
     * Lấy thống kê bài viết
     * 
     * @return Map thống kê bài viết
     */
    @Override
    public Map<String, Long> lvsGetPostStats() {
        Map<String, Long> lvsStats = new HashMap<>();

        // Thống kê theo trạng thái
        for (LvsPostStatus lvsStatus : LvsPostStatus.values()) {
            Long lvsCount = lvsPostRepository.countByLvsStatus(lvsStatus);
            lvsStats.put(lvsStatus.name(), lvsCount);
        }

        // Thống kê theo loại
        for (LvsPostType lvsType : LvsPostType.values()) {
            Long lvsCount = lvsPostRepository.countByLvsType(lvsType);
            lvsStats.put("TYPE_" + lvsType.name(), lvsCount);
        }

        return lvsStats;
    }

    // ==================== IMAGE METHODS IMPLEMENTATION ====================

    /**
     * Lưu bài viết kèm ảnh (tối đa 50 ảnh)
     */
    @Override
    public LvsPost lvsSavePostWithImages(LvsPost lvsPost,
            java.util.List<org.springframework.web.multipart.MultipartFile> images) throws Exception {
        // Lưu bài viết trước
        LvsPost savedPost = lvsSavePost(lvsPost);

        // Upload và lưu ảnh
        if (images != null && !images.isEmpty()) {
            // Giới hạn 50 ảnh
            int maxImages = Math.min(images.size(), 50);

            for (int i = 0; i < maxImages; i++) {
                org.springframework.web.multipart.MultipartFile file = images.get(i);

                if (file != null && !file.isEmpty()) {
                    // Upload file
                    String imageUrl = lvsFileUploadService.lvsSaveFile(file, "posts");

                    // Tạo PostImage entity
                    k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage postImage = new k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage();
                    postImage.setLvsPost(savedPost);
                    postImage.setLvsImageUrl(imageUrl);
                    postImage.setLvsImageOrder(i);

                    lvsPostImageRepository.save(postImage);

                    // Set thumbnail là ảnh đầu tiên
                    if (i == 0) {
                        savedPost.setLvsThumbnailUrl(imageUrl);
                        lvsPostRepository.save(savedPost);
                    }
                }
            }
        }

        return savedPost;
    }

    /**
     * Thêm ảnh vào bài viết đã tồn tại
     */
    @Override
    public void lvsAddImagesToPost(Long lvsPostId,
            java.util.List<org.springframework.web.multipart.MultipartFile> images) throws Exception {
        LvsPost post = lvsGetPostById(lvsPostId);
        if (post == null) {
            throw new Exception("Không tìm thấy bài viết");
        }

        // Đếm số ảnh hiện tại
        long currentImageCount = lvsPostImageRepository.countByLvsPost(post);

        if (images != null && !images.isEmpty()) {
            int startOrder = (int) currentImageCount;
            int maxNewImages = Math.min(images.size(), 50 - (int) currentImageCount);

            for (int i = 0; i < maxNewImages; i++) {
                org.springframework.web.multipart.MultipartFile file = images.get(i);

                if (file != null && !file.isEmpty()) {
                    String imageUrl = lvsFileUploadService.lvsSaveFile(file, "posts");

                    k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage postImage = new k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage();
                    postImage.setLvsPost(post);
                    postImage.setLvsImageUrl(imageUrl);
                    postImage.setLvsImageOrder(startOrder + i);

                    lvsPostImageRepository.save(postImage);
                }
            }
        }
    }

    /**
     * Xóa một ảnh của bài viết
     */
    @Override
    public void lvsDeletePostImage(Long lvsImageId) {
        lvsPostImageRepository.findById(lvsImageId).ifPresent(image -> {
            // Xóa file vật lý
            lvsFileUploadService.lvsDeleteFile(image.getLvsImageUrl());

            // Xóa record trong database
            lvsPostImageRepository.delete(image);
        });
    }

    /**
     * Lấy danh sách ảnh của bài viết
     */
    @Override
    public java.util.List<k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage> lvsGetPostImages(Long lvsPostId) {
        LvsPost post = lvsGetPostById(lvsPostId);
        if (post != null) {
            return lvsPostImageRepository.findByLvsPostOrderByLvsImageOrderAsc(post);
        }
        return new java.util.ArrayList<>();
    }
}