package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity lưu trữ ảnh của bài viết
 * Một bài viết có thể có nhiều ảnh (tối đa 50)
 */
@Entity
@Table(name = "LvsPostImage")
public class LvsPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsImageId")
    private Long lvsImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LvsPostId", nullable = false)
    private LvsPost lvsPost;

    @Column(name = "LvsImageUrl", length = 500, nullable = false)
    private String lvsImageUrl;

    @Column(name = "LvsImageOrder")
    private Integer lvsImageOrder = 0;

    @Column(name = "LvsCaption", length = 200)
    private String lvsCaption;

    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Constructors
    public LvsPostImage() {
    }

    public LvsPostImage(LvsPost lvsPost, String lvsImageUrl, Integer lvsImageOrder) {
        this.lvsPost = lvsPost;
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

    public LvsPost getLvsPost() {
        return lvsPost;
    }

    public void setLvsPost(LvsPost lvsPost) {
        this.lvsPost = lvsPost;
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

    public String getLvsCaption() {
        return lvsCaption;
    }

    public void setLvsCaption(String lvsCaption) {
        this.lvsCaption = lvsCaption;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }
}
