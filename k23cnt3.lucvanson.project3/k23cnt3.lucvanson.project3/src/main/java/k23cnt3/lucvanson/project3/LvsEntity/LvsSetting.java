package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsSetting")
@Data
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
}