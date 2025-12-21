package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
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
 * Controller quản lý Projects cho User
 * Theo mẫu LvsAdminProjectController
 */
@Controller
@RequestMapping("/LvsUser/LvsProject")
public class LvsUserProjectController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsCategoryService lvsCategoryService;

    /**
     * Danh sách projects (Shop page)
     * URL: GET /LvsUser/LvsProject/LvsList
     * View: LvsAreas/LvsUsers/LvsShop
     */
    @GetMapping("/LvsList")
    public String lvsListProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String lvsKeyword,
            @RequestParam(required = false) Integer lvsCategoryId,
            Model model,
            HttpSession session) {

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        // Lấy danh sách categories
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Tìm kiếm hoặc lọc
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        } else if (lvsCategoryId != null) {
            lvsProjects = lvsProjectService.lvsGetProjectsByCategory(lvsCategoryId, lvsPageable);
        } else {
            lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);
        }

        // Truyền dữ liệu - dùng tên UPPERCASE như admin
        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsSelectedCategoryId", lvsCategoryId);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsShop";
    }

    /**
     * Chi tiết project
     * URL: GET /LvsUser/LvsProject/LvsDetail/{id}
     * View: LvsAreas/LvsUsers/LvsProjects/LvsProjectDetail
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewProjectDetail(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        // Lấy project
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        // Nếu không tìm thấy hoặc chưa approved
        if (lvsProject == null || lvsProject.getLvsStatus() != LvsProject.LvsProjectStatus.APPROVED) {
            return "redirect:/LvsUser/LvsProject/LvsList";
        }

        // Tăng view count
        lvsProjectService.lvsIncrementViewCount(id);

        // Kiểm tra user đã mua chưa
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        boolean lvsHasPurchased = false;
        if (lvsCurrentUser != null) {
            lvsHasPurchased = lvsProjectService.lvsHasUserPurchasedProject(
                    lvsCurrentUser.getLvsUserId(), id);
        }

        // Truyền dữ liệu - dùng tên UPPERCASE
        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsHasPurchased", lvsHasPurchased);

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectDetail";
    }
}