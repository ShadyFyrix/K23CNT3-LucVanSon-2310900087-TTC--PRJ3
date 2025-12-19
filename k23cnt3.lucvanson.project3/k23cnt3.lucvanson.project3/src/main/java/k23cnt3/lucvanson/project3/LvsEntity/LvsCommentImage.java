package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity lưu trữ ảnh của bình luận
 * Một bình luận có thể có 1-5 ảnh
 */
@Entity
@Table(name = "LvsCommentImage")
public class LvsCommentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsImageId")
    private Long lvsImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LvsCommentId", nullable = false)
    private LvsComment lvsComment;

    @Column(name = "LvsImageUrl", length = 500, nullable = false)
    private String lvsImageUrl;

    @Column(name = "LvsImageOrder")
    private Integer lvsImageOrder = 0;

    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Constructors
    public LvsCommentImage() {
    }

    public LvsCommentImage(LvsComment lvsComment, String lvsImageUrl, Integer lvsImageOrder) {
        this.lvsComment = lvsComment;
        this.lvsImageUrl = lvsImageUrl;
        this.lvsImageOrder = lvsImageOrder;
    }

    // Getters and Setters
    public Long getLvsImageId() {
        return lvsImageId;
    }

    public void setLvsImageId(Long lvsImageId) {
        this.lvsImageId = lvsImageId;
    }

    public LvsComment getLvsComment() {
        return lvsComment;
    }

    public void setLvsComment(LvsComment lvsComment) {
        this.lvsComment = lvsComment;
    }

    public String getLvsImageUrl() {
        return lvsImageUrl;
    }

    public void setLvsImageUrl(String lvsImageUrl) {
        this.lvsImageUrl = lvsImageUrl;
    }

    public Integer getLvsImageOrder() {
        return lvsImageOrder;
    }

    public void setLvsImageOrder(Integer lvsImageOrder) {
        this.lvsImageOrder = lvsImageOrder;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }
}
