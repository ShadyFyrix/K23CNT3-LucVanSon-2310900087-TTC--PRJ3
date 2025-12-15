package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsPromotion")
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
    @Column(name = "LvsDiscountValue", nullable = false)
    private Double lvsDiscountValue;

    // Đơn hàng tối thiểu
    @Column(name = "LvsMinOrderValue")
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

    // Getters and Setters

    public Integer getLvsPromotionId() {
        return lvsPromotionId;
    }

    public void setLvsPromotionId(Integer lvsPromotionId) {
        this.lvsPromotionId = lvsPromotionId;
    }

    public String getLvsCode() {
        return lvsCode;
    }

    public void setLvsCode(String lvsCode) {
        this.lvsCode = lvsCode;
    }

    public String getLvsName() {
        return lvsName;
    }

    public void setLvsName(String lvsName) {
        this.lvsName = lvsName;
    }

    public LvsDiscountType getLvsDiscountType() {
        return lvsDiscountType;
    }

    public void setLvsDiscountType(LvsDiscountType lvsDiscountType) {
        this.lvsDiscountType = lvsDiscountType;
    }

    public Double getLvsDiscountValue() {
        return lvsDiscountValue;
    }

    public void setLvsDiscountValue(Double lvsDiscountValue) {
        this.lvsDiscountValue = lvsDiscountValue;
    }

    public Double getLvsMinOrderValue() {
        return lvsMinOrderValue;
    }

    public void setLvsMinOrderValue(Double lvsMinOrderValue) {
        this.lvsMinOrderValue = lvsMinOrderValue;
    }

    public Integer getLvsUsageLimit() {
        return lvsUsageLimit;
    }

    public void setLvsUsageLimit(Integer lvsUsageLimit) {
        this.lvsUsageLimit = lvsUsageLimit;
    }

    public Integer getLvsUsedCount() {
        return lvsUsedCount;
    }

    public void setLvsUsedCount(Integer lvsUsedCount) {
        this.lvsUsedCount = lvsUsedCount;
    }

    public LocalDate getLvsStartDate() {
        return lvsStartDate;
    }

    public void setLvsStartDate(LocalDate lvsStartDate) {
        this.lvsStartDate = lvsStartDate;
    }

    public LocalDate getLvsEndDate() {
        return lvsEndDate;
    }

    public void setLvsEndDate(LocalDate lvsEndDate) {
        this.lvsEndDate = lvsEndDate;
    }

    public Boolean getLvsIsActive() {
        return lvsIsActive;
    }

    public void setLvsIsActive(Boolean lvsIsActive) {
        this.lvsIsActive = lvsIsActive;
    }

    public String getLvsDescription() {
        return lvsDescription;
    }

    public void setLvsDescription(String lvsDescription) {
        this.lvsDescription = lvsDescription;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

}