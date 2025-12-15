package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsTransaction")
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
    @Column(name = "LvsAmount", nullable = false)
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

    // Getters and Setters

    public Long getLvsTransactionId() {
        return lvsTransactionId;
    }

    public void setLvsTransactionId(Long lvsTransactionId) {
        this.lvsTransactionId = lvsTransactionId;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public LvsTransactionType getLvsType() {
        return lvsType;
    }

    public void setLvsType(LvsTransactionType lvsType) {
        this.lvsType = lvsType;
    }

    public Double getLvsAmount() {
        return lvsAmount;
    }

    public void setLvsAmount(Double lvsAmount) {
        this.lvsAmount = lvsAmount;
    }

    public LvsTransactionStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsTransactionStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public String getLvsDescription() {
        return lvsDescription;
    }

    public void setLvsDescription(String lvsDescription) {
        this.lvsDescription = lvsDescription;
    }

    public String getLvsPaymentInfo() {
        return lvsPaymentInfo;
    }

    public void setLvsPaymentInfo(String lvsPaymentInfo) {
        this.lvsPaymentInfo = lvsPaymentInfo;
    }

    public LvsOrder getLvsOrder() {
        return lvsOrder;
    }

    public void setLvsOrder(LvsOrder lvsOrder) {
        this.lvsOrder = lvsOrder;
    }

    public LvsUser getLvsAdminApprover() {
        return lvsAdminApprover;
    }

    public void setLvsAdminApprover(LvsUser lvsAdminApprover) {
        this.lvsAdminApprover = lvsAdminApprover;
    }

    public LocalDateTime getLvsApprovedAt() {
        return lvsApprovedAt;
    }

    public void setLvsApprovedAt(LocalDateTime lvsApprovedAt) {
        this.lvsApprovedAt = lvsApprovedAt;
    }

    public String getLvsAdminNote() {
        return lvsAdminNote;
    }

    public void setLvsAdminNote(String lvsAdminNote) {
        this.lvsAdminNote = lvsAdminNote;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

}