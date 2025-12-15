package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsReport")
public class LvsReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsReportId")
    private Long lvsReportId;

    // Người báo cáo
    @ManyToOne
    @JoinColumn(name = "LvsReporterId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsReporter;

    // Loại báo cáo
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsReportType", nullable = false)
    private LvsReportType lvsReportType;

    // ID đối tượng bị báo cáo
    @Column(name = "LvsTargetId", nullable = false)
    private Long lvsTargetId;

    // Lý do
    @Column(name = "LvsReason", nullable = false, length = 255)
    private String lvsReason;

    // Chi tiết
    @Column(name = "LvsDetails", columnDefinition = "TEXT")
    private String lvsDetails;

    // Trạng thái xử lý
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsReportStatus lvsStatus = LvsReportStatus.PENDING;

    // Admin xử lý
    @ManyToOne
    @JoinColumn(name = "LvsAdminHandlerId", referencedColumnName = "LvsUserId")
    private LvsUser lvsAdminHandler;

    // Ghi chú của admin
    @Column(name = "LvsAdminNote", columnDefinition = "TEXT")
    private String lvsAdminNote;

    // Hành động đã thực hiện
    @Column(name = "LvsActionTaken", length = 500)
    private String lvsActionTaken;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsResolvedAt")
    private LocalDateTime lvsResolvedAt;

    // Enum
    public enum LvsReportType {
        USER, PROJECT, POST, COMMENT, MESSAGE, REVIEW
    }

    public enum LvsReportStatus {
        PENDING, UNDER_REVIEW, RESOLVED, REJECTED
    }

    // Getters and Setters

    public Long getLvsReportId() {
        return lvsReportId;
    }

    public void setLvsReportId(Long lvsReportId) {
        this.lvsReportId = lvsReportId;
    }

    public LvsUser getLvsReporter() {
        return lvsReporter;
    }

    public void setLvsReporter(LvsUser lvsReporter) {
        this.lvsReporter = lvsReporter;
    }

    public LvsReportType getLvsReportType() {
        return lvsReportType;
    }

    public void setLvsReportType(LvsReportType lvsReportType) {
        this.lvsReportType = lvsReportType;
    }

    public Long getLvsTargetId() {
        return lvsTargetId;
    }

    public void setLvsTargetId(Long lvsTargetId) {
        this.lvsTargetId = lvsTargetId;
    }

    public String getLvsReason() {
        return lvsReason;
    }

    public void setLvsReason(String lvsReason) {
        this.lvsReason = lvsReason;
    }

    public String getLvsDetails() {
        return lvsDetails;
    }

    public void setLvsDetails(String lvsDetails) {
        this.lvsDetails = lvsDetails;
    }

    public LvsReportStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsReportStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public LvsUser getLvsAdminHandler() {
        return lvsAdminHandler;
    }

    public void setLvsAdminHandler(LvsUser lvsAdminHandler) {
        this.lvsAdminHandler = lvsAdminHandler;
    }

    public String getLvsAdminNote() {
        return lvsAdminNote;
    }

    public void setLvsAdminNote(String lvsAdminNote) {
        this.lvsAdminNote = lvsAdminNote;
    }

    public String getLvsActionTaken() {
        return lvsActionTaken;
    }

    public void setLvsActionTaken(String lvsActionTaken) {
        this.lvsActionTaken = lvsActionTaken;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

    public LocalDateTime getLvsResolvedAt() {
        return lvsResolvedAt;
    }

    public void setLvsResolvedAt(LocalDateTime lvsResolvedAt) {
        this.lvsResolvedAt = lvsResolvedAt;
    }

}