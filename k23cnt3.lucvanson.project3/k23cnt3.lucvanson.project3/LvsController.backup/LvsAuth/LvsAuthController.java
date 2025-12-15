package k23cnt3.lucvanson.project3.LvsController.LvsAuth;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

/**
 * Controller quản lý xác thực người dùng
 * Xử lý đăng nhập, đăng ký, đăng xuất
 */
@Controller
@RequestMapping("/LvsAuth")
public class LvsAuthController {

    @Autowired
    private LvsUserService lvsUserService;

    // Hiển thị trang đăng nhập
    @GetMapping("/LvsLogin.html")
    public String lvsShowLoginPage() {
        return "LvsAuth/LvsLogin.html";
    }

    // Xử lý đăng nhập
    @PostMapping("/LvsLogin.html")
    public String lvsLogin(@RequestParam String lvsUsername,
                           @RequestParam String lvsPassword,
                           HttpSession session,
                           Model model) {
        try {
            // Xác thực thông tin đăng nhập
            LvsUser lvsUser = lvsUserService.lvsAuthenticate(lvsUsername, lvsPassword);

            if (lvsUser != null) {
                // Lưu thông tin user vào session
                session.setAttribute("LvsCurrentUser", lvsUser);

                // Kiểm tra role để chuyển hướng
                if (lvsUser.getLvsRole() == LvsUser.LvsRole.ADMIN ||
                        lvsUser.getLvsRole() == LvsUser.LvsRole.MODERATOR) {
                    return "redirect:/LvsAdmin/LvsDashboard";
                } else {
                    return "redirect:/LvsUser/LvsDashboard";
                }
            } else {
                model.addAttribute("LvsError", "Tên đăng nhập hoặc mật khẩu không đúng!");
                return "LvsAuth/LvsLogin.html";
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Đăng nhập thất bại: " + e.getMessage());
            return "LvsAuth/LvsLogin.html";
        }
    }

    // Hiển thị trang đăng ký
    @GetMapping("/LvsRegister")
    public String lvsShowRegisterPage() {
        return "LvsAuth/LvsRegister";
    }

    // Xử lý đăng ký
    @PostMapping("/LvsRegister")
    public String lvsRegister(@ModelAttribute LvsUser lvsUser,
                              @RequestParam String lvsConfirmPassword,
                              Model model) {
        try {
            // Kiểm tra mật khẩu xác nhận
            if (!lvsUser.getLvsPassword().equals(lvsConfirmPassword)) {
                model.addAttribute("LvsError", "Mật khẩu xác nhận không khớp!");
                return "LvsAuth/LvsRegister";
            }

            // Đăng ký user mới
            LvsUser lvsRegisteredUser = lvsUserService.lvsRegisterUser(lvsUser);

            if (lvsRegisteredUser != null) {
                model.addAttribute("LvsSuccess", "Đăng ký thành công! Vui lòng đăng nhập.");
                return "redirect:/LvsAuth/LvsLogin.html";
            } else {
                model.addAttribute("LvsError", "Đăng ký thất bại!");
                return "LvsAuth/LvsRegister";
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Đăng ký thất bại: " + e.getMessage());
            return "LvsAuth/LvsRegister";
        }
    }

    // Đăng xuất
    @GetMapping("/LvsLogout")
    public String lvsLogout(HttpSession session) {
        // Xóa session
        session.invalidate();
        return "redirect:/LvsAuth/LvsLogin.html";
    }

    // Quên mật khẩu
    @GetMapping("/LvsForgotPassword")
    public String lvsShowForgotPasswordPage() {
        return "LvsAuth/LvsForgotPassword";
    }

    // Xử lý quên mật khẩu
    @PostMapping("/LvsForgotPassword")
    public String lvsProcessForgotPassword(@RequestParam String lvsEmail, Model model) {
        try {
            lvsUserService.lvsSendPasswordResetEmail(lvsEmail);
            model.addAttribute("LvsSuccess", "Đã gửi email đặt lại mật khẩu!");
            return "LvsAuth/LvsForgotPassword";
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAuth/LvsForgotPassword";
        }
    }
}