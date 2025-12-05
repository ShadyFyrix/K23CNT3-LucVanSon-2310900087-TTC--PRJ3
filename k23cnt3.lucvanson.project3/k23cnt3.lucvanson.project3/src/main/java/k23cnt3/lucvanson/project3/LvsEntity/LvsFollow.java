package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsFollow")
@Data
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
}