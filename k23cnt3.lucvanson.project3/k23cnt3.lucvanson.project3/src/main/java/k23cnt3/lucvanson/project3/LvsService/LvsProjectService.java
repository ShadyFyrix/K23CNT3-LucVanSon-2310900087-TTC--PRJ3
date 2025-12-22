package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsEntity.LvsProject.LvsProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý dự án
 * Xử lý CRUD dự án, duyệt, từ chối, tìm kiếm
 */
public interface LvsProjectService {

    // Lấy dự án theo ID
    LvsProject lvsGetProjectById(Long lvsProjectId);

    // Lấy dự án theo ID với eager loading
    LvsProject lvsGetProjectByIdWithDetails(Long lvsProjectId);

    // Lấy tất cả dự án
    Page<LvsProject> lvsGetAllProjects(Pageable lvsPageable);

    // Lấy tất cả dự án với category và user eager loading (for forms)
    Page<LvsProject> lvsGetAllProjectsWithCategoryAndUser(Pageable lvsPageable);

    // Tìm kiếm dự án
    Page<LvsProject> lvsSearchProjects(String lvsKeyword, Pageable lvsPageable);

    // Lấy dự án theo danh mục
    Page<LvsProject> lvsGetProjectsByCategory(Integer lvsCategoryId, Pageable lvsPageable);

    // Lấy dự án theo trạng thái
    Page<LvsProject> lvsGetProjectsByStatus(String lvsStatus, Pageable lvsPageable);

    // Lấy dự án theo người đăng
    Page<LvsProject> lvsGetProjectsByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy dự án mới nhất
    List<LvsProject> lvsGetNewestProjects(Pageable lvsPageable);

    // Lấy dự án phổ biến nhất
    List<LvsProject> lvsGetPopularProjects(Pageable lvsPageable);

    // Lấy dự án nổi bật
    List<LvsProject> lvsGetFeaturedProjects(Pageable lvsPageable);

    // Lấy dự án đã mua gần đây
    List<LvsProject> lvsGetRecentPurchases(Long lvsUserId, int lvsLimit);

    // Lưu dự án
    LvsProject lvsSaveProject(LvsProject lvsProject);

    // Cập nhật dự án
    LvsProject lvsUpdateProject(LvsProject lvsProject);

    // Xóa dự án
    void lvsDeleteProject(Long lvsProjectId);

    // Xóa dự án với lý do
    void lvsDeleteProject(Long lvsProjectId, String lvsReason);

    // Duyệt dự án
    LvsProject lvsApproveProject(Long lvsProjectId, Long lvsAdminId, String lvsNotes);

    // Từ chối dự án
    LvsProject lvsRejectProject(Long lvsProjectId, Long lvsAdminId, String lvsReason);

    // Đánh dấu featured
    LvsProject lvsToggleFeatured(Long lvsProjectId);

    // Tăng lượt xem
    void lvsIncrementViewCount(Long lvsProjectId);

    // Tăng lượt mua
    void lvsIncrementPurchaseCount(Long lvsProjectId);

    // Tăng lượt tải
    void lvsIncrementDownloadCount(Long lvsProjectId);

    // Cập nhật rating trung bình
    void lvsUpdateProjectRating(Long lvsProjectId);

    // Kiểm tra user đã mua dự án chưa
    boolean lvsHasUserPurchasedProject(Long lvsUserId, Long lvsProjectId);

    // Đếm tổng số dự án
    Long lvsCountTotalProjects();

    // Đếm số dự án theo trạng thái
    Long lvsCountProjectsByStatus(String lvsStatus);

    // Đếm số dự án mới trong khoảng thời gian
    Long lvsCountNewProjects(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy top dự án bán chạy
    List<LvsProject> lvsGetTopSellingProjects(int lvsLimit);

    // Lấy dự án đang chờ duyệt
    Page<LvsProject> lvsGetPendingProjects(Pageable lvsPageable);

    // Lấy dự án đã được duyệt
    Page<LvsProject> lvsGetApprovedProjects(Pageable lvsPageable);

    // Lấy thống kê dự án
    Map<String, Long> lvsGetProjectStats();

    // Lấy dữ liệu biểu đồ thống kê dự án
    Map<String, Object> lvsGetProjectStatsChartData();
}