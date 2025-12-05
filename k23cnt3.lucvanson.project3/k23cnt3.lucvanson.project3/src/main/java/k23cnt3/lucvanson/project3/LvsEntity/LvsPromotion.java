package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsPromotion")
@Data
public class LvsPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsPromotionId")
    private Integer lvsPromotionId;

    // Mã khuyến mãi
    @Column(name = "LvsCode", unique = true, nullable = false, length = 20)
    private String lvsCode;

    // Tên khuyến mãi
    @Column(name = "LvsName", nullable = false, length = 100)
    private String lvsName;

    // Loại giảm giá
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsDiscountType")
    private LvsDiscountType lvsDiscountType = LvsDiscountType.PERCENT;

    // Giá trị giảm
    @Column(name = "LvsDiscountValue", nullable = false, precision = 10, scale = 2)
    private Double lvsDiscountValue;

    // Đơn hàng tối thiểu
    @Column(name = "LvsMinOrderValue", precision = 10, scale = 2)
    private Double lvsMinOrderValue = 0.0;

    // Giới hạn sử dụng
    @Column(name = "LvsUsageLimit")
    private Integer lvsUsageLimit = 1;

    @Column(name = "LvsUsedCount")
    private Integer lvsUsedCount = 0;

    // Thời gian hiệu lực
    @Column(name = "LvsStartDate", nullable = false)
    private LocalDate lvsStartDate;

    @Column(name = "LvsEndDate", nullable = false)
    private LocalDate lvsEndDate;

    // Trạng thái
    @Column(name = "LvsIsActive")
    private Boolean lvsIsActive = true;

    // Mô tả
    @Column(name = "LvsDescription", length = 500)
    private String lvsDescription;

    // Thời gian tạo
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Enum
    public enum LvsDiscountType {
        PERCENT, FIXED
    }
}