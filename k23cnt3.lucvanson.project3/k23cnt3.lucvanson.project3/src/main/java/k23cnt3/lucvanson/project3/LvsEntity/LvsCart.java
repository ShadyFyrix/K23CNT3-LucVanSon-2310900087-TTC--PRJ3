package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsCart")
@Data
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

    @Column(name = "LvsTotalPrice", precision = 15, scale = 2)
    private Double lvsTotalPrice = 0.0;

    @Column(name = "LvsDiscountAmount", precision = 15, scale = 2)
    private Double lvsDiscountAmount = 0.0;

    @Column(name = "LvsFinalPrice", precision = 15, scale = 2)
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
}