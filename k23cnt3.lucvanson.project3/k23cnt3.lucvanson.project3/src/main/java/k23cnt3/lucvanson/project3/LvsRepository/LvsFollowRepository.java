package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsFollow
 * Xử lý truy vấn liên quan đến theo dõi
 */
@Repository
public interface LvsFollowRepository extends JpaRepository<LvsFollow, Long> {

    // Tìm follow theo follower
    List<LvsFollow> findByLvsFollower_LvsUserId(Long lvsFollowerId);
    Page<LvsFollow> findByLvsFollower_LvsUserId(Long lvsFollowerId, Pageable pageable);

    // Tìm follow theo following
    List<LvsFollow> findByLvsFollowing_LvsUserId(Long lvsFollowingId);
    Page<LvsFollow> findByLvsFollowing_LvsUserId(Long lvsFollowingId, Pageable pageable);

    // Tìm follow theo follower và following
    Optional<LvsFollow> findByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(Long lvsFollowerId, Long lvsFollowingId);

    // Kiểm tra đang follow
    boolean existsByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(Long lvsFollowerId, Long lvsFollowingId);

    // Đếm follower
    Long countByLvsFollowing_LvsUserId(Long lvsFollowingId);

    // Đếm following
    Long countByLvsFollower_LvsUserId(Long lvsFollowerId);

    // Xóa follow
    void deleteByLvsFollower_LvsUserIdAndLvsFollowing_LvsUserId(Long lvsFollowerId, Long lvsFollowingId);

    // Lấy danh sách user đang follow tôi nhưng tôi chưa follow lại
    @Query("SELECT f.lvsFollower FROM LvsFollow f WHERE f.lvsFollowing.lvsUserId = :userId AND " +
            "NOT EXISTS (SELECT f2 FROM LvsFollow f2 WHERE f2.lvsFollower.lvsUserId = :userId AND f2.lvsFollowing.lvsUserId = f.lvsFollower.lvsUserId)")
    List<Object[]> getFollowersNotFollowingBack(@Param("userId") Long userId);

    // Lấy danh sách tôi đang follow nhưng không follow lại tôi
    @Query("SELECT f.lvsFollowing FROM LvsFollow f WHERE f.lvsFollower.lvsUserId = :userId AND " +
            "NOT EXISTS (SELECT f2 FROM LvsFollow f2 WHERE f2.lvsFollower.lvsUserId = f.lvsFollowing.lvsUserId AND f2.lvsFollowing.lvsUserId = :userId)")
    List<Object[]> getFollowingNotFollowingBack(@Param("userId") Long userId);

    // Lấy gợi ý follow (user chưa follow, có nhiều follower chung)
    @Query("SELECT u FROM LvsUser u WHERE u.lvsUserId != :userId AND " +
            "NOT EXISTS (SELECT f FROM LvsFollow f WHERE f.lvsFollower.lvsUserId = :userId AND f.lvsFollowing.lvsUserId = u.lvsUserId) " +
            "ORDER BY (SELECT COUNT(f2) FROM LvsFollow f2 WHERE f2.lvsFollowing.lvsUserId = u.lvsUserId AND " +
            "EXISTS (SELECT f3 FROM LvsFollow f3 WHERE f3.lvsFollower.lvsUserId = :userId AND f3.lvsFollowing.lvsUserId = f2.lvsFollower.lvsUserId)) DESC")
    Page<Object[]> getFollowSuggestions(@Param("userId") Long userId, Pageable pageable);

    // Lấy mutual follows (cùng follow nhau)
    @Query("SELECT f1.lvsFollowing FROM LvsFollow f1 WHERE f1.lvsFollower.lvsUserId = :userId AND " +
            "EXISTS (SELECT f2 FROM LvsFollow f2 WHERE f2.lvsFollower.lvsUserId = f1.lvsFollowing.lvsUserId AND f2.lvsFollowing.lvsUserId = :userId)")
    List<Object[]> getMutualFollows(@Param("userId") Long userId);
}