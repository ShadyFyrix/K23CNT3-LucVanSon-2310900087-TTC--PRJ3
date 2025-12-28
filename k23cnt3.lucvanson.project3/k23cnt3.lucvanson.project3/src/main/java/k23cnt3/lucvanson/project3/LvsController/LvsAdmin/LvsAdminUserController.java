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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Controller quản lý User (Người dùng) trong Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị danh sách users với phân trang, tìm kiếm và lọc</li>
 * <li>Xem chi tiết thông tin user</li>
 * <li>Tạo mới user</li>
 * <li>Chỉnh sửa thông tin user</li>
 * <li>Xóa user (soft delete và hard delete)</li>
 * <li>Khóa/mở khóa tài khoản user (ban/unban)</li>
 * <li>Quản lý coin và balance của user</li>
 * </ul>
 * 
 * <p>
 * Tính năng đặc biệt:
 * </p>
 * <ul>
 * <li><strong>Soft Delete:</strong> Vô hiệu hóa user (set status = INACTIVE),
 * có thể khôi phục</li>
 * <li><strong>Hard Delete:</strong> Xóa vĩnh viễn khỏi database, không thể khôi
 * phục</li>
 * <li><strong>Ban/Unban:</strong> Khóa/mở khóa tài khoản user (set status =
 * BANNED)</li>
 * <li><strong>Filter:</strong> Lọc theo role (ADMIN, MODERATOR, USER) và status
 * (ACTIVE, INACTIVE, BANNED)</li>
 * <li><strong>Search:</strong> Tìm kiếm theo username, email, fullname</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsUser/LvsList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsUser/LvsDetail.html</li>
 * <li>Create: LvsAreas/LvsAdmin/LvsUser/LvsCreate.html</li>
 * <li>Edit: LvsAreas/LvsAdmin/LvsUser/LvsEdit.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsUser")
public class LvsAdminUserController {

    /**
     * Service xử lý logic nghiệp vụ cho User
     */
    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Service xử lý upload file
     */
    @Autowired
    private k23cnt3.lucvanson.project3.LvsService.LvsFileUploadService lvsFileUploadService;

