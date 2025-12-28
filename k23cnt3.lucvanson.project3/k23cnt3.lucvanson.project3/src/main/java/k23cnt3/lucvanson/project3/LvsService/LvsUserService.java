package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsRole;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsUserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LvsUserService {

    // User CRUD operations
    LvsUser lvsGetCurrentUser();

    LvsUser lvsGetUserById(Long lvsUserId);

    LvsUser lvsGetUserByUsername(String lvsUsername);

    LvsUser lvsGetUserByEmail(String lvsEmail);

    Page<LvsUser> lvsGetAllUsers(Pageable lvsPageable);

    Page<LvsUser> lvsSearchUsers(String lvsKeyword, Pageable lvsPageable);

    Page<LvsUser> lvsGetUsersByRole(LvsRole lvsRole, Pageable lvsPageable);

    Page<LvsUser> lvsGetUsersByStatus(LvsUserStatus lvsStatus, Pageable lvsPageable);

    Page<LvsUser> lvsGetUsersByRoleAndStatus(LvsRole lvsRole, LvsUserStatus lvsStatus, Pageable lvsPageable);

    LvsUser lvsCreateUser(LvsUser lvsUser);

    LvsUser lvsUpdateUser(LvsUser lvsUser);

    // Delete operations - ADDED lvsHardDeleteUser
    void lvsDeleteUser(Long lvsUserId, String lvsReason); // Soft delete

    void lvsHardDeleteUser(Long lvsUserId, String lvsReason); // Hard delete

    // User management
    LvsUser lvsChangeUserRole(Long lvsUserId, LvsRole lvsRole);

    LvsUser lvsBanUser(Long lvsUserId, Long lvsAdminId, String lvsReason);

    LvsUser lvsUnbanUser(Long lvsUserId);

    // Password management
    String lvsResetPassword(Long lvsUserId);

    boolean lvsChangePassword(Long lvsUserId, String lvsCurrentPassword, String lvsNewPassword);

    String lvsEncodePassword(String lvsPassword);

    boolean lvsCheckPassword(LvsUser lvsUser, String lvsPassword);

    // User profile
    LvsUser lvsUpdateAvatar(Long lvsUserId, String lvsAvatarUrl);

    // Account status
    boolean lvsDeactivateAccount(Long lvsUserId);

    boolean lvsActivateAccount(Long lvsUserId);

    // Balance management
    LvsUser lvsUpdateCoinBalance(Long lvsUserId, Double lvsAmount);

    LvsUser lvsUpdateRevenueBalance(Long lvsUserId, Double lvsAmount);

    Double lvsGetTotalRevenue(Long lvsUserId);

    Double lvsGetTotalWithdrawn(Long lvsUserId);

    // Statistics and reports
    Long lvsCountTotalUsers();

    Long lvsCountNewUsers(LocalDate lvsStartDate, LocalDate lvsEndDate);

    /**
     * Get top active users based on comprehensive activity score
     * 
     * Activity Score Formula:
     * - Coin balance: 25% weight
     * - Projects created: 20% weight (50 points each)
     * - Comments: 15% weight (10 points each)
     * - Posts created: 10% weight (20 points each)
     * - Reviews written: 5% weight (15 points each)
     * 
     * Only includes ACTIVE users
     * 
     * @param lvsLimit Number of top users to retrieve
     * @return List of top active users ordered by activity score
     */
    List<LvsUser> lvsGetTopBuyers(int lvsLimit);

    /**
     * Get top users by coin balance only
     * 
     * @param lvsLimit Number of top users to retrieve
     * @return List of users with highest coin balance
     */
    List<LvsUser> lvsGetTopByCoin(int lvsLimit);

    /**
     * Get top users by sales (balance/revenue)
     * 
     * @param lvsLimit Number of top users to retrieve
     * @return List of users with highest sales revenue
     */
    List<LvsUser> lvsGetTopBySales(int lvsLimit);

    /**
     * Get top users by comprehensive activity score
     * Same as lvsGetTopBuyers but with clearer naming
     * 
     * @param lvsLimit Number of top users to retrieve
     * @return List of most active users
     */
    List<LvsUser> lvsGetTopByActivity(int lvsLimit);

    List<Object[]> lvsGetTopSellingProjects(Long lvsUserId, int lvsLimit);

    Map<String, Long> lvsGetUserStatsLast30Days();

    Map<String, Object> lvsGetUserRegistrationChartData();

    // Admin and session
    boolean lvsIsAdmin(HttpSession session);
}