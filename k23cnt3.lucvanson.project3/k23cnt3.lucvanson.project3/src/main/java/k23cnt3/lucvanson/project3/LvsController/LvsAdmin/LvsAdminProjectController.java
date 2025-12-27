package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controller quản lý Dự án (Project) trong Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị danh sách dự án với phân trang, tìm kiếm và lọc theo trạng
 * thái</li>
 * <li>Xem chi tiết thông tin dự án</li>
 * <li>Tạo mới dự án với upload ảnh (thumbnail + multiple images)</li>
 * <li>Chỉnh sửa thông tin dự án và quản lý ảnh</li>
 * <li>Xóa dự án</li>
 * <li>Duyệt/từ chối dự án</li>
 * <li>Đánh dấu dự án nổi bật (featured)</li>
 * </ul>
 * 
 * <p>
 * Tính năng đặc biệt:
 * </p>
 * <ul>
 * <li><strong>Upload ảnh:</strong> Hỗ trợ upload thumbnail và nhiều ảnh dự
 * án</li>
 * <li><strong>Quản lý ảnh:</strong> Thêm, xóa ảnh khi chỉnh sửa dự án</li>
 * <li><strong>JSON storage:</strong> Lưu danh sách ảnh dưới dạng JSON
 * array</li>
 * <li><strong>File validation:</strong> Kiểm tra định dạng và kích thước
 * file</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsProject/LvsList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsProject/LvsDetail.html</li>
 * <li>Create: LvsAreas/LvsAdmin/LvsProject/LvsCreate.html</li>
 * <li>Edit: LvsAreas/LvsAdmin/LvsProject/LvsEdit.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsProject")
public class LvsAdminProjectController {

    /**
     * Service xử lý logic nghiệp vụ cho Project
     */
    @Autowired
    private LvsProjectService lvsProjectService;

    /**
     * Service xử lý logic nghiệp vụ cho User
     */
    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Service xử lý logic nghiệp vụ cho Category
     */
    @Autowired
    private LvsCategoryService lvsCategoryService;

    /**
     * Service xử lý upload file (ảnh thumbnail và ảnh dự án)
     */
    @Autowired
    private LvsFileUploadService lvsFileUploadService;

    /**
     * Service xử lý logic nghiệp vụ cho Review
     */
    @Autowired
    private LvsReviewService lvsReviewService;

