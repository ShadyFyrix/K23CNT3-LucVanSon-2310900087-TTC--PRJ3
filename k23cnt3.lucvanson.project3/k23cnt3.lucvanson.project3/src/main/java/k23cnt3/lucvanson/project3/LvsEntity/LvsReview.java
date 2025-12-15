package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsReview")
public class LvsReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsReviewId")
    private Long lvsReviewId;

    // Người đánh giá và dự án được đánh giá
    @ManyToOne
    @JoinColumn(name = "LvsUserId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsUser;

    @ManyToOne
    @JoinColumn(name = "LvsProjectId", referencedColumnName = "LvsProjectId", nullable = false)
    private LvsProject lvsProject;

    // Điểm đánh giá (1-5 sao)
    @Column(name = "LvsRating", nullable = false)
    private Integer lvsRating;

    // Tiêu đề và nội dung
    @Column(name = "LvsTitle", length = 200)
    private String lvsTitle;

    @Column(name = "LvsContent", columnDefinition = "TEXT")
    private String lvsContent;

    // Hình ảnh đánh giá (JSON array)
    @Column(name = "LvsImages", columnDefinition = "TEXT")
    private String lvsImages;

    // Được duyệt chưa (admin có thể kiểm duyệt)
    @Column(name = "LvsIsApproved")
    private Boolean lvsIsApproved = true;

    // Số lượt thích
    @Column(name = "LvsLikeCount")
    private Integer lvsLikeCount = 0;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Getters and Setters

    public Long getLvsReviewId() {
        return lvsReviewId;
    }

    public void setLvsReviewId(Long lvsReviewId) {
        this.lvsReviewId = lvsReviewId;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public LvsProject getLvsProject() {
        return lvsProject;
    }

    public void setLvsProject(LvsProject lvsProject) {
        this.lvsProject = lvsProject;
    }

    public Integer getLvsRating() {
        return lvsRating;
    }

    public void setLvsRating(Integer lvsRating) {
        this.lvsRating = lvsRating;
    }

    public String getLvsTitle() {
        return lvsTitle;
    }

    public void setLvsTitle(String lvsTitle) {
        this.lvsTitle = lvsTitle;
    }

    public String getLvsContent() {
        return lvsContent;
    }

    public void setLvsContent(String lvsContent) {
        this.lvsContent = lvsContent;
    }

    public String getLvsImages() {
        return lvsImages;
    }

    public void setLvsImages(String lvsImages) {
        this.lvsImages = lvsImages;
    }

    public Boolean getLvsIsApproved() {
        return lvsIsApproved;
    }

    public void setLvsIsApproved(Boolean lvsIsApproved) {
        this.lvsIsApproved = lvsIsApproved;
    }

    public Integer getLvsLikeCount() {
        return lvsLikeCount;
    }

    public void setLvsLikeCount(Integer lvsLikeCount) {
        this.lvsLikeCount = lvsLikeCount;
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