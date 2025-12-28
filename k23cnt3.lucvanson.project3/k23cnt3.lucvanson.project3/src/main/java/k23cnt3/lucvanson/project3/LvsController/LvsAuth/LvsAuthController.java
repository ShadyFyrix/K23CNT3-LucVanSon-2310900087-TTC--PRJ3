package k23cnt3.lucvanson.project3.LvsController.LvsAuth;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import k23cnt3.lucvanson.project3.LvsService.LvsQuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

/**
 * LvsAuthController - Controller xử lý xác thực người dùng theo vai trò
 * 
 * Chức năng chính:
 * - Đăng nhập ADMIN: Sử dụng /LvsAdmin/LvsLogin
 * - Đăng nhập USER/MODERATOR: Sử dụng /LvsUser/LvsLogin
 * - Đăng ký (Register): Tạo tài khoản mới cho user
 * - Đăng xuất (Logout): Xóa session và đăng xuất user
 * - Quên mật khẩu (Forgot Password): Gửi link reset password
 * 
 * Role-Based Redirection:
 * - ADMIN → /LvsAdmin/LvsDashboard (via Admin Panel button)
 * - MODERATOR → /LvsModerator/LvsDashboard (via Moderator button)
 * - USER → /lvsforum (trang chủ người dùng)
 * 
 * Session Management:
 * - Lưu thông tin user vào session với key "LvsCurrentUser"
 * - Session được duy trì cho đến khi user logout hoặc hết hạn
 * 
 * @author LucVanSon
 * @version 2.0
 */
@Controller
public class LvsAuthController {

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsQuestService lvsQuestService;

    // ==================== ADMIN LOGIN (DEPRECATED - Use Unified Login)
    // ====================

    /*
     * DEPRECATED: Admin login has been moved to unified login page at
     * /LvsUser/LvsLogin
     * All users (ADMIN, MODERATOR, USER) now use the same login page.
     * After successful login, users are redirected based on their role.
     * 
     * This section is commented out to enforce the unified login system.
     * If you need to access admin dashboard, please use /LvsUser/LvsLogin
     */

