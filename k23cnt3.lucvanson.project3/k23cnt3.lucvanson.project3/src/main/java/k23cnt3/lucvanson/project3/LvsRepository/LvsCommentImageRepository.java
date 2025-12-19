package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsComment;
import k23cnt3.lucvanson.project3.LvsEntity.LvsCommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho LvsCommentImage
 * Quản lý ảnh của bình luận (tối đa 5 ảnh)
 */
@Repository
public interface LvsCommentImageRepository extends JpaRepository<LvsCommentImage, Long> {

    /**
     * Lấy tất cả ảnh của một comment, sắp xếp theo thứ tự
     */
    List<LvsCommentImage> findByLvsCommentOrderByLvsImageOrderAsc(LvsComment lvsComment);

    /**
     * Lấy tất cả ảnh của một comment dựa trên ID của comment, sắp xếp theo thứ tự
     */
    List<LvsCommentImage> findByLvsComment_LvsCommentIdOrderByLvsImageOrderAsc(Long commentId);

    /**
     * Xóa tất cả ảnh của một comment
     */
    void deleteByLvsComment(LvsComment lvsComment);

    /**
     * Đếm số lượng ảnh của một comment
     */
    long countByLvsComment(LvsComment lvsComment);
}
