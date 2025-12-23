package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho entity LvsMessage
 * Xử lý truy vấn liên quan đến tin nhắn
 */
@Repository
public interface LvsMessageRepository extends JpaRepository<LvsMessage, Long> {

        // Tìm tin nhắn theo sender
        List<LvsMessage> findByLvsSender_LvsUserId(Long lvsUserId);

        Page<LvsMessage> findByLvsSender_LvsUserId(Long lvsUserId, Pageable pageable);

        // Tìm tin nhắn theo receiver
        List<LvsMessage> findByLvsReceiver_LvsUserId(Long lvsUserId);

        Page<LvsMessage> findByLvsReceiver_LvsUserId(Long lvsUserId, Pageable pageable);

        // Tìm tin nhắn giữa 2 user
        List<LvsMessage> findByLvsSender_LvsUserIdAndLvsReceiver_LvsUserId(Long lvsSenderId, Long lvsReceiverId);

        Page<LvsMessage> findByLvsSender_LvsUserIdAndLvsReceiver_LvsUserId(Long lvsSenderId, Long lvsReceiverId,
                        Pageable pageable);

        // Tìm tin nhắn chưa đọc
        List<LvsMessage> findByLvsReceiver_LvsUserIdAndLvsIsReadFalse(Long lvsUserId);

        // Đếm tin nhắn chưa đọc
        Long countByLvsReceiver_LvsUserIdAndLvsIsReadFalse(Long lvsUserId);

        // Tìm kiếm tin nhắn theo nội dung
        Page<LvsMessage> findByLvsContentContaining(String content, Pageable pageable);

        // Tìm tin nhắn theo receiver sắp xếp theo ngày tạo giảm dần
        List<LvsMessage> findByLvsReceiver_LvsUserIdOrderByLvsCreatedAtDesc(Long receiverId);

        // Tìm tin nhắn theo sender sắp xếp theo ngày tạo giảm dần
        List<LvsMessage> findByLvsSender_LvsUserIdOrderByLvsCreatedAtDesc(Long senderId);

        // Tìm tin nhắn theo sender hoặc receiver
        Page<LvsMessage> findByLvsSender_LvsUserIdOrLvsReceiver_LvsUserId(Long senderId, Long receiverId,
                        Pageable pageable);

        // Tìm cuộc trò chuyện giữa 2 users
        @Query("SELECT m FROM LvsMessage m WHERE " +
                        "(m.lvsSender.lvsUserId = :userId1 AND m.lvsReceiver.lvsUserId = :userId2) OR " +
                        "(m.lvsSender.lvsUserId = :userId2 AND m.lvsReceiver.lvsUserId = :userId1) " +
                        "ORDER BY m.lvsCreatedAt DESC")
        Page<LvsMessage> findConversationBetweenUsers(@Param("userId1") Long userId1,
                        @Param("userId2") Long userId2,
                        Pageable pageable);

        // Tìm các cuộc trò chuyện riêng biệt của user
        @Query("SELECT DISTINCT m FROM LvsMessage m WHERE " +
                        "m.lvsSender.lvsUserId = :userId OR m.lvsReceiver.lvsUserId = :userId " +
                        "ORDER BY m.lvsCreatedAt DESC")
        List<LvsMessage> findDistinctConversationsByUserId(@Param("userId") Long userId);

        // Tìm tin nhắn giữa 2 users (cả 2 chiều)
        List<LvsMessage> findByLvsSender_LvsUserIdAndLvsReceiver_LvsUserIdOrLvsSender_LvsUserIdAndLvsReceiver_LvsUserId(
                        Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);

        // Tìm tin nhắn chưa đọc từ sender đến receiver
        List<LvsMessage> findByLvsSender_LvsUserIdAndLvsReceiver_LvsUserIdAndLvsIsReadFalse(Long senderId,
                        Long receiverId);

        // Đếm tin nhắn chưa đọc từ sender đến receiver
        Long countByLvsSender_LvsUserIdAndLvsReceiver_LvsUserIdAndLvsIsReadFalse(Long senderId, Long receiverId);

        // Tìm kiếm tin nhắn của user theo nội dung
        @Query("SELECT m FROM LvsMessage m WHERE " +
                        "(m.lvsSender.lvsUserId = :userId OR m.lvsReceiver.lvsUserId = :userId) AND " +
                        "LOWER(m.lvsContent) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<LvsMessage> findByUserAndContentContaining(@Param("userId") Long userId,
                        @Param("keyword") String keyword);

        // Tìm tin nhắn mới nhất của user
        @Query(value = "SELECT * FROM lvs_message WHERE " +
                        "lvs_sender_id = :userId OR lvs_receiver_id = :userId " +
                        "ORDER BY lvs_created_at DESC LIMIT :limit", nativeQuery = true)
        List<LvsMessage> findLatestMessagesByUserId(@Param("userId") Long userId,
                        @Param("limit") int limit);

