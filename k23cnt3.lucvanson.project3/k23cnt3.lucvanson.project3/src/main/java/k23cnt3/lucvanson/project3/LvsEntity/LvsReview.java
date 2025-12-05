package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsReview")
@Data
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
}