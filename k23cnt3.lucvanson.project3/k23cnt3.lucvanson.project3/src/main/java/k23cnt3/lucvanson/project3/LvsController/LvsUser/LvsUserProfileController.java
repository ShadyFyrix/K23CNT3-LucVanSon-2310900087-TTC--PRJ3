package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller quản lý hồ sơ cá nhân của người dùng
 * Xử lý cập nhật thông tin, đổi mật khẩu, xem doanh thu
 */
@Controller
@RequestMapping("/LvsUser/LvsProfile")
public class LvsUserProfileController {

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsTransactionService lvsTransactionService;

    @Autowired
    private LvsFollowService lvsFollowService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsPostService lvsPostService;

    @Autowired
    private LvsFileUploadService lvsFileUploadService;

    // Xem hồ sơ cá nhân
    @GetMapping("/LvsView")
    public String lvsViewProfile(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // Lấy thông tin mới nhất
        lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());
        session.setAttribute("LvsCurrentUser", lvsCurrentUser);

        // Lấy thống kê
        int lvsFollowersCount = lvsFollowService.lvsGetFollowerCount(lvsCurrentUser.getLvsUserId());
        int lvsFollowingCount = lvsFollowService.lvsGetFollowingCount(lvsCurrentUser.getLvsUserId());

        // Lấy danh sách posts, followers, following
        org.springframework.data.domain.Pageable lvsPageable = PageRequest.of(0, 12);
        org.springframework.data.domain.Page<LvsPost> lvsPosts = lvsPostService.lvsGetPostsByUser(
                lvsCurrentUser.getLvsUserId(), lvsPageable);
        org.springframework.data.domain.Page<LvsUser> lvsFollowersPage = lvsFollowService
                .lvsGetFollowers(lvsCurrentUser.getLvsUserId(), lvsPageable);
        org.springframework.data.domain.Page<LvsUser> lvsFollowingPage = lvsFollowService
                .lvsGetFollowing(lvsCurrentUser.getLvsUserId(), lvsPageable);
        List<LvsUser> lvsFollowers = lvsFollowersPage.getContent();
        List<LvsUser> lvsFollowing = lvsFollowingPage.getContent();

        int lvsProjectsCount = 0; // TODO: Fix lazy loading
        int lvsPostsCount = (int) lvsPosts.getTotalElements();

