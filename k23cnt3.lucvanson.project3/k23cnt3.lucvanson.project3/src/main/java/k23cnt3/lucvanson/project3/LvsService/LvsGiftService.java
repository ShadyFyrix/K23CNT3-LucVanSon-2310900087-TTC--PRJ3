package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LvsGiftService {

    /**
     * Send a gift to a follower
     * Validates:
     * - Sender owns the project
     * - Recipient is a follower
     * - Recipient doesn't already own the project
     * - No pending gift exists
     */
    LvsGift lvsSendGift(Long senderId, Long recipientId, Long projectId, String message);

    /**
     * Accept a gift
     * - Adds project to recipient's library
     * - Updates gift status to ACCEPTED
     */
    void lvsAcceptGift(Long giftId, Long userId);

    /**
     * Reject a gift
     * - Updates gift status to REJECTED
     */
    void lvsRejectGift(Long giftId, Long userId);

    /**
     * Get gifts sent by a user
     */
    Page<LvsGift> lvsGetGiftsSent(Long userId, Pageable pageable);

    /**
     * Get gifts received by a user
     */
    Page<LvsGift> lvsGetGiftsReceived(Long userId, Pageable pageable);

    /**
     * Get count of pending gifts for a user
     */
    int lvsGetPendingGiftsCount(Long userId);
}
