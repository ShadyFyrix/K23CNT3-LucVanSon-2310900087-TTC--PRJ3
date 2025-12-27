package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsOrder")
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
    @Column(name = "LvsTotalAmount", nullable = false)
    private Double lvsTotalAmount;

    // Giảm giá
    @Column(name = "LvsDiscountAmount")
    private Double lvsDiscountAmount = 0.0;

    @Column(name = "LvsFinalAmount")
    private Double lvsFinalAmount;

    // Trạng thái đơn hàng
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsOrderStatus lvsStatus = LvsOrderStatus.PENDING;

    // Phương thức thanh toán
    @Column(name = "LvsPaymentMethod", length = 50)
    private String lvsPaymentMethod = "COIN";

    // Khuyến mãi áp dụng
    @ManyToOne
    @JoinColumn(name = "LvsPromotionId")
    private LvsPromotion lvsPromotion;

    // Mã khuyến mãi (lưu lại để tham khảo)
    @Column(name = "LvsPromotionCode", length = 20)
    private String lvsPromotionCode;

    // Giảm giá từ promotion
    @Column(name = "LvsPromotionDiscount")
    private Double lvsPromotionDiscount = 0.0;

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
                    .mapToDouble(item -> item.getLvsItemTotal() != null ? item.getLvsItemTotal() : 0.0)
                    .sum();
        }
        // Only recalculate lvsFinalAmount if not already set (to preserve cart checkout
        // values)
        if (lvsFinalAmount == null || lvsFinalAmount == 0.0) {
            // Final amount = Total - Project Discounts - Promotion Discount
            lvsFinalAmount = lvsTotalAmount - lvsDiscountAmount
                    - (lvsPromotionDiscount != null ? lvsPromotionDiscount : 0.0);
        }
    }

    // Enum trạng thái đơn hàng
    public enum LvsOrderStatus {
        PENDING, PROCESSING, COMPLETED, CANCELLED, REFUNDED
    }

    // Getters and Setters

    public Long getLvsOrderId() {
        return lvsOrderId;
    }

    public void setLvsOrderId(Long lvsOrderId) {
        this.lvsOrderId = lvsOrderId;
    }

    public String getLvsOrderCode() {
        return lvsOrderCode;
    }

    public void setLvsOrderCode(String lvsOrderCode) {
        this.lvsOrderCode = lvsOrderCode;
    }

    public LvsUser getLvsBuyer() {
        return lvsBuyer;
    }

    public void setLvsBuyer(LvsUser lvsBuyer) {
        this.lvsBuyer = lvsBuyer;
    }

    public Double getLvsTotalAmount() {
        return lvsTotalAmount;
    }

    public void setLvsTotalAmount(Double lvsTotalAmount) {
        this.lvsTotalAmount = lvsTotalAmount;
    }

    public Double getLvsDiscountAmount() {
        return lvsDiscountAmount;
    }

    public void setLvsDiscountAmount(Double lvsDiscountAmount) {
        this.lvsDiscountAmount = lvsDiscountAmount;
    }

    public Double getLvsFinalAmount() {
        return lvsFinalAmount;
    }

    public void setLvsFinalAmount(Double lvsFinalAmount) {
        this.lvsFinalAmount = lvsFinalAmount;
    }

    public LvsOrderStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsOrderStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public String getLvsPaymentMethod() {
        return lvsPaymentMethod;
    }

    public void setLvsPaymentMethod(String lvsPaymentMethod) {
        this.lvsPaymentMethod = lvsPaymentMethod;
    }

    public String getLvsPromotionCode() {
        return lvsPromotionCode;
    }

    public void setLvsPromotionCode(String lvsPromotionCode) {
        this.lvsPromotionCode = lvsPromotionCode;
    }

    public Boolean getLvsIsPaid() {
        return lvsIsPaid;
    }

    public void setLvsIsPaid(Boolean lvsIsPaid) {
        this.lvsIsPaid = lvsIsPaid;
    }

    public String getLvsNotes() {
        return lvsNotes;
    }

    public void setLvsNotes(String lvsNotes) {
        this.lvsNotes = lvsNotes;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

    public LocalDateTime getLvsUpdatedAt() {
        return lvsUpdatedAt;
    }

    public void setLvsUpdatedAt(LocalDateTime lvsUpdatedAt) {
        this.lvsUpdatedAt = lvsUpdatedAt;
    }

    public LocalDateTime getLvsPaidAt() {
        return lvsPaidAt;
    }

    public void setLvsPaidAt(LocalDateTime lvsPaidAt) {
        this.lvsPaidAt = lvsPaidAt;
    }

    public List<LvsOrderItem> getLvsOrderItems() {
        return lvsOrderItems;
    }

    public void setLvsOrderItems(List<LvsOrderItem> lvsOrderItems) {
        this.lvsOrderItems = lvsOrderItems;
    }

    public LvsPromotion getLvsPromotion() {
        return lvsPromotion;
    }

    public void setLvsPromotion(LvsPromotion lvsPromotion) {
        this.lvsPromotion = lvsPromotion;
    }

    public Double getLvsPromotionDiscount() {
        return lvsPromotionDiscount;
    }

    public void setLvsPromotionDiscount(Double lvsPromotionDiscount) {
        this.lvsPromotionDiscount = lvsPromotionDiscount;
    }

}