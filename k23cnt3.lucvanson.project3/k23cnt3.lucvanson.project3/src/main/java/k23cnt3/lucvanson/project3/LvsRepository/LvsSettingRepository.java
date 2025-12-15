package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsSetting
 * Xử lý truy vấn liên quan đến cài đặt hệ thống
 */
@Repository
public interface LvsSettingRepository extends JpaRepository<LvsSetting, Integer> {

    // Tìm setting theo key
    Optional<LvsSetting> findByLvsKey(String lvsKey);

    // Kiểm tra key đã tồn tại chưa
    boolean existsByLvsKey(String lvsKey);

    // Tìm setting theo group
    List<LvsSetting> findByLvsGroup(String lvsGroup);

    // Tìm setting public
    List<LvsSetting> findByLvsIsPublicTrue();

    // Tìm setting private
    List<LvsSetting> findByLvsIsPublicFalse();

    // Tìm setting theo data type
    List<LvsSetting> findByLvsDataType(String lvsDataType);

    // Đếm setting theo group
    Long countByLvsGroup(String lvsGroup);

    // Đếm setting public
    Long countByLvsIsPublicTrue();

    // Đếm setting private
    Long countByLvsIsPublicFalse();

    // Cập nhật value
    @Modifying
    @Query("UPDATE LvsSetting s SET s.lvsValue = :value, s.lvsUpdatedAt = CURRENT_TIMESTAMP WHERE s.lvsKey = :key")
    void updateValue(@Param("key") String key, @Param("value") String value);

    // Cập nhật nhiều setting
    @Modifying
    @Query("UPDATE LvsSetting s SET s.lvsValue = :value, s.lvsUpdatedAt = CURRENT_TIMESTAMP WHERE s.lvsKey = :key")
    void updateMultipleValues(@Param("keys") List<String> keys, @Param("values") List<String> values);

    // Toggle public
    @Modifying
    @Query("UPDATE LvsSetting s SET s.lvsIsPublic = :isPublic WHERE s.lvsKey = :key")
    void updatePublicStatus(@Param("key") String key, @Param("isPublic") Boolean isPublic);

    // Lấy tất cả group
    @Query("SELECT DISTINCT s.lvsGroup FROM LvsSetting s ORDER BY s.lvsGroup")
    List<String> findAllGroups();

    // Lấy setting có label
    List<LvsSetting> findByLvsLabelIsNotNull();

    // Tìm kiếm setting theo keyword
    @Query("SELECT s FROM LvsSetting s WHERE " +
            "LOWER(s.lvsKey) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.lvsLabel) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.lvsDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.lvsValue) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<LvsSetting> searchSettings(@Param("keyword") String keyword);

    // Lấy giá trị setting
    @Query("SELECT s.lvsValue FROM LvsSetting s WHERE s.lvsKey = :key")
    String getValueByKey(@Param("key") String key);

    // Lấy giá trị setting với default
    @Query("SELECT COALESCE(s.lvsValue, :defaultValue) FROM LvsSetting s WHERE s.lvsKey = :key")
    String getValueByKeyWithDefault(@Param("key") String key, @Param("defaultValue") String defaultValue);
}