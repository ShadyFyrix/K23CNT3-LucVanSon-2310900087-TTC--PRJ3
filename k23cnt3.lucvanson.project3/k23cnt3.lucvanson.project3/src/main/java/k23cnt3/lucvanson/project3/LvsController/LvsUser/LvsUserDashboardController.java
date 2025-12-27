package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsRepository.LvsProjectRepository;
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
 * Controller hiển thị Dashboard/Home cho User
 * Theo mẫu LvsAdminDashboardController
 */
@Controller
@RequestMapping("/LvsUser")
public class LvsUserDashboardController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsCategoryService lvsCategoryService;

    @Autowired // Added injection
    private LvsProjectRepository lvsProjectRepository; // Added injection

    /**
     * Trang chủ - Hiển thị featured projects
     * URL: GET /LvsUser/LvsDashboard hoặc /LvsUser/
     * View: LvsAreas/LvsUsers/LvsHome
     */
    @GetMapping({ "/LvsDashboard", "/", "" })
    public String lvsDashboard(
            @RequestParam(required = false) String lvsKeyword,
            @RequestParam(required = false) Integer lvsCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model,
            HttpSession session) {

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        // Filter logic
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            // Search APPROVED projects
            lvsProjects = lvsProjectRepository.searchProjectsByStatus(
                    lvsKeyword,
                    LvsProject.LvsProjectStatus.APPROVED,
                    lvsPageable);
        } else if (lvsCategoryId != null) {
            // Filter by category AND APPROVED
            lvsProjects = lvsProjectRepository.findByCategoryAndStatusWithDetails(
                    lvsCategoryId,
                    LvsProject.LvsProjectStatus.APPROVED,
                    lvsPageable);
        } else {
            // Show all APPROVED projects
            lvsProjects = lvsProjectService.lvsGetProjectsByStatus("APPROVED", lvsPageable);
        }

        // Lấy categories cho filter
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Truyền dữ liệu ra view
        model.addAttribute("projects", lvsProjects.getContent());
        model.addAttribute("lvsCategories", lvsCategories);
        model.addAttribute("lvsKeyword", lvsKeyword);
        model.addAttribute("lvsCategoryId", lvsCategoryId);
        model.addAttribute("pageTitle", "Home - Electro Store");

        return "LvsAreas/LvsUsers/LvsHome";
    }
}