    /*
     * @GetMapping("/LvsAdmin/LvsLogin")
     * public String lvsShowAdminLoginPage(Model model,
     * 
     * @RequestParam(value = "error", required = false) String error,
     * 
     * @RequestParam(value = "logout", required = false) String logout) {
     * 
     * if (error != null) {
     * model.addAttribute("lvsError", "Tên đăng nhập hoặc mật khẩu không đúng!");
     * }
     * 
     * if (logout != null) {
     * model.addAttribute("lvsSuccess", "Đăng xuất thành công!");
     * }
     * 
     * return "LvsAreas/LvsAdmin/LvsSecurity/LvsLogin";
     * }
     * 
     * @PostMapping("/LvsAdmin/LvsLogin")
     * public String lvsProcessAdminLogin(
     * 
     * @RequestParam String lvsUsernameOrEmail,
     * 
     * @RequestParam String lvsPassword,
     * HttpSession session,
     * RedirectAttributes redirectAttributes,
     * HttpServletRequest request) {
     * 
     * try {
     * System.out.println("[ADMIN LOGIN] ========== LOGIN ATTEMPT ==========");
     * System.out.println("[ADMIN LOGIN] Raw username/email value: [" +
     * lvsUsernameOrEmail + "]");
     * System.out.println("[ADMIN LOGIN] Username length: " +
     * lvsUsernameOrEmail.length());
     * System.out.println("[ADMIN LOGIN] Password length: " + lvsPassword.length());
     * 
     * LvsUser lvsUser = lvsUserService.lvsGetUserByUsername(lvsUsernameOrEmail);
     * 
     * if (lvsUser == null) {
     * System.out.println("[ADMIN LOGIN] Not found by username, trying email...");
     * lvsUser = lvsUserService.lvsGetUserByEmail(lvsUsernameOrEmail);
     * }
     * 
     * if (lvsUser == null) {
     * System.out.println("[ADMIN LOGIN] User not found!");
     * redirectAttributes.addFlashAttribute("lvsError",
     * "Tên đăng nhập hoặc email không tồn tại!");
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     * 
     * System.out.println(
     * "[ADMIN LOGIN] User found: " + lvsUser.getLvsUsername() + ", Role: " +
     * lvsUser.getLvsRole());
     * 
     * boolean passwordMatch = lvsUserService.lvsCheckPassword(lvsUser,
     * lvsPassword);
     * System.out.println("[ADMIN LOGIN] Password match: " + passwordMatch);
     * 
     * if (!passwordMatch) {
     * redirectAttributes.addFlashAttribute("lvsError",
     * "Mật khẩu không đúng!");
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     * 
     * if (lvsUser.getLvsRole() != LvsUser.LvsRole.ADMIN) {
     * redirectAttributes.addFlashAttribute("lvsError",
     * "Bạn không có quyền truy cập vào trang quản trị!");
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     * 
     * if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.BANNED) {
     * redirectAttributes.addFlashAttribute("lvsError",
     * "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     * 
     * if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.INACTIVE) {
     * redirectAttributes.addFlashAttribute("lvsError",
     * "Tài khoản của bạn chưa được kích hoạt.");
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     * 
     * session.setAttribute("LvsCurrentUser", lvsUser);
     * System.out.println("[ADMIN LOGIN] Session created for user: " +
     * lvsUser.getLvsUsername());
     * 
     * UsernamePasswordAuthenticationToken authentication = new
     * UsernamePasswordAuthenticationToken(
     * lvsUser.getLvsUsername(),
     * lvsUser.getLvsPassword(),
     * Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +
     * lvsUser.getLvsRole().name())));
     * 
     * SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
     * securityContext.setAuthentication(authentication);
     * SecurityContextHolder.setContext(securityContext);
     * 
     * SecurityContextRepository securityContextRepository = new
     * HttpSessionSecurityContextRepository();
     * securityContextRepository.saveContext(securityContext, request, null);
     * 
     * System.out.
     * println("[ADMIN LOGIN] Spring Security authentication set and saved to session for: "
     * + lvsUser.getLvsUsername());
     * 
     * redirectAttributes.addFlashAttribute("lvsSuccess",
     * "Đăng nhập thành công! Chào mừng quản trị viên " + lvsUser.getLvsUsername());
     * 
     * System.out.println("[ADMIN LOGIN] Redirecting to /LvsAdmin/LvsDashboard");
     * return "redirect:/LvsAdmin/LvsDashboard";
     * 
     * } catch (Exception e) {
     * redirectAttributes.addFlashAttribute("lvsError",
     * "Lỗi đăng nhập: " + e.getMessage());
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     * }
     * 
     * @GetMapping("/LvsAdmin/LvsLogout")
     * public String lvsAdminLogout(HttpSession session, RedirectAttributes
     * redirectAttributes) {
     * session.invalidate();
     * 
     * redirectAttributes.addFlashAttribute("lvsSuccess",
     * "Đăng xuất thành công!");
     * 
     * return "redirect:/LvsAdmin/LvsLogin";
     * }
     */

    /**
     * Admin Logout - Redirect to User Homepage
     * 
     * When admin logs out, redirect to user homepage (not logged in state)
     * instead of login page for better UX
     */
    @GetMapping("/LvsAdmin/LvsLogout")
    public String lvsAdminLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalidate session
        session.invalidate();

        redirectAttributes.addFlashAttribute("lvsSuccess",
                "Đăng xuất thành công! Hẹn gặp lại bạn.");

