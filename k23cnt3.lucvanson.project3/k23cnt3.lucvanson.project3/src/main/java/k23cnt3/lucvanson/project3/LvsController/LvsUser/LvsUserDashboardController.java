package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LvsUserDashboardController - Controller quản lý trang chủ/dashboard cho người
 * dùng
 * 
 * Chức năng:
 * - Hiển thị dashboard cá nhân với thống kê (dự án, bài viết, đơn hàng,
 * followers)
 * - Hiển thị trang chủ công khai với dự án nổi bật
 * - Tìm kiếm toàn bộ hệ thống (dự án, bài viết, users)
 * - Cập nhật session và thông báo
 * 
 * @author LucVanSon
 */
@Controller
@RequestMapping("/LvsUser")
public class LvsUserDashboardController {

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsPostService lvsPostService;

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsTransactionService lvsTransactionService;

    @Autowired
    private LvsFollowService lvsFollowService;

    /**
     * Trang dashboard cá nhân của user
     * Hiển thị thống kê, dự án mới, bài viết mới, đơn hàng chờ thanh toán
     * 
     * @param model   Model để truyền dữ liệu
     * @param session HttpSession để lấy thông tin user hiện tại
     * @return Template dashboard, hoặc redirect đến login nếu chưa đăng nhập
     */
    @GetMapping("/LvsDashboard")
    public String lvsUserDashboard(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        // Cập nhật thông tin user từ database
        lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());
        session.setAttribute("LvsCurrentUser", lvsCurrentUser);

        // Lấy thống kê cá nhân
        int lvsTotalProjects = lvsCurrentUser.getLvsProjects().size();
        int lvsTotalPosts = lvsCurrentUser.getLvsPosts().size();
        int lvsTotalOrders = lvsCurrentUser.getLvsOrders().size();
        int lvsFollowersCount = lvsFollowService.lvsGetFollowerCount(lvsCurrentUser.getLvsUserId());
        int lvsFollowingCount = lvsFollowService.lvsGetFollowingCount(lvsCurrentUser.getLvsUserId());

        // Lấy dự án mới nhất
        // TODO: Fix method signature - expects Pageable not int
        // List<LvsProject> lvsNewProjects = lvsProjectService.lvsGetNewestProjects(8);
        List<LvsProject> lvsNewProjects = List.of();

        // Lấy bài viết mới nhất
        // TODO: Fix method signature - expects Pageable not int
        // List<LvsPost> lvsNewPosts = lvsPostService.lvsGetNewestPosts(6);
        List<LvsPost> lvsNewPosts = List.of();

        // Lấy dự án đã mua gần đây
        List<LvsProject> lvsRecentPurchases = lvsProjectService.lvsGetRecentPurchases(
                lvsCurrentUser.getLvsUserId(), 5);

        // Lấy đơn hàng chờ thanh toán
        List<LvsOrder> lvsPendingOrders = lvsOrderService.lvsGetPendingOrders(
                lvsCurrentUser.getLvsUserId());

        // Lấy tin nhắn chưa đọc (TODO: implement message service)
        int lvsUnreadMessages = 0;

        model.addAttribute("LvsUser", lvsCurrentUser);
        model.addAttribute("LvsTotalProjects", lvsTotalProjects);
        model.addAttribute("LvsTotalPosts", lvsTotalPosts);
        model.addAttribute("LvsTotalOrders", lvsTotalOrders);
        model.addAttribute("LvsFollowersCount", lvsFollowersCount);
        model.addAttribute("LvsFollowingCount", lvsFollowingCount);
        model.addAttribute("LvsNewProjects", lvsNewProjects);
        model.addAttribute("LvsNewPosts", lvsNewPosts);
        model.addAttribute("LvsRecentPurchases", lvsRecentPurchases);
        model.addAttribute("LvsPendingOrders", lvsPendingOrders);
        model.addAttribute("LvsUnreadMessages", lvsUnreadMessages);

