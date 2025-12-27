package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Controller quản lý Đánh giá (Review) trong Admin Panel
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsReview")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminReviewController {

    @Autowired
    private LvsReviewService lvsReviewService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsFileUploadService lvsFileUploadService;

    @GetMapping("/LvsList")
    public String lvsListReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Boolean lvsIsApproved,
            @RequestParam(required = false) Long lvsProjectId,
            Model model) {
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

        return "LvsAreas/LvsAdmin/LvsReview/LvsList";
    }

    @GetMapping("/LvsDetail/{id}")
    public String lvsViewReviewDetail(@PathVariable Long id, Model model) {
        LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);
        if (lvsReview == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        model.addAttribute("LvsReview", lvsReview);
        return "LvsAreas/LvsAdmin/LvsReview/LvsDetail";
    }

    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveReview(@PathVariable Long id, Model model) {
        try {
            lvsReviewService.lvsApproveReview(id);
            model.addAttribute("LvsSuccess", "Đã duyệt đánh giá!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReview/LvsDetail/" + id;
    }

    @PostMapping("/LvsHide/{id}")
    public String lvsHideReview(@PathVariable Long id, @RequestParam(required = false) String lvsReason, Model model) {
        try {
            lvsReviewService.lvsHideReview(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã ẩn đánh giá!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReview/LvsDetail/" + id;
    }

    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteReview(@PathVariable Long id, @RequestParam(required = false) String lvsReason,
            Model model) {
        try {
            LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);
            Long lvsProjectId = lvsReview.getLvsProject().getLvsProjectId();

            lvsReviewService.lvsDeleteReview(id, lvsReason);
            lvsProjectService.lvsUpdateProjectRating(lvsProjectId);

            model.addAttribute("LvsSuccess", "Đã xóa đánh giá!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReview/LvsList";
    }

    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditReviewForm(@PathVariable Long id, Model model) {
        LvsReview lvsReview = lvsReviewService.lvsGetReviewById(id);
        if (lvsReview == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        model.addAttribute("LvsReview", lvsReview);
        return "LvsAreas/LvsAdmin/LvsReview/LvsEdit";
    }

    @PostMapping("/LvsEdit/{id}")
    public String lvsEditReview(@PathVariable Long id,
            @ModelAttribute LvsReview lvsReview,
            @RequestParam(required = false) MultipartFile[] lvsImageFiles,
            Model model) {
        try {
            System.out.println("=== EDIT REVIEW - IMAGE UPLOAD DEBUG ===");
            System.out.println("Review ID: " + id);
            System.out.println("lvsImageFiles: " + (lvsImageFiles != null ? lvsImageFiles.length : "null"));

            LvsReview lvsExistingReview = lvsReviewService.lvsGetReviewById(id);

            // Upload images if provided
            if (lvsImageFiles != null && lvsImageFiles.length > 0) {
                List<MultipartFile> validFiles = Arrays.stream(lvsImageFiles)
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                System.out.println("Valid files count: " + validFiles.size());

                if (!validFiles.isEmpty()) {
                    List<String> imageUrls = lvsFileUploadService.lvsSaveFiles(validFiles, "reviews");
                    System.out.println("Image URLs: " + imageUrls);

                    ObjectMapper mapper = new ObjectMapper();
                    String imagesJson = mapper.writeValueAsString(imageUrls);
                    System.out.println("Images JSON: " + imagesJson);

                    lvsReview.setLvsImages(imagesJson);
                    System.out.println("Set images to review: " + lvsReview.getLvsImages());
                } else {
                    // Keep existing images if no new files
                    lvsReview.setLvsImages(lvsExistingReview.getLvsImages());
                    System.out.println("No new images, keeping existing: " + lvsExistingReview.getLvsImages());
                }
            } else {
                // Keep existing images if no files provided
                lvsReview.setLvsImages(lvsExistingReview.getLvsImages());
                System.out.println("No files provided, keeping existing: " + lvsExistingReview.getLvsImages());
            }

            lvsReview.setLvsReviewId(id);
            lvsReview.setLvsUser(lvsExistingReview.getLvsUser());
            lvsReview.setLvsProject(lvsExistingReview.getLvsProject());

            System.out.println("Before save - Images: " + lvsReview.getLvsImages());
            lvsReviewService.lvsSaveReview(lvsReview);
            System.out.println("After save - Success");

            lvsProjectService.lvsUpdateProjectRating(lvsExistingReview.getLvsProject().getLvsProjectId());

            model.addAttribute("LvsSuccess", "Cập nhật đánh giá thành công!");
            return "redirect:/LvsAdmin/LvsReview/LvsDetail/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsReview/LvsEdit";
        }
    }

    @GetMapping("/LvsProject/{projectId}")
    public String lvsViewProjectReviews(@PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            Model model) {
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

        return "LvsAreas/LvsAdmin/LvsReview/LvsProject";
    }

    @GetMapping("/LvsUser/{userId}")
    public String lvsViewUserReviews(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            Model model) {
        LvsUser lvsUser = lvsUserService.lvsGetUserById(userId);
        if (lvsUser == null) {
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetReviewsByUser(userId, lvsPageable);

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsReview/LvsUser";
    }

    /**
     * Show create review form
     */
    @GetMapping("/LvsCreate")
    public String lvsShowCreateReviewForm(Model model) {
        model.addAttribute("LvsReview", new LvsReview());

        // Get all projects and users for selection (unpaged for dropdown)
        Pageable unpaged = Pageable.unpaged();
        List<LvsProject> lvsProjects = lvsProjectService.lvsGetAllProjects(unpaged).getContent();
        List<LvsUser> lvsUsers = lvsUserService.lvsGetAllUsers(unpaged).getContent();

        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsUsers", lvsUsers);

        return "LvsAreas/LvsAdmin/LvsReview/LvsCreate";
    }

    /**
     * Create new review
     */
    @PostMapping("/LvsCreate")
    public String lvsCreateReview(@ModelAttribute LvsReview lvsReview,
            @RequestParam Long lvsProjectId,
            @RequestParam Long lvsUserId,
            @RequestParam(required = false) MultipartFile[] lvsImageFiles,
            RedirectAttributes redirectAttributes) {
        try {
            // Get project and user
            LvsProject lvsProject = lvsProjectService.lvsGetProjectById(lvsProjectId);
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);

            if (lvsProject == null || lvsUser == null) {
                redirectAttributes.addFlashAttribute("lvsError", "Project or User not found!");
                return "redirect:/LvsAdmin/LvsReview/LvsCreate";
            }

            // Upload images if provided
            System.out.println("=== IMAGE UPLOAD DEBUG ===");
            System.out.println("lvsImageFiles: " + (lvsImageFiles != null ? lvsImageFiles.length : "null"));

            if (lvsImageFiles != null && lvsImageFiles.length > 0) {
                List<MultipartFile> validFiles = Arrays.stream(lvsImageFiles)
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                System.out.println("Valid files count: " + validFiles.size());

                if (!validFiles.isEmpty()) {
                    List<String> imageUrls = lvsFileUploadService.lvsSaveFiles(validFiles, "reviews");
                    System.out.println("Image URLs: " + imageUrls);

                    ObjectMapper mapper = new ObjectMapper();
                    String imagesJson = mapper.writeValueAsString(imageUrls);
                    System.out.println("Images JSON: " + imagesJson);

                    lvsReview.setLvsImages(imagesJson);
                    System.out.println("Set images to review: " + lvsReview.getLvsImages());
                }
            }

            // Set relationships
            lvsReview.setLvsProject(lvsProject);
            lvsReview.setLvsUser(lvsUser);
            lvsReview.setLvsIsApproved(true); // Auto-approve admin-created reviews

            // Save review
            System.out.println("Before save - Images: " + lvsReview.getLvsImages());
            lvsReviewService.lvsSaveReview(lvsReview);
            System.out.println("After save - Review ID: " + lvsReview.getLvsReviewId());

            // Update project rating
            lvsProjectService.lvsUpdateProjectRating(lvsProjectId);

            redirectAttributes.addFlashAttribute("lvsSuccess", "Review created successfully!");
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            redirectAttributes.addFlashAttribute("lvsError", "Error: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsReview/LvsCreate";
        }
    }
}
