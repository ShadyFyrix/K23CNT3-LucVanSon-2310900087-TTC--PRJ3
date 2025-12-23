package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import k23cnt3.lucvanson.project3.LvsRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Autowired
    private LvsFollowService lvsFollowService;

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

        // Tìm kiếm hoặc lọc - SỬ DỤNG EAGER LOADING
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        } else if (lvsCategoryId != null) {
            lvsProjects = lvsProjectService.lvsGetProjectsByCategory(lvsCategoryId, lvsPageable);
        } else {
            // Dùng method eager load để tránh LazyInitializationException
            lvsProjects = lvsProjectService.lvsGetAllProjectsWithCategoryAndUser(lvsPageable);
        }

        // Truyền dữ liệu
        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsSelectedCategoryId", lvsCategoryId);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectList";
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

        // Lấy project với eager loading để tránh LazyInitializationException
        LvsProject lvsProject = lvsProjectService.lvsGetProjectByIdWithDetails(id);

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

            // Lấy danh sách followers (nếu user là owner của project)
            if (lvsProject.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
                Page<LvsUser> lvsFollowersPage = lvsFollowService.lvsGetFollowers(
                        lvsCurrentUser.getLvsUserId(),
                        PageRequest.of(0, 100));
                model.addAttribute("LvsFollowers", lvsFollowersPage.getContent());
            }
        }

        // Truyền dữ liệu
        model.addAttribute("project", lvsProject);
        model.addAttribute("LvsHasPurchased", lvsHasPurchased);

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectDetail";
    }

    /**
     * Mua project
     * URL: POST /LvsUser/LvsProject/LvsPurchase/{id}
     */
    @PostMapping("/LvsPurchase/{id}")
    public String lvsPurchaseProject(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Lấy user hiện tại
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để mua dự án");
            return "redirect:/LvsLogin";
        }

        try {
            // Gọi service để mua project
            LvsOrder lvsOrder = lvsOrderService.lvsPurchaseProject(id, lvsCurrentUser.getLvsUserId());

            // Cập nhật lại user trong session (vì coin đã thay đổi)
            LvsUser lvsUpdatedUser = lvsUserRepository.findById(lvsCurrentUser.getLvsUserId()).orElse(null);
            if (lvsUpdatedUser != null) {
                session.setAttribute("LvsCurrentUser", lvsUpdatedUser);
            }

            redirectAttributes.addFlashAttribute("success",
                    "Mua dự án thành công! Mã đơn hàng: " + lvsOrder.getLvsOrderCode());
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + id;
        }
    }
}