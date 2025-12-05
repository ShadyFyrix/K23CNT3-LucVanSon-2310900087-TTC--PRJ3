package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsComment")
@Data
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
    @ManyToOne
    @JoinColumn(name = "LvsParentId", referencedColumnName = "LvsCommentId")
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
}