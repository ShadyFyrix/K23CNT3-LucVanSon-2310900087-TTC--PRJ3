package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsMessage")
@Data
public class LvsMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsMessageId")
    private Long lvsMessageId;

    // Người gửi và người nhận
    @ManyToOne
    @JoinColumn(name = "LvsSenderId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsSender;

    @ManyToOne
    @JoinColumn(name = "LvsReceiverId", referencedColumnName = "LvsUserId", nullable = false)
    private LvsUser lvsReceiver;

    // Nội dung
    @Column(name = "LvsContent", columnDefinition = "TEXT", nullable = false)
    private String lvsContent;

    // Trạng thái đọc
    @Column(name = "LvsIsRead")
    private Boolean lvsIsRead = false;

    // Đã chỉnh sửa chưa
    @Column(name = "LvsIsEdited")
    private Boolean lvsIsEdited = false;

    // Loại tin nhắn
    @Column(name = "LvsMessageType", length = 20)
    private String lvsMessageType = "TEXT";

    // File đính kèm
    @Column(name = "LvsAttachmentUrl", length = 500)
    private String lvsAttachmentUrl;

    // Thời gian
    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt = LocalDateTime.now();

    @Column(name = "LvsUpdatedAt")
    private LocalDateTime lvsUpdatedAt = LocalDateTime.now();
}