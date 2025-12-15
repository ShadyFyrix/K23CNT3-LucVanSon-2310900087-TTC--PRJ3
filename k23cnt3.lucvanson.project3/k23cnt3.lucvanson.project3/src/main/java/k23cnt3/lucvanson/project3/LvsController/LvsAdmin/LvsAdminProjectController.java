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

import java.util.List;

@Controller
@RequestMapping("/LvsAdmin/LvsProject")
public class LvsAdminProjectController {

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsCategoryService lvsCategoryService;

    @GetMapping("/LvsList")
    public String lvsListProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model) {

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsProjects = lvsProjectService.lvsGetProjectsByStatus(lvsStatus, lvsPageable);
        } else {
            lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);
        }

        model.addAttribute("lvsProjects", lvsProjects);
        model.addAttribute("lvsStatuses", LvsProject.LvsProjectStatus.values());
        model.addAttribute("lvsSelectedStatus", lvsStatus);
        model.addAttribute("lvsKeyword", lvsKeyword);
        model.addAttribute("lvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsProject/LvsList";
    }

    @GetMapping("/LvsDetail/{id}")
    public String lvsViewProjectDetail(@PathVariable Long id, Model model) {
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);
        if (lvsProject == null) {
            return "redirect:/LvsAdmin/LvsProject/LvsList";
        }
        model.addAttribute("lvsProject", lvsProject);
        return "LvsAreas/LvsAdmin/LvsProject/LvsDetail";
    }

    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveProject(@PathVariable Long id, @RequestParam(required = false) String lvsNotes, Model model) {
        try {
            LvsUser lvsAdmin = lvsUserService.lvsGetCurrentUser();
            lvsProjectService.lvsApproveProject(id, lvsAdmin.getLvsUserId(), lvsNotes);
            model.addAttribute("LvsSuccess", "Da duyet du an!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Loi: " + e.getMessage());
        }
        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    @PostMapping("/LvsReject/{id}")
    public String lvsRejectProject(@PathVariable Long id, @RequestParam String lvsReason, Model model) {
        try {
            LvsUser lvsAdmin = lvsUserService.lvsGetCurrentUser();
            lvsProjectService.lvsRejectProject(id, lvsAdmin.getLvsUserId(), lvsReason);
            model.addAttribute("LvsSuccess", "Da tu choi du an!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Loi: " + e.getMessage());
        }
        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    @PostMapping("/LvsToggleFeatured/{id}")
    public String lvsToggleFeatured(@PathVariable Long id) {
        lvsProjectService.lvsToggleFeatured(id);
        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteProject(@PathVariable Long id, Model model) {
        try {
            lvsProjectService.lvsDeleteProject(id); // Hard delete - no reason parameter
            model.addAttribute("LvsSuccess", "Da xoa du an!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Loi: " + e.getMessage());
        }
        return "redirect:/LvsAdmin/LvsProject/LvsList";
    }

    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditProjectForm(@PathVariable Long id, Model model) {
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        model.addAttribute("lvsProject", lvsProject);
        model.addAttribute("lvsCategories", lvsCategories);
        return "LvsAreas/LvsAdmin/LvsProject/LvsEdit";
    }

    @PostMapping("/LvsEdit/{id}")
    public String lvsEditProject(@PathVariable Long id, @ModelAttribute LvsProject lvsProject, Model model) {
        try {
            lvsProject.setLvsProjectId(id);
            lvsProjectService.lvsUpdateProject(lvsProject);
            model.addAttribute("LvsSuccess", "Cap nhat du an thanh cong!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Loi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsProject/LvsEdit";
        }
    }

    @GetMapping("/LvsAdd")
    public String lvsShowAddProjectForm(Model model) {
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        Pageable lvsPageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<LvsUser> lvsUserPage = lvsUserService.lvsGetAllUsers(lvsPageable);
        List<LvsUser> lvsUsers = lvsUserPage.getContent();
        model.addAttribute("lvsProject", new LvsProject());
        model.addAttribute("lvsCategories", lvsCategories);
        model.addAttribute("lvsUsers", lvsUsers);
        return "LvsAreas/LvsAdmin/LvsProject/LvsCreate";
    }

    @PostMapping("/LvsAdd")
    public String lvsAddProject(@ModelAttribute LvsProject lvsProject, @RequestParam Long lvsUserId, Model model) {
        try {
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            lvsProject.setLvsUser(lvsUser);
            lvsProject.setLvsStatus(LvsProject.LvsProjectStatus.APPROVED);
            lvsProject.setLvsIsApproved(true);
            LvsProject lvsSavedProject = lvsProjectService.lvsSaveProject(lvsProject);
            model.addAttribute("LvsSuccess", "Them du an thanh cong!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + lvsSavedProject.getLvsProjectId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Loi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsProject/LvsCreate";
        }
    }
}
