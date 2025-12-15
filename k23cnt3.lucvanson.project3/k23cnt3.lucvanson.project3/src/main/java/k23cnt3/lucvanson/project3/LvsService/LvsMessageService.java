package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsMessage;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý tin nhắn
 * Xử lý gửi, nhận, xóa, tìm kiếm tin nhắn
 */
public interface LvsMessageService {

    // Lấy tin nhắn theo ID
    LvsMessage lvsGetMessageById(Long lvsMessageId);

    // Lấy tất cả tin nhắn
    Page<LvsMessage> lvsGetAllMessages(Pageable lvsPageable);

    // Tìm kiếm tin nhắn
    Page<LvsMessage> lvsSearchMessages(String lvsKeyword, Pageable lvsPageable);

    // Lấy hộp thư đến
    List<LvsMessage> lvsGetInboxMessages(Long lvsUserId);

    // Lấy hộp thư đi
    List<LvsMessage> lvsGetSentMessages(Long lvsUserId);

    // Lấy tin nhắn theo user
    Page<LvsMessage> lvsGetMessagesByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy cuộc trò chuyện giữa 2 user
    Page<LvsMessage> lvsGetConversation(Long lvsUserId1, Long lvsUserId2, Pageable lvsPageable);

    // Lấy danh sách cuộc trò chuyện
    List<LvsUser> lvsGetConversations(Long lvsUserId);

    // Gửi tin nhắn
    LvsMessage lvsSendMessage(LvsMessage lvsMessage);

    // Lưu tin nhắn
    LvsMessage lvsSaveMessage(LvsMessage lvsMessage);

    // Xóa tin nhắn
    void lvsDeleteMessage(Long lvsMessageId);

    // Xóa tin nhắn của user
    void lvsDeleteMessage(Long lvsMessageId, Long lvsUserId);

    // Xóa cuộc trò chuyện
    void lvsDeleteConversation(Long lvsUserId1, Long lvsUserId2);

    // Đánh dấu đã đọc
    boolean lvsMarkAsRead(Long lvsUserId, Long lvsOtherUserId);

    // Đánh dấu tin nhắn đã đọc
    boolean lvsMarkMessageAsRead(Long lvsMessageId);

    // Đếm tin nhắn chưa đọc
    int lvsGetUnreadCount(Long lvsUserId);

    // Tìm kiếm tin nhắn của user
    List<LvsMessage> lvsSearchMessages(Long lvsUserId, String lvsKeyword);

    // Lấy tin nhắn mới nhất
    List<LvsMessage> lvsGetLatestMessages(Long lvsUserId, int lvsLimit);

    // Đếm tổng số tin nhắn
    Long lvsCountTotalMessages();

    // Đếm tin nhắn hôm nay
    Long lvsCountTodayMessages();

    // Đếm tin nhắn chưa đọc
    Long lvsCountUnreadMessages();

    // Lấy tin nhắn theo loại
    Map<String, Long> lvsGetMessagesByType();

    // Gửi tin nhắn hệ thống
    void lvsSendSystemMessage(Long lvsUserId, String lvsContent);

    // Gửi thông báo
    void lvsSendNotification(Long lvsUserId, String lvsTitle, String lvsContent);
}