        // Redirect to login page
        return "redirect:/LvsUser/LvsLogin";
    }

    // ==================== USER/MODERATOR LOGIN ====================

    /**
     * Hiển thị trang đăng nhập cho USER (legacy URL)
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Đường dẫn đến template login của user
     */
    @GetMapping("/LvsLogin")
    public String lvsShowLoginPage(Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("lvsError", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("lvsSuccess", "Đăng xuất thành công!");
        }

        return "LvsAreas/LvsUsers/LvsSecurity/LvsLogin";
    }

    /**
     * Hiển thị trang đăng nhập cho USER/MODERATOR
     * 
     * @param model  Model để truyền dữ liệu sang view
     * @param error  Tham số lỗi nếu đăng nhập thất bại
     * @param logout Tham số khi đã đăng xuất
     * @return Đường dẫn đến template login của user
     */
    @GetMapping({ "/LvsUser/LvsLogin", "/LvsAuth/LvsLogin" })
    public String lvsShowUserLoginPage(Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("lvsError", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("lvsSuccess", "Đăng xuất thành công!");
        }

        return "LvsAreas/LvsUsers/LvsSecurity/LvsLogin";
    }

    /**
     * Xử lý đăng nhập cho USER/MODERATOR/ADMIN (Unified Login)
     * 
     * Flow xử lý:
     * 1. Nhận username/email và password từ form
     * 2. Kiểm tra thông tin đăng nhập qua LvsUserService
     * 3. Allow all roles (ADMIN, MODERATOR, USER)
     * 4. Redirect to user dashboard (admin can access admin panel via button)
     * 
     * @param lvsUsernameOrEmail Username hoặc email của user
     * @param lvsPassword        Mật khẩu của user
     * @param session            HttpSession để lưu thông tin user
     * @param redirectAttributes Để truyền flash message
     * @return Redirect đến trang tương ứng theo role
     */
    @PostMapping({ "/LvsUser/LvsLogin", "/LvsAuth/LvsLogin" })
    public String lvsProcessUserLogin(
            @RequestParam String lvsUsernameOrEmail,
            @RequestParam String lvsPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        try {
            System.out.println("[UNIFIED LOGIN] Attempting login for: " + lvsUsernameOrEmail);

            // Tìm user theo username hoặc email
            LvsUser lvsUser = lvsUserService.lvsGetUserByUsername(lvsUsernameOrEmail);

            // Nếu không tìm thấy theo username, thử tìm theo email
            if (lvsUser == null) {
                System.out.println("[UNIFIED LOGIN] Not found by username, trying email...");
                lvsUser = lvsUserService.lvsGetUserByEmail(lvsUsernameOrEmail);
            }

            // Kiểm tra user có tồn tại không
            if (lvsUser == null) {
                System.out.println("[UNIFIED LOGIN] User not found!");
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tên đăng nhập hoặc email không tồn tại!");
                return "redirect:/LvsAuth/LvsLogin";
            }

            System.out.println(
                    "[UNIFIED LOGIN] User found: " + lvsUser.getLvsUsername() + ", Role: " + lvsUser.getLvsRole());

            // Kiểm tra password
            boolean passwordMatch = lvsUserService.lvsCheckPassword(lvsUser, lvsPassword);
            System.out.println("[UNIFIED LOGIN] Password match: " + passwordMatch);

            if (!passwordMatch) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Mật khẩu không đúng!");
                return "redirect:/LvsAuth/LvsLogin";
            }

            // Allow all roles (ADMIN, MODERATOR, USER) to login via unified login page
            // Redirect will be based on role after successful authentication

            // Kiểm tra trạng thái tài khoản
            if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.BANNED) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ admin.");
                return "redirect:/LvsAuth/LvsLogin";
            }

            if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.INACTIVE) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tài khoản của bạn chưa được kích hoạt.");
                return "redirect:/LvsAuth/LvsLogin";
            }

            // Lưu thông tin user vào session
            session.setAttribute("LvsCurrentUser", lvsUser);
            System.out.println("[UNIFIED LOGIN] Session created for user: " + lvsUser.getLvsUsername());

            // Auto-assign active quests to user
            try {
                lvsQuestService.lvsAssignActiveQuests(lvsUser);
                System.out.println("[UNIFIED LOGIN] Active quests assigned to user: " + lvsUser.getLvsUsername());
            } catch (Exception e) {
                System.out.println("[UNIFIED LOGIN] Error assigning quests: " + e.getMessage());
            }

            // IMPORTANT: Authenticate with Spring Security to avoid 403 Forbidden
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    lvsUser.getLvsUsername(),
                    lvsUser.getLvsPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + lvsUser.getLvsRole().name())));

            // Set authentication in SecurityContext
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // CRITICAL: Save SecurityContext to session so it persists across redirect
            SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
            securityContextRepository.saveContext(securityContext, request, null);

            System.out.println("[UNIFIED LOGIN] Spring Security authentication set and saved to session for: "
                    + lvsUser.getLvsUsername());

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Đăng nhập thành công! Chào mừng " + lvsUser.getLvsUsername());

            // ALL users redirect to USER area first (not admin/moderator area)
            // Admin and Moderator can access their areas via special buttons
            System.out
                    .println("[UNIFIED LOGIN] Redirecting to /LvsUser/LvsHome for role: " + lvsUser.getLvsRole());
            return "redirect:/LvsUser/LvsHome";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi đăng nhập: " + e.getMessage());
            return "redirect:/LvsAuth/LvsLogin";
        }
    }

    /**
     * Xử lý đăng xuất cho USER/MODERATOR
     * 
     * @param session            HttpSession để xóa thông tin user
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về trang home
     */
    @GetMapping("/LvsUser/LvsLogout")
    public String lvsUserLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Xóa session
        session.invalidate();

        redirectAttributes.addFlashAttribute("lvsSuccess",
                "Đăng xuất thành công! Hẹn gặp lại bạn.");

        return "redirect:/LvsUser/LvsLogin";
    }

    // ==================== USER REGISTRATION ====================

    /**
     * Hiển thị trang đăng ký cho USER
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Đường dẫn đến template register của user
     */
    @GetMapping("/LvsUser/LvsRegister")
    public String lvsShowUserRegisterPage(Model model) {
        model.addAttribute("lvsUser", new LvsUser());
        return "LvsAreas/LvsUsers/LvsSecurity/LvsRegister";
    }

    /**
     * Xử lý đăng ký tài khoản mới cho USER
     * 
     * Flow xử lý:
     * 1. Nhận thông tin đăng ký từ form
     * 2. Validate password và confirm password
     * 3. Set role mặc định là USER và status là ACTIVE
     * 4. Tạo tài khoản mới qua LvsUserService
     * 5. Redirect đến trang login với thông báo thành công
     * 
     * @param lvsUser            Đối tượng LvsUser chứa thông tin đăng ký
     * @param lvsConfirmPassword Mật khẩu xác nhận
     * @param redirectAttributes Để truyền flash message
     * @return Redirect đến login nếu thành công, ngược lại quay lại register
     */
    @PostMapping("/LvsUser/LvsRegister")
    public String lvsProcessUserRegister(
            @ModelAttribute LvsUser lvsUser,
            @RequestParam String lvsConfirmPassword,
            RedirectAttributes redirectAttributes) {

        // Validate password match
        if (!lvsUser.getLvsPassword().equals(lvsConfirmPassword)) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Mật khẩu xác nhận không khớp!");
            redirectAttributes.addFlashAttribute("lvsUser", lvsUser);
            return "redirect:/LvsUser/LvsRegister";
        }

        try {
            // Set role và status mặc định cho user mới
            lvsUser.setLvsRole(LvsUser.LvsRole.USER);
            lvsUser.setLvsStatus(LvsUser.LvsUserStatus.ACTIVE);

            // Set giá trị mặc định cho coin và balance
            if (lvsUser.getLvsCoin() == null) {
                lvsUser.setLvsCoin(0.0);
            }
            if (lvsUser.getLvsBalance() == null) {
                lvsUser.setLvsBalance(0.0);
            }

            // Tạo tài khoản mới
            lvsUserService.lvsCreateUser(lvsUser);

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Đăng ký thành công! Vui lòng đăng nhập với tài khoản của bạn.");
            return "redirect:/LvsUser/LvsLogin";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi đăng ký: " + e.getMessage());
            redirectAttributes.addFlashAttribute("lvsUser", lvsUser);
            return "redirect:/LvsUser/LvsRegister";
        }
    }

    // ==================== FORGOT PASSWORD ====================

    /**
     * Hiển thị trang quên mật khẩu
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Đường dẫn đến template forgot password
     */
    @GetMapping("/LvsUser/LvsForgotPassword")
    public String lvsShowForgotPasswordPage(Model model) {
        return "LvsAreas/LvsUsers/LvsSecurity/LvsForgotPassword";
    }

    /**
     * Xử lý yêu cầu reset mật khẩu
     * 
     * Flow xử lý:
     * 1. Nhận email từ form
     * 2. Kiểm tra email có tồn tại trong hệ thống không
     * 3. Gửi link reset password đến email (TODO: implement email service)
     * 4. Hiển thị thông báo thành công
     * 
     * @param lvsEmail           Email của user cần reset password
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về trang forgot password với thông báo
     */
    @PostMapping("/LvsUser/LvsForgotPassword")
    public String lvsProcessForgotPassword(
            @RequestParam String lvsEmail,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra email có tồn tại không
            LvsUser lvsUser = lvsUserService.lvsGetUserByEmail(lvsEmail);

            if (lvsUser == null) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Email không tồn tại trong hệ thống!");
                return "redirect:/LvsUser/LvsForgotPassword";
            }

            // TODO: Implement email service để gửi link reset password
            // lvsEmailService.lvsSendResetPasswordEmail(lvsUser);

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Link reset mật khẩu đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư!");
            return "redirect:/LvsUser/LvsLogin";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi: " + e.getMessage());
            return "redirect:/LvsUser/LvsForgotPassword";
        }
    }

    // ==================== ERROR PAGES ====================

    /**
     * Hiển thị trang lỗi 403 - Access Denied
     * 
     * @return Đường dẫn đến template 403
     */
    @GetMapping("/403")
    public String lvsAccessDenied() {
        return "403";
    }
}