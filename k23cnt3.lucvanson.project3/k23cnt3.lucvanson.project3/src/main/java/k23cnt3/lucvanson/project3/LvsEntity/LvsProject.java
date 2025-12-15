package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsProject")
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

    @Column(name = "LvsPrice", nullable = false)
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

    @Column(name = "LvsRating")
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

    // Getters and Setters

    public Long getLvsProjectId() {
        return lvsProjectId;
    }

    public void setLvsProjectId(Long lvsProjectId) {
        this.lvsProjectId = lvsProjectId;
    }

    public String getLvsProjectName() {
        return lvsProjectName;
    }

    public void setLvsProjectName(String lvsProjectName) {
        this.lvsProjectName = lvsProjectName;
    }

    public String getLvsDescription() {
        return lvsDescription;
    }

    public void setLvsDescription(String lvsDescription) {
        this.lvsDescription = lvsDescription;
    }

    public Double getLvsPrice() {
        return lvsPrice;
    }

    public void setLvsPrice(Double lvsPrice) {
        this.lvsPrice = lvsPrice;
    }

    public LvsCategory getLvsCategory() {
        return lvsCategory;
    }

    public void setLvsCategory(LvsCategory lvsCategory) {
        this.lvsCategory = lvsCategory;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public LvsProjectStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsProjectStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public String getLvsThumbnailUrl() {
        return lvsThumbnailUrl;
    }

    public void setLvsThumbnailUrl(String lvsThumbnailUrl) {
        this.lvsThumbnailUrl = lvsThumbnailUrl;
    }

    public String getLvsImages() {
        return lvsImages;
    }

    public void setLvsImages(String lvsImages) {
        this.lvsImages = lvsImages;
    }

    public String getLvsFileUrl() {
        return lvsFileUrl;
    }

    public void setLvsFileUrl(String lvsFileUrl) {
        this.lvsFileUrl = lvsFileUrl;
    }

    public String getLvsDemoUrl() {
        return lvsDemoUrl;
    }

    public void setLvsDemoUrl(String lvsDemoUrl) {
        this.lvsDemoUrl = lvsDemoUrl;
    }

    public String getLvsSourceCodeUrl() {
        return lvsSourceCodeUrl;
    }

    public void setLvsSourceCodeUrl(String lvsSourceCodeUrl) {
        this.lvsSourceCodeUrl = lvsSourceCodeUrl;
    }

    public Integer getLvsViewCount() {
        return lvsViewCount;
    }

    public void setLvsViewCount(Integer lvsViewCount) {
        this.lvsViewCount = lvsViewCount;
    }

    public Integer getLvsDownloadCount() {
        return lvsDownloadCount;
    }

    public void setLvsDownloadCount(Integer lvsDownloadCount) {
        this.lvsDownloadCount = lvsDownloadCount;
    }

    public Integer getLvsPurchaseCount() {
        return lvsPurchaseCount;
    }

    public void setLvsPurchaseCount(Integer lvsPurchaseCount) {
        this.lvsPurchaseCount = lvsPurchaseCount;
    }

    public Double getLvsRating() {
        return lvsRating;
    }

    public void setLvsRating(Double lvsRating) {
        this.lvsRating = lvsRating;
    }

    public Integer getLvsReviewCount() {
        return lvsReviewCount;
    }

    public void setLvsReviewCount(Integer lvsReviewCount) {
        this.lvsReviewCount = lvsReviewCount;
    }

    public String getLvsTags() {
        return lvsTags;
    }

    public void setLvsTags(String lvsTags) {
        this.lvsTags = lvsTags;
    }

    public Boolean getLvsIsFeatured() {
        return lvsIsFeatured;
    }

    public void setLvsIsFeatured(Boolean lvsIsFeatured) {
        this.lvsIsFeatured = lvsIsFeatured;
    }

    public Boolean getLvsIsApproved() {
        return lvsIsApproved;
    }

    public void setLvsIsApproved(Boolean lvsIsApproved) {
        this.lvsIsApproved = lvsIsApproved;
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

    public List<LvsPost> getLvsPosts() {
        return lvsPosts;
    }

    public void setLvsPosts(List<LvsPost> lvsPosts) {
        this.lvsPosts = lvsPosts;
    }

    public List<LvsReview> getLvsReviews() {
        return lvsReviews;
    }

    public void setLvsReviews(List<LvsReview> lvsReviews) {
        this.lvsReviews = lvsReviews;
    }

    public List<LvsOrderItem> getLvsOrderItems() {
        return lvsOrderItems;
    }

    public void setLvsOrderItems(List<LvsOrderItem> lvsOrderItems) {
        this.lvsOrderItems = lvsOrderItems;
    }

}