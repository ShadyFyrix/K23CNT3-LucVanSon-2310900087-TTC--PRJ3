package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent.LvsEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LvsEventRepository extends JpaRepository<LvsEvent, Long> {

    // Find active events by type
    List<LvsEvent> findByLvsTypeAndLvsIsActiveTrue(LvsEventType lvsType);

    // Find all active events
    List<LvsEvent> findByLvsIsActiveTrue();

    // Find events within date range
    List<LvsEvent> findByLvsIsActiveTrueAndLvsStartDateBeforeAndLvsEndDateAfter(
            LocalDateTime now1, LocalDateTime now2);
}
