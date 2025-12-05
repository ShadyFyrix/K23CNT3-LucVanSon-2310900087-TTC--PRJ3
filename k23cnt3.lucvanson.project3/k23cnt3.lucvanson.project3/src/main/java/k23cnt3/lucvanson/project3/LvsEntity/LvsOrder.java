package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsOrder")
@Data
public class LvsOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsOrderId")
    private Long lvsOrderId;

    // Mã đơn hàng duy nhất
    @Column(name = "LvsOrderCode", unique = true, nullable = false, length = 20)
    private String lvsOrderCode;

    // Người mua
    @ManyToOne
    @JoinColumn(name = "LvsBuyerId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsBuyer;

    // Tổng tiền
    @Column(name = "LvsTotalAmount", nullable = false, precision = 15, scale = 2)
    private Double lvsTotalAmount;

    // Giảm giá
    @Column(name = "LvsDiscountAmount", precision = 15, scale = 2)
    private Double lvsDiscountAmount = 0.0;

    @Column(name = "LvsFinalAmount", precision = 15, scale = 2)
    private Double lvsFinalAmount;

    // Trạng thái đơn hàng
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsOrderStatus lvsStatus = LvsOrderStatus.PENDING;

    // Phương thức thanh toán
    @Column(name = "LvsPaymentMethod", length = 50)
    private String lvsPaymentMethod = "COIN";

    // Mã khuyến mãi
    @Column(name = "LvsPromotionCode", length = 20)
    private String lvsPromotionCode;

    // Đã thanh toán chưa
    @Column(name = "LvsIsPaid")
    private Boolean lvsIsPaid = false;

    // Ghi chú
    @Column(name = "LvsNotes", length = 500)
    private String lvsNotes;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    @Column(name = "LvsPaidAt")
    private LocalDateTime lvsPaidAt;

    // Quan hệ với các mục trong đơn hàng
    @OneToMany(mappedBy = "lvsOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LvsOrderItem> lvsOrderItems = new ArrayList<>();

    // Tính toán tổng tiền từ các mục
    @PrePersist
    @PreUpdate
    private void calculateAmounts() {
        if (lvsOrderItems != null && !lvsOrderItems.isEmpty()) {
            lvsTotalAmount = lvsOrderItems.stream()
                    .mapToDouble(LvsOrderItem::getLvsItemTotal)
                    .sum();
        }
        lvsFinalAmount = lvsTotalAmount - lvsDiscountAmount;
    }

    // Enum trạng thái đơn hàng
    public enum LvsOrderStatus {
        PENDING, PROCESSING, COMPLETED, CANCELLED, REFUNDED
    }
}