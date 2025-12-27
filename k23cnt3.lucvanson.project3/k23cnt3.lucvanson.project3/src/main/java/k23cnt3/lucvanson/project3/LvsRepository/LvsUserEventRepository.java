package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LvsUserEventRepository extends JpaRepository<LvsUserEvent, Long> {

    // Find user's event history
    List<LvsUserEvent> findByLvsUserOrderByLvsCompletedAtDesc(LvsUser lvsUser);

    // Check if user completed event today
    Optional<LvsUserEvent> findByLvsUserAndLvsEventAndLvsCompletedAtAfter(
            LvsUser lvsUser, LvsEvent lvsEvent, LocalDateTime startOfDay);

    // Find unclaimed rewards
    List<LvsUserEvent> findByLvsUserAndLvsClaimedFalse(LvsUser lvsUser);

    // Count user's login streak
    List<LvsUserEvent> findByLvsUserAndLvsEvent_LvsTypeOrderByLvsCompletedAtDesc(
            LvsUser lvsUser, LvsEvent.LvsEventType lvsType);
}
