package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsMessage;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsMessageRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation cho quản lý tin nhắn
 * Xử lý gửi, nhận, xóa, tìm kiếm tin nhắn
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsMessageServiceImpl implements LvsMessageService {

    private final LvsMessageRepository lvsMessageRepository;
    private final LvsUserRepository lvsUserRepository;

    /**
     * Lấy tin nhắn theo ID
     * @param lvsMessageId ID tin nhắn
     * @return Tin nhắn tìm thấy
     */
    @Override
    public LvsMessage lvsGetMessageById(Long lvsMessageId) {
        return lvsMessageRepository.findById(lvsMessageId).orElse(null);
    }

    /**
     * Lấy tất cả tin nhắn với phân trang
     * @param lvsPageable Thông tin phân trang
     * @return Trang tin nhắn
     */
    @Override
    public Page<LvsMessage> lvsGetAllMessages(Pageable lvsPageable) {
        return lvsMessageRepository.findAll(lvsPageable);
    }

    /**
     * Tìm kiếm tin nhắn theo keyword
     * @param lvsKeyword Từ khóa tìm kiếm
     * @param lvsPageable Thông tin phân trang
     * @return Trang tin nhắn tìm thấy
     */
    @Override
    public Page<LvsMessage> lvsSearchMessages(String lvsKeyword, Pageable lvsPageable) {
        return lvsMessageRepository.findByLvsContentContaining(lvsKeyword, lvsPageable);
    }

    /**
     * Lấy hộp thư đến
     * @param lvsUserId ID người dùng
     * @return Danh sách tin nhắn đến
     */
    @Override
    public List<LvsMessage> lvsGetInboxMessages(Long lvsUserId) {
        return lvsMessageRepository.findByLvsReceiver_LvsUserIdOrderByLvsCreatedAtDesc(lvsUserId);
    }

    /**
     * Lấy hộp thư đi
     * @param lvsUserId ID người dùng
     * @return Danh sách tin nhắn đi
     */
    @Override
    public List<LvsMessage> lvsGetSentMessages(Long lvsUserId) {
        return lvsMessageRepository.findByLvsSender_LvsUserIdOrderByLvsCreatedAtDesc(lvsUserId);
    }

    /**
     * Lấy tin nhắn theo user
     * @param lvsUserId ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang tin nhắn
     */
    @Override
    public Page<LvsMessage> lvsGetMessagesByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsMessageRepository.findByLvsSender_LvsUserIdOrLvsReceiver_LvsUserId(lvsUserId, lvsUserId, lvsPageable);
    }

    /**
     * Lấy cuộc trò chuyện giữa 2 user
     * @param lvsUserId1 ID user 1
     * @param lvsUserId2 ID user 2
     * @param lvsPageable Thông tin phân trang
     * @return Trang tin nhắn
     */
    @Override
    public Page<LvsMessage> lvsGetConversation(Long lvsUserId1, Long lvsUserId2, Pageable lvsPageable) {
        return lvsMessageRepository.findConversationBetweenUsers(lvsUserId1, lvsUserId2, lvsPageable);
    }

    /**
     * Lấy danh sách cuộc trò chuyện
     * @param lvsUserId ID người dùng
     * @return Danh sách user có trò chuyện
     */
    @Override
    public List<LvsUser> lvsGetConversations(Long lvsUserId) {
        List<LvsMessage> lvsMessages = lvsMessageRepository.findDistinctConversationsByUserId(lvsUserId);
        Set<Long> lvsUserIds = new HashSet<>();

        for (LvsMessage lvsMessage : lvsMessages) {
            if (lvsMessage.getLvsSender().getLvsUserId().equals(lvsUserId)) {
                lvsUserIds.add(lvsMessage.getLvsReceiver().getLvsUserId());
            } else {
                lvsUserIds.add(lvsMessage.getLvsSender().getLvsUserId());
            }
        }

        return lvsUserRepository.findAllById(lvsUserIds);
    }

    /**
     * Gửi tin nhắn
     * @param lvsMessage Thông tin tin nhắn
     * @return Tin nhắn đã gửi
     */
    @Override
    public LvsMessage lvsSendMessage(LvsMessage lvsMessage) {
        lvsMessage.setLvsCreatedAt(LocalDateTime.now());
        lvsMessage.setLvsUpdatedAt(LocalDateTime.now());
        lvsMessage.setLvsIsRead(false);
        return lvsMessageRepository.save(lvsMessage);
    }

    /**
     * Lưu tin nhắn
     * @param lvsMessage Thông tin tin nhắn
     * @return Tin nhắn đã lưu
     */
    @Override
    public LvsMessage lvsSaveMessage(LvsMessage lvsMessage) {
        return lvsMessageRepository.save(lvsMessage);
    }

    /**
     * Xóa tin nhắn
     * @param lvsMessageId ID tin nhắn
     */
    @Override
    public void lvsDeleteMessage(Long lvsMessageId) {
        lvsMessageRepository.deleteById(lvsMessageId);
    }

    /**
     * Xóa tin nhắn của user
     * @param lvsMessageId ID tin nhắn
     * @param lvsUserId ID người dùng
     */
    @Override
    public void lvsDeleteMessage(Long lvsMessageId, Long lvsUserId) {
        LvsMessage lvsMessage = lvsGetMessageById(lvsMessageId);
        if (lvsMessage != null &&
                (lvsMessage.getLvsSender().getLvsUserId().equals(lvsUserId) ||
                        lvsMessage.getLvsReceiver().getLvsUserId().equals(lvsUserId))) {
            lvsMessageRepository.delete(lvsMessage);
        }
    }

    /**
     * Xóa cuộc trò chuyện
     * @param lvsUserId1 ID user 1
     * @param lvsUserId2 ID user 2
     */
    @Override
    public void lvsDeleteConversation(Long lvsUserId1, Long lvsUserId2) {
        List<LvsMessage> lvsMessages = lvsMessageRepository.findByLvsSender_LvsUserIdAndLvsReceiver_LvsUserIdOrLvsSender_LvsUserIdAndLvsReceiver_LvsUserId(
                lvsUserId1, lvsUserId2, lvsUserId2, lvsUserId1);
        lvsMessageRepository.deleteAll(lvsMessages);
    }

    /**
     * Đánh dấu đã đọc
     * @param lvsUserId ID người dùng
     * @param lvsOtherUserId ID người kia
     * @return true nếu thành công
     */
    @Override
    public boolean lvsMarkAsRead(Long lvsUserId, Long lvsOtherUserId) {
        List<LvsMessage> lvsUnreadMessages = lvsMessageRepository
                .findByLvsSender_LvsUserIdAndLvsReceiver_LvsUserIdAndLvsIsReadFalse(lvsOtherUserId, lvsUserId);

        for (LvsMessage lvsMessage : lvsUnreadMessages) {
            lvsMessage.setLvsIsRead(true);
            lvsMessage.setLvsUpdatedAt(LocalDateTime.now());
            lvsMessageRepository.save(lvsMessage);
        }

        return !lvsUnreadMessages.isEmpty();
    }

    /**
     * Đánh dấu tin nhắn đã đọc
     * @param lvsMessageId ID tin nhắn
     * @return true nếu thành công
     */
    @Override
    public boolean lvsMarkMessageAsRead(Long lvsMessageId) {
        LvsMessage lvsMessage = lvsGetMessageById(lvsMessageId);
        if (lvsMessage != null) {
            lvsMessage.setLvsIsRead(true);
            lvsMessage.setLvsUpdatedAt(LocalDateTime.now());
            lvsMessageRepository.save(lvsMessage);
            return true;
        }
        return false;
    }

    /**
     * Đếm tin nhắn chưa đọc
     * @param lvsUserId ID người dùng
     * @return Số tin nhắn chưa đọc
     */
    @Override
    public int lvsGetUnreadCount(Long lvsUserId) {
        return lvsMessageRepository.countByLvsReceiver_LvsUserIdAndLvsIsReadFalse(lvsUserId);
    }

    /**
     * Tìm kiếm tin nhắn của user
     * @param lvsUserId ID người dùng
     * @param lvsKeyword Từ khóa tìm kiếm
     * @return Danh sách tin nhắn tìm thấy
     */
    @Override
    public List<LvsMessage> lvsSearchMessages(Long lvsUserId, String lvsKeyword) {
        return lvsMessageRepository.findByUserAndContentContaining(lvsUserId, lvsKeyword);
    }

    /**
     * Lấy tin nhắn mới nhất
     * @param lvsUserId ID người dùng
     * @param lvsLimit Giới hạn số lượng
     * @return Danh sách tin nhắn mới nhất
     */
    @Override
    public List<LvsMessage> lvsGetLatestMessages(Long lvsUserId, int lvsLimit) {
        return lvsMessageRepository.findLatestMessagesByUserId(lvsUserId, lvsLimit);
    }

    /**
     * Đếm tổng số tin nhắn
     * @return Tổng số tin nhắn
     */
    @Override
    public Long lvsCountTotalMessages() {
        return lvsMessageRepository.count();
    }

    /**
     * Đếm tin nhắn hôm nay
     * @return Số tin nhắn hôm nay
     */
    @Override
    public Long lvsCountTodayMessages() {
        LocalDateTime lvsStartOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime lvsEndOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return lvsMessageRepository.countByLvsCreatedAtBetween(lvsStartOfDay, lvsEndOfDay);
    }

    /**
     * Đếm tin nhắn chưa đọc
     * @return Số tin nhắn chưa đọc
     */
    @Override
    public Long lvsCountUnreadMessages() {
        return lvsMessageRepository.countByLvsIsReadFalse();
    }

    /**
     * Lấy tin nhắn theo loại
     * @return Map thống kê tin nhắn theo loại
     */
    @Override
    public Map<String, Long> lvsGetMessagesByType() {
        Map<String, Long> lvsStats = new HashMap<>();

        List<Object[]> lvsResults = lvsMessageRepository.countMessagesByType();
        for (Object[] result : lvsResults) {
            String lvsType = (String) result[0];
            Long lvsCount = (Long) result[1];
            lvsStats.put(lvsType, lvsCount);
        }

        return lvsStats;
    }

    /**
     * Gửi tin nhắn hệ thống
     * @param lvsUserId ID người dùng
     * @param lvsContent Nội dung tin nhắn
     */
    @Override
    public void lvsSendSystemMessage(Long lvsUserId, String lvsContent) {
        LvsUser lvsSystemUser = lvsUserRepository.findByLvsUsername("system").orElseGet(() -> {
            LvsUser lvsNewSystemUser = new LvsUser();
            lvsNewSystemUser.setLvsUsername("system");
            lvsNewSystemUser.setLvsEmail("system@example.com");
            lvsNewSystemUser.setLvsPassword("system");
            lvsNewSystemUser.setLvsFullName("Hệ thống");
            lvsNewSystemUser.setLvsRole(LvsUser.LvsRole.ADMIN);
            return lvsUserRepository.save(lvsNewSystemUser);
        });

        LvsUser lvsReceiver = lvsUserRepository.findById(lvsUserId).orElse(null);
        if (lvsReceiver != null) {
            LvsMessage lvsMessage = new LvsMessage();
            lvsMessage.setLvsSender(lvsSystemUser);
            lvsMessage.setLvsReceiver(lvsReceiver);
            lvsMessage.setLvsContent(lvsContent);
            lvsMessage.setLvsMessageType("SYSTEM");
            lvsMessage.setLvsIsRead(false);
            lvsMessage.setLvsCreatedAt(LocalDateTime.now());
            lvsMessage.setLvsUpdatedAt(LocalDateTime.now());

            lvsMessageRepository.save(lvsMessage);
        }
    }

    /**
     * Gửi thông báo
     * @param lvsUserId ID người dùng
     * @param lvsTitle Tiêu đề thông báo
     * @param lvsContent Nội dung thông báo
     */
    @Override
    public void lvsSendNotification(Long lvsUserId, String lvsTitle, String lvsContent) {
        LvsMessage lvsMessage = new LvsMessage();
        lvsMessage.setLvsMessageType("NOTIFICATION");
        lvsMessage.setLvsContent("<strong>" + lvsTitle + "</strong><br>" + lvsContent);
        lvsMessage.setLvsCreatedAt(LocalDateTime.now());
        lvsMessage.setLvsUpdatedAt(LocalDateTime.now());

        // TODO: Gửi thông báo thực tế (có thể kết hợp với WebSocket)
        System.out.println("Gửi thông báo cho user " + lvsUserId + ": " + lvsTitle);
    }
}