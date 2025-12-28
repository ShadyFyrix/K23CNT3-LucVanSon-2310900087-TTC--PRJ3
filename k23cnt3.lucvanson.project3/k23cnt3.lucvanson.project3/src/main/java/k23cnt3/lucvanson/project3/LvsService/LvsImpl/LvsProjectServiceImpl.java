package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsEntity.LvsProject.LvsProjectStatus;
import k23cnt3.lucvanson.project3.LvsRepository.LvsProjectRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation cho quản lý dự án
 * Xử lý CRUD dự án, duyệt, từ chối, tìm kiếm
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsProjectServiceImpl implements LvsProjectService {

    private final LvsProjectRepository lvsProjectRepository;

    /**
     * Lấy dự án theo ID
     * 
     * @param lvsProjectId ID dự án
     * @return Dự án tìm thấy
     */
    @Override
    public LvsProject lvsGetProjectById(Long lvsProjectId) {
        return lvsProjectRepository.findById(lvsProjectId).orElse(null);
    }

    /**
     * Lấy dự án theo ID với eager loading
     * 
     * @param lvsProjectId ID dự án
     * @return Dự án với category và user đã load
     */
    @Override
    public LvsProject lvsGetProjectByIdWithDetails(Long lvsProjectId) {
        return lvsProjectRepository.findWithDetailsByLvsProjectId(lvsProjectId).orElse(null);
    }

    /**
     * Lấy tất cả dự án với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án
     */
    @Override
    public Page<LvsProject> lvsGetAllProjects(Pageable lvsPageable) {
        return lvsProjectRepository.findAll(lvsPageable);
    }

    /**
     * Lấy tất cả dự án với category và user eager loading
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án với category và user đã load
     */
    @Override
    public Page<LvsProject> lvsGetAllProjectsWithCategoryAndUser(Pageable lvsPageable) {
        return lvsProjectRepository.findAllWithCategoryAndUser(lvsPageable);
    }

    /**
     * Tìm kiếm dự án theo keyword
     * 
     * @param lvsKeyword  Từ khóa tìm kiếm
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án tìm thấy
     */
    @Override
    public Page<LvsProject> lvsSearchProjects(String lvsKeyword, Pageable lvsPageable) {
        // Sử dụng phương thức searchProjects đã có trong repository
        return lvsProjectRepository.searchProjects(lvsKeyword, lvsPageable);
    }

    /**
     * Lấy dự án theo danh mục
     * 
     * @param lvsCategoryId ID danh mục
     * @param lvsPageable   Thông tin phân trang
     * @return Trang dự án
     */
    @Override
    public Page<LvsProject> lvsGetProjectsByCategory(Integer lvsCategoryId, Pageable lvsPageable) {
        return lvsProjectRepository.findByLvsCategory_LvsCategoryId(lvsCategoryId, lvsPageable);
    }

    /**
     * Lấy dự án theo trạng thái
     * 
     * @param lvsStatus   Trạng thái cần lọc
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án
     */
    @Override
    public Page<LvsProject> lvsGetProjectsByStatus(String lvsStatus, Pageable lvsPageable) {
        LvsProjectStatus lvsProjectStatus = LvsProjectStatus.valueOf(lvsStatus.toUpperCase());
        return lvsProjectRepository.findByLvsStatus(lvsProjectStatus, lvsPageable);
    }

    /**
     * Lấy dự án theo danh mục và trạng thái
     * 
     * @param lvsCategoryId ID danh mục
     * @param lvsStatus     Trạng thái dự án
     * @param lvsPageable   Thông tin phân trang
     * @return Trang dự án
     */
    @Override
    public Page<LvsProject> lvsGetProjectsByCategoryAndStatus(Integer lvsCategoryId, LvsProjectStatus lvsStatus,
            Pageable lvsPageable) {
        return lvsProjectRepository.findByLvsCategory_LvsCategoryIdAndLvsStatus(lvsCategoryId, lvsStatus, lvsPageable);
    }

    /**
     * Lấy dự án theo người đăng
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án
     */
    @Override
    public Page<LvsProject> lvsGetProjectsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsProjectRepository.findByLvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy dự án user đã mua (from completed orders)
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án đã mua
     */
    @Override
    public Page<LvsProject> lvsGetUserPurchasedProjects(Long lvsUserId, Pageable lvsPageable) {
        // Get all purchased projects (no pagination from repository)
        List<LvsProject> purchasedProjects = lvsProjectRepository.findPurchasedProjectsByUser(lvsUserId);

        // Manual pagination
        int start = (int) lvsPageable.getOffset();
        int end = Math.min((start + lvsPageable.getPageSize()), purchasedProjects.size());

        List<LvsProject> pageContent = purchasedProjects.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(
                pageContent,
                lvsPageable,
                purchasedProjects.size());
    }

    /**
     * Lấy dự án mới nhất
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Danh sách dự án mới nhất
     */
    @Override
    public List<LvsProject> lvsGetNewestProjects(Pageable lvsPageable) {
        // Lấy dự án đã APPROVED và sắp xếp theo ngày tạo
        return lvsProjectRepository.findByLvsStatusOrderByLvsCreatedAtDesc(
                LvsProjectStatus.APPROVED, lvsPageable).getContent();
    }

    /**
     * Lấy dự án phổ biến nhất
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Danh sách dự án phổ biến
     */
    @Override
    public List<LvsProject> lvsGetPopularProjects(Pageable lvsPageable) {
        // Lấy dự án đã APPROVED và sắp xếp theo lượt xem
        return lvsProjectRepository.findByLvsStatusOrderByLvsViewCountDesc(
                LvsProjectStatus.APPROVED, lvsPageable).getContent();
    }

    /**
     * Lấy dự án nổi bật
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Danh sách dự án nổi bật
     */
    @Override
    public List<LvsProject> lvsGetFeaturedProjects(Pageable lvsPageable) {
        // Chỉ lấy dự án đã approved và được đánh dấu featured
        Page<LvsProject> featuredProjects = lvsProjectRepository.findByLvsIsFeaturedTrue(lvsPageable);
        // Lọc chỉ lấy dự án đã approved
        return featuredProjects.getContent().stream()
                .filter(project -> project.getLvsStatus() == LvsProjectStatus.APPROVED)
                .collect(Collectors.toList());
    }

    /**
     * Lấy dự án với ưu tiên featured
     * Featured projects hiển thị trước, sau đó mới đến newest projects
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Danh sách dự án với featured priority
     */
    @Override
    public List<LvsProject> lvsGetFeaturedAndNewestProjects(Pageable lvsPageable) {
        int requestedSize = lvsPageable.getPageSize();

        // 1. Lấy featured projects (APPROVED và featured = true, sorted by newest)
        List<LvsProject> featuredProjects = lvsProjectRepository
                .findByLvsStatusAndLvsIsFeaturedOrderByLvsCreatedAtDesc(
                        LvsProjectStatus.APPROVED, true);

        // 2. Lấy newest projects (APPROVED và featured = false, sorted by newest)
        // Tính số lượng cần lấy thêm
        int remainingSize = Math.max(0, requestedSize - featuredProjects.size());
        List<LvsProject> newestProjects = new ArrayList<>();

        if (remainingSize > 0) {
            Pageable newestPageable = PageRequest.of(0, remainingSize);
            Page<LvsProject> newestPage = lvsProjectRepository
                    .findByLvsStatusAndLvsIsFeaturedOrderByLvsCreatedAtDesc(
                            LvsProjectStatus.APPROVED, false, newestPageable);
            newestProjects = newestPage.getContent();
        }

        // 3. Combine: featured first, then newest
        List<LvsProject> result = new ArrayList<>(featuredProjects);
        result.addAll(newestProjects);

        // 4. Return only requested amount
        return result.stream()
                .limit(requestedSize)
                .collect(Collectors.toList());
    }

    /**
     * Lấy dự án đã mua gần đây
     * 
     * @param lvsUserId ID người dùng
     * @param lvsLimit  Giới hạn số lượng
     * @return Danh sách dự án đã mua
     */
    @Override
    public List<LvsProject> lvsGetRecentPurchases(Long lvsUserId, int lvsLimit) {
        // Sử dụng phương thức findPurchasedProjectsByUser từ repository
        List<LvsProject> purchasedProjects = lvsProjectRepository.findPurchasedProjectsByUser(lvsUserId);

        // Sắp xếp theo thời gian mua (giả sử có trường createdAt trong order)
        // Nếu không có, có thể sắp xếp theo ID
        return purchasedProjects.stream()
                .sorted(Comparator.comparing(LvsProject::getLvsCreatedAt).reversed())
                .limit(lvsLimit)
                .collect(Collectors.toList());
    }

    /**
     * Lưu dự án
     * 
     * @param lvsProject Thông tin dự án
     * @return Dự án đã lưu
     */
    @Override
    public LvsProject lvsSaveProject(LvsProject lvsProject) {
        // Đặt trạng thái mặc định là PENDING khi tạo mới
        if (lvsProject.getLvsStatus() == null) {
            lvsProject.setLvsStatus(LvsProjectStatus.PENDING);
        }

        lvsProject.setLvsCreatedAt(LocalDateTime.now());
        lvsProject.setLvsUpdatedAt(LocalDateTime.now());

        // Đặt giá trị mặc định cho các trường
        if (lvsProject.getLvsViewCount() == null) {
            lvsProject.setLvsViewCount(0);
        }
        if (lvsProject.getLvsPurchaseCount() == null) {
            lvsProject.setLvsPurchaseCount(0);
        }
        if (lvsProject.getLvsDownloadCount() == null) {
            lvsProject.setLvsDownloadCount(0);
        }
        if (lvsProject.getLvsRating() == null) {
            lvsProject.setLvsRating(0.0);
        }
        if (lvsProject.getLvsReviewCount() == null) {
            lvsProject.setLvsReviewCount(0);
        }
        if (lvsProject.getLvsIsFeatured() == null) {
            lvsProject.setLvsIsFeatured(false);
        }
        if (lvsProject.getLvsIsApproved() == null) {
            lvsProject.setLvsIsApproved(false);
        }

        return lvsProjectRepository.save(lvsProject);
    }

    /**
     * Cập nhật dự án
     * 
     * @param lvsProject Thông tin dự án cập nhật
     * @return Dự án đã cập nhật
     */
    @Override
    public LvsProject lvsUpdateProject(LvsProject lvsProject) {
        LvsProject lvsExistingProject = lvsGetProjectById(lvsProject.getLvsProjectId());
        if (lvsExistingProject != null) {
            lvsExistingProject.setLvsProjectName(lvsProject.getLvsProjectName());
            lvsExistingProject.setLvsDescription(lvsProject.getLvsDescription());
            lvsExistingProject.setLvsPrice(lvsProject.getLvsPrice());
            lvsExistingProject.setLvsCategory(lvsProject.getLvsCategory());
            lvsExistingProject.setLvsTags(lvsProject.getLvsTags());
            lvsExistingProject.setLvsThumbnailUrl(lvsProject.getLvsThumbnailUrl());
            lvsExistingProject.setLvsImages(lvsProject.getLvsImages());
            lvsExistingProject.setLvsFileUrl(lvsProject.getLvsFileUrl());
            lvsExistingProject.setLvsDemoUrl(lvsProject.getLvsDemoUrl());
            lvsExistingProject.setLvsSourceCodeUrl(lvsProject.getLvsSourceCodeUrl());
            lvsExistingProject.setLvsStatus(lvsProject.getLvsStatus()); // FIX: Update status
            lvsExistingProject.setLvsUpdatedAt(LocalDateTime.now());
            return lvsProjectRepository.save(lvsExistingProject);
        }
        return null;
    }

    /**
     * Xóa dự án
     * 
     * @param lvsProjectId ID dự án
     */
    @Override
    public void lvsDeleteProject(Long lvsProjectId) {
        lvsProjectRepository.deleteById(lvsProjectId);
    }

    /**
     * Xóa dự án với lý do
     * 
     * @param lvsProjectId ID dự án
     * @param lvsReason    Lý do xóa
     */
    @Override
    public void lvsDeleteProject(Long lvsProjectId, String lvsReason) {
        LvsProject lvsProject = lvsGetProjectById(lvsProjectId);
        if (lvsProject != null) {
            // Thay vì xóa cứng, đánh dấu là REJECTED
            lvsProject.setLvsStatus(LvsProjectStatus.REJECTED);
            lvsProject.setLvsUpdatedAt(LocalDateTime.now());
            lvsProjectRepository.save(lvsProject);
        }
    }

    /**
     * Duyệt dự án
     * 
     * @param lvsProjectId ID dự án
     * @param lvsAdminId   ID LvsAdmin duyệt
     * @param lvsNotes     Ghi chú duyệt
     * @return Dự án đã duyệt
     */
    @Override
    public LvsProject lvsApproveProject(Long lvsProjectId, Long lvsAdminId, String lvsNotes) {
        LvsProject lvsProject = lvsGetProjectById(lvsProjectId);
        if (lvsProject != null) {
            lvsProject.setLvsStatus(LvsProjectStatus.APPROVED);
            lvsProject.setLvsIsApproved(true);
            lvsProject.setLvsUpdatedAt(LocalDateTime.now());
            return lvsProjectRepository.save(lvsProject);
        }
        return null;
    }

    /**
     * Từ chối dự án
     * 
     * @param lvsProjectId ID dự án
     * @param lvsAdminId   ID LvsAdmin từ chối
     * @param lvsReason    Lý do từ chối
     * @return Dự án đã từ chối
     */
    @Override
    public LvsProject lvsRejectProject(Long lvsProjectId, Long lvsAdminId, String lvsReason) {
        LvsProject lvsProject = lvsGetProjectById(lvsProjectId);
        if (lvsProject != null) {
            lvsProject.setLvsStatus(LvsProjectStatus.REJECTED);
            lvsProject.setLvsIsApproved(false);
            lvsProject.setLvsUpdatedAt(LocalDateTime.now());
            return lvsProjectRepository.save(lvsProject);
        }
        return null;
    }

    /**
     * Đánh dấu featured
     * 
     * @param lvsProjectId ID dự án
     * @return Dự án đã cập nhật
     */
    @Override
    public LvsProject lvsToggleFeatured(Long lvsProjectId) {
        LvsProject lvsProject = lvsGetProjectById(lvsProjectId);
        if (lvsProject != null) {
            lvsProject.setLvsIsFeatured(!lvsProject.getLvsIsFeatured());
            lvsProject.setLvsUpdatedAt(LocalDateTime.now());
            return lvsProjectRepository.save(lvsProject);
        }
        return null;
    }

    /**
     * Tăng lượt xem
     * 
     * @param lvsProjectId ID dự án
     */
    @Override
    public void lvsIncrementViewCount(Long lvsProjectId) {
        // Sử dụng phương thức từ repository để tối ưu hiệu suất
        lvsProjectRepository.incrementViewCount(lvsProjectId);
    }

    /**
     * Tăng lượt mua
     * 
     * @param lvsProjectId ID dự án
     */
    @Override
    public void lvsIncrementPurchaseCount(Long lvsProjectId) {
        // Sử dụng phương thức từ repository để tối ưu hiệu suất
        lvsProjectRepository.incrementPurchaseCount(lvsProjectId);
    }

    /**
     * Tăng lượt tải
     * 
     * @param lvsProjectId ID dự án
     */
    @Override
    public void lvsIncrementDownloadCount(Long lvsProjectId) {
        // Sử dụng phương thức từ repository để tối ưu hiệu suất
        lvsProjectRepository.incrementDownloadCount(lvsProjectId);
    }

    /**
     * Cập nhật rating trung bình
     * 
     * @param lvsProjectId ID dự án
     */
    @Override
    public void lvsUpdateProjectRating(Long lvsProjectId) {
        // Phương thức này cần được tính toán từ các review
        // Tạm thời để trống, cần có ReviewService để tính toán
        // Giả sử đã tính được rating và reviewCount
        // lvsProjectRepository.updateRating(projectId, newRating, newReviewCount);
    }

    /**
     * Kiểm tra user đã mua dự án chưa
     * 
     * @param lvsUserId    ID người dùng
     * @param lvsProjectId ID dự án
     * @return true nếu đã mua
     */
    @Override
    public boolean lvsHasUserPurchasedProject(Long lvsUserId, Long lvsProjectId) {
        List<LvsProject> purchasedProjects = lvsProjectRepository.findPurchasedProjectsByUser(lvsUserId);
        return purchasedProjects.stream()
                .anyMatch(project -> project.getLvsProjectId().equals(lvsProjectId));
    }

    /**
     * Đếm tổng số dự án
     * 
     * @return Tổng số dự án
     */
    @Override
    public Long lvsCountTotalProjects() {
        return lvsProjectRepository.count();
    }

    /**
     * Đếm số dự án theo trạng thái
     * 
     * @param lvsStatus Trạng thái cần đếm
     * @return Số dự án
     */
    @Override
    public Long lvsCountProjectsByStatus(String lvsStatus) {
        LvsProjectStatus lvsProjectStatus = LvsProjectStatus.valueOf(lvsStatus.toUpperCase());
        return lvsProjectRepository.countByLvsStatus(lvsProjectStatus);
    }

    /**
     * Đếm số dự án mới trong khoảng thời gian
     * 
     * @param lvsStartDate Ngày bắt đầu
     * @param lvsEndDate   Ngày kết thúc
     * @return Số dự án mới
     */
    @Override
    public Long lvsCountNewProjects(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        return lvsProjectRepository.countByLvsCreatedAtBetween(
                lvsStartDate.atStartOfDay(), lvsEndDate.atTime(23, 59, 59));
    }

    /**
     * Lấy top dự án bán chạy
     * 
     * @param lvsLimit Giới hạn số lượng
     * @return Danh sách dự án bán chạy
     */
    @Override
    public List<LvsProject> lvsGetTopSellingProjects(int lvsLimit) {
        // Lấy dự án đã APPROVED và sắp xếp theo lượt mua
        Pageable pageable = Pageable.ofSize(lvsLimit);
        return lvsProjectRepository.findByLvsStatusOrderByLvsPurchaseCountDesc(
                LvsProjectStatus.APPROVED, pageable).getContent();
    }

    /**
     * Lấy dự án đang chờ duyệt
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án chờ duyệt
     */
    @Override
    public Page<LvsProject> lvsGetPendingProjects(Pageable lvsPageable) {
        return lvsProjectRepository.findByLvsStatus(LvsProjectStatus.PENDING, lvsPageable);
    }

    /**
     * Lấy dự án đã được duyệt
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang dự án đã duyệt
     */
    @Override
    public Page<LvsProject> lvsGetApprovedProjects(Pageable lvsPageable) {
        return lvsProjectRepository.findByLvsStatus(LvsProjectStatus.APPROVED, lvsPageable);
    }

    /**
     * Lấy thống kê dự án
     * 
     * @return Map thống kê dự án
     */
    @Override
    public Map<String, Long> lvsGetProjectStats() {
        Map<String, Long> lvsStats = new HashMap<>();

        for (LvsProjectStatus lvsStatus : LvsProjectStatus.values()) {
            Long lvsCount = lvsProjectRepository.countByLvsStatus(lvsStatus);
            lvsStats.put(lvsStatus.name(), lvsCount);
        }

        return lvsStats;
    }

    /**
     * Lấy dữ liệu biểu đồ thống kê dự án
     * 
     * @return Dữ liệu biểu đồ
     */
    @Override
    public Map<String, Object> lvsGetProjectStatsChartData() {
        Map<String, Object> lvsChartData = new HashMap<>();
        Map<String, Long> lvsStats = lvsGetProjectStats();

        lvsChartData.put("labels", lvsStats.keySet());
        lvsChartData.put("data", lvsStats.values());

        return lvsChartData;
    }

    // ========== MODERATION METHODS IMPLEMENTATION ==========

    /**
     * Hide project (set lvsIsApproved = false)
     */
    @Override
    public void lvsHideProject(Long projectId) {
        LvsProject project = lvsProjectRepository.findById(projectId).orElse(null);
        if (project != null) {
            project.setLvsIsApproved(false);
            lvsProjectRepository.save(project);
        }
    }

    /**
     * Show project (set lvsIsApproved = true)
     */
    @Override
    public void lvsShowProject(Long projectId) {
        LvsProject project = lvsProjectRepository.findById(projectId).orElse(null);
        if (project != null) {
            project.setLvsIsApproved(true);
            lvsProjectRepository.save(project);
        }
    }
}