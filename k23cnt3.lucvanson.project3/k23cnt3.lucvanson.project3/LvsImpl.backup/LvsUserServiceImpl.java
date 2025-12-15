package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsRole;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser.LvsUserStatus;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation cho quản lý người dùng
 * Xử lý CRUD user, thay đổi role, khóa/mở khóa tài khoản
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsUserServiceImpl implements LvsUserService {

    private final LvsUserRepository lvsUserRepository;
    private final PasswordEncoder lvsPasswordEncoder;
    private final HttpSession lvsSession;

    /**
     * Lấy user hiện tại từ session
     * @return User hiện tại
     */
    @Override
    public LvsUser lvsGetCurrentUser() {
        Long lvsUserId = (Long) lvsSession.getAttribute("LvsUserId");
        if (lvsUserId != null) {
            return lvsUserRepository.findById(lvsUserId).orElse(null);
        }
        return null;
    }

    /**
     * Lấy user theo ID
     * @param lvsUserId ID của user
     * @return User tìm thấy
     */
    @Override
    public LvsUser lvsGetUserById(Long lvsUserId) {
        return lvsUserRepository.findById(lvsUserId).orElse(null);
    }

    /**
     * Lấy user theo username
     * @param lvsUsername Username cần tìm
     * @return User tìm thấy
     */
    @Override
    public LvsUser lvsGetUserByUsername(String lvsUsername) {
        return lvsUserRepository.findByLvsUsername(lvsUsername).orElse(null);
    }

    /**
     * Lấy user theo email
     * @param lvsEmail Email cần tìm
     * @return User tìm thấy
     */
    @Override
    public LvsUser lvsGetUserByEmail(String lvsEmail) {
        return lvsUserRepository.findByLvsEmail(lvsEmail).orElse(null);
    }

    /**
     * Lấy tất cả user với phân trang
     * @param lvsPageable Thông tin phân trang
     * @return Trang user
     */
    @Override
    public Page<LvsUser> lvsGetAllUsers(Pageable lvsPageable) {
        return lvsUserRepository.findAll(lvsPageable);
    }

    /**
     * Tìm kiếm user theo keyword
     * @param lvsKeyword Từ khóa tìm kiếm
     * @param lvsPageable Thông tin phân trang
     * @return Trang user tìm thấy
     */
    @Override
    public Page<LvsUser> lvsSearchUsers(String lvsKeyword, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsUsernameContainingOrLvsEmailContainingOrLvsFullNameContaining(
                lvsKeyword, lvsKeyword, lvsKeyword, lvsPageable);
    }

    /**
     * Lấy user theo role
     * @param lvsRole Role cần lọc
     * @param lvsPageable Thông tin phân trang
     * @return Trang user
     */
    @Override
    public Page<LvsUser> lvsGetUsersByRole(LvsRole lvsRole, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsRole(lvsRole, lvsPageable);
    }

    /**
     * Lấy user theo status
     * @param lvsStatus Status cần lọc
     * @param lvsPageable Thông tin phân trang
     * @return Trang user
     */
    @Override
    public Page<LvsUser> lvsGetUsersByStatus(LvsUserStatus lvsStatus, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsStatus(lvsStatus, lvsPageable);
    }

    /**
     * Lấy user theo role và status
     * @param lvsRole Role cần lọc
     * @param lvsStatus Status cần lọc
     * @param lvsPageable Thông tin phân trang
     * @return Trang user
     */
    @Override
    public Page<LvsUser> lvsGetUsersByRoleAndStatus(LvsRole lvsRole, LvsUserStatus lvsStatus, Pageable lvsPageable) {
        return lvsUserRepository.findByLvsRoleAndLvsStatus(lvsRole, lvsStatus, lvsPageable);
    }

    /**
     * Tạo user mới
     * @param lvsUser Thông tin user
     * @return User đã tạo
     */
    @Override
    public LvsUser lvsCreateUser(LvsUser lvsUser) {
        // Mã hóa password
        lvsUser.setLvsPassword(lvsEncodePassword(lvsUser.getLvsPassword()));
        lvsUser.setLvsCreatedAt(LocalDateTime.now());
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());
        return lvsUserRepository.save(lvsUser);
    }

    /**
     * Cập nhật thông tin user
     * @param lvsUser Thông tin user cập nhật
     * @return User đã cập nhật
     */
    @Override
    public LvsUser lvsUpdateUser(LvsUser lvsUser) {
        LvsUser lvsExistingUser = lvsGetUserById(lvsUser.getLvsUserId());
        if (lvsExistingUser != null) {
            // Chỉ cập nhật các trường cho phép
            lvsExistingUser.setLvsFullName(lvsUser.getLvsFullName());
            lvsExistingUser.setLvsEmail(lvsUser.getLvsEmail());
            lvsExistingUser.setLvsPhone(lvsUser.getLvsPhone());
            lvsExistingUser.setLvsAddress(lvsUser.getLvsAddress());
            lvsExistingUser.setLvsBio(lvsUser.getLvsBio());
            lvsExistingUser.setLvsAvatarUrl(lvsUser.getLvsAvatarUrl());
            lvsExistingUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsExistingUser);
        }
        return null;
    }

    /**
     * Xóa user với lý do
     * @param lvsUserId ID user cần xóa
     * @param lvsReason Lý do xóa
     */
    @Override
    public void lvsDeleteUser(Long lvsUserId, String lvsReason) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            // Thay vì xóa cứng, đánh dấu là banned
            lvsUser.setLvsStatus(LvsUserStatus.BANNED);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            lvsUserRepository.save(lvsUser);
        }
    }

    /**
     * Thay đổi role của user
     * @param lvsUserId ID user
     * @param lvsRole Role mới
     * @return User đã thay đổi
     */
    @Override
    public LvsUser lvsChangeUserRole(Long lvsUserId, LvsRole lvsRole) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsRole(lvsRole);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsUser);
        }
        return null;
    }

    /**
     * Khóa tài khoản user
     * @param lvsUserId ID user cần khóa
     * @param lvsAdminId ID LvsAdmin thực hiện
     * @param lvsReason Lý do khóa
     * @return User đã khóa
     */
    @Override
    public LvsUser lvsBanUser(Long lvsUserId, Long lvsAdminId, String lvsReason) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsStatus(LvsUserStatus.BANNED);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsUser);
        }
        return null;
    }

    /**
     * Mở khóa tài khoản user
     * @param lvsUserId ID user cần mở khóa
     * @return User đã mở khóa
     */
    @Override
    public LvsUser lvsUnbanUser(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsStatus(LvsUserStatus.ACTIVE);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsUser);
        }
        return null;
    }

    /**
     * Reset mật khẩu user
     * @param lvsUserId ID user cần reset
     * @return Mật khẩu mới
     */
    @Override
    public String lvsResetPassword(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            String lvsNewPassword = lvsGenerateRandomPassword();
            lvsUser.setLvsPassword(lvsEncodePassword(lvsNewPassword));
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            lvsUserRepository.save(lvsUser);
            return lvsNewPassword;
        }
        return null;
    }

    /**
     * Thay đổi mật khẩu user
     * @param lvsUserId ID user
     * @param lvsCurrentPassword Mật khẩu hiện tại
     * @param lvsNewPassword Mật khẩu mới
     * @return true nếu thành công
     */
    @Override
    public boolean lvsChangePassword(Long lvsUserId, String lvsCurrentPassword, String lvsNewPassword) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null && lvsCheckPassword(lvsUser, lvsCurrentPassword)) {
            lvsUser.setLvsPassword(lvsEncodePassword(lvsNewPassword));
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            lvsUserRepository.save(lvsUser);
            return true;
        }
        return false;
    }

    /**
     * Cập nhật avatar user
     * @param lvsUserId ID user
     * @param lvsAvatarUrl URL avatar mới
     * @return User đã cập nhật
     */
    @Override
    public LvsUser lvsUpdateAvatar(Long lvsUserId, String lvsAvatarUrl) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsAvatarUrl(lvsAvatarUrl);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsUser);
        }
        return null;
    }

    /**
     * Kiểm tra quyền LvsAdmin từ session
     * @param session HttpSession
     * @return true nếu là LvsAdmin
     */
    @Override
    public boolean lvsIsAdmin(HttpSession session) {
        Long lvsUserId = (Long) session.getAttribute("LvsUserId");
        if (lvsUserId != null) {
            LvsUser lvsUser = lvsGetUserById(lvsUserId);
            return lvsUser != null && lvsUser.getLvsRole() == LvsRole.ADMIN;
        }
        return false;
    }

    /**
     * Kiểm tra mật khẩu
     * @param lvsUser User cần kiểm tra
     * @param lvsPassword Mật khẩu cần kiểm tra
     * @return true nếu đúng
     */
    @Override
    public boolean lvsCheckPassword(LvsUser lvsUser, String lvsPassword) {
        return lvsPasswordEncoder.matches(lvsPassword, lvsUser.getLvsPassword());
    }

    /**
     * Mã hóa mật khẩu
     * @param lvsPassword Mật khẩu cần mã hóa
     * @return Mật khẩu đã mã hóa
     */
    @Override
    public String lvsEncodePassword(String lvsPassword) {
        return lvsPasswordEncoder.encode(lvsPassword);
    }

    /**
     * Đếm tổng số user
     * @return Tổng số user
     */
    @Override
    public Long lvsCountTotalUsers() {
        return lvsUserRepository.count();
    }

    /**
     * Đếm số user mới trong khoảng thời gian
     * @param lvsStartDate Ngày bắt đầu
     * @param lvsEndDate Ngày kết thúc
     * @return Số user mới
     */
    @Override
    public Long lvsCountNewUsers(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        return lvsUserRepository.countByLvsCreatedAtBetween(
                lvsStartDate.atStartOfDay(), lvsEndDate.atTime(23, 59, 59));
    }

    /**
     * Lấy top người mua
     * @param lvsLimit Giới hạn số lượng
     * @return Danh sách top người mua
     */
    @Override
    public List<LvsUser> lvsGetTopBuyers(int lvsLimit) {
        return lvsUserRepository.findTopBuyers(lvsLimit);
    }

    /**
     * Lấy thống kê user đăng ký 30 ngày gần nhất
     * @return Map thống kê
     */
    @Override
    public Map<String, Long> lvsGetUserStatsLast30Days() {
        Map<String, Long> lvsStats = new HashMap<>();
        LocalDate lvsEndDate = LocalDate.now();
        LocalDate lvsStartDate = lvsEndDate.minusDays(30);

        for (LocalDate lvsDate = lvsStartDate; !lvsDate.isAfter(lvsEndDate); lvsDate = lvsDate.plusDays(1)) {
            Long lvsCount = lvsUserRepository.countByLvsCreatedAtBetween(
                    lvsDate.atStartOfDay(), lvsDate.atTime(23, 59, 59));
            lvsStats.put(lvsDate.toString(), lvsCount);
        }

        return lvsStats;
    }

    /**
     * Lấy dữ liệu biểu đồ đăng ký user
     * @return Dữ liệu biểu đồ
     */
    @Override
    public Map<String, Object> lvsGetUserRegistrationChartData() {
        Map<String, Object> lvsChartData = new HashMap<>();
        Map<String, Long> lvsStats = lvsGetUserStatsLast30Days();

        lvsChartData.put("labels", lvsStats.keySet());
        lvsChartData.put("data", lvsStats.values());

        return lvsChartData;
    }

    /**
     * Vô hiệu hóa tài khoản
     * @param lvsUserId ID user
     * @return true nếu thành công
     */
    @Override
    public boolean lvsDeactivateAccount(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsStatus(LvsUserStatus.INACTIVE);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            lvsUserRepository.save(lvsUser);
            return true;
        }
        return false;
    }

    /**
     * Kích hoạt tài khoản
     * @param lvsUserId ID user
     * @return true nếu thành công
     */
    @Override
    public boolean lvsActivateAccount(Long lvsUserId) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsStatus(LvsUserStatus.ACTIVE);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            lvsUserRepository.save(lvsUser);
            return true;
        }
        return false;
    }

    /**
     * Lấy doanh thu tổng của user
     * @param lvsUserId ID user
     * @return Tổng doanh thu
     */
    @Override
    public Double lvsGetTotalRevenue(Long lvsUserId) {
        return lvsUserRepository.getTotalRevenueByUserId(lvsUserId);
    }

    /**
     * Lấy tổng số tiền đã rút
     * @param lvsUserId ID user
     * @return Tổng tiền đã rút
     */
    @Override
    public Double lvsGetTotalWithdrawn(Long lvsUserId) {
        return lvsUserRepository.getTotalWithdrawnByUserId(lvsUserId);
    }

    /**
     * Lấy dự án bán chạy nhất của user
     * @param lvsUserId ID user
     * @param lvsLimit Giới hạn số lượng
     * @return Danh sách dự án bán chạy
     */
    @Override
    public List<Object[]> lvsGetTopSellingProjects(Long lvsUserId, int lvsLimit) {
        return lvsUserRepository.getTopSellingProjectsByUserId(lvsUserId, lvsLimit);
    }

    /**
     * Cập nhật số dư coin
     * @param lvsUserId ID user
     * @param lvsAmount Số tiền cập nhật
     * @return User đã cập nhật
     */
    @Override
    public LvsUser lvsUpdateCoinBalance(Long lvsUserId, Double lvsAmount) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsCoin(lvsUser.getLvsCoin() + lvsAmount);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsUser);
        }
        return null;
    }

    /**
     * Cập nhật số dư doanh thu
     * @param lvsUserId ID user
     * @param lvsAmount Số tiền cập nhật
     * @return User đã cập nhật
     */
    @Override
    public LvsUser lvsUpdateRevenueBalance(Long lvsUserId, Double lvsAmount) {
        LvsUser lvsUser = lvsGetUserById(lvsUserId);
        if (lvsUser != null) {
            lvsUser.setLvsBalance(lvsUser.getLvsBalance() + lvsAmount);
            lvsUser.setLvsUpdatedAt(LocalDateTime.now());
            return lvsUserRepository.save(lvsUser);
        }
        return null;
    }

    /**
     * Tạo mật khẩu ngẫu nhiên
     * @return Mật khẩu ngẫu nhiên
     */
    private String lvsGenerateRandomPassword() {
        String lvsChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder lvsPassword = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int lvsIndex = (int) (Math.random() * lvsChars.length());
            lvsPassword.append(lvsChars.charAt(lvsIndex));
        }
        return lvsPassword.toString();
    }
}