    /**
     * Hiển thị danh sách dự án với phân trang, tìm kiếm và lọc
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy danh sách dự án theo trang</li>
     * <li>Tìm kiếm theo từ khóa (tên dự án, mô tả)</li>
     * <li>Lọc theo trạng thái (PENDING, APPROVED, REJECTED)</li>
     * <li>Lọc theo danh mục</li>
     * <li>Hiển thị thumbnail của từng dự án</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsProject/LvsList
     * </p>
     * <p>
     * Ví dụ:
     * /LvsAdmin/LvsProject/LvsList?page=0&size=20&lvsStatus=APPROVED&lvsKeyword=web&lvsCategoryId=1
     * </p>
     * 
     * @param page          Số trang hiện tại (mặc định = 0)
     * @param size          Số items mỗi trang (mặc định = 20)
     * @param lvsStatus     Trạng thái dự án để lọc (optional)
     * @param lvsKeyword    Từ khóa tìm kiếm (optional)
     * @param lvsCategoryId Danh mục để lọc (optional)
     * @param model         Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsProject/LvsList
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsProjects: Page&lt;LvsProject&gt; - Danh sách dự án có phân
     *         trang</li>
     *         <li>lvsStatuses: LvsProjectStatus[] - Tất cả trạng thái có thể</li>
     *         <li>lvsSelectedStatus: String - Trạng thái đang được chọn</li>
     *         <li>lvsKeyword: String - Từ khóa đang tìm kiếm</li>
     *         <li>lvsCategories: List&lt;LvsCategory&gt; - Danh sách danh mục</li>
     *         <li>lvsCategoryId: Integer - Danh mục đang được chọn</li>
     *         <li>lvsCurrentPage: int - Trang hiện tại</li>
     *         </ul>
     */
    @GetMapping("/LvsList")
    public String lvsListProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            @RequestParam(required = false) Integer lvsCategoryId,
            Model model) {

        // Tạo Pageable object
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsProject> lvsProjects;

        // Ưu tiên tìm kiếm theo keyword, sau đó lọc theo category và status
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsProjects = lvsProjectService.lvsSearchProjects(lvsKeyword, lvsPageable);
        } else if (lvsCategoryId != null && lvsStatus != null && !lvsStatus.isEmpty()) {
            // Filter by both category and status
            lvsProjects = lvsProjectService.lvsGetProjectsByCategoryAndStatus(
                    lvsCategoryId,
                    LvsProject.LvsProjectStatus.valueOf(lvsStatus),
                    lvsPageable);
        } else if (lvsCategoryId != null) {
            // Filter by category only
            lvsProjects = lvsProjectService.lvsGetProjectsByCategory(lvsCategoryId, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            // Filter by status only
            lvsProjects = lvsProjectService.lvsGetProjectsByStatus(lvsStatus, lvsPageable);
        } else {
            // Get all projects
            lvsProjects = lvsProjectService.lvsGetAllProjects(lvsPageable);
        }

        // Lấy danh sách categories
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Truyền dữ liệu ra view
        model.addAttribute("lvsProjects", lvsProjects);
        model.addAttribute("lvsStatuses", LvsProject.LvsProjectStatus.values());
        model.addAttribute("lvsSelectedStatus", lvsStatus);
        model.addAttribute("lvsKeyword", lvsKeyword);
        model.addAttribute("lvsCategories", lvsCategories);
        model.addAttribute("lvsCategoryId", lvsCategoryId);
        model.addAttribute("lvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsProject/LvsList";
    }

    /**
     * Hiển thị chi tiết thông tin dự án
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin đầy đủ của dự án</li>
     * <li>Hiển thị thumbnail</li>
     * <li>Hiển thị gallery ảnh dự án</li>
     * <li>Hiển thị thông tin người tạo, danh mục</li>
     * <li>Hiển thị trạng thái duyệt</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsProject/LvsDetail/{id}
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsProject/LvsDetail/1
     * </p>
     * 
     * @param id    ID của dự án cần xem
     * @param model Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsProject/LvsDetail
     *         hoặc redirect về list nếu không tìm thấy
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsProject: LvsProject - Thông tin chi tiết dự án</li>
     *         </ul>
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewProjectDetail(@PathVariable Long id, Model model) {
        // Lấy thông tin dự án từ database
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        // Nếu không tìm thấy, redirect về danh sách
        if (lvsProject == null) {
            return "redirect:/LvsAdmin/LvsProject/LvsList";
        }

        // Fetch ALL reviews (including unapproved) for admin
        Pageable reviewPageable = PageRequest.of(0, 100); // Show more reviews for admin
        Page<LvsReview> lvsReviews = lvsReviewService.lvsGetReviewsByProject(id, reviewPageable);

        // Get review statistics
        Double lvsAverageRating = lvsReviewService.lvsGetAverageRating(id);
        java.util.Map<Integer, Long> lvsRatingDistribution = lvsReviewService.lvsGetRatingDistribution(id);

        // Count reviews by status
        long lvsPendingCount = lvsReviews.getContent().stream()
                .filter(r -> r.getLvsIsApproved() != null && !r.getLvsIsApproved())
                .count();
        long lvsApprovedCount = lvsReviews.getContent().stream()
                .filter(r -> r.getLvsIsApproved() != null && r.getLvsIsApproved())
                .count();

        // Truyền dữ liệu ra view
        model.addAttribute("lvsProject", lvsProject);
        model.addAttribute("LvsReviews", lvsReviews);
        model.addAttribute("LvsAverageRating", lvsAverageRating != null ? lvsAverageRating : 0.0);
        model.addAttribute("LvsRatingDistribution", lvsRatingDistribution);
        model.addAttribute("LvsPendingCount", lvsPendingCount);
        model.addAttribute("LvsApprovedCount", lvsApprovedCount);

        return "LvsAreas/LvsAdmin/LvsProject/LvsDetail";
    }

    /**
     * Duyệt dự án (chuyển trạng thái sang APPROVED)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Cập nhật trạng thái dự án thành APPROVED</li>
     * <li>Ghi lại admin đã duyệt</li>
     * <li>Lưu ghi chú duyệt (nếu có)</li>
     * <li>Cập nhật thời gian duyệt</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsProject/LvsApprove/{id}
     * </p>
     * 
     * @param id       ID của dự án cần duyệt
     * @param lvsNotes Ghi chú khi duyệt (optional)
     * @param model    Model để truyền thông báo
     * @return Redirect về trang chi tiết dự án
     */
    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveProject(@PathVariable Long id, @RequestParam(required = false) String lvsNotes,
            Model model) {
        try {
            // Lấy thông tin admin hiện tại
            LvsUser lvsAdmin = lvsUserService.lvsGetCurrentUser();

            // Duyệt dự án qua service
            lvsProjectService.lvsApproveProject(id, lvsAdmin.getLvsUserId(), lvsNotes);

            // Thêm thông báo thành công
            model.addAttribute("LvsSuccess", "Đã duyệt dự án!");
        } catch (Exception e) {
            // Thêm thông báo lỗi nếu có exception
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    /**
     * Từ chối dự án (chuyển trạng thái sang REJECTED)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Cập nhật trạng thái dự án thành REJECTED</li>
     * <li>Ghi lại admin đã từ chối</li>
     * <li>Lưu lý do từ chối (bắt buộc)</li>
     * <li>Cập nhật thời gian từ chối</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsProject/LvsReject/{id}
     * </p>
     * 
     * @param id        ID của dự án cần từ chối
     * @param lvsReason Lý do từ chối (required)
     * @param model     Model để truyền thông báo
     * @return Redirect về trang chi tiết dự án
     */
    @PostMapping("/LvsReject/{id}")
    public String lvsRejectProject(@PathVariable Long id, @RequestParam String lvsReason, Model model) {
        try {
            // Lấy thông tin admin hiện tại
            LvsUser lvsAdmin = lvsUserService.lvsGetCurrentUser();

            // Từ chối dự án qua service
            lvsProjectService.lvsRejectProject(id, lvsAdmin.getLvsUserId(), lvsReason);

            // Thêm thông báo thành công
            model.addAttribute("LvsSuccess", "Đã từ chối dự án!");
        } catch (Exception e) {
            // Thêm thông báo lỗi nếu có exception
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        // Redirect về trang chi tiết
        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    /**
     * Toggle trạng thái featured (nổi bật) của dự án
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nếu đang featured -> bỏ featured</li>
     * <li>Nếu chưa featured -> đánh dấu featured</li>
     * <li>Dự án featured sẽ hiển thị ưu tiên trên trang chủ</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsProject/LvsToggleFeatured/{id}
     * </p>
     * 
     * @param id ID của dự án cần toggle
     * @return Redirect về trang chi tiết dự án
     */
    @PostMapping("/LvsToggleFeatured/{id}")
    public String lvsToggleFeatured(@PathVariable Long id) {
        // Toggle trạng thái featured qua service
        lvsProjectService.lvsToggleFeatured(id);

        // Redirect về trang chi tiết để xem kết quả
        return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;
    }

    /**
     * Xóa dự án (hard delete)
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Xóa vĩnh viễn dự án khỏi database</li>
     * <li>Xóa tất cả ảnh liên quan (thumbnail + project images)</li>
     * <li>Không thể khôi phục sau khi xóa</li>
     * </ul>
     * 
     * <p>
     * Lưu ý:
     * </p>
     * <ul>
     * <li>Cần kiểm tra ràng buộc với orders, reviews trước khi xóa</li>
     * <li>Nên backup dữ liệu trước khi xóa</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsProject/LvsDelete/{id}
     * </p>
     * 
     * @param id    ID của dự án cần xóa
     * @param model Model để truyền thông báo
     * @return Redirect về trang danh sách
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteProject(@PathVariable Long id, Model model) {
        try {
            // Xóa dự án qua service (hard delete)
            lvsProjectService.lvsDeleteProject(id);

            // Thêm thông báo thành công
            model.addAttribute("LvsSuccess", "Đã xóa dự án!");
        } catch (Exception e) {
            // Thêm thông báo lỗi nếu có exception
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        // Redirect về trang danh sách
        return "redirect:/LvsAdmin/LvsProject/LvsList";
    }

    /**
     * Hiển thị form chỉnh sửa dự án
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin dự án hiện tại</li>
     * <li>Lấy danh sách categories để chọn</li>
     * <li>Hiển thị ảnh hiện tại</li>
     * <li>Cho phép thêm/xóa ảnh</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsProject/LvsEdit/{id}
     * </p>
     * 
     * @param id    ID của dự án cần chỉnh sửa
     * @param model Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsProject/LvsEdit
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsProject: LvsProject - Thông tin dự án hiện tại</li>
     *         <li>lvsCategories: List&lt;LvsCategory&gt; - Danh sách
     *         categories</li>
     *         </ul>
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditProjectForm(@PathVariable Long id, Model model) {
        // Lấy thông tin dự án
        LvsProject lvsProject = lvsProjectService.lvsGetProjectById(id);

        // Lấy danh sách categories
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Truyền dữ liệu ra view
        model.addAttribute("lvsProject", lvsProject);
        model.addAttribute("lvsCategories", lvsCategories);

        return "LvsAreas/LvsAdmin/LvsProject/LvsEdit";
    }

    /**
     * Xử lý submit form chỉnh sửa dự án
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Cập nhật thông tin dự án</li>
     * <li>Upload thumbnail mới (nếu có) và xóa thumbnail cũ</li>
     * <li>Upload ảnh dự án mới (nếu có)</li>
     * <li>Xóa ảnh dự án được chọn</li>
     * <li>Lưu danh sách ảnh dưới dạng JSON</li>
     * </ul>
     * 
     * <p>
     * Quy trình xử lý ảnh:
     * </p>
     * <ol>
     * <li>Lấy dự án hiện tại từ database</li>
     * <li>Xử lý thumbnail: Upload mới (nếu có) và xóa cũ</li>
     * <li>Parse danh sách ảnh hiện tại từ JSON</li>
     * <li>Xóa các ảnh được đánh dấu xóa</li>
     * <li>Upload ảnh mới và thêm vào danh sách</li>
     * <li>Serialize danh sách ảnh thành JSON và lưu</li>
     * </ol>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsProject/LvsEdit/{id}
     * </p>
     * 
     * @param id                 ID của dự án đang chỉnh sửa
     * @param lvsProject         Object LvsProject được binding từ form
     * @param lvsThumbnailFile   File thumbnail mới (optional)
     * @param lvsImageFiles      Array file ảnh dự án mới (optional)
     * @param lvsDeletedImages   Chuỗi các URL ảnh cần xóa, phân cách bởi dấu phẩy
     *                           (optional)
     * @param redirectAttributes Để truyền flash messages
     * @return Redirect về detail page nếu thành công, hoặc edit page nếu lỗi
     * 
     *         <p>
     *         Success: redirect:/LvsAdmin/LvsProject/LvsDetail/{id}
     *         </p>
     *         <p>
     *         Error: redirect:/LvsAdmin/LvsProject/LvsEdit/{id}
     *         </p>
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditProject(
            @PathVariable Long id,
            @ModelAttribute LvsProject lvsProject,
            @RequestParam(required = false) MultipartFile lvsThumbnailFile,
            @RequestParam(required = false) MultipartFile lvsProjectFile, // FIX: Add project file parameter
            @RequestParam(required = false) MultipartFile[] lvsImageFiles,
            @RequestParam(required = false) String lvsDeletedImages,
            RedirectAttributes redirectAttributes) {
        try {
            // Lấy dự án hiện tại từ database
            LvsProject lvsExistingProject = lvsProjectService.lvsGetProjectById(id);
            if (lvsExistingProject == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Không tìm thấy dự án!");
                return "redirect:/LvsAdmin/LvsProject/LvsList";
            }

            // ===== XỬ LÝ THUMBNAIL =====
            if (lvsThumbnailFile != null && !lvsThumbnailFile.isEmpty()) {
                // Xóa thumbnail cũ nếu tồn tại
                if (lvsExistingProject.getLvsThumbnailUrl() != null) {
                    lvsFileUploadService.lvsDeleteFile(lvsExistingProject.getLvsThumbnailUrl());
                }
                // Upload thumbnail mới
                String lvsThumbnailUrl = lvsFileUploadService.lvsSaveFile(lvsThumbnailFile, "projects");
                lvsProject.setLvsThumbnailUrl(lvsThumbnailUrl);
            } else {
                // Giữ nguyên thumbnail cũ
                lvsProject.setLvsThumbnailUrl(lvsExistingProject.getLvsThumbnailUrl());
            }

            // ===== XỬ LÝ PROJECT FILE =====
            if (lvsProjectFile != null && !lvsProjectFile.isEmpty()) {
                // Xóa file cũ nếu tồn tại
                if (lvsExistingProject.getLvsFileUrl() != null) {
                    lvsFileUploadService.lvsDeleteFile(lvsExistingProject.getLvsFileUrl());
                }
                // Upload file mới (ZIP/RAR)
                String lvsFileUrl = lvsFileUploadService.lvsSaveProjectFile(lvsProjectFile, "projects");
                lvsProject.setLvsFileUrl(lvsFileUrl);
            } else {
                // Giữ nguyên file cũ
                lvsProject.setLvsFileUrl(lvsExistingProject.getLvsFileUrl());
            }

            // ===== XỬ LÝ DANH SÁCH ẢNH DỰ ÁN =====
            List<String> lvsCurrentImages = new ArrayList<>();

            // Parse danh sách ảnh hiện tại từ JSON
            if (lvsExistingProject.getLvsImages() != null && !lvsExistingProject.getLvsImages().isEmpty()) {
                try {
                    ObjectMapper lvsMapper = new ObjectMapper();
                    lvsCurrentImages = lvsMapper.readValue(
                            lvsExistingProject.getLvsImages(),
                            lvsMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                } catch (Exception e) {
                    System.err.println("Error parsing existing images: " + e.getMessage());
                }
            }

            // Xóa các ảnh được đánh dấu xóa
            if (lvsDeletedImages != null && !lvsDeletedImages.isEmpty()) {
                List<String> lvsImagesToDelete = Arrays.asList(lvsDeletedImages.split(","));
                for (String lvsImageUrl : lvsImagesToDelete) {
                    // Xóa file vật lý
                    lvsFileUploadService.lvsDeleteFile(lvsImageUrl);
                    // Xóa khỏi danh sách
                    lvsCurrentImages.remove(lvsImageUrl);
                }
            }

            // Upload ảnh mới và thêm vào danh sách
            if (lvsImageFiles != null && lvsImageFiles.length > 0) {
                // Lọc các file hợp lệ (không null và không empty)
                List<MultipartFile> lvsValidFiles = Arrays.stream(lvsImageFiles)
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                if (!lvsValidFiles.isEmpty()) {
                    // Upload tất cả file hợp lệ
                    List<String> lvsNewImageUrls = lvsFileUploadService.lvsSaveFiles(lvsValidFiles, "projects");
                    // Thêm vào danh sách hiện tại
                    lvsCurrentImages.addAll(lvsNewImageUrls);
                }
            }

            // Serialize danh sách ảnh thành JSON và lưu
            if (!lvsCurrentImages.isEmpty()) {
                try {
                    ObjectMapper lvsMapper = new ObjectMapper();
                    String lvsImagesJson = lvsMapper.writeValueAsString(lvsCurrentImages);
                    lvsProject.setLvsImages(lvsImagesJson);
                } catch (Exception e) {
                    System.err.println("Error serializing images: " + e.getMessage());
                }
            } else {
                // Nếu không còn ảnh nào, set null
                lvsProject.setLvsImages(null);
            }

            // ===== CẬP NHẬT DỰ ÁN =====
            lvsProject.setLvsProjectId(id);
            lvsProject.setLvsUser(lvsExistingProject.getLvsUser()); // Giữ nguyên user

            // ✅ UPDATE ALL FIELDS (explicit binding like User controller)
            lvsExistingProject.setLvsProjectName(lvsProject.getLvsProjectName());
            lvsExistingProject.setLvsDescription(lvsProject.getLvsDescription());
            lvsExistingProject.setLvsPrice(lvsProject.getLvsPrice());
            lvsExistingProject.setLvsCategory(lvsProject.getLvsCategory());
            lvsExistingProject.setLvsStatus(lvsProject.getLvsStatus());
            lvsExistingProject.setLvsThumbnailUrl(lvsProject.getLvsThumbnailUrl());
            lvsExistingProject.setLvsFileUrl(lvsProject.getLvsFileUrl());
            lvsExistingProject.setLvsImages(lvsProject.getLvsImages());
            lvsExistingProject.setLvsDemoUrl(lvsProject.getLvsDemoUrl());
            lvsExistingProject.setLvsSourceCodeUrl(lvsProject.getLvsSourceCodeUrl());

            // ✅ DISCOUNT FIELDS
            lvsExistingProject.setLvsDiscountPercent(lvsProject.getLvsDiscountPercent());
            lvsExistingProject.setLvsIsOnSale(lvsProject.getLvsIsOnSale());
            lvsExistingProject.setLvsDiscountStartDate(lvsProject.getLvsDiscountStartDate());
            lvsExistingProject.setLvsDiscountEndDate(lvsProject.getLvsDiscountEndDate());

            lvsProjectService.lvsUpdateProject(lvsExistingProject);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("LvsSuccess", "Cập nhật dự án thành công!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + id;

        } catch (IOException e) {
            // Lỗi upload file
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi upload file: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsProject/LvsEdit/" + id;
        } catch (Exception e) {
            // Lỗi khác
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsProject/LvsEdit/" + id;
        }
    }

    /**
     * Hiển thị form thêm dự án mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Tạo object LvsProject rỗng</li>
     * <li>Lấy danh sách categories để chọn</li>
     * <li>Lấy danh sách users để chọn người tạo</li>
     * <li>Hiển thị form upload ảnh</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsProject/LvsAdd
     * </p>
     * 
     * @param model Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsProject/LvsCreate
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>lvsProject: LvsProject - Object rỗng để binding</li>
     *         <li>lvsCategories: List&lt;LvsCategory&gt; - Danh sách
     *         categories</li>
     *         <li>lvsUsers: List&lt;LvsUser&gt; - Danh sách users</li>
     *         </ul>
     */
    @GetMapping("/LvsAdd")
    public String lvsShowAddProjectForm(Model model) {
        // Lấy danh sách categories
        List<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories();

        // Lấy tất cả users (không phân trang)
        Pageable lvsPageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<LvsUser> lvsUserPage = lvsUserService.lvsGetAllUsers(lvsPageable);
        List<LvsUser> lvsUsers = lvsUserPage.getContent();

        // Truyền dữ liệu ra view
        model.addAttribute("lvsProject", new LvsProject());
        model.addAttribute("lvsCategories", lvsCategories);
        model.addAttribute("lvsUsers", lvsUsers);

        return "LvsAreas/LvsAdmin/LvsProject/LvsCreate";
    }

    /**
     * Xử lý submit form thêm dự án mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nhận dữ liệu từ form</li>
     * <li>Upload thumbnail (nếu có)</li>
     * <li>Upload nhiều ảnh dự án (nếu có)</li>
     * <li>Lưu danh sách ảnh dưới dạng JSON</li>
     * <li>Set trạng thái mặc định là APPROVED</li>
     * <li>Lưu dự án vào database</li>
     * </ul>
     * 
     * <p>
     * Quy trình xử lý:
     * </p>
     * <ol>
     * <li>Validate user tồn tại</li>
     * <li>Upload thumbnail nếu có</li>
     * <li>Upload multiple images nếu có</li>
     * <li>Serialize danh sách ảnh thành JSON</li>
     * <li>Set các thuộc tính mặc định (status, isApproved)</li>
     * <li>Lưu vào database</li>
     * </ol>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsProject/LvsAdd
     * </p>
     * 
     * @param lvsProject         Object LvsProject được binding từ form
     * @param lvsUserId          ID của user tạo dự án
     * @param lvsThumbnailFile   File thumbnail (optional)
     * @param lvsImageFiles      Array file ảnh dự án (optional)
     * @param redirectAttributes Để truyền flash messages
     * @return Redirect về detail page nếu thành công, hoặc add page nếu lỗi
     * 
     *         <p>
     *         Success: redirect:/LvsAdmin/LvsProject/LvsDetail/{id}
     *         </p>
     *         <p>
     *         Error: redirect:/LvsAdmin/LvsProject/LvsAdd
     *         </p>
     */
    @PostMapping("/LvsAdd")
    public String lvsAddProject(
            @ModelAttribute LvsProject lvsProject,
            @RequestParam Long lvsUserId,
            @RequestParam(required = false) MultipartFile lvsThumbnailFile,
            @RequestParam(required = false) MultipartFile[] lvsImageFiles,
            RedirectAttributes redirectAttributes) {
        try {
            // ===== VALIDATE USER =====
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            if (lvsUser == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Không tìm thấy người dùng!");
                return "redirect:/LvsAdmin/LvsProject/LvsAdd";
            }

            // ===== XỬ LÝ THUMBNAIL =====
            if (lvsThumbnailFile != null && !lvsThumbnailFile.isEmpty()) {
                String lvsThumbnailUrl = lvsFileUploadService.lvsSaveFile(lvsThumbnailFile, "projects");
                lvsProject.setLvsThumbnailUrl(lvsThumbnailUrl);
            }

            // ===== XỬ LÝ MULTIPLE IMAGES =====
            if (lvsImageFiles != null && lvsImageFiles.length > 0) {
                // Lọc các file hợp lệ
                List<MultipartFile> lvsValidFiles = Arrays.stream(lvsImageFiles)
                        .filter(file -> file != null && !file.isEmpty())
                        .collect(Collectors.toList());

                if (!lvsValidFiles.isEmpty()) {
                    // Upload tất cả file
                    List<String> lvsImageUrls = lvsFileUploadService.lvsSaveFiles(lvsValidFiles, "projects");

                    // Convert danh sách URL thành JSON array string
                    try {
                        ObjectMapper lvsMapper = new ObjectMapper();
                        String lvsImagesJson = lvsMapper.writeValueAsString(lvsImageUrls);
                        lvsProject.setLvsImages(lvsImagesJson);
                    } catch (Exception e) {
                        System.err.println("Error serializing images: " + e.getMessage());
                    }
                }
            }

            // ===== SET CÁC THUỘC TÍNH MẶC ĐỊNH =====
            lvsProject.setLvsUser(lvsUser);
            lvsProject.setLvsStatus(LvsProject.LvsProjectStatus.APPROVED); // Admin tạo -> tự động duyệt
            lvsProject.setLvsIsApproved(true);

            // ✅ DISCOUNT FIELDS are already bound via @ModelAttribute
            // lvsProject.lvsDiscountPercent, lvsIsOnSale, lvsDiscountStartDate,
            // lvsDiscountEndDate
            // are automatically populated from form

            // ===== LƯU VÀO DATABASE =====
            LvsProject lvsSavedProject = lvsProjectService.lvsSaveProject(lvsProject);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("LvsSuccess", "Thêm dự án thành công!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + lvsSavedProject.getLvsProjectId();

        } catch (IOException e) {
            // Lỗi upload file
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi upload file: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsProject/LvsAdd";
        } catch (Exception e) {
            // Lỗi khác
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsProject/LvsAdd";
        }
    }

    // ==================== REVIEW MANAGEMENT METHODS ====================

    /**
     * Approve a review
     */
    @PostMapping("/LvsReview/LvsApprove/{id}")
    public String lvsApproveReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            LvsReview review = lvsReviewService.lvsGetReviewById(id);
            if (review == null) {
                redirectAttributes.addFlashAttribute("lvsError", "Review not found!");
                return "redirect:/LvsAdmin/LvsReview/LvsList";
            }

            // Approve review
            lvsReviewService.lvsApproveReview(id);

            redirectAttributes.addFlashAttribute("lvsSuccess", "Review approved successfully!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + review.getLvsProject().getLvsProjectId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError", "Error: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }
    }

    /**
     * Hide a review
     */
    @PostMapping("/LvsReview/LvsHide/{id}")
    public String lvsHideReview(@PathVariable Long id, @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        try {
            LvsReview review = lvsReviewService.lvsGetReviewById(id);
            if (review == null) {
                redirectAttributes.addFlashAttribute("lvsError", "Review not found!");
                return "redirect:/LvsAdmin/LvsReview/LvsList";
            }

            // Hide review (set isApproved to false)
            lvsReviewService.lvsHideReview(id, reason);

            redirectAttributes.addFlashAttribute("lvsSuccess", "Review hidden successfully!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + review.getLvsProject().getLvsProjectId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError", "Error: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }
    }

    /**
     * Delete a review
     */
    @PostMapping("/LvsReview/LvsDelete/{id}")
    public String lvsDeleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            LvsReview review = lvsReviewService.lvsGetReviewById(id);
            if (review == null) {
                redirectAttributes.addFlashAttribute("lvsError", "Review not found!");
                return "redirect:/LvsAdmin/LvsReview/LvsList";
            }

            Long projectId = review.getLvsProject().getLvsProjectId();

            // Delete review
            lvsReviewService.lvsDeleteReview(id);

            redirectAttributes.addFlashAttribute("lvsSuccess", "Review deleted successfully!");
            return "redirect:/LvsAdmin/LvsProject/LvsDetail/" + projectId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("lvsError", "Error: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsReview/LvsList";
        }
    }
}
