package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User quest progress tracking
 */
@Entity
@Table(name = "lvsuserquest")
public class LvsUserQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lvsuserquestid")
    private Long lvsUserQuestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lvsuserid", nullable = false)
    private LvsUser lvsUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lvsquestid", nullable = false)
    private LvsQuest lvsQuest;

    @Column(name = "lvscurrentcount", nullable = false)
    private Integer lvsCurrentCount = 0;

    @Column(name = "lvscompleted", nullable = false)
    private Boolean lvsCompleted = false;

    @Column(name = "lvscompleteddat")
    private LocalDateTime lvsCompletedAt;

    @Column(name = "lvsclaimed", nullable = false)
    private Boolean lvsClaimed = false;

    @Column(name = "lvsclaimedat")
    private LocalDateTime lvsClaimedAt;

    @Column(name = "lvscreatedat")
    private LocalDateTime lvsCreatedAt;

    // Constructors
    public LvsUserQuest() {
        this.lvsCreatedAt = LocalDateTime.now();
        this.lvsCurrentCount = 0;
        this.lvsCompleted = false;
        this.lvsClaimed = false;
    }

    public LvsUserQuest(LvsUser lvsUser, LvsQuest lvsQuest) {
        this();
        this.lvsUser = lvsUser;
        this.lvsQuest = lvsQuest;
    }

    // Getters and Setters
    public Long getLvsUserQuestId() {
        return lvsUserQuestId;
    }

    public void setLvsUserQuestId(Long lvsUserQuestId) {
        this.lvsUserQuestId = lvsUserQuestId;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public LvsQuest getLvsQuest() {
        return lvsQuest;
    }

    public void setLvsQuest(LvsQuest lvsQuest) {
        this.lvsQuest = lvsQuest;
    }

    public Integer getLvsCurrentCount() {
        return lvsCurrentCount;
    }

    public void setLvsCurrentCount(Integer lvsCurrentCount) {
        this.lvsCurrentCount = lvsCurrentCount;
    }

    public Boolean getLvsCompleted() {
        return lvsCompleted;
    }

    public void setLvsCompleted(Boolean lvsCompleted) {
        this.lvsCompleted = lvsCompleted;
    }

    public LocalDateTime getLvsCompletedAt() {
        return lvsCompletedAt;
    }

    public void setLvsCompletedAt(LocalDateTime lvsCompletedAt) {
        this.lvsCompletedAt = lvsCompletedAt;
    }

    public Boolean getLvsClaimed() {
        return lvsClaimed;
    }

    public void setLvsClaimed(Boolean lvsClaimed) {
        this.lvsClaimed = lvsClaimed;
    }

    public LocalDateTime getLvsClaimedAt() {
        return lvsClaimedAt;
    }

    public void setLvsClaimedAt(LocalDateTime lvsClaimedAt) {
        this.lvsClaimedAt = lvsClaimedAt;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }
}
