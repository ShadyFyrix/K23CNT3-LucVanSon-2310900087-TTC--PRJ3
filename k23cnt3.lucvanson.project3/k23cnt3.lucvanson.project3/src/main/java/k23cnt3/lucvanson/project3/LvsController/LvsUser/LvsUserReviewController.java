package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller quản lý đánh giá cho người dùng
 * Xử lý viết, chỉnh sửa, xóa đánh giá
 */
@Controller
@RequestMapping("/LvsUser/LvsReview")
public class LvsUserReviewController {

    @Autowired
    private LvsReviewService lvsReviewService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsFileUploadService lvsFileUploadService;

    // Xem đánh giá của dự án
    @GetMapping("/LvsProject/{projectId}")
    public String lvsViewProjectReviews(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(projectId);
        if (lvsProject == null) {
            return "redirect:/LvsUser/LvsProject/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetReviewsByProject(
                projectId, lvsPageable);

        // Tính trung bình rating
        Double lvsAverageRating = lvsReviewService.lvsGetAverageRating(projectId);

        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsAverageRating", lvsAverageRating);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsReviews/LvsReviewList";
    }

    // Viết đánh giá mới
    @GetMapping("/LvsWrite/{projectId}")
    public String lvsShowWriteReviewForm(@PathVariable Long projectId,
            Model model,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Kiểm tra đã mua dự án chưa
        boolean lvsHasPurchased = lvsProjectService.lvsHasUserPurchasedProject(
                lvsCurrentUser.getLvsUserId(), projectId);

        if (!lvsHasPurchased) {
            model.addAttribute("LvsError", "Bạn cần mua dự án để đánh giá!");
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + projectId;
        }

        // Kiểm tra đã đánh giá chưa
        boolean lvsHasReviewed = lvsReviewService.lvsHasUserReviewed(
                lvsCurrentUser.getLvsUserId(), projectId);

        if (lvsHasReviewed) {
            model.addAttribute("LvsError", "Bạn đã đánh giá dự án này rồi!");
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + projectId;
        }

        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(projectId);

        model.addAttribute("LvsProject", lvsProject);
        model.addAttribute("LvsReview", new LvsReview());

        return "LvsAreas/LvsUsers/LvsReviews/LvsReviewWrite";
    }

    // Xử lý viết đánh giá
    @PostMapping("/LvsWrite/{projectId}")
    public String lvsWriteReview(@PathVariable Long projectId,
            @ModelAttribute LvsReview lvsReview,
            @RequestParam(required = false) MultipartFile[] lvsImageFiles,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra rating hợp lệ
            if (lvsReview.getLvsRating() < 1 || lvsReview.getLvsRating() > 5) {
                model.addAttribute("LvsError", "Rating phải từ 1 đến 5 sao!");
                return "LvsAreas/LvsUsers/LvsReviews/LvsReviewWrite";
            }

            LvsProject lvsProject = lvsProjectService.lvsGetProjectById(projectId);

            // Upload images if provided
            if (lvsImageFiles != null && lvsImageFiles.length > 0) {
                List<MultipartFile> validFiles = Arrays.stream(lvsImageFiles)
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                if (!validFiles.isEmpty()) {
                    List<String> imageUrls = lvsFileUploadService.lvsSaveFiles(validFiles, "reviews");
                    ObjectMapper mapper = new ObjectMapper();
                    String imagesJson = mapper.writeValueAsString(imageUrls);
                    lvsReview.setLvsImages(imagesJson);
                }
            }

            lvsReview.setLvsUser(lvsCurrentUser);
            lvsReview.setLvsProject(lvsProject);
            lvsReview.setLvsIsApproved(true); // Auto-approve user reviews

            LvsReview lvsSavedReview = lvsReviewService.lvsSaveReview(lvsReview);

            // Cập nhật rating trung bình của dự án
            lvsProjectService.lvsUpdateProjectRating(projectId);

            model.addAttribute("LvsSuccess", "Đã gửi đánh giá thành công!");
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + projectId;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi gửi đánh giá: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsReviews/LvsReviewWrite";
        }
    }

    // Chỉnh sửa đánh giá
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditReviewForm(@PathVariable Long id,
            Model model,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);

        if (lvsCurrentUser == null ||
                !lvsReview.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsDashboard";
        }

        model.addAttribute("LvsReview", lvsReview);

        return "LvsAreas/LvsUsers/LvsReviews/LvsReviewEdit";
    }

    // Xử lý chỉnh sửa đánh giá
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditReview(@PathVariable Long id,
            @ModelAttribute LvsReview lvsReview,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsReview lvsExistingReview = lvsReviewService.lvsGetReviewById(id);

            if (!lvsExistingReview.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
                return "redirect:/LvsUser/LvsDashboard";
            }

            lvsReview.setLvsReviewId(id);
            lvsReview.setLvsUser(lvsCurrentUser);
            lvsReview.setLvsProject(lvsExistingReview.getLvsProject());

            lvsReviewService.lvsSaveReview(lvsReview);

            // Cập nhật rating trung bình của dự án
            lvsProjectService.lvsUpdateProjectRating(lvsExistingReview.getLvsProject().getLvsProjectId());

            model.addAttribute("LvsSuccess", "Cập nhật đánh giá thành công!");
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + lvsExistingReview.getLvsProject().getLvsProjectId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi cập nhật đánh giá: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsReviews/LvsReviewEdit";
        }
    }

    // Xóa đánh giá
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteReview(@PathVariable Long id,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);

        if (lvsCurrentUser != null &&
                lvsReview.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {

            Long lvsProjectId = lvsReview.getLvsProject().getLvsProjectId();
            lvsReviewService.lvsDeleteReview(id);

            // Cập nhật rating trung bình của dự án
            lvsProjectService.lvsUpdateProjectRating(lvsProjectId);
        }

        return "redirect:/LvsUser/LvsDashboard";
    }

    // Thích đánh giá
    @PostMapping("/LvsLike/{id}")
    @ResponseBody
    public java.util.Map<String, Object> lvsLikeReview(@PathVariable Long id, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return java.util.Map.of(
                    "success", false,
                    "message", "Vui lòng đăng nhập để thích đánh giá");
        }

        try {
            LvsReview review = lvsReviewService.lvsGetReviewById(id);
            if (review == null) {
                return java.util.Map.of("success", false, "message", "Không tìm thấy đánh giá");
            }

            // Simple increment - no tracking per user (as requested by user)
            review.setLvsLikeCount(review.getLvsLikeCount() + 1);
            lvsReviewService.lvsSaveReview(review);

            return java.util.Map.of(
                    "success", true,
                    "liked", true,
                    "likeCount", review.getLvsLikeCount());
        } catch (Exception e) {
            return java.util.Map.of("success", false, "message", "Đã có lỗi xảy ra");
        }
    }

    // Xem đánh giá của tôi
    @GetMapping("/LvsMyReviews")
    public String lvsViewMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetReviewsByUser(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsReviews/LvsMyReviews";
    }

    // Báo cáo đánh giá không phù hợp
    @PostMapping("/LvsReport/{id}")
    public String lvsReportReview(@PathVariable Long id,
            @RequestParam String lvsReason,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsReviewService.lvsReportReview(id, lvsCurrentUser.getLvsUserId(), lvsReason);
            model.addAttribute("LvsSuccess", "Đã gửi báo cáo thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsReview/LvsMyReviews";
    }
}