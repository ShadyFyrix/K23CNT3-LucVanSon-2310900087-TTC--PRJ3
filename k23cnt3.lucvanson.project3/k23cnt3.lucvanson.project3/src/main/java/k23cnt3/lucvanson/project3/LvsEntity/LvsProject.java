package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsProject")
@Data
public class LvsProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsProjectId")
    private Long lvsProjectId;

    // Thông tin cơ bản
    @Column(name = "LvsProjectName", nullable = false, length = 200)
    private String lvsProjectName;

    @Column(name = "LvsDescription", columnDefinition = "TEXT")
    private String lvsDescription;

    @Column(name = "LvsPrice", nullable = false, precision = 15, scale = 2)
    private Double lvsPrice;

    // Danh mục và người đăng
    @ManyToOne
    @JoinColumn(name = "LvsCategoryId", referencedColumnName = "LvsCategoryId")
    private LvsCategory lvsCategory;

    @ManyToOne
    @JoinColumn(name = "LvsUserId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsUser;

    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsProjectStatus lvsStatus = LvsProjectStatus.DRAFT;

    // Hình ảnh và file
    @Column(name = "LvsThumbnailUrl", length = 500)
    private String lvsThumbnailUrl;

    @Column(name = "LvsImages", columnDefinition = "TEXT")
    private String lvsImages; // JSON array các ảnh

    @Column(name = "LvsFileUrl", length = 500)
    private String lvsFileUrl;

    @Column(name = "LvsDemoUrl", length = 500)
    private String lvsDemoUrl;

    @Column(name = "LvsSourceCodeUrl", length = 500)
    private String lvsSourceCodeUrl;

    // Thống kê
    @Column(name = "LvsViewCount")
    private Integer lvsViewCount = 0;

    @Column(name = "LvsDownloadCount")
    private Integer lvsDownloadCount = 0;

    @Column(name = "LvsPurchaseCount")
    private Integer lvsPurchaseCount = 0;

    @Column(name = "LvsRating", precision = 3, scale = 2)
    private Double lvsRating = 0.0;

    @Column(name = "LvsReviewCount")
    private Integer lvsReviewCount = 0;

    // Tags và featured
    @Column(name = "LvsTags", length = 500)
    private String lvsTags;

    @Column(name = "LvsIsFeatured")
    private Boolean lvsIsFeatured = false;

    @Column(name = "LvsIsApproved")
    private Boolean lvsIsApproved = false; // Admin duyệt

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Quan hệ
    @OneToMany(mappedBy = "lvsProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsPost> lvsPosts = new ArrayList<>();

    @OneToMany(mappedBy = "lvsProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsReview> lvsReviews = new ArrayList<>();

    @OneToMany(mappedBy = "lvsProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsOrderItem> lvsOrderItems = new ArrayList<>();

    // Enum
    public enum LvsProjectStatus {
        DRAFT, PENDING, APPROVED, REJECTED, SOLD
    }
}