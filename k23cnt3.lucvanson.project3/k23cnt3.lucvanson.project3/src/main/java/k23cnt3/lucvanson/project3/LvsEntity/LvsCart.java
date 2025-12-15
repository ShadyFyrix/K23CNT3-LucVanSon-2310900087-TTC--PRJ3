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

    // Mã khuyến mãi
    @Column(name = "LvsPromotionCode", length = 20)
    private String lvsPromotionCode;

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
    @PreUpdate
    private void calculateTotals() {
        if (lvsCartItems != null) {
            lvsTotalItems = lvsCartItems.stream()
                    .mapToInt(LvsCartItem::getLvsQuantity)
                    .sum();

            lvsTotalPrice = lvsCartItems.stream()
                    .mapToDouble(item -> item.getLvsItemTotal() != null ? item.getLvsItemTotal() : 0.0)
                    .sum();

            lvsFinalPrice = lvsTotalPrice - lvsDiscountAmount;
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

}