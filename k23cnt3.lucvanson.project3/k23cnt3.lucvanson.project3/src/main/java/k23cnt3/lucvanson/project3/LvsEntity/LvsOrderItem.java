package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsOrderItem")
public class LvsOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsOrderItemId")
    private Long lvsOrderItemId;

    // Đơn hàng chứa mục này
    @ManyToOne
    @JoinColumn(name = "LvsOrderId", referencedColumnName = "LvsOrderId", nullable = false)
    private LvsOrder lvsOrder;

    // Dự án được mua
    @ManyToOne
    @JoinColumn(name = "LvsProjectId", referencedColumnName = "LvsProjectId", nullable = false)
    private LvsProject lvsProject;

    // Người bán (tác giả dự án)
    @ManyToOne
    @JoinColumn(name = "LvsSellerId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsSeller;

    // Số lượng và giá
    @Column(name = "LvsQuantity")
    private Integer lvsQuantity = 1;

    @Column(name = "LvsUnitPrice")
    private Double lvsUnitPrice;

    @Column(name = "LvsItemTotal")
    private Double lvsItemTotal;

    // Thời gian tạo
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Tính toán tổng tiền cho mục này
    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        if (lvsUnitPrice == null && lvsProject != null) {
            lvsUnitPrice = lvsProject.getLvsPrice();
        }
        if (lvsSeller == null && lvsProject != null && lvsProject.getLvsUser() != null) {
            lvsSeller = lvsProject.getLvsUser();
        }
        if (lvsUnitPrice != null && lvsQuantity != null) {
            lvsItemTotal = lvsUnitPrice * lvsQuantity;
        }
    }

    // Getters and Setters

    public Long getLvsOrderItemId() {
        return lvsOrderItemId;
    }

    public void setLvsOrderItemId(Long lvsOrderItemId) {
        this.lvsOrderItemId = lvsOrderItemId;
    }

    public LvsOrder getLvsOrder() {
        return lvsOrder;
    }

    public void setLvsOrder(LvsOrder lvsOrder) {
        this.lvsOrder = lvsOrder;
    }

    public LvsProject getLvsProject() {
        return lvsProject;
    }

    public void setLvsProject(LvsProject lvsProject) {
        this.lvsProject = lvsProject;
    }

    public LvsUser getLvsSeller() {
        return lvsSeller;
    }

    public void setLvsSeller(LvsUser lvsSeller) {
        this.lvsSeller = lvsSeller;
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

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

}