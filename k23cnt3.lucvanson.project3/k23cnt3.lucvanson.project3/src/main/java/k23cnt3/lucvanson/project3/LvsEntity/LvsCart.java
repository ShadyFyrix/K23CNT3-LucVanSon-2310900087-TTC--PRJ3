package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsCart")
public class LvsCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsCartId")
    private Long lvsCartId;

    // Mỗi user có một giỏ hàng
    @OneToOne
    @JoinColumn(name = "LvsUserId", referencedColumnName = "LvsUserId", unique = true)
    private LvsUser lvsUser;

    // Thống kê giỏ hàng
    @Column(name = "LvsTotalItems")
    private Integer lvsTotalItems = 0;

    @Column(name = "LvsTotalPrice")
    private Double lvsTotalPrice = 0.0;

    @Column(name = "LvsDiscountAmount")
    private Double lvsDiscountAmount = 0.0;

    @Column(name = "LvsFinalPrice")
    private Double lvsFinalPrice = 0.0;

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

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Quan hệ với các mục trong giỏ hàng
    @OneToMany(mappedBy = "lvsCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LvsCartItem> lvsCartItems = new ArrayList<>();

    // Tính toán lại tổng tiền khi có thay đổi
    @PrePersist
    // @PreUpdate - REMOVED: Causes issues with lazy-loaded items
    private void calculateTotals() {
        if (lvsCartItems != null) {
            // Count only selected items
            lvsTotalItems = lvsCartItems.stream()
                    .filter(item -> item.getLvsIsSelected() != null && item.getLvsIsSelected())
                    .mapToInt(LvsCartItem::getLvsQuantity)
                    .sum();

            // Sum only selected items
            lvsTotalPrice = lvsCartItems.stream()
                    .filter(item -> item.getLvsIsSelected() != null && item.getLvsIsSelected())
                    .mapToDouble(item -> item.getLvsItemTotal() != null ? item.getLvsItemTotal() : 0.0)
                    .sum();

            // Final price = Total - Project Discounts - Promotion Discount
            lvsFinalPrice = lvsTotalPrice - lvsDiscountAmount
                    - (lvsPromotionDiscount != null ? lvsPromotionDiscount : 0.0);
        }
    }

    // Getters and Setters

    public Long getLvsCartId() {
        return lvsCartId;
    }

    public void setLvsCartId(Long lvsCartId) {
        this.lvsCartId = lvsCartId;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public Integer getLvsTotalItems() {
        return lvsTotalItems;
    }

    public void setLvsTotalItems(Integer lvsTotalItems) {
        this.lvsTotalItems = lvsTotalItems;
    }

    public Double getLvsTotalPrice() {
        return lvsTotalPrice;
    }

    public void setLvsTotalPrice(Double lvsTotalPrice) {
        this.lvsTotalPrice = lvsTotalPrice;
    }

    public Double getLvsDiscountAmount() {
        return lvsDiscountAmount;
    }

    public void setLvsDiscountAmount(Double lvsDiscountAmount) {
        this.lvsDiscountAmount = lvsDiscountAmount;
    }

    public Double getLvsFinalPrice() {
        return lvsFinalPrice;
    }

    public void setLvsFinalPrice(Double lvsFinalPrice) {
        this.lvsFinalPrice = lvsFinalPrice;
    }

    public String getLvsPromotionCode() {
        return lvsPromotionCode;
    }

    public void setLvsPromotionCode(String lvsPromotionCode) {
        this.lvsPromotionCode = lvsPromotionCode;
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

    public List<LvsCartItem> getLvsCartItems() {
        return lvsCartItems;
    }

    public void setLvsCartItems(List<LvsCartItem> lvsCartItems) {
        this.lvsCartItems = lvsCartItems;
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