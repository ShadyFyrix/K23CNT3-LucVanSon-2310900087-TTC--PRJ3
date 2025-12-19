package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsPost")
public class LvsPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsPostId")
    private Long lvsPostId;

    // Tiêu đề và nội dung
    @Column(name = "LvsTitle", nullable = false, length = 200)
    private String lvsTitle;

    @Column(name = "LvsContent", columnDefinition = "TEXT", nullable = false)
    private String lvsContent;

    // Loại bài viết
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsType")
    private LvsPostType lvsType = LvsPostType.DISCUSSION;

    // Người đăng và dự án liên quan (nếu có)
    @ManyToOne
    @JoinColumn(name = "LvsUserId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsUser;

    @ManyToOne
    @JoinColumn(name = "LvsProjectId", referencedColumnName = "LvsProjectId")
    private LvsProject lvsProject;

    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsPostStatus lvsStatus = LvsPostStatus.DRAFT;

    // Thống kê
    @Column(name = "LvsViewCount")
    private Integer lvsViewCount = 0;

    @Column(name = "LvsLikeCount")
    private Integer lvsLikeCount = 0;

    @Column(name = "LvsCommentCount")
    private Integer lvsCommentCount = 0;

    @Column(name = "LvsShareCount")
    private Integer lvsShareCount = 0;

    // Ghim và duyệt
    @Column(name = "LvsIsPinned")
    private Boolean lvsIsPinned = false;

    @Column(name = "LvsIsApproved")
    private Boolean lvsIsApproved = true;

    // Tags
    @Column(name = "LvsTags", length = 500)
    private String lvsTags;

    // Ảnh đại diện
    @Column(name = "LvsThumbnailUrl", length = 500)
    private String lvsThumbnailUrl;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Quan hệ
    @OneToMany(mappedBy = "lvsPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsComment> lvsComments = new ArrayList<>();

    @OneToMany(mappedBy = "lvsPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LvsPostImage> lvsImages = new ArrayList<>();

    // Enum
    public enum LvsPostType {
        DISCUSSION, TUTORIAL, REVIEW, NEWS, SHOWCASE, QUESTION, ANNOUNCEMENT
    }

    public enum LvsPostStatus {
        DRAFT, PUBLISHED, HIDDEN, DELETED
    }

    // Getters and Setters

    public Long getLvsPostId() {
        return lvsPostId;
    }

    public void setLvsPostId(Long lvsPostId) {
        this.lvsPostId = lvsPostId;
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

    public LvsPostType getLvsType() {
        return lvsType;
    }

    public void setLvsType(LvsPostType lvsType) {
        this.lvsType = lvsType;
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

    public LvsPostStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsPostStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public Integer getLvsViewCount() {
        return lvsViewCount;
    }

    public void setLvsViewCount(Integer lvsViewCount) {
        this.lvsViewCount = lvsViewCount;
    }

    public Integer getLvsLikeCount() {
        return lvsLikeCount;
    }

    public void setLvsLikeCount(Integer lvsLikeCount) {
        this.lvsLikeCount = lvsLikeCount;
    }

    public Integer getLvsCommentCount() {
        return lvsCommentCount;
    }

    public void setLvsCommentCount(Integer lvsCommentCount) {
        this.lvsCommentCount = lvsCommentCount;
    }

    public Integer getLvsShareCount() {
        return lvsShareCount;
    }

    public void setLvsShareCount(Integer lvsShareCount) {
        this.lvsShareCount = lvsShareCount;
    }

    public Boolean getLvsIsPinned() {
        return lvsIsPinned;
    }

    public void setLvsIsPinned(Boolean lvsIsPinned) {
        this.lvsIsPinned = lvsIsPinned;
    }

    public Boolean getLvsIsApproved() {
        return lvsIsApproved;
    }

    public void setLvsIsApproved(Boolean lvsIsApproved) {
        this.lvsIsApproved = lvsIsApproved;
    }

    public String getLvsTags() {
        return lvsTags;
    }

    public void setLvsTags(String lvsTags) {
        this.lvsTags = lvsTags;
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

    public List<LvsComment> getLvsComments() {
        return lvsComments;
    }

    public void setLvsComments(List<LvsComment> lvsComments) {
        this.lvsComments = lvsComments;
    }

    public String getLvsThumbnailUrl() {
        return lvsThumbnailUrl;
    }

    public void setLvsThumbnailUrl(String lvsThumbnailUrl) {
        this.lvsThumbnailUrl = lvsThumbnailUrl;
    }

    public List<LvsPostImage> getLvsImages() {
        return lvsImages;
    }

    public void setLvsImages(List<LvsPostImage> lvsImages) {
        this.lvsImages = lvsImages;
    }
}