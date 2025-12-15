package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Controller quản lý danh mục cho LvsAdmin
 * Xử lý thêm, sửa, xóa danh mục
 */
@Controller
@RequestMapping("/LvsAdmin/LvsCategory")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // <-- QUAN TRỌNG: Bảo vệ toàn bộ controller
public class LvsAdminCategoryController {

    @Autowired
    private LvsCategoryService lvsCategoryService;

    @Autowired
    private LvsUserService lvsUserService;

    // Danh sách danh mục
    @GetMapping("/LvsList")
    public String lvsListCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories(lvsPageable);

        // Calculate statistics
        long lvsActiveCount = lvsCategories.getContent().stream()
                .filter(LvsCategory::getLvsIsActive)
                .count();
        long lvsTotalProjects = lvsCategories.getContent().stream()
                .mapToLong(cat -> cat.getLvsProjects() != null ? cat.getLvsProjects().size() : 0)
                .sum();

        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsCurrentPage", page);
        model.addAttribute("LvsActiveCount", lvsActiveCount);
        model.addAttribute("LvsTotalProjects", lvsTotalProjects);

        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryList";
    }

    // Thêm danh mục mới
    @GetMapping("/LvsAdd")
    public String lvsShowAddCategoryForm(Model model) {
        model.addAttribute("LvsCategory", new LvsCategory());
        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd";
    }

    // Xử lý thêm danh mục
    @PostMapping("/LvsAdd")
    public String lvsAddCategory(@ModelAttribute LvsCategory lvsCategory,
            Model model) {

        try {
            lvsCategory.setLvsIsActive(true);
            LvsCategory lvsSavedCategory = lvsCategoryService.lvsSaveCategory(lvsCategory);

            model.addAttribute("LvsSuccess", "Thêm danh mục thành công!");
            return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + lvsSavedCategory.getLvsCategoryId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd";
        }
    }

    // Xem chi tiết danh mục
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewCategoryDetail(@PathVariable Integer id,
            Model model) {

        LvsCategory lvsCategory = lvsCategoryService.lvsGetCategoryById(id);

        if (lvsCategory == null) {
            return "redirect:/LvsAdmin/LvsCategory/LvsList";
        }

        List<LvsProject> lvsProjects = lvsCategory.getLvsProjects();
        int lvsProjectCount = lvsProjects != null ? lvsProjects.size() : 0;

        model.addAttribute("LvsCategory", lvsCategory);
        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsProjectCount", lvsProjectCount);

        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryDetail";
    }

    // Chỉnh sửa danh mục
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditCategoryForm(@PathVariable Integer id,
            Model model) {

        LvsCategory lvsCategory = lvsCategoryService.lvsGetCategoryById(id);

        if (lvsCategory == null) {
            return "redirect:/LvsAdmin/LvsCategory/LvsList";
        }

        model.addAttribute("LvsCategory", lvsCategory);

        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit";
    }

    // Xử lý chỉnh sửa danh mục
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditCategory(@PathVariable Integer id,
            @ModelAttribute LvsCategory lvsCategory,
            Model model) {

        try {
            lvsCategory.setLvsCategoryId(id);
            lvsCategoryService.lvsSaveCategory(lvsCategory);

            model.addAttribute("LvsSuccess", "Cập nhật danh mục thành công!");
            return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit";
        }
    }

    // Xóa danh mục
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteCategory(@PathVariable Integer id,
            Model model) {
        try {
            lvsCategoryService.lvsDeleteCategory(id);
            model.addAttribute("LvsSuccess", "Đã xóa danh mục!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsCategory/LvsList";
    }

    // Kích hoạt/vô hiệu hóa danh mục
    @PostMapping("/LvsToggleActive/{id}")
    public String lvsToggleCategoryActive(@PathVariable Integer id) {
        lvsCategoryService.lvsToggleCategoryActive(id);

        return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + id;
    }

    // Sắp xếp lại thứ tự danh mục
    @PostMapping("/LvsReorder")
    public String lvsReorderCategories(@RequestParam List<Integer> lvsCategoryIds) {
        lvsCategoryService.lvsReorderCategories(lvsCategoryIds);

        return "redirect:/LvsAdmin/LvsCategory/LvsList";
    }

    // Helper method để lấy thông tin user hiện tại (nếu cần)
    private LvsUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return lvsUserService.lvsGetUserByUsername(username);
        }
        return null;
    }
}