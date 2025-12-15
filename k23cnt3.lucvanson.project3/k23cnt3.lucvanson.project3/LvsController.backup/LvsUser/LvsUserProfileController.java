package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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

    // Xem hồ sơ cá nhân
    @GetMapping("/LvsView")
    public String lvsViewProfile(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Lấy thông tin mới nhất
        lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());
        session.setAttribute("LvsCurrentUser", lvsCurrentUser);

        // Lấy thống kê
        int lvsFollowersCount = lvsFollowService.lvsGetFollowerCount(lvsCurrentUser.getLvsUserId());
        int lvsFollowingCount = lvsFollowService.lvsGetFollowingCount(lvsCurrentUser.getLvsUserId());
        int lvsProjectsCount = lvsCurrentUser.getLvsProjects().size();
        int lvsPostsCount = lvsCurrentUser.getLvsPosts().size();

        model.addAttribute("LvsUser", lvsCurrentUser);
        model.addAttribute("LvsFollowersCount", lvsFollowersCount);
        model.addAttribute("LvsFollowingCount", lvsFollowingCount);
        model.addAttribute("LvsProjectsCount", lvsProjectsCount);
        model.addAttribute("LvsPostsCount", lvsPostsCount);

        return "LvsUser/LvsProfileView";
    }

    // Xem hồ sơ người dùng khác
    @GetMapping("/LvsView/{userId}")
    public String lvsViewOtherProfile(@PathVariable Long userId,
                                      Model model,
                                      HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsUser lvsUser = lvsUserService.lvsGetUserById(userId);

        if (lvsUser == null) {
            return "redirect:/LvsUser/LvsDashboard";
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
        int lvsProjectsCount = lvsUser.getLvsProjects().size();
        int lvsPostsCount = lvsUser.getLvsPosts().size();

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsIsFollowing", lvsIsFollowing);
        model.addAttribute("LvsFollowersCount", lvsFollowersCount);
        model.addAttribute("LvsFollowingCount", lvsFollowingCount);
        model.addAttribute("LvsProjectsCount", lvsProjectsCount);
        model.addAttribute("LvsPostsCount", lvsPostsCount);

        return "LvsUser/LvsProfileViewOther";
    }

    // Chỉnh sửa hồ sơ
    @GetMapping("/LvsEdit")
    public String lvsShowEditProfileForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsUser", lvsCurrentUser);
        return "LvsUser/LvsProfileEdit";
    }

    // Xử lý chỉnh sửa hồ sơ
    @PostMapping("/LvsEdit")
    public String lvsEditProfile(@ModelAttribute LvsUser lvsUser,
                                 @RequestParam(required = false) String lvsNewPassword,
                                 HttpSession session,
                                 Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Cập nhật thông tin
            lvsUser.setLvsUserId(lvsCurrentUser.getLvsUserId());

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
            return "LvsUser/LvsProfileEdit";
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

        // Lấy lịch sử giao dịch
        List<LvsTransaction> lvsTransactions = lvsTransactionService.lvsGetTransactionsByUser(
                lvsCurrentUser.getLvsUserId());

        // Lấy dự án bán chạy
        List<LvsProject> lvsTopProjects = lvsUserService.lvsGetTopSellingProjects(
                lvsCurrentUser.getLvsUserId(), 5);

        model.addAttribute("LvsTotalRevenue", lvsTotalRevenue);
        model.addAttribute("LvsAvailableBalance", lvsAvailableBalance);
        model.addAttribute("LvsTotalWithdrawn", lvsTotalWithdrawn);
        model.addAttribute("LvsTransactions", lvsTransactions);
        model.addAttribute("LvsTopProjects", lvsTopProjects);

        return "LvsUser/LvsProfileRevenue";
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
        return "LvsUser/LvsProfileDeposit";
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

        return "LvsUser/LvsProfileDeposit";
    }

    // Đổi mật khẩu
    @GetMapping("/LvsChangePassword")
    public String lvsShowChangePasswordForm(Model model) {
        return "LvsUser/LvsProfileChangePassword";
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
                return "LvsUser/LvsProfileChangePassword";
            }

            // Kiểm tra mật khẩu mới
            if (!lvsNewPassword.equals(lvsConfirmPassword)) {
                model.addAttribute("LvsError", "Mật khẩu xác nhận không khớp!");
                return "LvsUser/LvsProfileChangePassword";
            }

            // Đổi mật khẩu
            lvsUserService.lvsChangePassword(lvsCurrentUser.getLvsUserId(), lvsNewPassword);

            model.addAttribute("LvsSuccess", "Đổi mật khẩu thành công!");
            return "redirect:/LvsUser/LvsProfile/LvsView";
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsUser/LvsProfileChangePassword";
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