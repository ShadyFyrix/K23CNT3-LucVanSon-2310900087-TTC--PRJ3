package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsFollow")
public class LvsFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsFollowId")
    private Long lvsFollowId;

    @ManyToOne
    @JoinColumn(name = "LvsFollowerId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsFollower;

    @ManyToOne
    @JoinColumn(name = "LvsFollowingId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsFollowing;

    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    // Getters and Setters

    public Long getLvsFollowId() {
        return lvsFollowId;
    }

    public void setLvsFollowId(Long lvsFollowId) {
        this.lvsFollowId = lvsFollowId;
    }

    public LvsUser getLvsFollower() {
        return lvsFollower;
    }

    public void setLvsFollower(LvsUser lvsFollower) {
        this.lvsFollower = lvsFollower;
    }

    public LvsUser getLvsFollowing() {
        return lvsFollowing;
    }

    public void setLvsFollowing(LvsUser lvsFollowing) {
        this.lvsFollowing = lvsFollowing;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

}