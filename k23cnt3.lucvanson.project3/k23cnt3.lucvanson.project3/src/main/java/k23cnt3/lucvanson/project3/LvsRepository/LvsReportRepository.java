package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsReport;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport.LvsReportStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport.LvsReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho entity LvsReport
 * Xử lý truy vấn liên quan đến báo cáo
 */
@Repository
public interface LvsReportRepository extends JpaRepository<LvsReport, Long> {

    // Tìm report theo reporter
    List<LvsReport> findByLvsReporter_LvsUserId(Long lvsUserId);
    Page<LvsReport> findByLvsReporter_LvsUserId(Long lvsUserId, Pageable pageable);

    // Tìm report theo status
    List<LvsReport> findByLvsStatus(LvsReportStatus lvsStatus);
    Page<LvsReport> findByLvsStatus(LvsReportStatus lvsStatus, Pageable pageable);

    // Tìm report theo type
    List<LvsReport> findByLvsReportType(LvsReportType lvsReportType);
    Page<LvsReport> findByLvsReportType(LvsReportType lvsReportType, Pageable pageable);

    // Tìm report theo status và type
    List<LvsReport> findByLvsStatusAndLvsReportType(LvsReportStatus lvsStatus, LvsReportType lvsReportType);
    Page<LvsReport> findByLvsStatusAndLvsReportType(LvsReportStatus lvsStatus, LvsReportType lvsReportType, Pageable pageable);

    // Tìm report theo target
    List<LvsReport> findByLvsTargetId(Long lvsTargetId);
    Page<LvsReport> findByLvsTargetId(Long lvsTargetId, Pageable pageable);

    // Tìm report theo LvsAdmin handler
    List<LvsReport> findByLvsAdminHandler_LvsUserId(Long lvsUserId);
    Page<LvsReport> findByLvsAdminHandler_LvsUserId(Long lvsUserId, Pageable pageable);

    // Đếm report theo status
    Long countByLvsStatus(LvsReportStatus lvsStatus);

    // Đếm report theo type
    Long countByLvsReportType(LvsReportType lvsReportType);

    // Đếm report theo reporter
    Long countByLvsReporter_LvsUserId(Long lvsUserId);

    // Đếm report trong khoảng thời gian
    Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Lấy report mới nhất
    List<LvsReport> findByOrderByLvsCreatedAtDesc();
    Page<LvsReport> findByOrderByLvsCreatedAtDesc(Pageable pageable);

    // Cập nhật status
    @Modifying
    @Query("UPDATE LvsReport r SET r.lvsStatus = :status WHERE r.lvsReportId = :reportId")
    void updateStatus(@Param("reportId") Long reportId, @Param("status") LvsReportStatus status);

    // Gán LvsAdmin handler
    @Modifying
    @Query("UPDATE LvsReport r SET r.lvsAdminHandler.lvsUserId = :adminId WHERE r.lvsReportId = :reportId")
    void assignAdmin(@Param("reportId") Long reportId, @Param("adminId") Long adminId);

    // Cập nhật resolved
    @Modifying
    @Query("UPDATE LvsReport r SET r.lvsStatus = 'RESOLVED', r.lvsResolvedAt = :resolvedAt, r.lvsActionTaken = :actionTaken, r.lvsAdminNote = :adminNote WHERE r.lvsReportId = :reportId")
    void resolveReport(@Param("reportId") Long reportId, @Param("resolvedAt") LocalDateTime resolvedAt,
                       @Param("actionTaken") String actionTaken, @Param("adminNote") String adminNote);

    // Lấy report chưa xử lý
    List<LvsReport> findByLvsStatusIn(List<LvsReportStatus> statuses);

    // Lấy thống kê report theo type
    @Query("SELECT r.lvsReportType, COUNT(r) FROM LvsReport r GROUP BY r.lvsReportType")
    List<Object[]> getReportStatsByType();

    // Lấy thống kê report theo status
    @Query("SELECT r.lvsStatus, COUNT(r) FROM LvsReport r GROUP BY r.lvsStatus")
    List<Object[]> getReportStatsByStatus();

    // Lấy thống kê report theo thời gian
    @Query("SELECT DATE(r.lvsCreatedAt), COUNT(r) FROM LvsReport r WHERE r.lvsCreatedAt >= :startDate GROUP BY DATE(r.lvsCreatedAt) ORDER BY DATE(r.lvsCreatedAt) DESC")
    List<Object[]> getReportStatsByTime(@Param("startDate") LocalDateTime startDate);

    // Kiểm tra user đã report target chưa
    boolean existsByLvsReporter_LvsUserIdAndLvsTargetIdAndLvsReportType(Long lvsUserId, Long lvsTargetId, LvsReportType lvsReportType);

    // Lấy report đã resolved
    List<LvsReport> findByLvsStatusAndLvsResolvedAtIsNotNull(LvsReportStatus lvsStatus);
}