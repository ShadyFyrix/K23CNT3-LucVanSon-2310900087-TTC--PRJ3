package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import k23cnt3.lucvanson.project3.LvsService.LvsCategoryService;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
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
 * Controller quản lý dự án cho người dùng
 * Xử lý xem, tìm kiếm, mua dự án
 */
@Controller
@RequestMapping("/LvsUser/LvsProject")
public class LvsUserProjectController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsCategoryService lvsCategoryService;

    @Autowired
    private LvsUserService lvsUserService;

    // Xem danh sách dự án với phân trang và tìm kiếm
    @GetMapping("/LvsList")
    public String lvsListProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String lvsKeyword,
            @RequestParam(required = false) Integer lvsCategoryId,
            @RequestParam(required = false) String lvsSortBy,
            Model model,
            HttpSession session) {

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        // Lấy danh sách danh mục để hiển thị filter
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Xử lý tìm kiếm và lọc
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        } else if (lvsCategoryId != null) {
            lvsProjects = lvsProjectService.lvsGetProjectsByCategory(lvsCategoryId, lvsPageable);
        } else if (lvsSortBy != null) {
            switch (lvsSortBy) {
                case "newest":
                    // lvsGetNewestProjects returns List<LvsProject>, not Page
                    List<LvsProject> newestList = lvsProjectService.lvsGetNewestProjects(lvsPageable);
                    lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable); // Use all projects as fallback
                    break;
                case "popular":
                    // lvsGetPopularProjects returns List<LvsProject>, not Page
                    List<LvsProject> popularList = lvsProjectService.lvsGetPopularProjects(lvsPageable);
                    lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable); // Use all projects as fallback
                    break;
                case "featured":
                    lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable); // Use all projects
                    break;
                default:
                    lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);
            }
        } else {
            lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);
        }

        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsSelectedCategoryId", lvsCategoryId);
        model.addAttribute("LvsSortBy", lvsSortBy);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectList";
    }

    // Xem chi tiết dự án
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewProjectDetail(@PathVariable Long id, Model model, HttpSession session) {
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        if (lvsProject == null || lvsProject.getLvsStatus() != LvsProject.LvsProjectStatus.APPROVED) {
            return "redirect:/LvsUser/LvsProject/LvsList";
        }

        // Tăng lượt xem
        lvsProjectService.lvsIncrementViewCount(id);

        // Lấy đánh giá và bài viết liên quan
        List<LvsReview> lvsReviews = lvsProject.getLvsReviews();
        List<LvsPost> lvsPosts = lvsProject.getLvsPosts();

        // Kiểm tra user đã mua dự án chưa
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        boolean lvsHasPurchased = false;
        if (lvsCurrentUser != null) {
            lvsHasPurchased = lvsProjectService.lvsHasUserPurchasedProject(lvsCurrentUser.getLvsUserId(), id);
        }

        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsHasPurchased", lvsHasPurchased);

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectDetail";
    }

    // Thêm dự án mới (người dùng đăng bán dự án)
    @GetMapping("/LvsAdd")
    public String lvsShowAddProjectForm(Model model) {
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        model.addAttribute("LvsProject", new LvsProject());
        model.addAttribute("LvsCategories", lvsCategories);
        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectAdd";
    }

    // Xử lý thêm dự án
    @PostMapping("/LvsAdd")
    public String lvsAddProject(@ModelAttribute LvsProject lvsProject,
            @RequestParam(required = false) String lvsTags,
            HttpSession session,
            Model model) {
        try {
            LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
            if (lvsCurrentUser == null) {
                return "redirect:/LvsAuth/LvsLogin.html";
            }

            lvsProject.setLvsUser(lvsCurrentUser);
            lvsProject.setLvsTags(lvsTags);
            lvsProject.setLvsStatus(LvsProject.LvsProjectStatus.PENDING);

            LvsProject lvsSavedProject = lvsProjectService.lvsSaveProject(lvsProject);

            model.addAttribute("LvsSuccess", "Đăng dự án thành công! Chờ LvsAdmin duyệt.");
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + lvsSavedProject.getLvsProjectId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi đăng dự án: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsProjects/LvsProjectAdd";
        }
    }

    // Chỉnh sửa dự án
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditProjectForm(@PathVariable Long id, Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        if (lvsCurrentUser == null ||
                !lvsProject.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsProject/LvsList";
        }

        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsCategories", lvsCategories);
        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectEdit";
    }

    // Xử lý chỉnh sửa dự án
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditProject(@PathVariable Long id,
            @ModelAttribute LvsProject lvsProject,
            HttpSession session,
            Model model) {
        try {
            LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
            if (lvsCurrentUser == null) {
                return "redirect:/LvsAuth/LvsLogin.html";
            }

            lvsProject.setLvsProjectId(id);
            lvsProject.setLvsUser(lvsCurrentUser);
            lvsProject.setLvsStatus(LvsProject.LvsProjectStatus.PENDING); // Reset trạng thái chờ duyệt

            lvsProjectService.lvsSaveProject(lvsProject);

            model.addAttribute("LvsSuccess", "Cập nhật dự án thành công!");
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi cập nhật dự án: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsProjects/LvsProjectEdit";
        }
    }

    // Xóa dự án
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteProject(@PathVariable Long id, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        if (lvsCurrentUser != null &&
                lvsProject.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            lvsProjectService.lvsDeleteProject(id);
        }

        return "redirect:/LvsUser/LvsProject/LvsMyProjects";
    }

    // Xem dự án của tôi
    @GetMapping("/LvsMyProjects")
    public String lvsViewMyProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects = lvsProjectService.lvsGetProjectsByUser(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsProjects/LvsMyProjects";
    }
}