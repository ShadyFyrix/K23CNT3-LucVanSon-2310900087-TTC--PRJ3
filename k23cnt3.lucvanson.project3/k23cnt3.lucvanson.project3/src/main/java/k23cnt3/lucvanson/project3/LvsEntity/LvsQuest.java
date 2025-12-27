package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Quest definition entity
 */
@Entity
@Table(name = "lvsquest")
public class LvsQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lvsquestid")
    private Long lvsQuestId;

    @Column(name = "lvsquestname", nullable = false, length = 200)
    private String lvsQuestName;

    @Column(name = "lvsquestdesc", columnDefinition = "TEXT")
    private String lvsQuestDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "lvsquesttype", nullable = false, length = 50)
    private LvsQuestType lvsQuestType;

    @Column(name = "lvstargetcount", nullable = false)
    private Integer lvsTargetCount; // Milestone to reach

    @Column(name = "lvsrewardcoins", nullable = false)
    private Integer lvsRewardCoins;

    @Column(name = "lvsisactive", nullable = false)
    private Boolean lvsIsActive = true;

    @Column(name = "lvscreatedat")
    private LocalDateTime lvsCreatedAt;

    // Quest types
    public enum LvsQuestType {
        VIEW_POST,
        VIEW_PROJECT,
        VIEW_USER,
        FOLLOW_USER,
        CREATE_COMMENT,
        CREATE_POST,
        CREATE_PROJECT,
        MAKE_PURCHASE
    }

    // Constructors
    public LvsQuest() {
        this.lvsCreatedAt = LocalDateTime.now();
        this.lvsIsActive = true;
    }

    // Getters and Setters
    public Long getLvsQuestId() {
        return lvsQuestId;
    }

    public void setLvsQuestId(Long lvsQuestId) {
        this.lvsQuestId = lvsQuestId;
    }

    public String getLvsQuestName() {
        return lvsQuestName;
    }

    public void setLvsQuestName(String lvsQuestName) {
        this.lvsQuestName = lvsQuestName;
    }

    public String getLvsQuestDesc() {
        return lvsQuestDesc;
    }

    public void setLvsQuestDesc(String lvsQuestDesc) {
        this.lvsQuestDesc = lvsQuestDesc;
    }

    public LvsQuestType getLvsQuestType() {
        return lvsQuestType;
    }

    public void setLvsQuestType(LvsQuestType lvsQuestType) {
        this.lvsQuestType = lvsQuestType;
    }

    public Integer getLvsTargetCount() {
        return lvsTargetCount;
    }

    public void setLvsTargetCount(Integer lvsTargetCount) {
        this.lvsTargetCount = lvsTargetCount;
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

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }
}
