package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsTransaction")
@Data
public class LvsTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsTransactionId")
    private Long lvsTransactionId;

    // Người dùng thực hiện giao dịch
    @ManyToOne
    @JoinColumn(name = "LvsUserId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsUser;

    // Loại giao dịch
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsType", nullable = false)
    private LvsTransactionType lvsType;

    // Số tiền
    @Column(name = "LvsAmount", nullable = false, precision = 15, scale = 2)
    private Double lvsAmount;

    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsTransactionStatus lvsStatus = LvsTransactionStatus.PENDING;

    // Mô tả
    @Column(name = "LvsDescription", length = 255)
    private String lvsDescription;

    // Thông tin thanh toán
    @Column(name = "LvsPaymentInfo", length = 500)
    private String lvsPaymentInfo;

    // Liên kết với đơn hàng (nếu có)
    @ManyToOne
    @JoinColumn(name = "LvsOrderId", referencedColumnName = "LvsOrderId")
    private LvsOrder lvsOrder;

    // Admin duyệt giao dịch (với nạp/rút)
    @ManyToOne
    @JoinColumn(name = "LvsAdminApproverId", referencedColumnName = "LvsUserId")
    private LvsUser lvsAdminApprover;

    // Thời gian duyệt
    @Column(name = "LvsApprovedAt")
    private LocalDateTime lvsApprovedAt;

    // Ghi chú của admin
    @Column(name = "LvsAdminNote", length = 500)
    private String lvsAdminNote;

    // Thời gian tạo
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Enum
    public enum LvsTransactionType {
        DEPOSIT, WITHDRAWAL, PURCHASE, SALE, REFUND
    }

    public enum LvsTransactionStatus {
        PENDING, SUCCESS, FAILED
    }
}