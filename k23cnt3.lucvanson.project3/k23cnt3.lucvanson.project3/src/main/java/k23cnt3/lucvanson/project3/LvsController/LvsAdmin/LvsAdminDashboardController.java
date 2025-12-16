package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller hiển thị Dashboard (Trang tổng quan) của Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị trang tổng quan với các thống kê quan trọng</li>
 * <li>Hiển thị biểu đồ và số liệu tổng hợp</li>
 * <li>Cung cấp quick links đến các chức năng chính</li>
 * </ul>
 * 
 * <p>
 * Thống kê hiển thị:
 * </p>
 * <ul>
 * <li>Tổng số users trong hệ thống</li>
 * <li>Tổng số orders</li>
 * <li>Tổng doanh thu</li>
 * <li>Các chỉ số khác (projects, categories, reviews...)</li>
 * </ul>
 * 
 * <p>
 * Template path:
 * </p>
 * <ul>
 * <li>Dashboard: LvsAreas/LvsAdmin/LvsLayout/LvsDashboard.html</li>
 * </ul>
 * 
 * <p>
 * Lưu ý:
 * </p>
 * <ul>
 * <li>Hiện tại đang dùng mock data</li>
 * <li>Cần thay thế bằng dữ liệu thực từ database</li>
 * <li>Có thể thêm biểu đồ, charts để trực quan hóa dữ liệu</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin")
public class LvsAdminDashboardController {

    /**
     * Hiển thị trang Dashboard với các thống kê tổng quan
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Tính toán các chỉ số thống kê</li>
     * <li>Lấy dữ liệu cho biểu đồ</li>
     * <li>Hiển thị thông tin tổng quan</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsDashboard
     * </p>
     * 
     * @param model   Model để truyền dữ liệu ra view
     * @param request HttpServletRequest để lấy thông tin request (nếu cần)
     * @return Template path: LvsAreas/LvsAdmin/LvsLayout/LvsDashboard
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>totalUsers: int - Tổng số users</li>
     *         <li>totalOrders: int - Tổng số orders</li>
     *         <li>totalRevenue: double - Tổng doanh thu</li>
     *         <li>pageTitle: String - Tiêu đề trang</li>
     *         <li>activePage: String - Trang đang active (để highlight
     *         sidebar)</li>
     *         </ul>
     * 
     *         <p>
     *         TODO:
     *         </p>
     *         <ul>
     *         <li>Thay mock data bằng dữ liệu thực từ services</li>
     *         <li>Thêm thống kê theo thời gian (hôm nay, tuần này, tháng này)</li>
     *         <li>Thêm biểu đồ doanh thu theo thời gian</li>
     *         <li>Thêm top 5 dự án bán chạy nhất</li>
     *         <li>Thêm danh sách orders gần đây</li>
     *         </ul>
     */
    @GetMapping("/LvsDashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // ===== MOCK DATA - CẦN THAY BẰNG DỮ LIỆU THỰC =====
        // TODO: Lấy dữ liệu thực từ các services
        // Ví dụ: int totalUsers = lvsUserService.lvsCountAllUsers();

        model.addAttribute("totalUsers", 150); // Tổng số users
        model.addAttribute("totalOrders", 45); // Tổng số orders
        model.addAttribute("totalRevenue", 12500); // Tổng doanh thu (VNĐ)
        model.addAttribute("pageTitle", "Dashboard");

        // Set activePage để sidebar biết trang nào đang active
        // Dùng để highlight menu item tương ứng
        model.addAttribute("activePage", "dashboard");

        return "LvsAreas/LvsAdmin/LvsLayout/LvsDashboard";
    }

    /**
     * Root mapping cho /LvsAdmin - redirect đến dashboard
     */
    @GetMapping({ "", "/" })
    public String root() {
        return "redirect:/LvsAdmin/LvsDashboard";
    }
}