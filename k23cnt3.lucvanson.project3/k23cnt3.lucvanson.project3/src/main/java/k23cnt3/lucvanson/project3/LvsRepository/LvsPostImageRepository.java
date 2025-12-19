package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPost;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho LvsPostImage
 * Quản lý ảnh của bài viết
 */
@Repository
public interface LvsPostImageRepository extends JpaRepository<LvsPostImage, Long> {

    /**
     * Lấy tất cả ảnh của một bài viết, sắp xếp theo thứ tự
     */
    List<LvsPostImage> findByLvsPostOrderByLvsImageOrderAsc(LvsPost lvsPost);

    /**
     * Xóa tất cả ảnh của một bài viết
     */
    void deleteByLvsPost(LvsPost lvsPost);

    /**
     * Đếm số lượng ảnh của một bài viết
     */
    long countByLvsPost(LvsPost lvsPost);
}
