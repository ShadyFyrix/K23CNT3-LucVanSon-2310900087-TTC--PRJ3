package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LvsGiftRepository extends JpaRepository<LvsGift, Long> {

    // Find gifts sent by a user
    Page<LvsGift> findByLvsSenderLvsUserId(Long senderId, Pageable pageable);

    // Find gifts received by a user
    Page<LvsGift> findByLvsRecipientLvsUserId(Long recipientId, Pageable pageable);

    // Find pending gifts for a recipient
    Page<LvsGift> findByLvsRecipientLvsUserIdAndLvsStatus(
            Long recipientId, LvsGift.LvsGiftStatus status, Pageable pageable);

    // Check if gift already exists (prevent duplicates)
    boolean existsByLvsSenderLvsUserIdAndLvsRecipientLvsUserIdAndLvsProjectLvsProjectIdAndLvsStatus(
            Long senderId, Long recipientId, Long projectId, LvsGift.LvsGiftStatus status);

    // Count pending gifts for a user
    int countByLvsRecipientLvsUserIdAndLvsStatus(Long recipientId, LvsGift.LvsGiftStatus status);
}
