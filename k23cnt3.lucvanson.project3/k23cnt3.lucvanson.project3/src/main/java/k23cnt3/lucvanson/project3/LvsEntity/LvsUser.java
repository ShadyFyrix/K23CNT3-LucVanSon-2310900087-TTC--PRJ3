package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LvsUser")
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
    @Column(name = "LvsCoin")
    private Double lvsCoin = 0.0;

    @Column(name = "LvsBalance")
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

    // Title/Badge dựa trên hoạt động
    @Enumerated(EnumType.STRING)
    @Column(name = "LvsTitle")
    private LvsUserTitle lvsTitle = LvsUserTitle.NEWBIE;

    // Activity Score (transient - not persisted)
    @Transient
    private Double lvsActivityScore;

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

    /**
     * User Title/Badge System
     * Titles are awarded based on monthly activity:
     * - Posts created
     * - Comments made
     * - Projects published
     * - Sales completed
     */
    public enum LvsUserTitle {
        // Default title
        NEWBIE("Newbie", "#9CA3AF"), // Gray - New users

        // Activity-based titles (monthly achievements)
        ACTIVE_MEMBER("Active Member", "#10B981"), // Green - 10+ comments/month
        CONTRIBUTOR("Contributor", "#3B82F6"), // Blue - 5+ posts/month
        CREATOR("Creator", "#8B5CF6"), // Purple - 3+ projects/month
        SELLER("Top Seller", "#F59E0B"), // Orange - 10+ sales/month

        // Combined achievements
        INFLUENCER("Influencer", "#EC4899"), // Pink - 20+ posts + 50+ comments/month
        MASTER_CREATOR("Master Creator", "#EF4444"), // Red - 10+ projects + 20+ sales/month

        // Special titles
        LEGEND("Legend", "#FBBF24"), // Gold - 50+ posts + 100+ comments + 20+ projects

        // Staff titles (assigned manually)
        MODERATOR("Moderator", "#06B6D4"), // Cyan - Moderator role
        ADMIN("Admin", "rainbow"); // Rainbow effect - Admin role

        private final String displayName;
        private final String color; // Color code or "rainbow" for special effect

        LvsUserTitle(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColor() {
            return color;
        }

        public boolean isRainbow() {
            return "rainbow".equals(color);
        }
    }

    @PreUpdate
    protected void lvsOnUpdate() {
        this.lvsUpdatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getLvsUserId() {
        return lvsUserId;
    }

    public void setLvsUserId(Long lvsUserId) {
        this.lvsUserId = lvsUserId;
    }

    public String getLvsUsername() {
        return lvsUsername;
    }

    public void setLvsUsername(String lvsUsername) {
        this.lvsUsername = lvsUsername;
    }

    public String getLvsPassword() {
        return lvsPassword;
    }

    public void setLvsPassword(String lvsPassword) {
        this.lvsPassword = lvsPassword;
    }

    public String getLvsEmail() {
        return lvsEmail;
    }

    public void setLvsEmail(String lvsEmail) {
        this.lvsEmail = lvsEmail;
    }

    public String getLvsFullName() {
        return lvsFullName;
    }

    public void setLvsFullName(String lvsFullName) {
        this.lvsFullName = lvsFullName;
    }

    public LvsRole getLvsRole() {
        return lvsRole;
    }

    public void setLvsRole(LvsRole lvsRole) {
        this.lvsRole = lvsRole;
    }

    public Double getLvsCoin() {
        return lvsCoin;
    }

    public void setLvsCoin(Double lvsCoin) {
        this.lvsCoin = lvsCoin;
    }

    public Double getLvsBalance() {
        return lvsBalance;
    }

    public void setLvsBalance(Double lvsBalance) {
        this.lvsBalance = lvsBalance;
    }

    public String getLvsAvatarUrl() {
        return lvsAvatarUrl;
    }

    public void setLvsAvatarUrl(String lvsAvatarUrl) {
        this.lvsAvatarUrl = lvsAvatarUrl;
    }

    public LvsUserStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsUserStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public String getLvsPhone() {
        return lvsPhone;
    }

    public void setLvsPhone(String lvsPhone) {
        this.lvsPhone = lvsPhone;
    }

    public String getLvsAddress() {
        return lvsAddress;
    }

    public void setLvsAddress(String lvsAddress) {
        this.lvsAddress = lvsAddress;
    }

    public String getLvsBio() {
        return lvsBio;
    }

    public void setLvsBio(String lvsBio) {
        this.lvsBio = lvsBio;
    }

    public LvsUserTitle getLvsTitle() {
        return lvsTitle;
    }

    public void setLvsTitle(LvsUserTitle lvsTitle) {
        this.lvsTitle = lvsTitle;
    }

    public LocalDateTime getLvsLastLogin() {
        return lvsLastLogin;
    }

    public void setLvsLastLogin(LocalDateTime lvsLastLogin) {
        this.lvsLastLogin = lvsLastLogin;
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

    public List<LvsProject> getLvsProjects() {
        return lvsProjects;
    }

    public void setLvsProjects(List<LvsProject> lvsProjects) {
        this.lvsProjects = lvsProjects;
    }

    public List<LvsPost> getLvsPosts() {
        return lvsPosts;
    }

    public void setLvsPosts(List<LvsPost> lvsPosts) {
        this.lvsPosts = lvsPosts;
    }

    public List<LvsOrder> getLvsOrders() {
        return lvsOrders;
    }

    public void setLvsOrders(List<LvsOrder> lvsOrders) {
        this.lvsOrders = lvsOrders;
    }

    public List<LvsTransaction> getLvsTransactions() {
        return lvsTransactions;
    }

    public void setLvsTransactions(List<LvsTransaction> lvsTransactions) {
        this.lvsTransactions = lvsTransactions;
    }

    public List<LvsReport> getLvsReports() {
        return lvsReports;
    }

    public void setLvsReports(List<LvsReport> lvsReports) {
        this.lvsReports = lvsReports;
    }

    public List<LvsMessage> getLvsSentMessages() {
        return lvsSentMessages;
    }

    public void setLvsSentMessages(List<LvsMessage> lvsSentMessages) {
        this.lvsSentMessages = lvsSentMessages;
    }

    public List<LvsMessage> getLvsReceivedMessages() {
        return lvsReceivedMessages;
    }

    public void setLvsReceivedMessages(List<LvsMessage> lvsReceivedMessages) {
        this.lvsReceivedMessages = lvsReceivedMessages;
    }

    public LvsCart getLvsCart() {
        return lvsCart;
    }

    public void setLvsCart(LvsCart lvsCart) {
        this.lvsCart = lvsCart;
    }

    public List<LvsReview> getLvsReviews() {
        return lvsReviews;
    }

    public void setLvsReviews(List<LvsReview> lvsReviews) {
        this.lvsReviews = lvsReviews;
    }

    public Double getLvsActivityScore() {
        return lvsActivityScore;
    }

    public void setLvsActivityScore(Double lvsActivityScore) {
        this.lvsActivityScore = lvsActivityScore;
    }

}