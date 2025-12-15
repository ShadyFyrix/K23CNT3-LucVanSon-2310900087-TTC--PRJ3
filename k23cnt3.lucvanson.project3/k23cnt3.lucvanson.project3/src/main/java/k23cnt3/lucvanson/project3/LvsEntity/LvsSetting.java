package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsSetting")
public class LvsSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsSettingId")
    private Integer lvsSettingId;

    // Khóa cài đặt (duy nhất)
    @Column(name = "LvsKey", unique = true, nullable = false, length = 50)
    private String lvsKey;

    // Giá trị cài đặt
    @Column(name = "LvsValue", columnDefinition = "TEXT", nullable = false)
    private String lvsValue;

    // Nhóm cài đặt
    @Column(name = "LvsGroup", length = 30)
    private String lvsGroup = "general";

    // Nhãn hiển thị
    @Column(name = "LvsLabel", length = 100)
    private String lvsLabel;

    // Kiểu dữ liệu
    @Column(name = "LvsDataType", length = 20)
    private String lvsDataType = "STRING";

    // Có công khai không
    @Column(name = "LvsIsPublic")
    private Boolean lvsIsPublic = false;

    // Mô tả
    @Column(name = "LvsDescription", length = 255)
    private String lvsDescription;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Getters and Setters

    public Integer getLvsSettingId() {
        return lvsSettingId;
    }

    public void setLvsSettingId(Integer lvsSettingId) {
        this.lvsSettingId = lvsSettingId;
    }

    public String getLvsKey() {
        return lvsKey;
    }

    public void setLvsKey(String lvsKey) {
        this.lvsKey = lvsKey;
    }

    public String getLvsValue() {
        return lvsValue;
    }

    public void setLvsValue(String lvsValue) {
        this.lvsValue = lvsValue;
    }

    public String getLvsGroup() {
        return lvsGroup;
    }

    public void setLvsGroup(String lvsGroup) {
        this.lvsGroup = lvsGroup;
    }

    public String getLvsLabel() {
        return lvsLabel;
    }

    public void setLvsLabel(String lvsLabel) {
        this.lvsLabel = lvsLabel;
    }

    public String getLvsDataType() {
        return lvsDataType;
    }

    public void setLvsDataType(String lvsDataType) {
        this.lvsDataType = lvsDataType;
    }

    public Boolean getLvsIsPublic() {
        return lvsIsPublic;
    }

    public void setLvsIsPublic(Boolean lvsIsPublic) {
        this.lvsIsPublic = lvsIsPublic;
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

    public LocalDateTime getLvsUpdatedAt() {
        return lvsUpdatedAt;
    }

    public void setLvsUpdatedAt(LocalDateTime lvsUpdatedAt) {
        this.lvsUpdatedAt = lvsUpdatedAt;
    }

}