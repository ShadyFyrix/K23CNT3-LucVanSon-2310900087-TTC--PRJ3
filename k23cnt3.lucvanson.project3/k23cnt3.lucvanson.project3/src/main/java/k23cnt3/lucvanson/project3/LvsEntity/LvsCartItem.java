package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsCartItem")
@Data
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

    @Column(name = "LvsUnitPrice", precision = 15, scale = 2)
    private Double lvsUnitPrice;

    @Column(name = "LvsItemTotal", precision = 15, scale = 2)
    private Double lvsItemTotal;

    // Có được chọn để thanh toán không
    @Column(name = "LvsIsSelected")
    private Boolean lvsIsSelected = true;

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
}