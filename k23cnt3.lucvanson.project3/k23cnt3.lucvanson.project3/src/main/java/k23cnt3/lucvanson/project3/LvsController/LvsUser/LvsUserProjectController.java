package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import k23cnt3.lucvanson.project3.LvsRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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

    @Autowired
    private LvsFileUploadService lvsFileUploadService;

    @Autowired
    private LvsProjectRepository lvsProjectRepository;

    @Autowired
    private LvsReviewService lvsReviewService;

    @Autowired
    private LvsOrderRepository lvsOrderRepository;

    /**
     * Show Add Project Form
     * URL: GET /LvsUser/LvsProject/LvsAdd
     */
    @GetMapping("/LvsAdd")
    public String lvsShowAddForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // Get categories for dropdown
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsProject", new LvsProject());

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectAdd";
    }

    /**
     * Create New Project with File Uploads
     * URL: POST /LvsUser/LvsProject/LvsCreate
     */
    @PostMapping("/LvsCreate")
    public String lvsCreateProject(
            @ModelAttribute LvsProject lvsProject,
            @RequestParam(required = false) MultipartFile projectFile,
            @RequestParam(required = false) MultipartFile thumbnail,
            @RequestParam(required = false) List<MultipartFile> additionalImages,
            @RequestParam(required = false) String demoUrl,
            @RequestParam(required = false) String sourceCodeUrl,
            @RequestParam(required = false) String tags,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Set user
            lvsProject.setLvsUser(lvsCurrentUser);

            // Upload project file (REQUIRED)
            if (projectFile != null && !projectFile.isEmpty()) {
                String fileUrl = lvsFileUploadService.lvsSaveProjectFile(projectFile, "projects");
                lvsProject.setLvsFileUrl(fileUrl);
            } else {
                redirectAttributes.addFlashAttribute("error", "Vui lòng upload file dự án!");
                return "redirect:/LvsUser/LvsProject/LvsAdd";
            }

            // Upload thumbnail
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbUrl = lvsFileUploadService.lvsSaveFile(thumbnail, "projects");
                lvsProject.setLvsThumbnailUrl(thumbUrl);
            }

            // Upload additional images
            if (additionalImages != null && !additionalImages.isEmpty()) {
                List<MultipartFile> validFiles = additionalImages.stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                if (!validFiles.isEmpty()) {
                    List<String> imageUrls = lvsFileUploadService.lvsSaveFiles(validFiles, "projects");
                    // Serialize to JSON
                    ObjectMapper mapper = new ObjectMapper();
                    String imagesJson = mapper.writeValueAsString(imageUrls);
                    lvsProject.setLvsImages(imagesJson);
                }
            }

            // Set URLs
            lvsProject.setLvsDemoUrl(demoUrl);
            lvsProject.setLvsSourceCodeUrl(sourceCodeUrl);
            lvsProject.setLvsTags(tags);

            // Set status to PENDING (waiting for approval)
            lvsProject.setLvsStatus(LvsProject.LvsProjectStatus.PENDING);

            // Save project
            LvsProject savedProject = lvsProjectService.lvsSaveProject(lvsProject);

            redirectAttributes.addFlashAttribute("success", "Tạo dự án thành công! Đang chờ duyệt.");
            return "redirect:/LvsUser/LvsProject/LvsMyProjects";

        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/LvsUser/LvsProject/LvsAdd";
        }
    }

    /**
     * Show Edit Project Form
     * URL: GET /LvsUser/LvsProject/LvsEdit/{id}
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditForm(@PathVariable Long id, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // Get project
        LvsProject lvsProject = lvsProjectService.lvsGetProjectByIdWithDetails(id);
        if (lvsProject == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dự án!");
            return "redirect:/LvsUser/LvsProject/LvsMyProjects";
        }

        // Check ownership
        if (!lvsProject.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền sửa dự án này!");
            return "redirect:/LvsUser/LvsProject/LvsMyProjects";
        }

        // Get categories
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsProject", lvsProject);

        return "LvsAreas/LvsUsers/LvsProjects/LvsProjectEdit";
    }

    /**
     * Update Project with File Uploads
     * URL: POST /LvsUser/LvsProject/LvsUpdate/{id}
     */
    @PostMapping("/LvsUpdate/{id}")
    public String lvsUpdateProject(
            @PathVariable Long id,
            @ModelAttribute LvsProject lvsProject,
            @RequestParam(required = false) MultipartFile projectFile,
            @RequestParam(required = false) MultipartFile thumbnail,
            @RequestParam(required = false) List<MultipartFile> additionalImages,
            @RequestParam(required = false) String deletedImages,
            @RequestParam(required = false) String demoUrl,
            @RequestParam(required = false) String sourceCodeUrl,
            @RequestParam(required = false) String tags,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Get existing project
            LvsProject existingProject = lvsProjectService.lvsGetProjectByIdWithDetails(id);
            if (existingProject == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy dự án!");
                return "redirect:/LvsUser/LvsProject/LvsMyProjects";
            }

            // Check ownership
            if (!existingProject.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền sửa dự án này!");
                return "redirect:/LvsUser/LvsProject/LvsMyProjects";
            }

            // Update basic fields
            existingProject.setLvsProjectName(lvsProject.getLvsProjectName());
            existingProject.setLvsDescription(lvsProject.getLvsDescription());
            existingProject.setLvsPrice(lvsProject.getLvsPrice());
            existingProject.setLvsCategory(lvsProject.getLvsCategory());

            // ✨ UPDATE DISCOUNT FIELDS
            existingProject.setLvsDiscountPercent(lvsProject.getLvsDiscountPercent());
            existingProject.setLvsIsOnSale(lvsProject.getLvsIsOnSale());
            existingProject.setLvsDiscountStartDate(lvsProject.getLvsDiscountStartDate());
            existingProject.setLvsDiscountEndDate(lvsProject.getLvsDiscountEndDate());

            // Upload new project file if provided
            if (projectFile != null && !projectFile.isEmpty()) {
                String fileUrl = lvsFileUploadService.lvsSaveProjectFile(projectFile, "projects");
                existingProject.setLvsFileUrl(fileUrl);
            }

            // Upload new thumbnail if provided
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbUrl = lvsFileUploadService.lvsSaveFile(thumbnail, "projects");
                existingProject.setLvsThumbnailUrl(thumbUrl);
            }

            // Handle additional images
            ObjectMapper mapper = new ObjectMapper();
            List<String> finalImageUrls = new ArrayList<>();

            // Get existing images
            if (existingProject.getLvsImages() != null && !existingProject.getLvsImages().isEmpty()) {
                try {
                    List<String> existingImages = mapper.readValue(existingProject.getLvsImages(),
                            new TypeReference<List<String>>() {
                            });
                    finalImageUrls.addAll(existingImages);
                } catch (Exception e) {
                    System.out.println("Error parsing existing images: " + e.getMessage());
                }
            }

            // Remove deleted images
            if (deletedImages != null && !deletedImages.isEmpty()) {
                try {
                    List<String> toDelete = mapper.readValue(deletedImages,
                            new TypeReference<List<String>>() {
                            });
                    finalImageUrls.removeAll(toDelete);
                    System.out.println("Removed " + toDelete.size() + " images");
                } catch (Exception e) {
                    System.out.println("Error parsing deleted images: " + e.getMessage());
                }
            }

            // Upload new additional images if provided
            if (additionalImages != null && !additionalImages.isEmpty()) {
                List<MultipartFile> validFiles = additionalImages.stream()
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                if (!validFiles.isEmpty()) {
                    List<String> newImageUrls = lvsFileUploadService.lvsSaveFiles(validFiles, "projects");
                    finalImageUrls.addAll(newImageUrls);
                }
            }

            // Save final images list
            if (!finalImageUrls.isEmpty()) {
                String imagesJson = mapper.writeValueAsString(finalImageUrls);
                existingProject.setLvsImages(imagesJson);
            } else {
                existingProject.setLvsImages(null);
            }

            // Update URLs
            existingProject.setLvsDemoUrl(demoUrl);
            existingProject.setLvsSourceCodeUrl(sourceCodeUrl);
            existingProject.setLvsTags(tags);

            // Save project
            lvsProjectService.lvsSaveProject(existingProject);

            redirectAttributes.addFlashAttribute("success", "Cập nhật dự án thành công!");
            return "redirect:/LvsUser/LvsProject/LvsMyProjects";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/LvsUser/LvsProject/LvsEdit/" + id;
        }
    }

    /**
     * Danh sách dự án (public)
     * URL: GET /LvsUser/LvsProject/LvsList
     * View: LvsAreas/LvsUsers/LvsProjects/LvsProjectList
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

        // FILTER APPROVED PROJECTS ONLY
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            // Search in APPROVED projects only
            lvsProjects = lvsProjectRepository.searchProjectsByStatus(
                    lvsKeyword,
                    LvsProject.LvsProjectStatus.APPROVED,
                    lvsPageable);
        } else if (lvsCategoryId != null) {
            // Filter by category AND APPROVED status
            lvsProjects = lvsProjectRepository.findByCategoryAndStatusWithDetails(
                    lvsCategoryId,
                    LvsProject.LvsProjectStatus.APPROVED,
                    lvsPageable);
        } else {
            // Show all APPROVED projects with eager loading
            lvsProjects = lvsProjectRepository.findByStatusWithCategoryAndUser(
                    LvsProject.LvsProjectStatus.APPROVED,
                    lvsPageable);
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

        // Nếu không tìm thấy
        if (lvsProject == null) {
            return "redirect:/LvsUser/LvsProject/LvsList";
        }

        // Kiểm tra quyền xem: phải APPROVED HOẶC là owner
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        boolean isOwner = lvsCurrentUser != null &&
                lvsProject.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId());

        if (lvsProject.getLvsStatus() != LvsProject.LvsProjectStatus.APPROVED && !isOwner) {
            return "redirect:/LvsUser/LvsProject/LvsList";
        }

        // Tăng view count
        lvsProjectService.lvsIncrementViewCount(id);

        // Kiểm tra user đã mua chưa (lvsCurrentUser already declared above)
        boolean lvsHasPurchased = false;
        if (lvsCurrentUser != null) {
            lvsHasPurchased = lvsProjectService.lvsHasUserPurchasedProject(
                    lvsCurrentUser.getLvsUserId(), id);

            // Lấy danh sách followers của current user (for gift feature)
            Page<LvsUser> lvsFollowersPage = lvsFollowService.lvsGetFollowers(
                    lvsCurrentUser.getLvsUserId(),
                    PageRequest.of(0, 100));
            model.addAttribute("LvsFollowers", lvsFollowersPage.getContent());
        }

        // Fetch reviews for this project
        Pageable reviewPageable = PageRequest.of(0, 10); // First 10 reviews
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetApprovedReviewsByProject(id, reviewPageable); // Only
                                                                                                          // approved

        // Get average rating
        Double lvsAverageRating = lvsReviewService.lvsGetAverageRating(id);

        // Get rating distribution (1-5 stars)
        java.util.Map<Integer, Long> lvsRatingDistribution = lvsReviewService.lvsGetRatingDistribution(id);

        // Check if current user has reviewed
        boolean lvsHasReviewed = false;
        if (lvsCurrentUser != null) {
            lvsHasReviewed = lvsReviewService.lvsHasUserReviewed(lvsCurrentUser.getLvsUserId(), id);
        }

        // Get download count (number of orders for this project)
        long lvsDownloadCount = lvsOrderRepository.countByLvsProject_LvsProjectId(id);

        // Truyền dữ liệu
        model.addAttribute("project", lvsProject);
        model.addAttribute("LvsHasPurchased", lvsHasPurchased);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsAverageRating", lvsAverageRating != null ? lvsAverageRating : 0.0);
        model.addAttribute("LvsRatingDistribution", lvsRatingDistribution);
        model.addAttribute("LvsHasReviewed", lvsHasReviewed);
        model.addAttribute("lvsDownloadCount", lvsDownloadCount);

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

    /**
     * My Projects - Projects user created and purchased
     * URL: GET /LvsUser/LvsProject/LvsMyProjects
     * View: LvsAreas/LvsUsers/LvsProjects/LvsMyProjects
     */
    @GetMapping("/LvsMyProjects")
    public String lvsMyProjects(
            @RequestParam(defaultValue = "created") String tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model,
            HttpSession session) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        if ("purchased".equals(tab)) {
            // Get purchased projects (from completed orders)
            lvsProjects = lvsProjectService.lvsGetUserPurchasedProjects(
                    lvsCurrentUser.getLvsUserId(), lvsPageable);
        } else {
            // Get created projects (uploaded by user)
            lvsProjects = lvsProjectService.lvsGetProjectsByUser(
                    lvsCurrentUser.getLvsUserId(), lvsPageable);
        }

        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsActiveTab", tab);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsProjects/LvsMyProjects";
    }
}