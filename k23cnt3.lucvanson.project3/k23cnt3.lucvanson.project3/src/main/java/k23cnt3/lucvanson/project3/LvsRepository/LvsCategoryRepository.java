package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsCategory
 * Xử lý truy vấn liên quan đến danh mục
 */
@Repository
public interface LvsCategoryRepository extends JpaRepository<LvsCategory, Integer> {

    // Tìm category theo name
    Optional<LvsCategory> findByLvsCategoryName(String lvsCategoryName);

    // Kiểm tra category name đã tồn tại chưa
    boolean existsByLvsCategoryName(String lvsCategoryName);

    // Tìm category active
    List<LvsCategory> findByLvsIsActiveTrue();
    Page<LvsCategory> findByLvsIsActiveTrue(Pageable pageable);

    // Tìm category inactive
    List<LvsCategory> findByLvsIsActiveFalse();
    Page<LvsCategory> findByLvsIsActiveFalse(Pageable pageable);

    // Lấy category theo sort order
    List<LvsCategory> findByOrderByLvsSortOrderAsc();
    Page<LvsCategory> findByOrderByLvsSortOrderAsc(Pageable pageable);

    // Đếm category active
    Long countByLvsIsActiveTrue();

    // Đếm category inactive
    Long countByLvsIsActiveFalse();

    // Tăng project count
    @Modifying
    @Query("UPDATE LvsCategory c SET c.lvsProjectCount = c.lvsProjectCount + 1 WHERE c.lvsCategoryId = :categoryId")
    void incrementProjectCount(@Param("categoryId") Integer categoryId);

    // Giảm project count
    @Modifying
    @Query("UPDATE LvsCategory c SET c.lvsProjectCount = c.lvsProjectCount - 1 WHERE c.lvsCategoryId = :categoryId")
    void decrementProjectCount(@Param("categoryId") Integer categoryId);

    // Cập nhật project count
    @Modifying
    @Query("UPDATE LvsCategory c SET c.lvsProjectCount = :projectCount WHERE c.lvsCategoryId = :categoryId")
    void updateProjectCount(@Param("categoryId") Integer categoryId, @Param("projectCount") Integer projectCount);

    // Toggle active
    @Modifying
    @Query("UPDATE LvsCategory c SET c.lvsIsActive = :active WHERE c.lvsCategoryId = :categoryId")
    void updateActiveStatus(@Param("categoryId") Integer categoryId, @Param("active") Boolean active);

    // Cập nhật sort order
    @Modifying
    @Query("UPDATE LvsCategory c SET c.lvsSortOrder = :sortOrder WHERE c.lvsCategoryId = :categoryId")
    void updateSortOrder(@Param("categoryId") Integer categoryId, @Param("sortOrder") Integer sortOrder);

    // Lấy category có nhiều project nhất
    @Query("SELECT c FROM LvsCategory c ORDER BY c.lvsProjectCount DESC")
    Page<LvsCategory> findTopByProjectCount(Pageable pageable);

    // Lấy tổng số project trong tất cả category
    @Query("SELECT SUM(c.lvsProjectCount) FROM LvsCategory c")
    Long getTotalProjectCount();

    // Tìm kiếm category theo keyword
    @Query("SELECT c FROM LvsCategory c WHERE " +
            "LOWER(c.lvsCategoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lvsDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<LvsCategory> searchCategories(@Param("keyword") String keyword, Pageable pageable);

    // Lấy category có icon
    List<LvsCategory> findByLvsIconIsNotNull();

    // Lấy category có color
    List<LvsCategory> findByLvsColorIsNotNull();
}