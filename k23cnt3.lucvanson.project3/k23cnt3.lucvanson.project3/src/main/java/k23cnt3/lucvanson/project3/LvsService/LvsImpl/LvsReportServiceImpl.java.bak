package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsReport;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport.LvsReportStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport.LvsReportType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsReportRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation cho quản lý báo cáo
 * Xử lý gửi, xem, xử lý báo cáo
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsReportServiceImpl implements LvsReportService {

    private final LvsReportRepository lvsReportRepository;
    private final LvsUserRepository lvsUserRepository;

    /**
     * Lấy báo cáo theo ID
     * 
     * @param lvsReportId ID báo cáo
     * @return Báo cáo tìm thấy
     */
    @Override
    public LvsReport lvsGetReportById(Long lvsReportId) {
        return lvsReportRepository.findById(lvsReportId).orElse(null);
    }

    /**
     * Lấy tất cả báo cáo với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang báo cáo
     */
    @Override
    public Page<LvsReport> lvsGetAllReports(Pageable lvsPageable) {
        return lvsReportRepository.findAll(lvsPageable);
    }

    /**
     * Lấy báo cáo theo user
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang báo cáo
     */
    @Override
    public Page<LvsReport> lvsGetReportsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsReportRepository.findByLvsReporter_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy báo cáo theo status
     * 
     * @param lvsStatus   Trạng thái báo cáo
     * @param lvsPageable Thông tin phân trang
     * @return Trang báo cáo
     */
    @Override
    public Page<LvsReport> lvsGetReportsByStatus(String lvsStatus, Pageable lvsPageable) {
        LvsReportStatus lvsReportStatus = LvsReportStatus.valueOf(lvsStatus.toUpperCase());
        return lvsReportRepository.findByLvsStatus(lvsReportStatus, lvsPageable);
    }

    /**
     * Lấy báo cáo theo type
     * 
     * @param lvsType     Loại báo cáo
     * @param lvsPageable Thông tin phân trang
     * @return Trang báo cáo
     */
    @Override
    public Page<LvsReport> lvsGetReportsByType(String lvsType, Pageable lvsPageable) {
        LvsReportType lvsReportType = LvsReportType.valueOf(lvsType.toUpperCase());
        return lvsReportRepository.findByLvsReportType(lvsReportType, lvsPageable);
    }

    /**
     * Lấy báo cáo theo status và type
     * 
     * @param lvsStatus   Trạng thái báo cáo
     * @param lvsType     Loại báo cáo
     * @param lvsPageable Thông tin phân trang
     * @return Trang báo cáo
     */
    @Override
    public Page<LvsReport> lvsGetReportsByStatusAndType(String lvsStatus, String lvsType, Pageable lvsPageable) {
        LvsReportStatus lvsReportStatus = LvsReportStatus.valueOf(lvsStatus.toUpperCase());
        LvsReportType lvsReportType = LvsReportType.valueOf(lvsType.toUpperCase());
        return lvsReportRepository.findByLvsStatusAndLvsReportType(lvsReportStatus, lvsReportType, lvsPageable);
    }

    /**
     * Lưu báo cáo
     * 
     * @param lvsReport Thông tin báo cáo
     * @return Báo cáo đã lưu
     */
    @Override
    public LvsReport lvsSaveReport(LvsReport lvsReport) {
        lvsReport.setLvsCreatedAt(LocalDateTime.now());
        return lvsReportRepository.save(lvsReport);
    }

    /**
     * Cập nhật báo cáo
     * 
     * @param lvsReport Thông tin báo cáo cập nhật
     * @return Báo cáo đã cập nhật
     */
    @Override
    public LvsReport lvsUpdateReport(LvsReport lvsReport) {
        LvsReport lvsExistingReport = lvsGetReportById(lvsReport.getLvsReportId());
        if (lvsExistingReport != null) {
            lvsExistingReport.setLvsStatus(lvsReport.getLvsStatus());
            lvsExistingReport.setLvsAdminNote(lvsReport.getLvsAdminNote());
            lvsExistingReport.setLvsActionTaken(lvsReport.getLvsActionTaken());

            if (lvsReport.getLvsStatus() == LvsReportStatus.RESOLVED ||
                    lvsReport.getLvsStatus() == LvsReportStatus.REJECTED) {
                lvsExistingReport.setLvsResolvedAt(LocalDateTime.now());
            }

            return lvsReportRepository.save(lvsExistingReport);
        }
        return null;
    }

    /**
     * Xóa báo cáo
     * 
     * @param lvsReportId ID báo cáo
     */
    @Override
    public void lvsDeleteReport(Long lvsReportId) {
        lvsReportRepository.deleteById(lvsReportId);
    }

    /**
     * Xử lý báo cáo
     * 
     * @param lvsReportId    ID báo cáo
     * @param lvsAdminId     ID LvsAdmin xử lý
     * @param lvsStatus      Trạng thái mới
     * @param lvsActionTaken Hành động đã thực hiện
     * @param lvsAdminNote   Ghi chú của LvsAdmin
     * @return Báo cáo đã xử lý
     */
    @Override
    public LvsReport lvsHandleReport(Long lvsReportId, Long lvsAdminId, LvsReportStatus lvsStatus,
            String lvsActionTaken, String lvsAdminNote) {
        LvsReport lvsReport = lvsGetReportById(lvsReportId);
        if (lvsReport != null) {
            lvsReport.setLvsStatus(lvsStatus);
            lvsReport.setLvsActionTaken(lvsActionTaken);
            lvsReport.setLvsAdminNote(lvsAdminNote);

            var lvsAdmin = lvsUserRepository.findById(lvsAdminId).orElse(null);
            lvsReport.setLvsAdminHandler(lvsAdmin);

            if (lvsStatus == LvsReportStatus.RESOLVED || lvsStatus == LvsReportStatus.REJECTED) {
                lvsReport.setLvsResolvedAt(LocalDateTime.now());
            }

            return lvsReportRepository.save(lvsReport);
        }
        return null;
    }

    /**
     * Cập nhật trạng thái báo cáo
     * 
     * @param lvsReportId ID báo cáo
     * @param lvsStatus   Trạng thái mới
     * @return Báo cáo đã cập nhật
     */
    @Override
    public LvsReport lvsUpdateReportStatus(Long lvsReportId, LvsReportStatus lvsStatus) {
        LvsReport lvsReport = lvsGetReportById(lvsReportId);
        if (lvsReport != null) {
            lvsReport.setLvsStatus(lvsStatus);

            if (lvsStatus == LvsReportStatus.RESOLVED || lvsStatus == LvsReportStatus.REJECTED) {
                lvsReport.setLvsResolvedAt(LocalDateTime.now());
            }

            return lvsReportRepository.save(lvsReport);
        }
        return null;
    }

    /**
     * Gán LvsAdmin xử lý
     * 
     * @param lvsReportId ID báo cáo
     * @param lvsAdminId  ID LvsAdmin
     * @return Báo cáo đã gán
     */
    @Override
    public LvsReport lvsAssignReport(Long lvsReportId, Long lvsAdminId) {
        LvsReport lvsReport = lvsGetReportById(lvsReportId);
        if (lvsReport != null) {
            var lvsAdmin = lvsUserRepository.findById(lvsAdminId).orElse(null);
            lvsReport.setLvsAdminHandler(lvsAdmin);
            lvsReport.setLvsStatus(LvsReportStatus.UNDER_REVIEW);
            return lvsReportRepository.save(lvsReport);
        }
        return null;
    }

    /**
     * Đếm tổng số báo cáo
     * 
     * @return Tổng số báo cáo
     */
    @Override
    public Long lvsCountTotalReports() {
        return lvsReportRepository.count();
    }

    /**
     * Đếm số báo cáo theo trạng thái
     * 
     * @param lvsStatus Trạng thái cần đếm
     * @return Số báo cáo
     */
    @Override
    public Long lvsCountReportsByStatus(String lvsStatus) {
        LvsReportStatus lvsReportStatus = LvsReportStatus.valueOf(lvsStatus.toUpperCase());
        return lvsReportRepository.countByLvsStatus(lvsReportStatus);
    }

    /**
     * Đếm số báo cáo đang chờ xử lý
     * 
     * @return Số báo cáo chờ xử lý
     */
    @Override
    public Long lvsCountPendingReports() {
        return lvsReportRepository.countByLvsStatus(LvsReportStatus.PENDING);
    }

    /**
     * Lấy thống kê báo cáo theo loại
     * 
     * @return Map thống kê theo loại
     */
    @Override
    public Map<String, Long> lvsGetReportStatsByType() {
        Map<String, Long> lvsStats = new HashMap<>();

        for (LvsReportType lvsType : LvsReportType.values()) {
            Long lvsCount = lvsReportRepository.countByLvsReportType(lvsType);
            lvsStats.put(lvsType.name(), lvsCount);
        }

        return lvsStats;
    }

    /**
     * Lấy thống kê báo cáo theo trạng thái
     * 
     * @return Map thống kê theo trạng thái
     */
    @Override
    public Map<String, Long> lvsGetReportStatsByStatus() {
        Map<String, Long> lvsStats = new HashMap<>();

        for (LvsReportStatus lvsStatus : LvsReportStatus.values()) {
            Long lvsCount = lvsReportRepository.countByLvsStatus(lvsStatus);
            lvsStats.put(lvsStatus.name(), lvsCount);
        }

        return lvsStats;
    }

    /**
     * Lấy thống kê báo cáo theo thời gian
     * 
     * @return Map thống kê theo thời gian
     */
    @Override
    public Map<String, Long> lvsGetReportStatsByTime() {
        Map<String, Long> lvsStats = new HashMap<>();

        LocalDate lvsToday = LocalDate.now();

        // Hôm nay
        Long lvsTodayCount = lvsReportRepository.countByLvsCreatedAtBetween(
                lvsToday.atStartOfDay(), lvsToday.atTime(23, 59, 59));
        lvsStats.put("TODAY", lvsTodayCount);

        // Tuần này
        LocalDate lvsWeekStart = lvsToday.minusDays(lvsToday.getDayOfWeek().getValue() - 1);
        Long lvsThisWeekCount = lvsReportRepository.countByLvsCreatedAtBetween(
                lvsWeekStart.atStartOfDay(), lvsToday.atTime(23, 59, 59));
        lvsStats.put("THIS_WEEK", lvsThisWeekCount);

        // Tháng này
        LocalDate lvsMonthStart = lvsToday.withDayOfMonth(1);
        Long lvsThisMonthCount = lvsReportRepository.countByLvsCreatedAtBetween(
                lvsMonthStart.atStartOfDay(), lvsToday.atTime(23, 59, 59));
        lvsStats.put("THIS_MONTH", lvsThisMonthCount);

        return lvsStats;
    }

    /**
     * Gửi báo cáo tự động
     * 
     * @param lvsTargetId ID đối tượng bị báo cáo
     * @param lvsType     Loại báo cáo
     * @param lvsReason   Lý do báo cáo
     * @param lvsDetails  Chi tiết báo cáo
     */
    @Override
    public void lvsSendAutoReport(Long lvsTargetId, LvsReportType lvsType, String lvsReason, String lvsDetails) {
        // TODO: Tạo báo cáo tự động từ hệ thống
        LvsReport lvsReport = new LvsReport();
        lvsReport.setLvsReportType(lvsType);
        lvsReport.setLvsTargetId(lvsTargetId);
        lvsReport.setLvsReason(lvsReason);
        lvsReport.setLvsDetails(lvsDetails);
        lvsReport.setLvsStatus(LvsReportStatus.PENDING);
        lvsReport.setLvsCreatedAt(LocalDateTime.now());

        // Tìm LvsAdmin đầu tiên để gán là reporter
        var lvsAdmin = lvsUserRepository.findFirstByLvsRoleOrderByLvsUserIdAsc(LvsUser.LvsRole.ADMIN).orElse(null);
        if (lvsAdmin != null) {
            lvsReport.setLvsReporter(lvsAdmin);
            lvsReportRepository.save(lvsReport);
        }
    }
}