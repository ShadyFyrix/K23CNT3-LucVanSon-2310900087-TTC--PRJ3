package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsCartItem")
public class LvsCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsCartItemId")
    private Long lvsCartItemId;

    // Giỏ hàng chứa mục này
    @ManyToOne
    @JoinColumn(name = "LvsCartId", referencedColumnName = "LvsCartId", nullable = false)
    private LvsCart lvsCart;

    // Dự án được thêm vào giỏ
    @ManyToOne
    @JoinColumn(name = "LvsProjectId", referencedColumnName = "LvsProjectId", nullable = false)
    private LvsProject lvsProject;

    // Số lượng và giá
    @Column(name = "LvsQuantity")
    private Integer lvsQuantity = 1;

    @Column(name = "LvsUnitPrice")
    private Double lvsUnitPrice;

    @Column(name = "LvsItemTotal")
    private Double lvsItemTotal;

    // Có được chọn để thanh toán không
    @Column(name = "LvsIsSelected")
    private Boolean lvsIsSelected = true;

    // Gift fields
    @Column(name = "lvsIsGift")
    private Boolean lvsIsGift = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lvsGiftRecipientId")
    private LvsUser lvsGiftRecipient;

    // Thời gian thêm vào giỏ
    @Column(name = "LvsAddedAt")
    private LocalDateTime lvsAddedAt = LocalDateTime.now();

    // Tính toán tổng tiền cho mục này
    @PrePersist
    @PreUpdate
    private void calculateTotals() {
        if (lvsUnitPrice == null && lvsProject != null) {
            lvsUnitPrice = lvsProject.getLvsPrice();
        }
        if (lvsUnitPrice != null && lvsQuantity != null) {
            lvsItemTotal = lvsUnitPrice * lvsQuantity;
        }
    }

    // Getters and Setters

    public Long getLvsCartItemId() {
        return lvsCartItemId;
    }

    public void setLvsCartItemId(Long lvsCartItemId) {
        this.lvsCartItemId = lvsCartItemId;
    }

    public LvsCart getLvsCart() {
        return lvsCart;
    }

    public void setLvsCart(LvsCart lvsCart) {
        this.lvsCart = lvsCart;
    }

    public LvsProject getLvsProject() {
        return lvsProject;
    }

    public void setLvsProject(LvsProject lvsProject) {
        this.lvsProject = lvsProject;
    }

    public Integer getLvsQuantity() {
        return lvsQuantity;
    }

    public void setLvsQuantity(Integer lvsQuantity) {
        this.lvsQuantity = lvsQuantity;
    }

    public Double getLvsUnitPrice() {
        return lvsUnitPrice;
    }

    public void setLvsUnitPrice(Double lvsUnitPrice) {
        this.lvsUnitPrice = lvsUnitPrice;
    }

    public Double getLvsItemTotal() {
        return lvsItemTotal;
    }

    public void setLvsItemTotal(Double lvsItemTotal) {
        this.lvsItemTotal = lvsItemTotal;
    }

    public Boolean getLvsIsSelected() {
        return lvsIsSelected;
    }

    public void setLvsIsSelected(Boolean lvsIsSelected) {
        this.lvsIsSelected = lvsIsSelected;
    }

    public LocalDateTime getLvsAddedAt() {
        return lvsAddedAt;
    }

    public void setLvsAddedAt(LocalDateTime lvsAddedAt) {
        this.lvsAddedAt = lvsAddedAt;
    }

    public Boolean getLvsIsGift() {
        return lvsIsGift;
    }

    public void setLvsIsGift(Boolean lvsIsGift) {
        this.lvsIsGift = lvsIsGift;
    }

    public LvsUser getLvsGiftRecipient() {
        return lvsGiftRecipient;
    }

    public void setLvsGiftRecipient(LvsUser lvsGiftRecipient) {
        this.lvsGiftRecipient = lvsGiftRecipient;
    }

}