package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller quản lý trang chủ/dashboard cho người dùng
 * Hiển thị thống kê, dự án mới, bài viết mới
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

    // Trang chủ/dashboard của user
    @GetMapping("/LvsDashboard")
    public String lvsUserDashboard(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
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
        List<LvsProject> lvsNewProjects = lvsProjectService.lvsGetNewestProjects(8);

        // Lấy bài viết mới nhất
        List<LvsPost> lvsNewPosts = lvsPostService.lvsGetNewestPosts(6);

        // Lấy dự án đã mua gần đây
        List<LvsProject> lvsRecentPurchases = lvsProjectService.lvsGetRecentPurchases(
                lvsCurrentUser.getLvsUserId(), 5);

        // Lấy đơn hàng chờ thanh toán
        List<LvsOrder> lvsPendingOrders = lvsOrderService.lvsGetPendingOrders(
                lvsCurrentUser.getLvsUserId());

        // Lấy tin nhắn chưa đọc
        int lvsUnreadMessages = 0; // Giả sử có service lấy tin nhắn chưa đọc

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

        return "LvsUser/LvsDashboard";
    }

    // Trang chủ công khai (cho user chưa đăng nhập)
    @GetMapping("/LvsHome")
    public String lvsHomePage(Model model) {
        // Lấy dự án nổi bật
        List<LvsProject> lvsFeaturedProjects = lvsProjectService.lvsGetFeaturedProjects(6);

        // Lấy bài viết phổ biến
        List<LvsPost> lvsPopularPosts = lvsPostService.lvsGetPopularPosts(6);

        // Lấy dự án mới nhất
        List<LvsProject> lvsNewestProjects = lvsProjectService.lvsGetNewestProjects(8);

        // Lấy danh mục
        // List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        model.addAttribute("LvsFeaturedProjects", lvsFeaturedProjects);
        model.addAttribute("LvsPopularPosts", lvsPopularPosts);
        model.addAttribute("LvsNewestProjects", lvsNewestProjects);
        // model.addAttribute("LvsCategories", lvsCategories);

        return "LvsUser/LvsHome";
    }

    // Tìm kiếm toàn bộ hệ thống
    @GetMapping("/LvsSearch")
    public String lvsGlobalSearch(@RequestParam String lvsKeyword,
                                  @RequestParam(defaultValue = "all") String lvsType,
                                  Model model) {

        if (lvsKeyword == null || lvsKeyword.trim().isEmpty()) {
            return "redirect:/LvsUser/LvsHome";
        }

        switch (lvsType) {
            case "projects":
                List<LvsProject> lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword);
                model.addAttribute("LvsProjects", lvsProjects);
                break;
            case "posts":
                List<LvsPost> lvsPosts = lvsPostService.lvsSearchPosts(lvsKeyword);
                model.addAttribute("LvsPosts", lvsPosts);
                break;
            case "LvsUsers":
                List<LvsUser> lvsUsers = lvsUserService.lvsSearchUsers(lvsKeyword);
                model.addAttribute("LvsUsers", lvsUsers);
                break;
            default: // all
                List<LvsProject> lvsAllProjects = lvsProjectService.lvsSearchProjects(lvsKeyword);
                List<LvsPost> lvsAllPosts = lvsPostService.lvsSearchPosts(lvsKeyword);
                List<LvsUser> lvsAllUsers = lvsUserService.lvsSearchUsers(lvsKeyword);
                model.addAttribute("LvsProjects", lvsAllProjects);
                model.addAttribute("LvsPosts", lvsAllPosts);
                model.addAttribute("LvsUsers", lvsAllUsers);
                break;
        }

        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsSearchType", lvsType);

        return "LvsUser/LvsSearchResults";
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
        // List<LvsNotification> lvsNotifications = lvsNotificationService.lvsGetUnreadNotifications(
        //     lvsCurrentUser.getLvsUserId());

        return "{\"notifications\": [], \"unreadCount\": 0}";
    }
}