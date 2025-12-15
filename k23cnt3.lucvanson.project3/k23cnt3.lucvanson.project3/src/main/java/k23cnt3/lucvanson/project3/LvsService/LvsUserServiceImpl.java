package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsRole;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsUserStatus;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LvsUserServiceImpl implements LvsUserService {

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    @Override
    public LvsUser lvsGetCurrentUser() {
        Long lvsUserId = (Long) session.getAttribute("lvsUserId");
        if (lvsUserId != null) {
            return lvsUserRepository.findById(lvsUserId).orElse(null);
        }
        return null;
    }

    @Override
    public LvsUser lvsGetUserById(Long lvsUserId) {
        return lvsUserRepository.findById(lvsUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + lvsUserId));
    }

    @Override
    public LvsUser lvsGetUserByUsername(String lvsUsername) {
        return lvsUserRepository.findByLvsUsername(lvsUsername)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + lvsUsername));
    }

    @Override
    public LvsUser lvsGetUserByEmail(String lvsEmail) {
        return lvsUserRepository.findByLvsEmail(lvsEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + lvsEmail));
    }

    @Override
    public Page<LvsUser> lvsGetAllUsers(Pageable lvsPageable) {
        return lvsUserRepository.findAll(lvsPageable);
    }

    @Override
    public Page<LvsUser> lvsSearchUsers(String lvsKeyword, Pageable lvsPageable) {
        return lvsUserRepository.searchUsers(lvsKeyword, lvsPageable);
    }

    @Override
    public Page<LvsUser> lvsGetUsersByRole(LvsRole lvsRole, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsRole(lvsRole, lvsPageable);
    }

    @Override
    public Page<LvsUser> lvsGetUsersByStatus(LvsUserStatus lvsStatus, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsStatus(lvsStatus, lvsPageable);
    }

    @Override
    public Page<LvsUser> lvsGetUsersByRoleAndStatus(LvsRole lvsRole, LvsUserStatus lvsStatus, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsRoleAndLvsStatus(lvsRole, lvsStatus, lvsPageable);
    }

    @Override
    public LvsUser lvsCreateUser(LvsUser lvsUser) {
        // Check if username exists
        if (lvsUserRepository.existsByLvsUsername(lvsUser.getLvsUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (lvsUserRepository.existsByLvsEmail(lvsUser.getLvsEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check phone if provided
        if (lvsUser.getLvsPhone() != null && !lvsUser.getLvsPhone().isEmpty()
                && lvsUserRepository.existsByLvsPhone(lvsUser.getLvsPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Encode password
        lvsUser.setLvsPassword(passwordEncoder.encode(lvsUser.getLvsPassword()));

        // Set default values if not provided
        if (lvsUser.getLvsRole() == null) {
            lvsUser.setLvsRole(LvsRole.USER);
        }
        if (lvsUser.getLvsStatus() == null) {
            lvsUser.setLvsStatus(LvsUserStatus.ACTIVE);
        }
        if (lvsUser.getLvsCoin() == null) {
            lvsUser.setLvsCoin(0.0);
        }
        if (lvsUser.getLvsBalance() == null) {
            lvsUser.setLvsBalance(0.0);
        }

        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        lvsUser.setLvsCreatedAt(now);
        lvsUser.setLvsUpdatedAt(now);

        return lvsUserRepository.save(lvsUser);
    }

    @Override
    public LvsUser lvsUpdateUser(LvsUser lvsUser) {
        LvsUser lvsExisting = lvsGetUserById(lvsUser.getLvsUserId());

        // Check username uniqueness if changed
        if (!lvsExisting.getLvsUsername().equals(lvsUser.getLvsUsername())
                && lvsUserRepository.existsByLvsUsername(lvsUser.getLvsUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check email uniqueness if changed
        if (!lvsExisting.getLvsEmail().equals(lvsUser.getLvsEmail())
                && lvsUserRepository.existsByLvsEmail(lvsUser.getLvsEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check phone uniqueness if changed
        if (lvsUser.getLvsPhone() != null && !lvsUser.getLvsPhone().isEmpty()
                && !lvsUser.getLvsPhone().equals(lvsExisting.getLvsPhone())
                && lvsUserRepository.existsByLvsPhone(lvsUser.getLvsPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Update fields
        lvsExisting.setLvsUsername(lvsUser.getLvsUsername());
        lvsExisting.setLvsEmail(lvsUser.getLvsEmail());
        lvsExisting.setLvsFullName(lvsUser.getLvsFullName());
        lvsExisting.setLvsPhone(lvsUser.getLvsPhone());
        lvsExisting.setLvsAddress(lvsUser.getLvsAddress());
        lvsExisting.setLvsBio(lvsUser.getLvsBio());
        lvsExisting.setLvsRole(lvsUser.getLvsRole());
        lvsExisting.setLvsStatus(lvsUser.getLvsStatus());
        lvsExisting.setLvsCoin(lvsUser.getLvsCoin());
        lvsExisting.setLvsBalance(lvsUser.getLvsBalance());
        lvsExisting.setLvsAvatarUrl(lvsUser.getLvsAvatarUrl());
        lvsExisting.setLvsUpdatedAt(LocalDateTime.now());

        return lvsUserRepository.save(lvsExisting);
    }

    @Override
    public void lvsDeleteUser(Long lvsUserId, String lvsReason) {
        // SOFT DELETE - chỉ đổi status thành INACTIVE
        LvsUser lvsUser = lvsGetUserById(lvsUserId);

        System.out.println("SOFT DELETE: Changing status to INACTIVE for user: " +
                lvsUser.getLvsUsername() + ", ID: " + lvsUserId + ", Reason: " + lvsReason);

        lvsUser.setLvsStatus(LvsUserStatus.INACTIVE);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());

        // Thêm ghi chú lý do xóa vào bio
        String currentBio = lvsUser.getLvsBio() != null ? lvsUser.getLvsBio() : "";
        lvsUser.setLvsBio(currentBio + "\n[Account deactivated on " + LocalDateTime.now() +
                " - Reason: " + lvsReason + "]");

        lvsUserRepository.save(lvsUser);
    }

    @Override
    public void lvsHardDeleteUser(Long lvsUserId, String lvsReason) {
        // HARD DELETE - xóa vĩnh viễn khỏi database
        LvsUser lvsUser = lvsGetUserById(lvsUserId);

        System.out.println("HARD DELETE: Permanently deleting user: " +
                lvsUser.getLvsUsername() + ", ID: " + lvsUserId +
                ", Reason: " + lvsReason);

        // Log thông tin trước khi xóa (có thể ghi vào log file)
        System.out.println("User to be deleted: " + lvsUser.getLvsUsername() +
                " (ID: " + lvsUserId + ", Email: " + lvsUser.getLvsEmail() + ")");

        // Xóa cứng khỏi database
        lvsUserRepository.delete(lvsUser);

        System.out.println("User " + lvsUserId + " has been permanently deleted.");
    }

    @Override
    public LvsUser lvsChangeUserRole(Long lvsUserId, LvsRole lvsRole) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsRole(lvsRole);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    @Override
    public LvsUser lvsBanUser(Long lvsUserId, Long lvsAdminId, String lvsReason) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsStatus(LvsUserStatus.BANNED);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    @Override
    public LvsUser lvsUnbanUser(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsStatus(LvsUserStatus.ACTIVE);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    @Override
    public String lvsResetPassword(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        // Generate random 8-character password
        String lvsNewPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
        lvsUser.setLvsPassword(passwordEncoder.encode(lvsNewPassword));
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        lvsUserRepository.save(lvsUser);
        return lvsNewPassword;
    }

    @Override
    public boolean lvsChangePassword(Long lvsUserId, String lvsCurrentPassword, String lvsNewPassword) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);

        // Verify current password
        if (!passwordEncoder.matches(lvsCurrentPassword, lvsUser.getLvsPassword())) {
            return false;
        }

        // Update to new password
        lvsUser.setLvsPassword(passwordEncoder.encode(lvsNewPassword));
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        lvsUserRepository.save(lvsUser);
        return true;
    }

    @Override
    public LvsUser lvsUpdateAvatar(Long lvsUserId, String lvsAvatarUrl) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsAvatarUrl(lvsAvatarUrl);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    @Override
    public boolean lvsIsAdmin(HttpSession session) {
        LvsUser lvsCurrentUser = lvsGetCurrentUser();
        return lvsCurrentUser != null && lvsCurrentUser.getLvsRole() == LvsRole.ADMIN;
    }

    @Override
    public boolean lvsCheckPassword(LvsUser lvsUser, String lvsPassword) {
        return passwordEncoder.matches(lvsPassword, lvsUser.getLvsPassword());
    }

    @Override
    public String lvsEncodePassword(String lvsPassword) {
        return passwordEncoder.encode(lvsPassword);
    }

    @Override
    public Long lvsCountTotalUsers() {
        return lvsUserRepository.count();
    }

    @Override
    public Long lvsCountNewUsers(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        LocalDateTime startDateTime = lvsStartDate.atStartOfDay();
        LocalDateTime endDateTime = lvsEndDate.atTime(23, 59, 59);
        return lvsUserRepository.countByLvsCreatedAtBetween(startDateTime, endDateTime);
    }

    @Override
    public List<LvsUser> lvsGetTopBuyers(int lvsLimit) {
        // Using top users by balance as proxy for top buyers
        // You might need to adjust this based on your actual business logic
        return lvsUserRepository.findTopByBalance(org.springframework.data.domain.PageRequest.of(0, lvsLimit))
                .getContent();
    }

    @Override
    public Map<String, Long> lvsGetUserStatsLast30Days() {
        Map<String, Long> stats = new HashMap<>();

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        // Total active users
        stats.put("totalActive", lvsUserRepository.countByLvsStatus(LvsUserStatus.ACTIVE));

        // New users in last 30 days
        stats.put("newUsers", lvsUserRepository.countByLvsCreatedAtBetween(startDate, endDate));

        // Count by role
        stats.put("totalAdmins", lvsUserRepository.countByLvsRole(LvsRole.ADMIN));
        stats.put("totalModerators", lvsUserRepository.countByLvsRole(LvsRole.MODERATOR));
        stats.put("totalUsers", lvsUserRepository.countByLvsRole(LvsRole.USER));

        // Count by status
        stats.put("totalBanned", lvsUserRepository.countByLvsStatus(LvsUserStatus.BANNED));
        stats.put("totalInactive", lvsUserRepository.countByLvsStatus(LvsUserStatus.INACTIVE));

        return stats;
    }

    @Override
    public Map<String, Object> lvsGetUserRegistrationChartData() {
        Map<String, Object> chartData = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> dataPoints = new java.util.ArrayList<>();

        // Get registration data for last 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            Long count = lvsUserRepository.countByLvsCreatedAtBetween(startOfDay, endOfDay);

            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", date.toString());
            dataPoint.put("count", count);
            dataPoints.add(dataPoint);
        }

        chartData.put("labels", dataPoints.stream().map(d -> d.get("date")).collect(Collectors.toList()));
        chartData.put("data", dataPoints.stream().map(d -> d.get("count")).collect(Collectors.toList()));
        chartData.put("total", lvsCountTotalUsers());

        return chartData;
    }

    @Override
    public boolean lvsDeactivateAccount(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsStatus(LvsUserStatus.INACTIVE);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        lvsUserRepository.save(lvsUser);
        return true;
    }

    @Override
    public boolean lvsActivateAccount(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsStatus(LvsUserStatus.ACTIVE);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        lvsUserRepository.save(lvsUser);
        return true;
    }

    @Override
    public Double lvsGetTotalRevenue(Long lvsUserId) {
        // This should query from Order or Transaction repository
        // For now, return the user's balance as revenue
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        return lvsUser.getLvsBalance() != null ? lvsUser.getLvsBalance() : 0.0;
    }

    @Override
    public Double lvsGetTotalWithdrawn(Long lvsUserId) {
        // This should query from Transaction repository where type = WITHDRAWAL
        // For now, return 0.0 as placeholder
        return 0.0;
    }

    @Override
    public List<Object[]> lvsGetTopSellingProjects(Long lvsUserId, int lvsLimit) {
        // This should query from Project and Order repositories
        // For now, return empty list as placeholder
        return new java.util.ArrayList<>();
    }

    @Override
    public LvsUser lvsUpdateCoinBalance(Long lvsUserId, Double lvsAmount) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        Double currentCoin = lvsUser.getLvsCoin() != null ? lvsUser.getLvsCoin() : 0.0;
        lvsUser.setLvsCoin(currentCoin + lvsAmount);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    @Override
    public LvsUser lvsUpdateRevenueBalance(Long lvsUserId, Double lvsAmount) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        Double currentBalance = lvsUser.getLvsBalance() != null ? lvsUser.getLvsBalance() : 0.0;
        lvsUser.setLvsBalance(currentBalance + lvsAmount);
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    // Helper method to update last login
    public void lvsUpdateLastLogin(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        lvsUser.setLvsLastLogin(LocalDateTime.now());
        lvsUserRepository.save(lvsUser);
    }

    // Additional method to find users by phone
    public Optional<LvsUser> lvsGetUserByPhone(String lvsPhone) {
        return lvsUserRepository.findByLvsPhone(lvsPhone);
    }
}