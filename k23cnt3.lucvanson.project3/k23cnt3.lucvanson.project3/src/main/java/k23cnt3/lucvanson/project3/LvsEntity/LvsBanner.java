package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LvsBanner - Entity cho banner quảng cáo/khuyến mãi
 * Admin có thể tạo và quản lý các banner hiển thị trên trang chủ
 */
@Entity
@Table(name = "LvsBanner")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LvsBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lvsBannerId;

    @Column(nullable = false, length = 200)
    private String lvsTitle;

    @Column(columnDefinition = "TEXT")
    private String lvsDescription;

    @Column(length = 500)
    private String lvsImageUrl; // URL ảnh banner

    @Column(length = 500)
    private String lvsLinkUrl; // Link đến project/promotion

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LvsBannerType lvsType; // HERO, SIDEBAR, PROMOTION

    @Column(nullable = false)
    private Boolean lvsIsActive = true;

    @Column(nullable = false)
    private Integer lvsDisplayOrder = 0; // Thứ tự hiển thị

    private LocalDateTime lvsStartDate;
    private LocalDateTime lvsEndDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        lvsCreatedAt = LocalDateTime.now();
    }

    // Enum cho loại banner
    public enum LvsBannerType {
        HERO("Hero Banner"), // Banner lớn ở đầu trang
        SIDEBAR("Sidebar Banner"), // Banner bên cạnh
        PROMOTION("Promotion Banner"); // Banner khuyến mãi

        private final String displayName;

        LvsBannerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
