package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

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
 * Controller quản lý danh mục cho LvsAdmin
 * Xử lý thêm, sửa, xóa danh mục
 */
@Controller
@RequestMapping("/LvsAdmin/LvsCategory")
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
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories(lvsPageable);

        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsCategoryList";
    }

    // Thêm danh mục mới
    @GetMapping("/LvsAdd")
    public String lvsShowAddCategoryForm(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsCategory", new LvsCategory());

        return "LvsAdmin/LvsCategoryAdd";
    }

    // Xử lý thêm danh mục
    @PostMapping("/LvsAdd")
    public String lvsAddCategory(@ModelAttribute LvsCategory lvsCategory,
                                 HttpSession session,
                                 Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCategory.setLvsIsActive(true);
            LvsCategory lvsSavedCategory = lvsCategoryService.lvsSaveCategory(lvsCategory);

            model.addAttribute("LvsSuccess", "Thêm danh mục thành công!");
            return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + lvsSavedCategory.getLvsCategoryId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsCategoryAdd";
        }
    }

    // Xem chi tiết danh mục
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewCategoryDetail(@PathVariable Integer id,
                                        Model model,
                                        HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsCategory lvsCategory = lvsCategoryService.lvsGetCategoryById(id);

        if (lvsCategory == null) {
            return "redirect:/LvsAdmin/LvsCategory/LvsList";
        }

        List<LvsProject> lvsProjects = lvsCategory.getLvsProjects();

        model.addAttribute("LvsCategory", lvsCategory);
        model.addAttribute("LvsProjects", lvsProjects);

        return "LvsAdmin/LvsCategoryDetail";
    }

    // Chỉnh sửa danh mục
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditCategoryForm(@PathVariable Integer id,
                                          Model model,
                                          HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsCategory lvsCategory = lvsCategoryService.lvsGetCategoryById(id);

        if (lvsCategory == null) {
            return "redirect:/LvsAdmin/LvsCategory/LvsList";
        }

        model.addAttribute("LvsCategory", lvsCategory);

        return "LvsAdmin/LvsCategoryEdit";
    }

    // Xử lý chỉnh sửa danh mục
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditCategory(@PathVariable Integer id,
                                  @ModelAttribute LvsCategory lvsCategory,
                                  HttpSession session,
                                  Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCategory.setLvsCategoryId(id);
            lvsCategoryService.lvsSaveCategory(lvsCategory);

            model.addAttribute("LvsSuccess", "Cập nhật danh mục thành công!");
            return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsCategoryEdit";
        }
    }

    // Xóa danh mục
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteCategory(@PathVariable Integer id,
                                    HttpSession session,
                                    Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

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
    public String lvsToggleCategoryActive(@PathVariable Integer id,
                                          HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCategoryService.lvsToggleCategoryActive(id);

        return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + id;
    }

    // Sắp xếp lại thứ tự danh mục
    @PostMapping("/LvsReorder")
    public String lvsReorderCategories(@RequestParam List<Integer> lvsCategoryIds,
                                       HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCategoryService.lvsReorderCategories(lvsCategoryIds);

        return "redirect:/LvsAdmin/LvsCategory/LvsList";
    }
}