package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * LvsAdminUserController - Controller quản lý người dùng cho admin
 * Chức năng: CRUD user, phân trang, tìm kiếm, filter theo role/status
 */
@Controller
@RequestMapping("/LvsAdmin/LvsUsers")
public class LvsAdminUserController {

    @Autowired
    private LvsUserService lvsUserService;

    // List LvsUsers với tìm kiếm và phân trang
    @GetMapping
    public String lvsListUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LvsUser.LvsRole role,
            @RequestParam(required = false) LvsUser.LvsUserStatus status,
            Model model) {

        Pageable lvsPageable = PageRequest.of(page, 10);
        Page<LvsUser> lvsUsers;

        if (keyword != null && !keyword.isEmpty()) {
            // Tìm kiếm theo keyword
            lvsUsers = lvsUserService.lvsSearchUsers(keyword, lvsPageable);
        } else if (role != null && status != null) {
            // Filter theo cả role và status
            lvsUsers = lvsUserService.lvsGetUsersByRoleAndStatus(role, status, lvsPageable);
        } else if (role != null) {
            // Filter theo role
            lvsUsers = lvsUserService.lvsGetUsersByRole(role, lvsPageable);
        } else if (status != null) {
            // Filter theo status
            lvsUsers = lvsUserService.lvsGetUsersByStatus(status, lvsPageable);
        } else {
            // Lấy tất cả
            lvsUsers = lvsUserService.lvsGetAllUsers(lvsPageable);
        }

        model.addAttribute("lvsUsers", lvsUsers);
        model.addAttribute("lvsKeyword", keyword);
        model.addAttribute("lvsSelectedRole", role);
        model.addAttribute("lvsSelectedStatus", status);
        model.addAttribute("lvsPageTitle", "Quản lý User");
        model.addAttribute("lvsRoles", LvsUser.LvsRole.values());
        model.addAttribute("lvsStatuses", LvsUser.LvsUserStatus.values());

        return "LvsAreas/LvsAdmin/LvsUsers/LvsList";
    }

    // Hiển thị form thêm user mới
    @GetMapping("/LvsCreate")
    public String lvsShowCreateForm(Model model) {
        model.addAttribute("lvsUser", new LvsUser());
        model.addAttribute("lvsPageTitle", "Thêm User mới");
        model.addAttribute("lvsRoles", LvsUser.LvsRole.values());
        model.addAttribute("lvsStatuses", LvsUser.LvsUserStatus.values());
        return "LvsAreas/LvsAdmin/LvsUsers/LvsCreate";
    }

    // Xử lý thêm user mới
    @PostMapping("/LvsCreate")
    public String lvsCreateUser(@ModelAttribute LvsUser lvsUser,
                                @RequestParam String lvsConfirmPassword,
                                RedirectAttributes lvsRedirectAttributes) {

        // Validate password
        if (!lvsUser.getLvsPassword().equals(lvsConfirmPassword)) {
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Mật khẩu xác nhận không khớp!");
            return "redirect:/LvsAdmin/LvsUsers/LvsCreate";
        }

        try {
            lvsUserService.lvsCreateUser(lvsUser);
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Thêm user thành công!");
        } catch (Exception e) {
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUsers";
    }

    // Hiển thị chi tiết user
    @GetMapping("/{lvsUserId}/LvsDetail")
    public String lvsShowUserDetail(@PathVariable Long lvsUserId, Model model) {
        try {
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            model.addAttribute("lvsUser", lvsUser);
            model.addAttribute("lvsPageTitle", "Chi tiết User: " + lvsUser.getLvsUsername());
            return "LvsAreas/LvsAdmin/LvsUsers/LvsDetail";
        } catch (Exception e) {
            return "redirect:/LvsAdmin/LvsUsers?error=User+not+found";
        }
    }

    // Hiển thị form chỉnh sửa user
    @GetMapping("/{lvsUserId}/LvsEdit")
    public String lvsShowEditForm(@PathVariable Long lvsUserId, Model model) {
        try {
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            model.addAttribute("lvsUser", lvsUser);
            model.addAttribute("lvsPageTitle", "Chỉnh sửa User: " + lvsUser.getLvsUsername());
            model.addAttribute("lvsRoles", LvsUser.LvsRole.values());
            model.addAttribute("lvsStatuses", LvsUser.LvsUserStatus.values());
            return "LvsAreas/LvsAdmin/LvsUsers/LvsEdit";
        } catch (Exception e) {
            return "redirect:/LvsAdmin/LvsUsers?error=User+not+found";
        }
    }

    // Xử lý cập nhật user
    @PostMapping("/{lvsUserId}/LvsEdit")
    public String lvsUpdateUser(@PathVariable Long lvsUserId,
                                @ModelAttribute LvsUser lvsUser,
                                @RequestParam(required = false) String lvsNewPassword,
                                RedirectAttributes lvsRedirectAttributes) {

        try {
            LvsUser lvsExistingUser = lvsUserService.lvsGetUserById(lvsUserId);

            // Cập nhật thông tin cơ bản
            lvsExistingUser.setLvsUsername(lvsUser.getLvsUsername());
            lvsExistingUser.setLvsEmail(lvsUser.getLvsEmail());
            lvsExistingUser.setLvsFullName(lvsUser.getLvsFullName());
            lvsExistingUser.setLvsPhone(lvsUser.getLvsPhone());
            lvsExistingUser.setLvsAddress(lvsUser.getLvsAddress());
            lvsExistingUser.setLvsBio(lvsUser.getLvsBio());
            lvsExistingUser.setLvsRole(lvsUser.getLvsRole());
            lvsExistingUser.setLvsStatus(lvsUser.getLvsStatus());
            lvsExistingUser.setLvsCoin(lvsUser.getLvsCoin());
            lvsExistingUser.setLvsBalance(lvsUser.getLvsBalance());

            // Cập nhật password nếu có
            if (lvsNewPassword != null && !lvsNewPassword.trim().isEmpty()) {
                if (lvsUserService.lvsChangePassword(lvsUserId, lvsExistingUser.getLvsPassword(), lvsNewPassword)) {
                    lvsExistingUser.setLvsPassword(lvsUserService.lvsEncodePassword(lvsNewPassword));
                }
            }

            lvsUserService.lvsUpdateUser(lvsExistingUser);
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Cập nhật user thành công!");
        } catch (Exception e) {
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUsers/" + lvsUserId + "/LvsDetail";
    }

    // Xử lý xóa user (SOFT DELETE)
    @PostMapping("/{lvsUserId}/LvsDelete")
    public String lvsDeleteUser(@PathVariable Long lvsUserId,
                                @RequestParam(required = false) String lvsReason,
                                RedirectAttributes lvsRedirectAttributes,
                                HttpServletRequest request) {

        System.out.println("=== SOFT DELETE REQUEST ===");
        System.out.println("User ID: " + lvsUserId);
        System.out.println("Reason: " + lvsReason);
        System.out.println("Method: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("=========================");

        try {
            lvsUserService.lvsDeleteUser(lvsUserId, lvsReason != null ? lvsReason : "Admin delete");
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã vô hiệu hóa user thành công (Soft Delete)!");
        } catch (Exception e) {
            System.err.println("SOFT DELETE ERROR: " + e.getMessage());
            e.printStackTrace();
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUsers";
    }

    // Xử lý xóa user (HARD DELETE)
    @PostMapping("/{lvsUserId}/LvsHardDelete")
    public String lvsHardDeleteUser(@PathVariable Long lvsUserId,
                                    @RequestParam(required = false) String lvsReason,
                                    RedirectAttributes lvsRedirectAttributes,
                                    HttpServletRequest request) {

        System.out.println("=== HARD DELETE REQUEST ===");
        System.out.println("User ID: " + lvsUserId);
        System.out.println("Reason: " + lvsReason);
        System.out.println("Method: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("==========================");

        try {
            lvsUserService.lvsHardDeleteUser(lvsUserId, lvsReason != null ? lvsReason : "Admin hard delete");
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã xóa user vĩnh viễn thành công!");
        } catch (Exception e) {
            System.err.println("HARD DELETE ERROR: " + e.getMessage());
            e.printStackTrace();
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUsers";
    }

    // Khóa tài khoản user
    @PostMapping("/{lvsUserId}/lvs-ban")
    public String lvsBanUser(@PathVariable Long lvsUserId,
                             @RequestParam String lvsReason,
                             RedirectAttributes lvsRedirectAttributes) {

        try {
            // Giả sử admin ID là 1 (trong thực tế lấy từ session/authentication)
            Long lvsAdminId = 1L;
            lvsUserService.lvsBanUser(lvsUserId, lvsAdminId, lvsReason);
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã khóa tài khoản user!");
        } catch (Exception e) {
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUsers/" + lvsUserId + "/LvsDetail";
    }

    // Mở khóa tài khoản user
    @PostMapping("/{lvsUserId}/lvs-unban")
    public String lvsUnbanUser(@PathVariable Long lvsUserId,
                               RedirectAttributes lvsRedirectAttributes) {

        try {
            lvsUserService.lvsUnbanUser(lvsUserId);
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã mở khóa tài khoản user!");
        } catch (Exception e) {
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUsers/" + lvsUserId + "/LvsDetail";
    }
}