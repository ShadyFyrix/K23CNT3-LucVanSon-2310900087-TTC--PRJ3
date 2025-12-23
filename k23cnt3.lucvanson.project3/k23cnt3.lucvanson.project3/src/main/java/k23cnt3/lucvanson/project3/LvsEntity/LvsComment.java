package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsComment")
public class LvsComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsCommentId")
    private Long lvsCommentId;

    // Bài viết và người bình luận
    @ManyToOne
    @JoinColumn(name = "LvsPostId", referencedColumnName = "LvsPostId", nullable = false)
    private LvsPost lvsPost;

    @ManyToOne
    @JoinColumn(name = "LvsUserId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsUser;

    // Nội dung
    @Column(name = "LvsContent", columnDefinition = "TEXT", nullable = false)
    private String lvsContent;

    // Bình luận cha (nếu là reply)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lvsParentCommentId", referencedColumnName = "LvsCommentId")
    private LvsComment lvsParent;

    // Thống kê
    @Column(name = "LvsLikeCount")
    private Integer lvsLikeCount = 0;

    // Trạng thái
    @Column(name = "LvsIsEdited")
    private Boolean lvsIsEdited = false;

    @Column(name = "LvsIsApproved")
    private Boolean lvsIsApproved = true;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Quan hệ (reply)
    @OneToMany(mappedBy = "lvsParent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsComment> lvsReplies = new ArrayList<>();

    @OneToMany(mappedBy = "lvsComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LvsCommentImage> lvsImages = new ArrayList<>();

    // Getters and Setters

    public Long getLvsCommentId() {
        return lvsCommentId;
    }

    public void setLvsCommentId(Long lvsCommentId) {
        this.lvsCommentId = lvsCommentId;
    }

    public LvsPost getLvsPost() {
        return lvsPost;
    }

    public void setLvsPost(LvsPost lvsPost) {
        this.lvsPost = lvsPost;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public String getLvsContent() {
        return lvsContent;
    }

    public void setLvsContent(String lvsContent) {
        this.lvsContent = lvsContent;
    }

    public LvsComment getLvsParent() {
        return lvsParent;
    }

    public void setLvsParent(LvsComment lvsParent) {
        this.lvsParent = lvsParent;
    }

    public Integer getLvsLikeCount() {
        return lvsLikeCount;
    }

    public void setLvsLikeCount(Integer lvsLikeCount) {
        this.lvsLikeCount = lvsLikeCount;
    }

    public Boolean getLvsIsEdited() {
        return lvsIsEdited;
    }

    public void setLvsIsEdited(Boolean lvsIsEdited) {
        this.lvsIsEdited = lvsIsEdited;
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

    public List<LvsComment> getLvsReplies() {
        return lvsReplies;
    }

    public void setLvsReplies(List<LvsComment> lvsReplies) {
        this.lvsReplies = lvsReplies;
    }

    public List<LvsCommentImage> getLvsImages() {
        return lvsImages;
    }

    public void setLvsImages(List<LvsCommentImage> lvsImages) {
        this.lvsImages = lvsImages;
    }
}