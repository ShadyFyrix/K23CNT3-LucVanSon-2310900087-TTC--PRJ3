package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Controller quản lý người dùng cho LvsAdmin
 * Xử lý khóa, mở khóa, phân quyền, xóa người dùng
 */
@Controller
@RequestMapping("/LvsAdmin/LvsUser")
public class LvsAdminUserController {

    @Autowired
    private LvsUserService lvsUserService;

    // Danh sách người dùng
    @GetMapping("/LvsList")
    public String lvsListUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String lvsRole,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsUser> lvsUsers;

        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsUsers = lvsUserService.lvsSearchUsers(lvsKeyword, lvsPageable);
        } else if (lvsRole != null && !lvsRole.isEmpty() && lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsUsers = lvsUserService.lvsGetUsersByRoleAndStatus(lvsRole, lvsStatus, lvsPageable);
        } else if (lvsRole != null && !lvsRole.isEmpty()) {
            lvsUsers = lvsUserService.lvsGetUsersByRole(lvsRole, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsUsers = lvsUserService.lvsGetUsersByStatus(lvsStatus, lvsPageable);
        } else {
            lvsUsers = lvsUserService.lvsGetAllUsers(lvsPageable);
        }

        model.addAttribute("LvsUsers", lvsUsers);
        model.addAttribute("LvsRoles", LvsUser.LvsRole.values());
        model.addAttribute("LvsStatuses", LvsUser.LvsUserStatus.values());
        model.addAttribute("LvsSelectedRole", lvsRole);
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsUserList";
    }

    // Xem chi tiết người dùng
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewUserDetail(@PathVariable Long id,
                                    Model model,
                                    HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsUser lvsUser = lvsUserService.lvsGetUserById(id);

        if (lvsUser == null) {
            return "redirect:/LvsAdmin/LvsUser/LvsList";
        }

        // Lấy thống kê của user
        int lvsProjectsCount = lvsUser.getLvsProjects().size();
        int lvsPostsCount = lvsUser.getLvsPosts().size();
        int lvsOrdersCount = lvsUser.getLvsOrders().size();

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsProjectsCount", lvsProjectsCount);
        model.addAttribute("LvsPostsCount", lvsPostsCount);
        model.addAttribute("LvsOrdersCount", lvsOrdersCount);

        return "LvsAdmin/LvsUserDetail";
    }

    // Khóa tài khoản
    @PostMapping("/LvsBan/{id}")
    public String lvsBanUser(@PathVariable Long id,
                             @RequestParam String lvsReason,
                             HttpSession session,
                             Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsUserService.lvsBanUser(id, lvsAdmin.getLvsUserId(), lvsReason);

            model.addAttribute("LvsSuccess", "Đã khóa tài khoản!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsDetail/" + id;
    }

    // Mở khóa tài khoản
    @PostMapping("/LvsUnban/{id}")
    public String lvsUnbanUser(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsUserService.lvsUnbanUser(id);
            model.addAttribute("LvsSuccess", "Đã mở khóa tài khoản!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsDetail/" + id;
    }

    // Thay đổi role
    @PostMapping("/LvsChangeRole/{id}")
    public String lvsChangeUserRole(@PathVariable Long id,
                                    @RequestParam LvsUser.LvsRole lvsRole,
                                    HttpSession session,
                                    Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsUserService.lvsChangeUserRole(id, lvsRole);
            model.addAttribute("LvsSuccess", "Đã thay đổi quyền!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsDetail/" + id;
    }

    // Reset mật khẩu
    @PostMapping("/LvsResetPassword/{id}")
    public String lvsResetUserPassword(@PathVariable Long id,
                                       HttpSession session,
                                       Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            String lvsNewPassword = lvsUserService.lvsResetPassword(id);
            model.addAttribute("LvsSuccess", "Đã reset mật khẩu! Mật khẩu mới: " + lvsNewPassword);
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsDetail/" + id;
    }

    // Xóa tài khoản
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteUser(@PathVariable Long id,
                                @RequestParam(required = false) String lvsReason,
                                HttpSession session,
                                Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsUserService.lvsDeleteUser(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã xóa tài khoản!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsList";
    }

    // Thêm người dùng mới (LvsAdmin)
    @GetMapping("/LvsAdd")
    public String lvsShowAddUserForm(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsUser", new LvsUser());

        return "LvsAdmin/LvsUserAdd";
    }

    // Xử lý thêm người dùng
    @PostMapping("/LvsAdd")
    public String lvsAddUser(@ModelAttribute LvsUser lvsUser,
                             @RequestParam String lvsConfirmPassword,
                             HttpSession session,
                             Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra mật khẩu xác nhận
            if (!lvsUser.getLvsPassword().equals(lvsConfirmPassword)) {
                model.addAttribute("LvsError", "Mật khẩu xác nhận không khớp!");
                return "LvsAdmin/LvsUserAdd";
            }

            LvsUser lvsSavedUser = lvsUserService.lvsCreateUser(lvsUser);

            model.addAttribute("LvsSuccess", "Thêm người dùng thành công!");
            return "redirect:/LvsAdmin/LvsUser/LvsDetail/" + lvsSavedUser.getLvsUserId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsUserAdd";
        }
    }

    // Chỉnh sửa người dùng
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditUserForm(@PathVariable Long id,
                                      Model model,
                                      HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsUser lvsUser = lvsUserService.lvsGetUserById(id);

        if (lvsUser == null) {
            return "redirect:/LvsAdmin/LvsUser/LvsList";
        }

        model.addAttribute("LvsUser", lvsUser);

        return "LvsAdmin/LvsUserEdit";
    }

    // Xử lý chỉnh sửa người dùng
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditUser(@PathVariable Long id,
                              @ModelAttribute LvsUser lvsUser,
                              HttpSession session,
                              Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsUser.setLvsUserId(id);
            lvsUserService.lvsUpdateUser(lvsUser);

            model.addAttribute("LvsSuccess", "Cập nhật người dùng thành công!");
            return "redirect:/LvsAdmin/LvsUser/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsUserEdit";
        }
    }
}