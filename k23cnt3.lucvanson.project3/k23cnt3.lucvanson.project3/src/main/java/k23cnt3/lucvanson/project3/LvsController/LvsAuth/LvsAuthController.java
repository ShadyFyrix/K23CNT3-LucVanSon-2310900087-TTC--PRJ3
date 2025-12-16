package k23cnt3.lucvanson.project3.LvsController.LvsAuth;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
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
 * - ADMIN → /LvsAdmin/LvsDashboard
 * - MODERATOR → /LvsModerator/LvsDashboard (trang phê duyệt)
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

    // ==================== ADMIN LOGIN ====================

    /**
     * Hiển thị trang đăng nhập cho ADMIN
     * 
     * @param model  Model để truyền dữ liệu sang view
     * @param error  Tham số lỗi nếu đăng nhập thất bại
     * @param logout Tham số khi đã đăng xuất
     * @return Đường dẫn đến template login của admin
     */
    @GetMapping("/LvsAdmin/LvsLogin")
    public String lvsShowAdminLoginPage(Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout) {

        if (error != null) {
            model.addAttribute("lvsError", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("lvsSuccess", "Đăng xuất thành công!");
        }

        return "LvsAreas/LvsAdmin/LvsSecurity/LvsLogin";
    }

    /**
     * Xử lý đăng nhập cho ADMIN
     * 
     * Flow xử lý:
     * 1. Nhận username/email và password từ form
     * 2. Kiểm tra thông tin đăng nhập qua LvsUserService
     * 3. Kiểm tra role có phải ADMIN không
     * 4. Nếu đúng: Lưu user vào session và redirect đến admin dashboard
     * 5. Nếu sai: Hiển thị thông báo lỗi và quay lại trang login
     * 
     * @param lvsUsernameOrEmail Username hoặc email của admin
     * @param lvsPassword        Mật khẩu của admin
     * @param session            HttpSession để lưu thông tin user
     * @param redirectAttributes Để truyền flash message
     * @return Redirect đến admin dashboard nếu thành công
     */
    @PostMapping("/LvsAdmin/LvsLogin")
    public String lvsProcessAdminLogin(
            @RequestParam String lvsUsernameOrEmail,
            @RequestParam String lvsPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        try {
            System.out.println("[ADMIN LOGIN] ========== LOGIN ATTEMPT ==========");
            System.out.println("[ADMIN LOGIN] Raw username/email value: [" + lvsUsernameOrEmail + "]");
            System.out.println("[ADMIN LOGIN] Username length: " + lvsUsernameOrEmail.length());
            System.out.println("[ADMIN LOGIN] Password length: " + lvsPassword.length());

            // Tìm user theo username hoặc email
            LvsUser lvsUser = lvsUserService.lvsGetUserByUsername(lvsUsernameOrEmail);

            // Nếu không tìm thấy theo username, thử tìm theo email
            if (lvsUser == null) {
                System.out.println("[ADMIN LOGIN] Not found by username, trying email...");
                lvsUser = lvsUserService.lvsGetUserByEmail(lvsUsernameOrEmail);
            }

            // Kiểm tra user có tồn tại không
            if (lvsUser == null) {
                System.out.println("[ADMIN LOGIN] User not found!");
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tên đăng nhập hoặc email không tồn tại!");
                return "redirect:/LvsAdmin/LvsLogin";
            }

            System.out.println(
                    "[ADMIN LOGIN] User found: " + lvsUser.getLvsUsername() + ", Role: " + lvsUser.getLvsRole());

            // Kiểm tra password
            boolean passwordMatch = lvsUserService.lvsCheckPassword(lvsUser, lvsPassword);
            System.out.println("[ADMIN LOGIN] Password match: " + passwordMatch);

            if (!passwordMatch) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Mật khẩu không đúng!");
                return "redirect:/LvsAdmin/LvsLogin";
            }

            // Kiểm tra role - CHỈ ADMIN mới được đăng nhập từ trang này
            if (lvsUser.getLvsRole() != LvsUser.LvsRole.ADMIN) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Bạn không có quyền truy cập vào trang quản trị!");
                return "redirect:/LvsAdmin/LvsLogin";
            }

            // Kiểm tra trạng thái tài khoản
            if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.BANNED) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
                return "redirect:/LvsAdmin/LvsLogin";
            }

            if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.INACTIVE) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tài khoản của bạn chưa được kích hoạt.");
                return "redirect:/LvsAdmin/LvsLogin";
            }

            // Lưu thông tin user vào session
            session.setAttribute("LvsCurrentUser", lvsUser);
            System.out.println("[ADMIN LOGIN] Session created for user: " + lvsUser.getLvsUsername());

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

            System.out.println("[ADMIN LOGIN] Spring Security authentication set and saved to session for: "
                    + lvsUser.getLvsUsername());

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Đăng nhập thành công! Chào mừng quản trị viên " + lvsUser.getLvsUsername());

            System.out.println("[ADMIN LOGIN] Redirecting to /LvsAdmin/LvsDashboard");
            // Redirect đến Admin Dashboard
            return "redirect:/LvsAdmin/LvsDashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi đăng nhập: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsLogin";
        }
    }

    /**
     * Xử lý đăng xuất cho ADMIN
     * 
     * @param session            HttpSession để xóa thông tin user
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về trang login admin
     */
    @GetMapping("/LvsAdmin/LvsLogout")
    public String lvsAdminLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Xóa session
        session.invalidate();

        redirectAttributes.addFlashAttribute("lvsSuccess",
                "Đăng xuất thành công!");

        return "redirect:/LvsAdmin/LvsLogin";
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
    @GetMapping("/LvsUser/LvsLogin")
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
     * Xử lý đăng nhập cho USER/MODERATOR
     * 
     * Flow xử lý:
     * 1. Nhận username/email và password từ form
     * 2. Kiểm tra thông tin đăng nhập qua LvsUserService
     * 3. Kiểm tra role (USER hoặc MODERATOR)
     * 4. Redirect theo role:
     * - MODERATOR → Trang phê duyệt
     * - USER → Trang chủ forum
     * 
     * @param lvsUsernameOrEmail Username hoặc email của user
     * @param lvsPassword        Mật khẩu của user
     * @param session            HttpSession để lưu thông tin user
     * @param redirectAttributes Để truyền flash message
     * @return Redirect đến trang tương ứng theo role
     */
    @PostMapping("/LvsUser/LvsLogin")
    public String lvsProcessUserLogin(
            @RequestParam String lvsUsernameOrEmail,
            @RequestParam String lvsPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        try {
            System.out.println("[USER LOGIN] Attempting login for: " + lvsUsernameOrEmail);

            // Tìm user theo username hoặc email
            LvsUser lvsUser = lvsUserService.lvsGetUserByUsername(lvsUsernameOrEmail);

            // Nếu không tìm thấy theo username, thử tìm theo email
            if (lvsUser == null) {
                System.out.println("[USER LOGIN] Not found by username, trying email...");
                lvsUser = lvsUserService.lvsGetUserByEmail(lvsUsernameOrEmail);
            }

            // Kiểm tra user có tồn tại không
            if (lvsUser == null) {
                System.out.println("[USER LOGIN] User not found!");
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tên đăng nhập hoặc email không tồn tại!");
                return "redirect:/LvsUser/LvsLogin";
            }

            System.out.println(
                    "[USER LOGIN] User found: " + lvsUser.getLvsUsername() + ", Role: " + lvsUser.getLvsRole());

            // Kiểm tra password
            boolean passwordMatch = lvsUserService.lvsCheckPassword(lvsUser, lvsPassword);
            System.out.println("[USER LOGIN] Password match: " + passwordMatch);

            if (!passwordMatch) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Mật khẩu không đúng!");
                return "redirect:/LvsUser/LvsLogin";
            }

            // Kiểm tra role - ADMIN không được đăng nhập từ trang này
            if (lvsUser.getLvsRole() == LvsUser.LvsRole.ADMIN) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Vui lòng sử dụng trang đăng nhập dành cho quản trị viên!");
                return "redirect:/LvsUser/LvsLogin";
            }

            // Kiểm tra trạng thái tài khoản
            if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.BANNED) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ admin.");
                return "redirect:/LvsUser/LvsLogin";
            }

            if (lvsUser.getLvsStatus() == LvsUser.LvsUserStatus.INACTIVE) {
                redirectAttributes.addFlashAttribute("lvsError",
                        "Tài khoản của bạn chưa được kích hoạt.");
                return "redirect:/LvsUser/LvsLogin";
            }

            // Lưu thông tin user vào session
            session.setAttribute("LvsCurrentUser", lvsUser);
            System.out.println("[USER LOGIN] Session created for user: " + lvsUser.getLvsUsername());

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

            System.out.println("[USER LOGIN] Spring Security authentication set and saved to session for: "
                    + lvsUser.getLvsUsername());

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Đăng nhập thành công! Chào mừng " + lvsUser.getLvsUsername());

            // Redirect theo role
            if (lvsUser.getLvsRole() == LvsUser.LvsRole.MODERATOR) {
                System.out.println("[USER LOGIN] Redirecting MODERATOR to /LvsModerator/LvsDashboard");
                // MODERATOR → Trang phê duyệt (moderator dashboard)
                return "redirect:/LvsModerator/LvsDashboard";
            } else {
                System.out.println("[USER LOGIN] Redirecting USER to /lvsforum");
                // USER → Trang chủ forum
                return "redirect:/lvsforum";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi đăng nhập: " + e.getMessage());
            return "redirect:/LvsUser/LvsLogin";
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

        return "redirect:/";
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