package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

/**
 * LvsHomeController - Controller quản lý trang chủ công khai
 * 
 * Chức năng:
 * - Hiển thị trang chủ với danh sách dự án nổi bật
 * - Hiển thị trang shop với danh sách sản phẩm, phân trang, filter
 * - Hiển thị chi tiết sản phẩm
 * 
 * @author LucVanSon
 */

@Controller
@RequestMapping("/")
public class LvsHomeController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private k23cnt3.lucvanson.project3.LvsRepository.LvsCategoryRepository lvsCategoryRepository;

    /**
     * Trang chủ - Hiển thị danh sách dự án nổi bật
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Template trang chủ
     */
    @GetMapping
    public String index(Model model) {
        PageRequest pageable = PageRequest.of(0, 8);
        List<LvsProject> projects = lvsProjectService.lvsGetAllProjects(pageable).getContent();

        model.addAttribute("projects", projects);
        model.addAttribute("categories", lvsCategoryRepository.findAll()); // Thêm danh mục cho menu
        return "LvsAreas/LvsUsers/LvsHome";
    }

    /**
     * Alias cho trang chủ - /lvsforum cũng trỏ về home
     * 
     * @param model Model để truyền dữ liệu sang view
     * @return Template trang chủ
     */
    @GetMapping("/lvsforum")
    public String lvsforumHome(Model model) {
        PageRequest pageable = PageRequest.of(0, 8);
        List<LvsProject> projects = lvsProjectService.lvsGetAllProjects(pageable).getContent();

        model.addAttribute("projects", projects);
        model.addAttribute("categories", lvsCategoryRepository.findAll());
        return "LvsAreas/LvsUsers/LvsHome";
    }

    /**
     * Trang shop - Hiển thị danh sách sản phẩm với phân trang và filter
     * 
     * @param model      Model để truyền dữ liệu
     * @param page       Trang hiện tại (bắt đầu từ 0)
     * @param size       Số sản phẩm mỗi trang
     * @param categoryId ID danh mục để filter (optional)
     * @return Template trang shop
     */
    @GetMapping("/user/san-pham")
    public String shop(Model model,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "12") int size,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Long categoryId) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<LvsProject> projectPage;

        if (categoryId != null) {
            // TODO: Cần implement lvsGetProjectsByCategory trong service
            // Hiện tại lấy tất cả (cần tối ưu sau)
            projectPage = lvsProjectService.lvsGetAllProjects(pageable);
        } else {
            projectPage = lvsProjectService.lvsGetAllProjects(pageable);
        }

        model.addAttribute("projects", projectPage); // Truyền Page object để hỗ trợ phân trang
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("categories", lvsCategoryRepository.findAll());
        model.addAttribute("totalProjects", projectPage.getTotalElements());

        return "LvsAreas/LvsUsers/LvsShop";
    }

    /**
     * Trang chi tiết sản phẩm
     * 
     * @param model Model để truyền dữ liệu
     * @param id    ID của dự án cần xem
     * @return Template chi tiết sản phẩm, hoặc redirect về shop nếu không tìm thấy
     */
    @GetMapping("/user/detail/{id}")
    public String detail(Model model, @org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        LvsProject project = lvsProjectService.lvsGetProjectById(id);
        if (project == null) {
            return "redirect:/user/san-pham"; // Redirect về shop nếu không tìm thấy
        }
        model.addAttribute("project", project);
        return "LvsAreas/LvsUsers/LvsDetail";
    }
}
