package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsPost")
@Data
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

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Quan hệ
    @OneToMany(mappedBy = "lvsPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsComment> lvsComments = new ArrayList<>();

    // Enum
    public enum LvsPostType {
        DISCUSSION, TUTORIAL, REVIEW, NEWS, SHOWCASE, QUESTION, ANNOUNCEMENT
    }

    public enum LvsPostStatus {
        DRAFT, PUBLISHED, HIDDEN, DELETED
    }
}