package k23cnt3.lucvanson.project3.LvsController.LvsModerator;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost;
import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsService.LvsPostService;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * LvsModeratorDashboardController - Controller xử lý trang quản lý của
 * Moderator
 * 
 * Chức năng chính:
 * - Hiển thị dashboard với danh sách nội dung cần phê duyệt
 * - Phê duyệt hoặc từ chối bài viết (Posts)
 * - Phê duyệt hoặc từ chối dự án (Projects)
 * - Quản lý báo cáo từ người dùng
 * 
 * Access Control:
 * - Chỉ MODERATOR mới có quyền truy cập
 * - Kiểm tra session và role trước khi cho phép thao tác
 * 
 * @author LucVanSon
 * @version 1.0
 */
@Controller
@RequestMapping("/LvsModerator")
public class LvsModeratorDashboardController {

    // @Autowired
    // private LvsPostService lvsPostService;

    // @Autowired
    // private LvsProjectService lvsProjectService;

    /**
     * Hiển thị trang dashboard của Moderator
     * 
     * Dashboard hiển thị:
     * - Thống kê tổng quan (số bài viết, dự án chờ duyệt)
     * - Danh sách bài viết mới nhất cần phê duyệt
     * - Danh sách dự án mới nhất cần phê duyệt
     * - Báo cáo từ người dùng
     * 
     * @param model   Model để truyền dữ liệu sang view
     * @param session HttpSession để kiểm tra đăng nhập
     * @return Đường dẫn đến template dashboard của moderator
     */
    @GetMapping("/LvsDashboard")
    public String lvsShowModeratorDashboard(Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        // Kiểm tra role - chỉ MODERATOR mới được truy cập
        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // Lấy danh sách bài viết chờ duyệt (giả sử có status PENDING)
            // List<LvsPost> lvsPendingPosts =
            // lvsPostService.lvsGetPostsByStatus("PENDING");

            // Lấy danh sách dự án chờ duyệt
            // List<LvsProject> lvsPendingProjects =
            // lvsProjectService.lvsGetProjectsByStatus("PENDING");

            // Thống kê
            // Long lvsTotalPendingPosts = lvsPostService.lvsCountByStatus("PENDING");
            // Long lvsTotalPendingProjects = lvsProjectService.lvsCountByStatus("PENDING");

            // Truyền dữ liệu sang view
            model.addAttribute("lvsCurrentUser", lvsCurrentUser);
            // model.addAttribute("lvsPendingPosts", lvsPendingPosts);
            // model.addAttribute("lvsPendingProjects", lvsPendingProjects);
            // model.addAttribute("lvsTotalPendingPosts", lvsTotalPendingPosts);
            // model.addAttribute("lvsTotalPendingProjects", lvsTotalPendingProjects);

            return "LvsAreas/LvsModerator/LvsDashboard";

        } catch (Exception e) {
            model.addAttribute("lvsError", "Lỗi khi tải dashboard: " + e.getMessage());
            return "LvsAreas/LvsModerator/LvsDashboard";
        }
    }

    /**
     * Hiển thị danh sách bài viết cần phê duyệt
     * 
     * @param model   Model để truyền dữ liệu sang view
     * @param session HttpSession để kiểm tra đăng nhập
     * @return Đường dẫn đến template danh sách bài viết
     */
    @GetMapping("/LvsPosts")
    public String lvsShowPendingPosts(Model model, HttpSession session) {
        // Kiểm tra đăng nhập và role
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // Lấy tất cả bài viết (hoặc chỉ bài viết chờ duyệt)
            // List<LvsPost> lvsAllPosts = lvsPostService
            // .lvsGetAllPosts(org.springframework.data.domain.Pageable.unpaged()).getContent();

            model.addAttribute("lvsCurrentUser", lvsCurrentUser);
            // model.addAttribute("lvsPosts", lvsAllPosts);

            return "LvsAreas/LvsModerator/LvsPosts";

        } catch (Exception e) {
            model.addAttribute("lvsError", "Lỗi khi tải danh sách bài viết: " + e.getMessage());
            return "LvsAreas/LvsModerator/LvsPosts";
        }
    }

    /**
     * Hiển thị danh sách dự án cần phê duyệt
     * 
     * @param model   Model để truyền dữ liệu sang view
     * @param session HttpSession để kiểm tra đăng nhập
     * @return Đường dẫn đến template danh sách dự án
     */
    @GetMapping("/LvsProjects")
    public String lvsShowPendingProjects(Model model, HttpSession session) {
        // Kiểm tra đăng nhập và role
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // Lấy tất cả dự án (hoặc chỉ dự án chờ duyệt)
            // List<LvsProject> lvsAllProjects = lvsProjectService
            // .lvsGetAllProjects(org.springframework.data.domain.Pageable.unpaged()).getContent();

            model.addAttribute("lvsCurrentUser", lvsCurrentUser);
            // model.addAttribute("lvsProjects", lvsAllProjects);

            return "LvsAreas/LvsModerator/LvsProjects";

        } catch (Exception e) {
            model.addAttribute("lvsError", "Lỗi khi tải danh sách dự án: " + e.getMessage());
            return "LvsAreas/LvsModerator/LvsProjects";
        }
    }

    /**
     * Phê duyệt bài viết
     * 
     * @param lvsPostId          ID của bài viết cần phê duyệt
     * @param session            HttpSession để kiểm tra đăng nhập
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về danh sách bài viết
     */
    @PostMapping("/LvsApprovePost/{lvsPostId}")
    public String lvsApprovePost(
            @PathVariable Long lvsPostId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập và role
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // TODO: Implement approval logic
            // lvsPostService.lvsApprovePost(lvsPostId);

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Bài viết đã được phê duyệt thành công!");
            return "redirect:/LvsModerator/LvsPosts";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi khi phê duyệt bài viết: " + e.getMessage());
            return "redirect:/LvsModerator/LvsPosts";
        }
    }

    /**
     * Từ chối bài viết
     * 
     * @param lvsPostId          ID của bài viết cần từ chối
     * @param session            HttpSession để kiểm tra đăng nhập
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về danh sách bài viết
     */
    @PostMapping("/LvsRejectPost/{lvsPostId}")
    public String lvsRejectPost(
            @PathVariable Long lvsPostId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập và role
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // TODO: Implement rejection logic
            // lvsPostService.lvsRejectPost(lvsPostId);

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Bài viết đã bị từ chối!");
            return "redirect:/LvsModerator/LvsPosts";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi khi từ chối bài viết: " + e.getMessage());
            return "redirect:/LvsModerator/LvsPosts";
        }
    }

    /**
     * Phê duyệt dự án
     * 
     * @param lvsProjectId       ID của dự án cần phê duyệt
     * @param session            HttpSession để kiểm tra đăng nhập
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về danh sách dự án
     */
    @PostMapping("/LvsApproveProject/{lvsProjectId}")
    public String lvsApproveProject(
            @PathVariable Long lvsProjectId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập và role
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // TODO: Implement approval logic
            // lvsProjectService.lvsApproveProject(lvsProjectId);

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Dự án đã được phê duyệt thành công!");
            return "redirect:/LvsModerator/LvsProjects";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi khi phê duyệt dự án: " + e.getMessage());
            return "redirect:/LvsModerator/LvsProjects";
        }
    }

    /**
     * Từ chối dự án
     * 
     * @param lvsProjectId       ID của dự án cần từ chối
     * @param session            HttpSession để kiểm tra đăng nhập
     * @param redirectAttributes Để truyền flash message
     * @return Redirect về danh sách dự án
     */
    @PostMapping("/LvsRejectProject/{lvsProjectId}")
    public String lvsRejectProject(
            @PathVariable Long lvsProjectId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra đăng nhập và role
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        if (lvsCurrentUser.getLvsRole() != LvsUser.LvsRole.MODERATOR) {
            return "redirect:/403";
        }

        try {
            // TODO: Implement rejection logic
            // lvsProjectService.lvsRejectProject(lvsProjectId);

            redirectAttributes.addFlashAttribute("lvsSuccess",
                    "Dự án đã bị từ chối!");
            return "redirect:/LvsModerator/LvsProjects";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError",
                    "Lỗi khi từ chối dự án: " + e.getMessage());
            return "redirect:/LvsModerator/LvsProjects";
        }
    }
}
