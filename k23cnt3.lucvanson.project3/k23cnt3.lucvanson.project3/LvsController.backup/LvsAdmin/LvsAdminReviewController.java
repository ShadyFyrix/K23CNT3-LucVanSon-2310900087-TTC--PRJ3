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
 * Controller quản lý đánh giá cho LvsAdmin
 * Xử lý duyệt, ẩn, xóa đánh giá
 */
@Controller
@RequestMapping("/LvsAdmin/LvsReview")
public class LvsAdminReviewController {

    @Autowired
    private LvsReviewService lvsReviewService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsProjectService lvsProjectService;

    // Danh sách đánh giá
    @GetMapping("/LvsList")
    public String lvsListReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Boolean lvsIsApproved,
            @RequestParam(required = false) Long lvsProjectId,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReview> lvsReviews;

        if (lvsProjectId != null) {
            lvsReviews = lvsReviewService.lvsGetReviewsByProject(lvsProjectId, lvsPageable);
        } else if (lvsIsApproved != null) {
            lvsReviews = lvsReviewService.lvsGetReviewsByApproval(lvsIsApproved, lvsPageable);
        } else {
            lvsReviews = lvsReviewService.lvsGetAllReviews(lvsPageable);
        }

        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsIsApproved", lvsIsApproved);
        model.addAttribute("LvsProjectId", lvsProjectId);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsReviewList";
    }

    // Xem chi tiết đánh giá
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewReviewDetail(@PathVariable Long id,
                                      Model model,
                                      HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);

        if (lvsReview == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        model.addAttribute("LvsReview", lvsReview);

        return "LvsAdmin/LvsReviewDetail";
    }

    // Duyệt đánh giá
    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveReview(@PathVariable Long id,
                                   HttpSession session,
                                   Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsReviewService.lvsApproveReview(id);
            model.addAttribute("LvsSuccess", "Đã duyệt đánh giá!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReview/LvsDetail/" + id;
    }

    // Ẩn đánh giá
    @PostMapping("/LvsHide/{id}")
    public String lvsHideReview(@PathVariable Long id,
                                @RequestParam(required = false) String lvsReason,
                                HttpSession session,
                                Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsReviewService.lvsHideReview(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã ẩn đánh giá!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReview/LvsDetail/" + id;
    }

    // Xóa đánh giá
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteReview(@PathVariable Long id,
                                  @RequestParam(required = false) String lvsReason,
                                  HttpSession session,
                                  Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);
            Long lvsProjectId = lvsReview.getLvsProject().getLvsProjectId();

            lvsReviewService.lvsDeleteReview(id, lvsReason);

            // Cập nhật rating trung bình của dự án
            lvsProjectService.lvsUpdateProjectRating(lvsProjectId);

            model.addAttribute("LvsSuccess", "Đã xóa đánh giá!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReview/LvsList";
    }

    // Chỉnh sửa đánh giá
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditReviewForm(@PathVariable Long id,
                                        Model model,
                                        HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);

        if (lvsReview == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        model.addAttribute("LvsReview", lvsReview);

        return "LvsAdmin/LvsReviewEdit";
    }

    // Xử lý chỉnh sửa đánh giá
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditReview(@PathVariable Long id,
                                @ModelAttribute LvsReview lvsReview,
                                HttpSession session,
                                Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsReview lvsExistingReview = lvsReviewService.lvsGetReviewById(id);

            lvsReview.setLvsReviewId(id);
            lvsReview.setLvsUser(lvsExistingReview.getLvsUser());
            lvsReview.setLvsProject(lvsExistingReview.getLvsProject());

            lvsReviewService.lvsSaveReview(lvsReview);

            // Cập nhật rating trung bình của dự án
            lvsProjectService.lvsUpdateProjectRating(lvsExistingReview.getLvsProject().getLvsProjectId());

            model.addAttribute("LvsSuccess", "Cập nhật đánh giá thành công!");
            return "redirect:/LvsAdmin/LvsReview/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsReviewEdit";
        }
    }

    // Xem đánh giá của dự án
    @GetMapping("/LvsProject/{projectId}")
    public String lvsViewProjectReviews(@PathVariable Long projectId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "30") int size,
                                        Model model,
                                        HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(projectId);
        if (lvsProject == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetReviewsByProject(projectId, lvsPageable);

        Double lvsAverageRating = lvsReviewService.lvsGetAverageRating(projectId);

        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsAverageRating", lvsAverageRating);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsReviewProject";
    }

    // Xem đánh giá của người dùng
    @GetMapping("/LvsUser/{userId}")
    public String lvsViewUserReviews(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "30") int size,
                                     Model model,
                                     HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsUser lvsUser = lvsUserService.lvsGetUserById(userId);
        if (lvsUser == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetReviewsByUser(userId, lvsPageable);

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsReviewUser";
    }
}