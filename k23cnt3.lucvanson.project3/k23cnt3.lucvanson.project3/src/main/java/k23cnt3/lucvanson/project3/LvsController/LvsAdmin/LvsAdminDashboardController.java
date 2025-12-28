package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import jakarta.servlet.http.HttpServletRequest;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder.LvsOrderStatus;
import k23cnt3.lucvanson.project3.LvsRepository.LvsCommentRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsOrderRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsPostRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsProjectRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
 * @author LucVanSon
 * @version 2.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin")
public class LvsAdminDashboardController {

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Autowired
    private LvsOrderRepository lvsOrderRepository;

    @Autowired
    private LvsPostRepository lvsPostRepository;

    @Autowired
    private LvsProjectRepository lvsProjectRepository;

    @Autowired
    private LvsCommentRepository lvsCommentRepository;

    /**
     * Hiển thị trang Dashboard với các thống kê tổng quan
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Tính toán các chỉ số thống kê từ database</li>
     * <li>Lấy dữ liệu thực từ hệ thống</li>
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
     *         <li>totalUsers: Long - Tổng số users</li>
     *         <li>totalOrders: Long - Tổng số orders</li>
     *         <li>totalRevenue: Double - Tổng doanh thu (Coins)</li>
     *         <li>totalPosts: Long - Tổng số bài viết</li>
     *         <li>totalProjects: Long - Tổng số dự án</li>
     *         <li>totalComments: Long - Tổng số bình luận</li>
     *         <li>pageTitle: String - Tiêu đề trang</li>
     *         <li>activePage: String - Trang đang active (để highlight
     *         sidebar)</li>
     *         </ul>
     */
    @GetMapping("/LvsDashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // ===== REAL DATA FROM DATABASE =====

        // 1. Tổng số users trong hệ thống
        Long totalUsers = lvsUserRepository.count();

        // 2. Tổng số orders
        Long totalOrders = lvsOrderRepository.count();

        // 3. Tính tổng doanh thu từ các orders đã hoàn thành
        List<LvsOrder> completedOrders = lvsOrderRepository.findByLvsStatus(LvsOrderStatus.COMPLETED);
        Double totalRevenue = completedOrders.stream()
                .mapToDouble(order -> order.getLvsFinalAmount() != null ? order.getLvsFinalAmount() : 0.0)
                .sum();

        // 4. Tổng số bài viết
        Long totalPosts = lvsPostRepository.count();

        // 5. Tổng số dự án
        Long totalProjects = lvsProjectRepository.count();

        // 6. Tổng số bình luận
        Long totalComments = lvsCommentRepository.count();

        // Add to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("totalComments", totalComments);
        model.addAttribute("pageTitle", "Dashboard");

        // Set activePage để sidebar biết trang nào đang active
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
