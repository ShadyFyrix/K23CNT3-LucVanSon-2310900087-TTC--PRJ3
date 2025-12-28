package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsUser
 * Xử lý truy vấn liên quan đến người dùng
 */
@Repository
public interface LvsUserRepository extends JpaRepository<LvsUser, Long> {

    // Tìm user theo username
    Optional<LvsUser> findByLvsUsername(String lvsUsername);

    // Tìm user theo email
    Optional<LvsUser> findByLvsEmail(String lvsEmail);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByLvsUsername(String lvsUsername);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByLvsEmail(String lvsEmail);

    // Tìm user theo role
    List<LvsUser> findByLvsRole(LvsUser.LvsRole lvsRole);

    Page<LvsUser> findByLvsRole(LvsUser.LvsRole lvsRole, Pageable pageable);

    // Tìm user theo status
    List<LvsUser> findByLvsStatus(LvsUser.LvsUserStatus lvsStatus);

    Page<LvsUser> findByLvsStatus(LvsUser.LvsUserStatus lvsStatus, Pageable pageable);

    // Tìm user theo role và status
    List<LvsUser> findByLvsRoleAndLvsStatus(LvsUser.LvsRole lvsRole, LvsUser.LvsUserStatus lvsStatus);

    Page<LvsUser> findByLvsRoleAndLvsStatus(LvsUser.LvsRole lvsRole, LvsUser.LvsUserStatus lvsStatus,
            Pageable pageable);

    // Tìm kiếm user theo keyword (username, email, fullname)
    @Query("SELECT u FROM LvsUser u WHERE " +
            "LOWER(u.lvsUsername) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lvsEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lvsFullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<LvsUser> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    // Đếm số user theo role
    Long countByLvsRole(LvsUser.LvsRole lvsRole);

    // Đếm số user theo status
    Long countByLvsStatus(LvsUser.LvsUserStatus lvsStatus);

    // Tìm user đăng ký trong khoảng thời gian
    List<LvsUser> findByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Đếm user đăng ký trong khoảng thời gian
    Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm user có coin lớn hơn
    List<LvsUser> findByLvsCoinGreaterThan(Double minCoin);

    // Tìm user có balance lớn hơn
    List<LvsUser> findByLvsBalanceGreaterThan(Double minBalance);

    // Cập nhật last login
    @Query("UPDATE LvsUser u SET u.lvsLastLogin = :lastLogin WHERE u.lvsUserId = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);

    // Tìm user chưa đăng nhập trong thời gian dài
    List<LvsUser> findByLvsLastLoginBefore(LocalDateTime date);

    // Tìm user theo phone
    Optional<LvsUser> findByLvsPhone(String lvsPhone);

    // Tìm user active
    List<LvsUser> findByLvsStatusAndLvsRole(LvsUser.LvsUserStatus status, LvsUser.LvsRole role);

    // Lấy top user theo coin
    @Query("SELECT u FROM LvsUser u ORDER BY u.lvsCoin DESC")
    Page<LvsUser> findTopByCoin(Pageable pageable);

    // Lấy top user theo balance (doanh thu)
    @Query("SELECT u FROM LvsUser u ORDER BY u.lvsBalance DESC")
    Page<LvsUser> findTopByBalance(Pageable pageable);

    /**
     * Lấy top user theo Activity Score tổng hợp
     * Score = (Coin × 0.25) + (Projects × 50 × 0.20) + (Comments × 10 × 0.15) +
     * (Posts × 20 × 0.10) + (Reviews × 15 × 0.05)
     * 
     * Weighted factors:
     * - Coin: 25% - Financial contribution
     * - Projects: 20% - Content creation
     * - Comments: 15% - Engagement (via subquery)
     * - Posts: 10% - Community content
     * - Reviews: 5% - Quality feedback
     */
    @Query("""
            SELECT u FROM LvsUser u
            LEFT JOIN u.lvsProjects p
            LEFT JOIN u.lvsPosts post
            LEFT JOIN u.lvsReviews r
            WHERE u.lvsStatus = 'ACTIVE'
            GROUP BY u.lvsUserId
            ORDER BY (
                COALESCE(u.lvsCoin, 0) * 0.25 +
                COUNT(DISTINCT CASE WHEN p.lvsStatus = 'APPROVED' THEN p.lvsProjectId END) * 50 * 0.20 +
                (SELECT COUNT(c) FROM LvsComment c WHERE c.lvsUser.lvsUserId = u.lvsUserId) * 10 * 0.15 +
                COUNT(DISTINCT post.lvsPostId) * 20 * 0.10 +
                COUNT(DISTINCT r.lvsReviewId) * 15 * 0.05
            ) DESC
            """)
    Page<LvsUser> findTopByActivityScore(Pageable pageable);

    /**
     * Lấy top user theo Coin balance
     * Chỉ xếp hạng theo số coin hiện có
     */
    @Query("SELECT u FROM LvsUser u WHERE u.lvsStatus = 'ACTIVE' ORDER BY u.lvsCoin DESC")
    Page<LvsUser> findTopByCoinBalance(Pageable pageable);

    /**
     * Lấy top user theo Sales (Balance/Revenue)
     * Xếp hạng theo doanh thu từ bán projects
     */
    @Query("SELECT u FROM LvsUser u WHERE u.lvsStatus = 'ACTIVE' ORDER BY u.lvsBalance DESC")
    Page<LvsUser> findTopBySales(Pageable pageable);

    // Kiểm tra phone đã tồn tại chưa
    boolean existsByLvsPhone(String lvsPhone);

    // Tìm user có username hoặc email
    Optional<LvsUser> findByLvsUsernameOrLvsEmail(String username, String email);

    // Tìm user đầu tiên theo role
    Optional<LvsUser> findFirstByLvsRoleOrderByLvsUserIdAsc(LvsUser.LvsRole role);
}