        // Đếm tin nhắn theo loại
        @Query("SELECT m.lvsMessageType, COUNT(m) FROM LvsMessage m GROUP BY m.lvsMessageType")
        List<Object[]> countMessagesByType();

        // Đếm tất cả tin nhắn chưa đọc
        Long countByLvsIsReadFalse();

        // Tìm tin nhắn theo type
        List<LvsMessage> findByLvsMessageType(String lvsMessageType);

        // Tìm kiếm tin nhắn theo content
        @Query("SELECT m FROM LvsMessage m WHERE " +
                        "LOWER(m.lvsContent) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<LvsMessage> searchMessages(@Param("keyword") String keyword, Pageable pageable);

        // Tìm kiếm tin nhắn của user theo content
        @Query("SELECT m FROM LvsMessage m WHERE (m.lvsSender.lvsUserId = :userId OR m.lvsReceiver.lvsUserId = :userId) AND "
                        +
                        "LOWER(m.lvsContent) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<LvsMessage> searchMessagesByUser(@Param("userId") Long userId, @Param("keyword") String keyword,
                        Pageable pageable);

        // Đếm tin nhắn theo sender
        Long countByLvsSender_LvsUserId(Long lvsUserId);

        // Đếm tin nhắn theo receiver
        Long countByLvsReceiver_LvsUserId(Long lvsUserId);

        // Đếm tin nhắn trong khoảng thời gian
        Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Lấy tin nhắn mới nhất
        List<LvsMessage> findByOrderByLvsCreatedAtDesc();

        Page<LvsMessage> findByOrderByLvsCreatedAtDesc(Pageable pageable);

        // Đánh dấu đã đọc
        @Modifying
        @Query("UPDATE LvsMessage m SET m.lvsIsRead = true WHERE m.lvsReceiver.lvsUserId = :receiverId AND m.lvsSender.lvsUserId = :senderId")
        void markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

        // Đánh dấu tin nhắn cụ thể đã đọc
        @Modifying
        @Query("UPDATE LvsMessage m SET m.lvsIsRead = true WHERE m.lvsMessageId = :messageId")
        void markMessageAsRead(@Param("messageId") Long messageId);

        // Đánh dấu tin nhắn đã đọc (alias method)
        @Modifying
        @Query("UPDATE LvsMessage m SET m.lvsIsRead = true WHERE m.lvsMessageId = :messageId")
        void markAsRead(@Param("messageId") Long messageId);

        // Tìm tin nhắn chưa đọc theo receiver ID
        @Query("SELECT m FROM LvsMessage m WHERE m.lvsReceiver.lvsUserId = :receiverId AND m.lvsIsRead = false ORDER BY m.lvsCreatedAt DESC")
        List<LvsMessage> findUnreadMessagesByReceiverId(@Param("receiverId") Long receiverId);

        // Đánh dấu tất cả đã đọc
        @Modifying
        @Query("UPDATE LvsMessage m SET m.lvsIsRead = true WHERE m.lvsReceiver.lvsUserId = :userId")
        void markAllAsRead(@Param("userId") Long userId);

        // Xóa tin nhắn giữa 2 user
        @Modifying
        @Query("DELETE FROM LvsMessage m WHERE " +
                        "(m.lvsSender.lvsUserId = :userId1 AND m.lvsReceiver.lvsUserId = :userId2) OR " +
                        "(m.lvsSender.lvsUserId = :userId2 AND m.lvsReceiver.lvsUserId = :userId1)")
        void deleteConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

        // Lấy cuộc trò chuyện gần đây
        @Query("SELECT m FROM LvsMessage m WHERE " +
                        "(m.lvsSender.lvsUserId = :userId OR m.lvsReceiver.lvsUserId = :userId) AND " +
                        "m.lvsCreatedAt = (SELECT MAX(m2.lvsCreatedAt) FROM LvsMessage m2 WHERE " +
                        "(m2.lvsSender.lvsUserId = m.lvsSender.lvsUserId AND m2.lvsReceiver.lvsUserId = m.lvsReceiver.lvsUserId) OR "
                        +
                        "(m2.lvsSender.lvsUserId = m.lvsReceiver.lvsUserId AND m2.lvsReceiver.lvsUserId = m.lvsSender.lvsUserId)) "
                        +
                        "ORDER BY m.lvsCreatedAt DESC")
        Page<LvsMessage> findRecentConversations(@Param("userId") Long userId, Pageable pageable);

        // Lấy tin nhắn có file đính kèm
        List<LvsMessage> findByLvsAttachmentUrlIsNotNull();

        // Lấy tin nhắn đã chỉnh sửa
        List<LvsMessage> findByLvsIsEditedTrue();

        // Đếm tổng số tin nhắn
        @Query("SELECT COUNT(m) FROM LvsMessage m")
        Long countAllMessages();

        // Đếm tin nhắn hôm nay
        @Query("SELECT COUNT(m) FROM LvsMessage m WHERE DATE(m.lvsCreatedAt) = CURRENT_DATE")
        Long countTodayMessages();
}