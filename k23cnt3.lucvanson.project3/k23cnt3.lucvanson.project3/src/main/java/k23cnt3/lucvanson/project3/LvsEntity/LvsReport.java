package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsReport")
@Data
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
}