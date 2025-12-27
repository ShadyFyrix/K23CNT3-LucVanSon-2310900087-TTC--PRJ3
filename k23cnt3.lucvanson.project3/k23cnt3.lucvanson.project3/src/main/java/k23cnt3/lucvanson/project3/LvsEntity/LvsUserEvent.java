package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity tracking user participation in events
 */
@Entity
@Table(name = "lvsuserevent")
public class LvsUserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lvsusereventid")
    private Long lvsUserEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lvsuserid", nullable = false)
    private LvsUser lvsUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lvseventid", nullable = false)
    private LvsEvent lvsEvent;

    @Column(name = "lvscompletedat", nullable = false)
    private LocalDateTime lvsCompletedAt;

    @Column(name = "lvscoinsearn", nullable = false)
    private Integer lvsCoinsEarned;

    @Column(name = "lvsclaimed", nullable = false)
    private Boolean lvsClaimed = false;

    @Column(name = "lvsclaimedat")
    private LocalDateTime lvsClaimedAt;

    // Constructors
    public LvsUserEvent() {
        this.lvsCompletedAt = LocalDateTime.now();
        this.lvsClaimed = false;
    }

    public LvsUserEvent(LvsUser lvsUser, LvsEvent lvsEvent, Integer lvsCoinsEarned) {
        this.lvsUser = lvsUser;
        this.lvsEvent = lvsEvent;
        this.lvsCoinsEarned = lvsCoinsEarned;
        this.lvsCompletedAt = LocalDateTime.now();
        this.lvsClaimed = false;
    }

    // Getters and Setters
    public Long getLvsUserEventId() {
        return lvsUserEventId;
    }

    public void setLvsUserEventId(Long lvsUserEventId) {
        this.lvsUserEventId = lvsUserEventId;
    }

    public LvsUser getLvsUser() {
        return lvsUser;
    }

    public void setLvsUser(LvsUser lvsUser) {
        this.lvsUser = lvsUser;
    }

    public LvsEvent getLvsEvent() {
        return lvsEvent;
    }

    public void setLvsEvent(LvsEvent lvsEvent) {
        this.lvsEvent = lvsEvent;
    }

    public LocalDateTime getLvsCompletedAt() {
        return lvsCompletedAt;
    }

    public void setLvsCompletedAt(LocalDateTime lvsCompletedAt) {
        this.lvsCompletedAt = lvsCompletedAt;
    }

    public Integer getLvsCoinsEarned() {
        return lvsCoinsEarned;
    }

    public void setLvsCoinsEarned(Integer lvsCoinsEarned) {
        this.lvsCoinsEarned = lvsCoinsEarned;
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
}
