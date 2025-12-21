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

    /**
     * Trang chủ - Hiển thị featured projects
     * URL: GET /LvsUser/LvsDashboard hoặc /LvsUser/
     * View: LvsAreas/LvsUsers/LvsHome
     */
    @GetMapping({ "/LvsDashboard", "/", "" })
    public String lvsDashboard(Model model, HttpSession session) {
        // Lấy 12 projects mới nhất để hiển thị
        Pageable lvsPageable = PageRequest.of(0, 12);
        Page<LvsProject> lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);

        // Lấy categories cho sidebar
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Truyền dữ liệu ra view - dùng tên giống admin
        model.addAttribute("projects", lvsProjects.getContent());
        model.addAttribute("lvsCategories", lvsCategories);
        model.addAttribute("pageTitle", "Home - Electro Store");

        return "LvsAreas/LvsUsers/LvsHome";
    }
}