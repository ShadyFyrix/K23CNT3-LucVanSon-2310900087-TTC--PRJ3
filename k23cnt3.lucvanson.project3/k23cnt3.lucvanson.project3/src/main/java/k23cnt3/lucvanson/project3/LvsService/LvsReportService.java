package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsReport;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport.LvsReportStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport.LvsReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý báo cáo
 * Xử lý gửi, xem, xử lý báo cáo
 */
public interface LvsReportService {

    // Lấy báo cáo theo ID
    LvsReport lvsGetReportById(Long lvsReportId);

    // Lấy tất cả báo cáo
    Page<LvsReport> lvsGetAllReports(Pageable lvsPageable);

    // Lấy báo cáo theo user
    Page<LvsReport> lvsGetReportsByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy báo cáo theo status
    Page<LvsReport> lvsGetReportsByStatus(String lvsStatus, Pageable lvsPageable);

    // Lấy báo cáo theo type
    Page<LvsReport> lvsGetReportsByType(String lvsType, Pageable lvsPageable);

    // Lấy báo cáo theo status và type
    Page<LvsReport> lvsGetReportsByStatusAndType(String lvsStatus, String lvsType, Pageable lvsPageable);

    // Lưu báo cáo
    LvsReport lvsSaveReport(LvsReport lvsReport);

    // Cập nhật báo cáo
    LvsReport lvsUpdateReport(LvsReport lvsReport);

    // Xóa báo cáo
    void lvsDeleteReport(Long lvsReportId);

    // Xử lý báo cáo
    LvsReport lvsHandleReport(Long lvsReportId, Long lvsAdminId, LvsReportStatus lvsStatus,
            String lvsActionTaken, String lvsAdminNote);

    // Cập nhật trạng thái báo cáo
    LvsReport lvsUpdateReportStatus(Long lvsReportId, LvsReportStatus lvsStatus);

    // Gán LvsAdmin xử lý
    LvsReport lvsAssignReport(Long lvsReportId, Long lvsAdminId);

    // Đếm tổng số báo cáo
    Long lvsCountTotalReports();

    // Đếm số báo cáo theo trạng thái
    Long lvsCountReportsByStatus(String lvsStatus);

    // Đếm số báo cáo đang chờ xử lý
    Long lvsCountPendingReports();

    // Lấy thống kê báo cáo theo loại
    Map<String, Long> lvsGetReportStatsByType();

    // Lấy thống kê báo cáo theo trạng thái
    Map<String, Long> lvsGetReportStatsByStatus();

    // Lấy thống kê báo cáo theo thời gian
    Map<String, Long> lvsGetReportStatsByTime();

    // Gửi báo cáo tự động
    void lvsSendAutoReport(Long lvsTargetId, LvsReportType lvsType, String lvsReason, String lvsDetails);

    // ========== MODERATION PANEL METHODS ==========

    // Lấy tất cả báo cáo (không phân trang - cho moderation)
    List<LvsReport> lvsGetAllReports();

    // Lấy báo cáo theo status (không phân trang)
    List<LvsReport> lvsGetReportsByStatus(LvsReportStatus status);

    // Lấy báo cáo theo type (không phân trang)
    List<LvsReport> lvsGetReportsByType(LvsReportType type);

    // Xử lý báo cáo (moderation)
    void lvsResolveReport(Long reportId, k23cnt3.lucvanson.project3.LvsEntity.LvsUser handler,
            String adminNote, String actionTaken, LvsReportStatus newStatus);
}