        return "LvsAreas/LvsUsers/LvsDashboard";
    }

    /**
     * Trang chủ công khai (cho user chưa đăng nhập)
     * Hiển thị dự án nổi bật, bài viết phổ biến, dự án mới nhất
     * 
     * @param model Model để truyền dữ liệu
     * @return Template trang chủ công khai
     */
    @GetMapping("/LvsHome")
    public String lvsHomePage(Model model) {
        // Lấy dự án nổi bật
        List<LvsProject> lvsFeaturedProjects = lvsProjectService.lvsGetFeaturedProjects(PageRequest.of(0, 6))
                .getContent();

        // Lấy bài viết phổ biến
        List<LvsPost> lvsPopularPosts = lvsPostService.lvsGetPopularPosts(PageRequest.of(0, 6)).getContent();

        // Lấy dự án mới nhất
        List<LvsProject> lvsNewestProjects = lvsProjectService.lvsGetNewestProjects(PageRequest.of(0, 8)).getContent();

        model.addAttribute("LvsFeaturedProjects", lvsFeaturedProjects);
        model.addAttribute("LvsPopularPosts", lvsPopularPosts);
        model.addAttribute("LvsNewestProjects", lvsNewestProjects);

        return "LvsAreas/LvsUsers/LvsHome";
    }

    /**
     * Tìm kiếm toàn bộ hệ thống
     * Hỗ trợ tìm kiếm dự án, bài viết, users theo keyword
     * 
     * @param lvsKeyword Từ khóa tìm kiếm
     * @param lvsType    Loại tìm kiếm (all, projects, posts, users)
     * @param model      Model để truyền dữ liệu
     * @return Template kết quả tìm kiếm
     */
    @GetMapping("/LvsSearch")
    public String lvsGlobalSearch(@RequestParam String lvsKeyword,
            @RequestParam(defaultValue = "all") String lvsType,
            Model model) {

        if (lvsKeyword == null || lvsKeyword.trim().isEmpty()) {
            return "redirect:/LvsUser/LvsHome";
        }

        switch (lvsType) {
            case "projects":
                List<LvsProject> lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, PageRequest.of(0, 20))
                        .getContent();
                model.addAttribute("LvsProjects", lvsProjects);
                break;
            case "posts":
                List<LvsPost> lvsPosts = lvsPostService.lvsSearchPosts(lvsKeyword, PageRequest.of(0, 20)).getContent();
                model.addAttribute("LvsPosts", lvsPosts);
                break;
            case "LvsUsers":
                List<LvsUser> lvsUsers = lvsUserService.lvsSearchUsers(lvsKeyword, PageRequest.of(0, 20)).getContent();
                model.addAttribute("LvsUsers", lvsUsers);
                break;
            default: // all
                List<LvsProject> lvsAllProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, PageRequest.of(0, 20))
                        .getContent();
                List<LvsPost> lvsAllPosts = lvsPostService.lvsSearchPosts(lvsKeyword, PageRequest.of(0, 20))
                        .getContent();
                List<LvsUser> lvsAllUsers = lvsUserService.lvsSearchUsers(lvsKeyword, PageRequest.of(0, 20))
                        .getContent();
                model.addAttribute("LvsProjects", lvsAllProjects);
                model.addAttribute("LvsPosts", lvsAllPosts);
                model.addAttribute("LvsUsers", lvsAllUsers);
                break;
        }

        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsSearchType", lvsType);

        return "LvsAreas/LvsUsers/LvsSearchResults";
    }

    // Cập nhật thông tin session
    @PostMapping("/LvsUpdateSession")
    @ResponseBody
    public String lvsUpdateUserSession(HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser != null) {
            lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());
            session.setAttribute("LvsCurrentUser", lvsCurrentUser);
            return "{\"success\": true}";
        }
        return "{\"success\": false}";
    }

    // Kiểm tra thông báo
    @GetMapping("/LvsNotifications")
    @ResponseBody
    public String lvsGetNotifications(HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "{\"notifications\": []}";
        }

        // Giả sử có service lấy thông báo
        // List<LvsNotification> lvsNotifications =
        // lvsNotificationService.lvsGetUnreadNotifications(
        // lvsCurrentUser.getLvsUserId());

        return "{\"notifications\": [], \"unreadCount\": 0}";
    }
}