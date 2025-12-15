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
 * Controller quản lý dự án cho LvsAdmin
 * Xử lý duyệt, từ chối, xóa dự án
 */
@Controller
@RequestMapping("/LvsAdmin/LvsProject")
public class LvsAdminProjectController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsCategoryService lvsCategoryService;

    // Danh sách dự án
    @GetMapping("/LvsList")
    public String lvsListProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsProjects = lvsProjectService.lvsGetProjectsByStatus(lvsStatus, lvsPageable);
        } else {
            lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);
        }

        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsStatuses", LvsProject.LvsProjectStatus.values());
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsProjectList";
    }

    // Xem chi tiết dự án
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewProjectDetail(@PathVariable Long id,
                                       Model model,
                                       HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        if (lvsProject == null) {
            return "redirect:/LvsAdmin/LvsProject/LvsList";
        }

        model.addAttribute("LvsProject", lvsProject);

        return "LvsAdmin/LvsProjectDetail";
    }

    // Duyệt dự án
    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveProject(@PathVariable Long id,
                                    @RequestParam(required = false) String lvsNotes,
                                    HttpSession session,
                                    Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsProjectService.lvsApproveProject(id, lvsAdmin.getLvsUserId(), lvsNotes);

            model.addAttribute("LvsSuccess", "Đã duyệt dự án!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    // Từ chối dự án
    @PostMapping("/LvsReject/{id}")
    public String lvsRejectProject(@PathVariable Long id,
                                   @RequestParam String lvsReason,
                                   HttpSession session,
                                   Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsProjectService.lvsRejectProject(id, lvsAdmin.getLvsUserId(), lvsReason);

            model.addAttribute("LvsSuccess", "Đã từ chối dự án!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    // Đánh dấu featured
    @PostMapping("/LvsToggleFeatured/{id}")
    public String lvsToggleFeatured(@PathVariable Long id,
                                    HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsProjectService.lvsToggleFeatured(id);

        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    // Xóa dự án
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteProject(@PathVariable Long id,
                                   @RequestParam(required = false) String lvsReason,
                                   HttpSession session,
                                   Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsProjectService.lvsDeleteProject(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã xóa dự án!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsProject/LvsList";
    }

    // Chỉnh sửa dự án
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditProjectForm(@PathVariable Long id,
                                         Model model,
                                         HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsCategories", lvsCategories);

        return "LvsAdmin/LvsProjectEdit";
    }

    // Xử lý chỉnh sửa dự án
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditProject(@PathVariable Long id,
                                 @ModelAttribute LvsProject lvsProject,
                                 HttpSession session,
                                 Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsProject.setLvsProjectId(id);
            lvsProjectService.lvsUpdateProject(lvsProject);

            model.addAttribute("LvsSuccess", "Cập nhật dự án thành công!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsProjectEdit";
        }
    }

    // Thêm dự án mới (LvsAdmin)
    @GetMapping("/LvsAdd")
    public String lvsShowAddProjectForm(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        List<LvsUser> lvsUsers = lvsUserService.lvsGetAllUsers();

        model.addAttribute("LvsProject", new LvsProject());
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsUsers", lvsUsers);

        return "LvsAdmin/LvsProjectAdd";
    }

    // Xử lý thêm dự án
    @PostMapping("/LvsAdd")
    public String lvsAddProject(@ModelAttribute LvsProject lvsProject,
                                @RequestParam Long lvsUserId,
                                HttpSession session,
                                Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            lvsProject.setLvsUser(lvsUser);
            lvsProject.setLvsStatus(LvsProject.LvsProjectStatus.APPROVED);
            lvsProject.setLvsIsApproved(true);

            LvsProject lvsSavedProject = lvsProjectService.lvsSaveProject(lvsProject);

            model.addAttribute("LvsSuccess", "Thêm dự án thành công!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + lvsSavedProject.getLvsProjectId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsProjectAdd";
        }
    }
}