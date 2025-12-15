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
    List<LvsUser> lvsGetTopBuyers(int lvsLimit);
    List<Object[]> lvsGetTopSellingProjects(Long lvsUserId, int lvsLimit);
    Map<String, Long> lvsGetUserStatsLast30Days();
    Map<String, Object> lvsGetUserRegistrationChartData();

    // Admin and session
    boolean lvsIsAdmin(HttpSession session);
}