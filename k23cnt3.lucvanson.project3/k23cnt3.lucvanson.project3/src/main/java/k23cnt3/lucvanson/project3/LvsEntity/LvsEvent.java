package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for Events/Rewards system
 * Manages daily login, tasks, and special events
 */
@Entity
@Table(name = "lvsevent")
public class LvsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lvseventid")
    private Long lvsEventId;

    @Column(name = "lvseventname", nullable = false, length = 200)
    private String lvsEventName;

    @Column(name = "lvsdescription", columnDefinition = "TEXT")
    private String lvsDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "lvstype", nullable = false)
    private LvsEventType lvsType;

    @Column(name = "lvsrewardcoins", nullable = false)
    private Integer lvsRewardCoins;

    @Column(name = "lvsisactive", nullable = false)
    private Boolean lvsIsActive = true;

    @Column(name = "lvsstartdate")
    private LocalDateTime lvsStartDate;

    @Column(name = "lvsenddate")
    private LocalDateTime lvsEndDate;

    @Column(name = "lvscreatedat")
    private LocalDateTime lvsCreatedAt;

    @Column(name = "lvsmilestone")
    private Integer lvsMilestone; // Streak milestone (7, 14, 30, etc.)

    @Column(name = "lvsmilestonecoins")
    private Integer lvsMilestoneCoins; // Bonus coins for milestone

    // Event types
    public enum LvsEventType {
        DAILY_LOGIN, // Daily check-in
        DAILY_TASK, // Daily tasks (comment, upload, etc.)
        SPECIAL // Special events
    }

    // Constructors
    public LvsEvent() {
        this.lvsCreatedAt = LocalDateTime.now();
        this.lvsIsActive = true;
    }

    // Getters and Setters
    public Long getLvsEventId() {
        return lvsEventId;
    }

    public void setLvsEventId(Long lvsEventId) {
        this.lvsEventId = lvsEventId;
    }

    public String getLvsEventName() {
        return lvsEventName;
    }

    public void setLvsEventName(String lvsEventName) {
        this.lvsEventName = lvsEventName;
    }

    public String getLvsDescription() {
        return lvsDescription;
    }

    public void setLvsDescription(String lvsDescription) {
        this.lvsDescription = lvsDescription;
    }

    public LvsEventType getLvsType() {
        return lvsType;
    }

    public void setLvsType(LvsEventType lvsType) {
        this.lvsType = lvsType;
    }

    public Integer getLvsRewardCoins() {
        return lvsRewardCoins;
    }

    public void setLvsRewardCoins(Integer lvsRewardCoins) {
        this.lvsRewardCoins = lvsRewardCoins;
    }

    public Boolean getLvsIsActive() {
        return lvsIsActive;
    }

    public void setLvsIsActive(Boolean lvsIsActive) {
        this.lvsIsActive = lvsIsActive;
    }

    public LocalDateTime getLvsStartDate() {
        return lvsStartDate;
    }

    public void setLvsStartDate(LocalDateTime lvsStartDate) {
        this.lvsStartDate = lvsStartDate;
    }

    public LocalDateTime getLvsEndDate() {
        return lvsEndDate;
    }

    public void setLvsEndDate(LocalDateTime lvsEndDate) {
        this.lvsEndDate = lvsEndDate;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

    public Integer getLvsMilestone() {
        return lvsMilestone;
    }

    public void setLvsMilestone(Integer lvsMilestone) {
        this.lvsMilestone = lvsMilestone;
    }

    public Integer getLvsMilestoneCoins() {
        return lvsMilestoneCoins;
    }

    public void setLvsMilestoneCoins(Integer lvsMilestoneCoins) {
        this.lvsMilestoneCoins = lvsMilestoneCoins;
    }
}
