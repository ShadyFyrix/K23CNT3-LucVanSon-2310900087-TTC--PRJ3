package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsUser")
@Data
public class LvsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsUserId")
    private Long lvsUserId;

    // Thông tin đăng nhập
    @Column(name = "LvsUsername", unique = true, nullable = false, length = 50)
    private String lvsUsername;

    @Column(name = "LvsPassword", nullable = false, length = 255)
    private String lvsPassword;

    @Column(name = "LvsEmail", unique = true, nullable = false, length = 100)
    private String lvsEmail;

    // Thông tin cá nhân
    @Column(name = "LvsFullName", length = 100)
    private String lvsFullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "LvsRole")
    private LvsRole lvsRole = LvsRole.USER;

    // Số dư và coin
    @Column(name = "LvsCoin", precision = 15, scale = 2)
    private Double lvsCoin = 0.0;

    @Column(name = "LvsBalance", precision = 15, scale = 2)
    private Double lvsBalance = 0.0; // Doanh thu từ bán dự án

    // Avatar và trạng thái
    @Column(name = "LvsAvatarUrl", length = 255)
    private String lvsAvatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "LvsStatus")
    private LvsUserStatus lvsStatus = LvsUserStatus.ACTIVE;

    // Thông tin liên hệ
    @Column(name = "LvsPhone", length = 15)
    private String lvsPhone;

    @Column(name = "LvsAddress", length = 500)
    private String lvsAddress;

    @Column(name = "LvsBio", columnDefinition = "TEXT")
    private String lvsBio;

    // Thời gian
    @Column(name = "LvsLastLogin")
    private LocalDateTime lvsLastLogin;

    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();

    // Quan hệ
    @OneToMany(mappedBy = "lvsUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsProject> lvsProjects = new ArrayList<>();

    @OneToMany(mappedBy = "lvsUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsPost> lvsPosts = new ArrayList<>();

    @OneToMany(mappedBy = "lvsBuyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsOrder> lvsOrders = new ArrayList<>();

    @OneToMany(mappedBy = "lvsUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsTransaction> lvsTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "lvsReporter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsReport> lvsReports = new ArrayList<>();

    @OneToMany(mappedBy = "lvsSender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsMessage> lvsSentMessages = new ArrayList<>();

    @OneToMany(mappedBy = "lvsReceiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsMessage> lvsReceivedMessages = new ArrayList<>();

    @OneToOne(mappedBy = "lvsUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LvsCart lvsCart;

    @OneToMany(mappedBy = "lvsUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LvsReview> lvsReviews = new ArrayList<>();

    // Enum
    public enum LvsRole {
        ADMIN, MODERATOR, USER
    }

    public enum LvsUserStatus {
        ACTIVE, INACTIVE, BANNED
    }
}