    /**
     * Hiển thị danh sách users với phân trang, tìm kiếm và lọc
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy danh sách users theo trang (10 users/trang)</li>
     * <li>Tìm kiếm theo keyword (username, email, fullname)</li>
     * <li>Lọc theo role (ADMIN, MODERATOR, USER)</li>
     * <li>Lọc theo status (ACTIVE, INACTIVE, BANNED)</li>
     * <li>Kết hợp nhiều điều kiện lọc</li>
     * </ul>
     * 
     * <p>
     * Ưu tiên xử lý:
     * </p>
     * <ol>
     * <li>Nếu có keyword -> tìm kiếm theo keyword</li>
     * <li>Nếu có cả role và status -> lọc theo cả hai</li>
     * <li>Nếu chỉ có role -> lọc theo role</li>
     * <li>Nếu chỉ có status -> lọc theo status</li>
     * <li>Nếu không có gì -> lấy tất cả</li>
     * </ol>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsUsers
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsUsers?page=0&keyword=admin&role=ADMIN&status=ACTIVE
     * </p>
     * 
     * @param page    Số trang hiện tại (mặc định = 0)
     * @param keyword Từ khóa tìm kiếm (optional)
     * @param role    Role để lọc (optional)
     * @param status  Status để lọc (optional)
     * @param model   Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsUser/LvsList
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsUsers: Page&lt;LvsUser&gt; - Danh sách users có phân
     *         trang</li>
     *         <li>lvsKeyword: String - Từ khóa đang tìm kiếm</li>
     *         <li>lvsSelectedRole: LvsRole - Role đang được chọn</li>
     *         <li>lvsSelectedStatus: LvsUserStatus - Status đang được chọn</li>
     *         <li>lvsPageTitle: String - Tiêu đề trang</li>
     *         <li>lvsRoles: LvsRole[] - Tất cả roles có thể</li>
     *         <li>lvsStatuses: LvsUserStatus[] - Tất cả statuses có thể</li>
     *         </ul>
     */
    @GetMapping("/LvsList")
    public String lvsListUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LvsUser.LvsRole role,
            @RequestParam(required = false) LvsUser.LvsUserStatus status,
            Model model) {

        // Tạo Pageable object (10 users mỗi trang)
        Pageable lvsPageable = PageRequest.of(page, 10);
        Page<LvsUser> lvsUsers;

        // Xử lý theo thứ tự ưu tiên
        if (keyword != null && !keyword.isEmpty()) {
            // Tìm kiếm theo keyword (username, email, fullname)
            lvsUsers = lvsUserService.lvsSearchUsers(keyword, lvsPageable);
        } else if (role != null && status != null) {
            // Lọc theo cả role và status
            lvsUsers = lvsUserService.lvsGetUsersByRoleAndStatus(role, status, lvsPageable);
        } else if (role != null) {
            // Lọc chỉ theo role
            lvsUsers = lvsUserService.lvsGetUsersByRole(role, lvsPageable);
        } else if (status != null) {
            // Lọc chỉ theo status
            lvsUsers = lvsUserService.lvsGetUsersByStatus(status, lvsPageable);
        } else {
            // Lấy tất cả users
            lvsUsers = lvsUserService.lvsGetAllUsers(lvsPageable);
        }

        // Truyền dữ liệu ra view
        model.addAttribute("lvsUsers", lvsUsers);
        model.addAttribute("lvsKeyword", keyword);
        model.addAttribute("lvsSelectedRole", role);
        model.addAttribute("lvsSelectedStatus", status);
        model.addAttribute("lvsPageTitle", "Quản lý User");
        model.addAttribute("lvsRoles", LvsUser.LvsRole.values());
        model.addAttribute("lvsStatuses", LvsUser.LvsUserStatus.values());

        return "LvsAreas/LvsAdmin/LvsUser/LvsList";
    }

    /**
     * Hiển thị form thêm user mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Tạo object LvsUser rỗng</li>
     * <li>Lấy danh sách roles và statuses để chọn</li>
     * <li>Hiển thị form nhập thông tin</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsUser/LvsCreate
     * </p>
     * 
     * @param model Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsUser/LvsCreate
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsUser: LvsUser - Object rỗng để binding</li>
     *         <li>lvsPageTitle: String - Tiêu đề trang</li>
     *         <li>lvsRoles: LvsRole[] - Danh sách roles</li>
     *         <li>lvsStatuses: LvsUserStatus[] - Danh sách statuses</li>
     *         </ul>
     */
    @GetMapping("/LvsCreate")
    public String lvsShowCreateForm(Model model) {
        // Tạo object rỗng để binding với form
        model.addAttribute("lvsUser", new LvsUser());
        model.addAttribute("lvsPageTitle", "Thêm User mới");
        model.addAttribute("lvsRoles", LvsUser.LvsRole.values());
        model.addAttribute("lvsStatuses", LvsUser.LvsUserStatus.values());

        return "LvsAreas/LvsAdmin/LvsUser/LvsCreate";
    }

    /**
     * Xử lý submit form thêm user mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nhận dữ liệu từ form</li>
     * <li>Validate password confirmation</li>
     * <li>Mã hóa password</li>
     * <li>Lưu user vào database</li>
     * </ul>
     * 
     * <p>
     * Validation:
     * </p>
     * <ul>
     * <li>Password phải khớp với confirm password</li>
     * <li>Username phải unique</li>
     * <li>Email phải unique và đúng format</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsUser/LvsCreate
     * </p>
     * 
     * @param lvsUser               Object LvsUser được binding từ form
     * @param lvsConfirmPassword    Password xác nhận
     * @param lvsRedirectAttributes Để truyền flash messages
     * @return Redirect về list nếu thành công, hoặc create page nếu lỗi
     * 
     *         <p>
     *         Success: redirect:/LvsAdmin/LvsUser
     *         </p>
     *         <p>
     *         Error: redirect:/LvsAdmin/LvsUser/LvsCreate
     *         </p>
     */
    @PostMapping("/LvsCreate")
    public String lvsCreateUser(@ModelAttribute LvsUser lvsUser,
            @RequestParam String lvsConfirmPassword,
            @RequestParam(required = false) MultipartFile lvsAvatarFile,
            RedirectAttributes lvsRedirectAttributes) {

        // Validate password confirmation
        if (!lvsUser.getLvsPassword().equals(lvsConfirmPassword)) {
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Mật khẩu xác nhận không khớp!");
            return "redirect:/LvsAdmin/LvsUser/LvsCreate";
        }

        try {
            // Handle avatar upload using service
            if (lvsAvatarFile != null && !lvsAvatarFile.isEmpty()) {
                String avatarUrl = lvsFileUploadService.lvsSaveFile(lvsAvatarFile, "avatars");
                lvsUser.setLvsAvatarUrl(avatarUrl);
            }

            // Tạo user mới (service sẽ tự động mã hóa password)
            lvsUserService.lvsCreateUser(lvsUser);

            // Thêm thông báo thành công
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Thêm user thành công!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsList";
    }

    /**
     * Hiển thị chi tiết thông tin user
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin đầy đủ của user</li>
     * <li>Hiển thị avatar, thông tin cá nhân</li>
     * <li>Hiển thị coin, balance</li>
     * <li>Hiển thị các dự án đã tạo</li>
     * <li>Hiển thị lịch sử hoạt động</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsUser/{lvsUserId}/LvsDetail
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsUser/1/LvsDetail
     * </p>
     * 
     * @param lvsUserId ID của user cần xem
     * @param model     Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsUser/LvsDetail
     *         hoặc redirect về list nếu không tìm thấy
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsUser: LvsUser - Thông tin chi tiết user</li>
     *         <li>lvsPageTitle: String - Tiêu đề trang</li>
     *         </ul>
     */
    @GetMapping("/{lvsUserId}/LvsDetail")
    public String lvsShowUserDetail(@PathVariable Long lvsUserId, Model model) {
        try {
            // Lấy thông tin user từ database
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);

            // Truyền dữ liệu ra view
            model.addAttribute("lvsUser", lvsUser);
            model.addAttribute("lvsPageTitle", "Chi tiết User: " + lvsUser.getLvsUsername());

            return "LvsAreas/LvsAdmin/LvsUser/LvsDetail";
        } catch (Exception e) {
            // Nếu không tìm thấy user, redirect về list với thông báo lỗi
            return "redirect:/LvsAdmin/LvsUser?error=User+not+found";
        }
    }

    /**
     * Hiển thị form chỉnh sửa user
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin user hiện tại</li>
     * <li>Lấy danh sách roles và statuses</li>
     * <li>Hiển thị form với dữ liệu hiện tại</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsUser/{lvsUserId}/LvsEdit
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsUser/1/LvsEdit
     * </p>
     * 
     * @param lvsUserId ID của user cần chỉnh sửa
     * @param model     Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsUser/LvsEdit
     *         hoặc redirect về list nếu không tìm thấy
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsUser: LvsUser - Thông tin user hiện tại</li>
     *         <li>lvsPageTitle: String - Tiêu đề trang</li>
     *         <li>lvsRoles: LvsRole[] - Danh sách roles</li>
     *         <li>lvsStatuses: LvsUserStatus[] - Danh sách statuses</li>
     *         </ul>
     */
    @GetMapping("/{lvsUserId}/LvsEdit")
    public String lvsShowEditForm(@PathVariable Long lvsUserId, Model model) {
        try {
            // Lấy thông tin user
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);

            // Truyền dữ liệu ra view
            model.addAttribute("lvsUser", lvsUser);
            model.addAttribute("lvsPageTitle", "Chỉnh sửa User: " + lvsUser.getLvsUsername());
            model.addAttribute("lvsRoles", LvsUser.LvsRole.values());
            model.addAttribute("lvsStatuses", LvsUser.LvsUserStatus.values());

            return "LvsAreas/LvsAdmin/LvsUser/LvsEdit";
        } catch (Exception e) {
            // Nếu không tìm thấy user, redirect về list
            return "redirect:/LvsAdmin/LvsUser?error=User+not+found";
        }
    }

    /**
     * Xử lý submit form chỉnh sửa user
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Cập nhật thông tin cơ bản (username, email, fullname, phone, address,
     * bio)</li>
     * <li>Cập nhật role và status</li>
     * <li>Cập nhật coin và balance</li>
     * <li>Đổi password (nếu có nhập password mới)</li>
     * </ul>
     * 
     * <p>
     * Quy trình:
     * </p>
     * <ol>
     * <li>Lấy user hiện tại từ database</li>
     * <li>Cập nhật từng field với giá trị mới</li>
     * <li>Nếu có password mới -> mã hóa và cập nhật</li>
     * <li>Lưu thay đổi vào database</li>
     * </ol>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsUser/{lvsUserId}/LvsEdit
     * </p>
     * 
     * @param lvsUserId             ID của user đang chỉnh sửa
     * @param lvsUser               Object LvsUser được binding từ form
     * @param lvsNewPassword        Password mới (optional, chỉ nhập nếu muốn đổi)
     * @param lvsRedirectAttributes Để truyền flash messages
     * @return Redirect về detail page nếu thành công
     * 
     *         <p>
     *         Success: redirect:/LvsAdmin/LvsUser/{id}/LvsDetail
     *         </p>
     */
    @PostMapping("/{lvsUserId}/LvsEdit")
    public String lvsUpdateUser(@PathVariable Long lvsUserId,
            @ModelAttribute LvsUser lvsUser,
            @RequestParam(required = false) String lvsNewPassword,
            @RequestParam(required = false) MultipartFile lvsAvatarFile,
            RedirectAttributes lvsRedirectAttributes) {

        try {
            // Lấy user hiện tại từ database
            LvsUser lvsExistingUser = lvsUserService.lvsGetUserById(lvsUserId);

            // ===== CẬP NHẬT THÔNG TIN CƠ BẢN =====
            lvsExistingUser.setLvsUsername(lvsUser.getLvsUsername());
            lvsExistingUser.setLvsEmail(lvsUser.getLvsEmail());
            lvsExistingUser.setLvsFullName(lvsUser.getLvsFullName());
            lvsExistingUser.setLvsPhone(lvsUser.getLvsPhone());
            lvsExistingUser.setLvsAddress(lvsUser.getLvsAddress());
            lvsExistingUser.setLvsBio(lvsUser.getLvsBio());
            lvsExistingUser.setLvsRole(lvsUser.getLvsRole());
            lvsExistingUser.setLvsStatus(lvsUser.getLvsStatus());
            lvsExistingUser.setLvsTitle(lvsUser.getLvsTitle()); // ← CẬP NHẬT TITLE
            lvsExistingUser.setLvsCoin(lvsUser.getLvsCoin());
            lvsExistingUser.setLvsBalance(lvsUser.getLvsBalance());

            // ===== CẬP NHẬT AVATAR (NẾU CÓ FILE MỚI) =====
            if (lvsAvatarFile != null && !lvsAvatarFile.isEmpty()) {
                String avatarUrl = lvsFileUploadService.lvsSaveFile(lvsAvatarFile, "avatars");
                lvsExistingUser.setLvsAvatarUrl(avatarUrl);
            }
            // Nếu không có file mới, giữ nguyên avatar cũ (không cần set lại)

            // ===== CẬP NHẬT PASSWORD (NẾU CÓ) =====
            if (lvsNewPassword != null && !lvsNewPassword.trim().isEmpty()) {
                // Đổi password qua service (service sẽ validate và mã hóa)
                if (lvsUserService.lvsChangePassword(lvsUserId, lvsExistingUser.getLvsPassword(), lvsNewPassword)) {
                    lvsExistingUser.setLvsPassword(lvsUserService.lvsEncodePassword(lvsNewPassword));
                }
            }

            // Lưu thay đổi vào database
            lvsUserService.lvsUpdateUser(lvsExistingUser);

            // Thêm thông báo thành công
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Cập nhật user thành công!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/" + lvsUserId + "/LvsDetail";
    }

    /**
     * Xóa user (SOFT DELETE - Vô hiệu hóa)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Set status của user thành INACTIVE</li>
     * <li>Lưu lý do xóa vào database</li>
     * <li>User vẫn còn trong database, có thể khôi phục</li>
     * <li>User không thể đăng nhập</li>
     * </ul>
     * 
     * <p>
     * Khác biệt với Hard Delete:
     * </p>
     * <ul>
     * <li>Soft Delete: Chỉ đánh dấu inactive, có thể khôi phục</li>
     * <li>Hard Delete: Xóa vĩnh viễn khỏi database, không thể khôi phục</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsUser/{lvsUserId}/LvsDelete
     * </p>
     * 
     * @param lvsUserId             ID của user cần xóa
     * @param lvsReason             Lý do xóa (optional)
     * @param lvsRedirectAttributes Để truyền flash messages
     * @param request               HttpServletRequest để log thông tin request
     * @return Redirect về trang danh sách
     */
    @PostMapping("/{lvsUserId}/LvsDelete")
    public String lvsDeleteUser(@PathVariable Long lvsUserId,
            @RequestParam(required = false) String lvsReason,
            RedirectAttributes lvsRedirectAttributes,
            HttpServletRequest request) {

        // Log thông tin request để debug
        System.out.println("=== SOFT DELETE REQUEST ===");
        System.out.println("User ID: " + lvsUserId);
        System.out.println("Reason: " + lvsReason);
        System.out.println("Method: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("=========================");

        try {
            // Soft delete user (set status = INACTIVE)
            lvsUserService.lvsDeleteUser(lvsUserId, lvsReason != null ? lvsReason : "Admin delete");

            // Thêm thông báo thành công
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã vô hiệu hóa user thành công (Soft Delete)!");
        } catch (Exception e) {
            // Log lỗi chi tiết
            System.err.println("SOFT DELETE ERROR: " + e.getMessage());
            e.printStackTrace();

            // Thêm thông báo lỗi
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsList";
    }

    /**
     * Xóa user (HARD DELETE - Xóa vĩnh viễn)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Xóa vĩnh viễn user khỏi database</li>
     * <li>Lưu log lý do xóa trước khi xóa</li>
     * <li>KHÔNG THỂ KHÔI PHỤC sau khi xóa</li>
     * <li>Cần cẩn thận khi sử dụng</li>
     * </ul>
     * 
     * <p>
     * Cảnh báo:
     * </p>
     * <ul>
     * <li>Hành động này không thể hoàn tác</li>
     * <li>Nên backup dữ liệu trước khi hard delete</li>
     * <li>Cần kiểm tra ràng buộc với projects, orders, reviews</li>
     * <li>Nên dùng soft delete thay vì hard delete trong hầu hết trường hợp</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsUser/{lvsUserId}/LvsHardDelete
     * </p>
     * 
     * @param lvsUserId             ID của user cần xóa vĩnh viễn
     * @param lvsReason             Lý do xóa (optional)
     * @param lvsRedirectAttributes Để truyền flash messages
     * @param request               HttpServletRequest để log thông tin request
     * @return Redirect về trang danh sách
     */
    @PostMapping("/{lvsUserId}/LvsHardDelete")
    public String lvsHardDeleteUser(@PathVariable Long lvsUserId,
            @RequestParam(required = false) String lvsReason,
            RedirectAttributes lvsRedirectAttributes,
            HttpServletRequest request) {

        // Log thông tin request để debug
        System.out.println("=== HARD DELETE REQUEST ===");
        System.out.println("User ID: " + lvsUserId);
        System.out.println("Reason: " + lvsReason);
        System.out.println("Method: " + request.getMethod());
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("==========================");

        try {
            // Hard delete user (xóa vĩnh viễn khỏi database)
            lvsUserService.lvsHardDeleteUser(lvsUserId, lvsReason != null ? lvsReason : "Admin hard delete");

            // Thêm thông báo thành công
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã xóa user vĩnh viễn thành công!");
        } catch (Exception e) {
            // Log lỗi chi tiết
            System.err.println("HARD DELETE ERROR: " + e.getMessage());
            e.printStackTrace();

            // Thêm thông báo lỗi
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/LvsList";
    }

    /**
     * Khóa tài khoản user (Ban)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Set status của user thành BANNED</li>
     * <li>Lưu lý do khóa và admin đã khóa</li>
     * <li>User không thể đăng nhập</li>
     * <li>Có thể mở khóa sau này</li>
     * </ul>
     * 
     * <p>
     * Khác biệt với Delete:
     * </p>
     * <ul>
     * <li>Ban: Tạm thời khóa, có thể unban</li>
     * <li>Delete: Vô hiệu hóa hoặc xóa vĩnh viễn</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsUser/{lvsUserId}/lvs-ban
     * </p>
     * 
     * @param lvsUserId             ID của user cần khóa
     * @param lvsReason             Lý do khóa (required)
     * @param lvsRedirectAttributes Để truyền flash messages
     * @return Redirect về trang chi tiết user
     */
    @PostMapping("/{lvsUserId}/lvs-ban")
    public String lvsBanUser(@PathVariable Long lvsUserId,
            @RequestParam String lvsReason,
            RedirectAttributes lvsRedirectAttributes) {

        try {
            // TODO: Lấy admin ID từ session/authentication thay vì hardcode
            // Giả sử admin ID là 1 (trong thực tế lấy từ SecurityContext)
            Long lvsAdminId = 1L;

            // Ban user (set status = BANNED)
            lvsUserService.lvsBanUser(lvsUserId, lvsAdminId, lvsReason);

            // Thêm thông báo thành công
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã khóa tài khoản user!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/" + lvsUserId + "/LvsDetail";
    }

    /**
     * Mở khóa tài khoản user (Unban)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Set status của user từ BANNED về ACTIVE</li>
     * <li>User có thể đăng nhập lại</li>
     * <li>Khôi phục quyền truy cập</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsUser/{lvsUserId}/lvs-unban
     * </p>
     * 
     * @param lvsUserId             ID của user cần mở khóa
     * @param lvsRedirectAttributes Để truyền flash messages
     * @return Redirect về trang chi tiết user
     */
    @PostMapping("/{lvsUserId}/lvs-unban")
    public String lvsUnbanUser(@PathVariable Long lvsUserId,
            RedirectAttributes lvsRedirectAttributes) {

        try {
            // Unban user (set status = ACTIVE)
            lvsUserService.lvsUnbanUser(lvsUserId);

            // Thêm thông báo thành công
            lvsRedirectAttributes.addFlashAttribute("lvsSuccess", "Đã mở khóa tài khoản user!");
        } catch (Exception e) {
            // Thêm thông báo lỗi
            lvsRedirectAttributes.addFlashAttribute("lvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsUser/" + lvsUserId + "/LvsDetail";
    }
}