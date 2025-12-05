package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsOrderItem")
@Data
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

    @Column(name = "LvsUnitPrice", precision = 15, scale = 2)
    private Double lvsUnitPrice;

    @Column(name = "LvsItemTotal", precision = 15, scale = 2)
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
}