        model.addAttribute("LvsUser", lvsCurrentUser);
        model.addAttribute("LvsIsFollowing", false); // Not applicable for own profile
        model.addAttribute("LvsFollowersCount", lvsFollowersCount);
        model.addAttribute("LvsFollowingCount", lvsFollowingCount);
        model.addAttribute("LvsProjectsCount", lvsProjectsCount);
        model.addAttribute("LvsPostsCount", lvsPostsCount);
        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsFollowers", lvsFollowers);
        model.addAttribute("LvsFollowing", lvsFollowing);

        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileViewOther";
    }

    // Xem hồ sơ người dùng khác
    @GetMapping("/LvsView/{userId}")
    public String lvsViewOtherProfile(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsUser lvsUser = lvsUserService.lvsGetUserById(userId);

        if (lvsUser == null) {
            return "redirect:/LvsUser/LvsHome";
        }

        // Kiểm tra đang theo dõi chưa
        boolean lvsIsFollowing = false;
        if (lvsCurrentUser != null) {
            lvsIsFollowing = lvsFollowService.lvsIsFollowing(
                    lvsCurrentUser.getLvsUserId(), userId);
        }

        // Lấy thống kê
        int lvsFollowersCount = lvsFollowService.lvsGetFollowerCount(userId);
        int lvsFollowingCount = lvsFollowService.lvsGetFollowingCount(userId);

        // Load projects and posts
        org.springframework.data.domain.Pageable lvsPageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<LvsProject> lvsProjects = lvsProjectService.lvsGetProjectsByUser(userId,
                lvsPageable);
        org.springframework.data.domain.Page<LvsPost> lvsPosts = lvsPostService.lvsGetPostsByUser(userId, lvsPageable);

        // Load followers and following lists
        org.springframework.data.domain.Page<LvsUser> lvsFollowersPage = lvsFollowService.lvsGetFollowers(userId,
                lvsPageable);
        org.springframework.data.domain.Page<LvsUser> lvsFollowingPage = lvsFollowService.lvsGetFollowing(userId,
                lvsPageable);

        java.util.List<LvsUser> lvsFollowers = lvsFollowersPage.getContent();
        java.util.List<LvsUser> lvsFollowing = lvsFollowingPage.getContent();

        // DEBUG: Log project count
        System.out.println("DEBUG: User ID: " + userId);
        System.out.println("DEBUG: Projects Page - Total Elements: " + lvsProjects.getTotalElements());
        System.out.println("DEBUG: Projects Page - Content Size: " + lvsProjects.getContent().size());
        System.out.println("DEBUG: Projects Content: " + lvsProjects.getContent());

        int lvsProjectsCount = (int) lvsProjects.getTotalElements();
        int lvsPostsCount = (int) lvsPosts.getTotalElements();

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsIsFollowing", lvsIsFollowing);
        model.addAttribute("LvsFollowersCount", lvsFollowersCount);
        model.addAttribute("LvsFollowingCount", lvsFollowingCount);
        model.addAttribute("LvsProjectsCount", lvsProjectsCount);
        model.addAttribute("LvsPostsCount", lvsPostsCount);
        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsFollowers", lvsFollowers);
        model.addAttribute("LvsFollowing", lvsFollowing);

        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileViewOther";
    }

    // Chỉnh sửa hồ sơ
    @GetMapping("/LvsEdit")
    public String lvsShowEditProfileForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // IMPORTANT: Refresh user data from database to get latest avatar and all
        // fields
        lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());
        session.setAttribute("LvsCurrentUser", lvsCurrentUser);

        model.addAttribute("LvsUser", lvsCurrentUser);
        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileEdit";
    }

    // Xử lý chỉnh sửa hồ sơ
    @PostMapping("/LvsEdit")
    public String lvsEditProfile(@ModelAttribute LvsUser lvsUser,
            @RequestParam(required = false) String lvsNewPassword,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile lvsAvatarFile,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // CRITICAL: Preserve all non-editable fields from current user
            // Form only contains: username, email, fullName, bio
            // Must preserve: role, title, coin, balance, status, createdAt, etc.
            lvsUser.setLvsUserId(lvsCurrentUser.getLvsUserId());
            lvsUser.setLvsRole(lvsCurrentUser.getLvsRole());
            lvsUser.setLvsTitle(lvsCurrentUser.getLvsTitle());
            lvsUser.setLvsCoin(lvsCurrentUser.getLvsCoin());
            lvsUser.setLvsBalance(lvsCurrentUser.getLvsBalance());
            lvsUser.setLvsStatus(lvsCurrentUser.getLvsStatus());
            lvsUser.setLvsCreatedAt(lvsCurrentUser.getLvsCreatedAt());

            // Handle avatar upload
            if (lvsAvatarFile != null && !lvsAvatarFile.isEmpty()) {
                String avatarUrl = lvsFileUploadService.lvsSaveFile(lvsAvatarFile, "avatars");
                lvsUser.setLvsAvatarUrl(avatarUrl);
            } else {
                // Keep existing avatar
                lvsUser.setLvsAvatarUrl(lvsCurrentUser.getLvsAvatarUrl());
            }

            // Giữ nguyên mật khẩu cũ nếu không thay đổi
            if (lvsNewPassword == null || lvsNewPassword.isEmpty()) {
                lvsUser.setLvsPassword(lvsCurrentUser.getLvsPassword());
            } else {
                // Mã hóa mật khẩu mới
                lvsUser.setLvsPassword(lvsUserService.lvsEncodePassword(lvsNewPassword));
            }

            LvsUser lvsUpdatedUser = lvsUserService.lvsUpdateUser(lvsUser);
            session.setAttribute("LvsCurrentUser", lvsUpdatedUser);

            model.addAttribute("LvsSuccess", "Cập nhật hồ sơ thành công!");
            return "redirect:/LvsUser/LvsProfile/LvsView";
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsProfile/LvsProfileEdit";
        }
    }

    // Upload avatar
    @PostMapping("/LvsUploadAvatar")
    public String lvsUploadAvatar(@RequestParam String lvsAvatarUrl,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsUserService.lvsUpdateAvatar(lvsCurrentUser.getLvsUserId(), lvsAvatarUrl);
            // Cập nhật session
            lvsCurrentUser.setLvsAvatarUrl(lvsAvatarUrl);
            session.setAttribute("LvsCurrentUser", lvsCurrentUser);

            model.addAttribute("LvsSuccess", "Cập nhật ảnh đại diện thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi cập nhật ảnh đại diện: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsProfile/LvsView";
    }

    // Xem doanh thu
    @GetMapping("/LvsRevenue")
    public String lvsViewRevenue(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Lấy thống kê doanh thu
        Double lvsTotalRevenue = lvsUserService.lvsGetTotalRevenue(lvsCurrentUser.getLvsUserId());
        Double lvsAvailableBalance = lvsCurrentUser.getLvsBalance();
        Double lvsTotalWithdrawn = lvsUserService.lvsGetTotalWithdrawn(lvsCurrentUser.getLvsUserId());

        // Lấy lịch sử giao dịch (10 giao dịch gần nhất)
        org.springframework.data.domain.Pageable lvsPageable = org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Order.desc("lvsCreatedAt")));
        org.springframework.data.domain.Page<LvsTransaction> lvsTransactionPage = lvsTransactionService
                .lvsGetTransactionsByUser(lvsCurrentUser.getLvsUserId(), lvsPageable);
        List<LvsTransaction> lvsTransactions = lvsTransactionPage.getContent();

        // Lấy dự án bán chạy
        // TODO: Fix return type - method returns List<Object[]> not List<LvsProject>
        // List<LvsProject> lvsTopProjects =
        // lvsUserService.lvsGetTopSellingProjects(lvsCurrentUser.getLvsUserId(), 5);
        List<LvsProject> lvsTopProjects = new ArrayList<>();

        model.addAttribute("LvsTotalRevenue", lvsTotalRevenue);
        model.addAttribute("LvsAvailableBalance", lvsAvailableBalance);
        model.addAttribute("LvsTotalWithdrawn", lvsTotalWithdrawn);
        model.addAttribute("LvsTransactions", lvsTransactions);
        model.addAttribute("LvsTopProjects", lvsTopProjects);

        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileRevenue";
    }

    // Yêu cầu rút tiền
    @PostMapping("/LvsWithdraw")
    public String lvsRequestWithdrawal(@RequestParam Double lvsAmount,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra số dư
            if (lvsAmount > lvsCurrentUser.getLvsBalance()) {
                model.addAttribute("LvsError", "Số dư không đủ!");
                return "redirect:/LvsUser/LvsProfile/LvsRevenue";
            }

            // Tạo yêu cầu rút tiền
            LvsTransaction lvsTransaction = lvsTransactionService.lvsCreateWithdrawalRequest(
                    lvsCurrentUser.getLvsUserId(), lvsAmount);

            if (lvsTransaction != null) {
                model.addAttribute("LvsSuccess", "Đã gửi yêu cầu rút tiền! Chờ LvsAdmin duyệt.");
            } else {
                model.addAttribute("LvsError", "Gửi yêu cầu thất bại!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsProfile/LvsRevenue";
    }

    // Nạp coin
    @GetMapping("/LvsDeposit")
    public String lvsShowDepositPage(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsUser", lvsCurrentUser);
        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileDeposit";
    }

    // Xử lý nạp coin
    @PostMapping("/LvsDeposit")
    public String lvsProcessDeposit(@RequestParam Double lvsAmount,
            @RequestParam String lvsPaymentMethod,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Tạo yêu cầu nạp tiền
            LvsTransaction lvsTransaction = lvsTransactionService.lvsCreateDepositRequest(
                    lvsCurrentUser.getLvsUserId(), lvsAmount, lvsPaymentMethod);

            if (lvsTransaction != null) {
                model.addAttribute("LvsSuccess", "Đã tạo yêu cầu nạp tiền! Chờ LvsAdmin duyệt.");
                model.addAttribute("LvsTransactionId", lvsTransaction.getLvsTransactionId());
            } else {
                model.addAttribute("LvsError", "Tạo yêu cầu thất bại!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileDeposit";
    }

    // Đổi mật khẩu
    @GetMapping("/LvsChangePassword")
    public String lvsShowChangePasswordForm(Model model) {
        return "LvsAreas/LvsUsers/LvsProfile/LvsProfileChangePassword";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/LvsChangePassword")
    public String lvsChangePassword(@RequestParam String lvsCurrentPassword,
            @RequestParam String lvsNewPassword,
            @RequestParam String lvsConfirmPassword,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra mật khẩu hiện tại
            if (!lvsUserService.lvsCheckPassword(lvsCurrentUser, lvsCurrentPassword)) {
                model.addAttribute("LvsError", "Mật khẩu hiện tại không đúng!");
                return "LvsAreas/LvsUsers/LvsProfile/LvsProfileChangePassword";
            }

            // Kiểm tra mật khẩu mới
            if (!lvsNewPassword.equals(lvsConfirmPassword)) {
                model.addAttribute("LvsError", "Mật khẩu xác nhận không khớp!");
                return "LvsAreas/LvsUsers/LvsProfile/LvsProfileChangePassword";
            }

            // Đổi mật khẩu
            // TODO: Fix method signature - check UserService interface
            // lvsUserService.lvsChangePassword(lvsCurrentUser.getLvsUserId(),
            // lvsNewPassword);
            lvsCurrentUser.setLvsPassword(lvsUserService.lvsEncodePassword(lvsNewPassword));
            lvsUserService.lvsUpdateUser(lvsCurrentUser);

            model.addAttribute("LvsSuccess", "Đổi mật khẩu thành công!");
            return "redirect:/LvsUser/LvsProfile/LvsView";
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsProfile/LvsProfileChangePassword";
        }
    }

    // Xóa tài khoản
    @PostMapping("/LvsDeleteAccount")
    public String lvsDeleteAccount(@RequestParam String lvsPassword,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra mật khẩu
            if (!lvsUserService.lvsCheckPassword(lvsCurrentUser, lvsPassword)) {
                model.addAttribute("LvsError", "Mật khẩu không đúng!");
                return "redirect:/LvsUser/LvsProfile/LvsView";
            }

            // Xóa tài khoản (chuyển sang trạng thái inactive)
            lvsUserService.lvsDeactivateAccount(lvsCurrentUser.getLvsUserId());

            // Xóa session
            session.invalidate();

            model.addAttribute("LvsSuccess", "Đã xóa tài khoản thành công!");
            return "redirect:/LvsAuth/LvsLogin.html";
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsUser/LvsProfile/LvsView";
        }
    }
}