package k23cnt3.lucvanson.project3.LvsEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LvsMessage")
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

    // Getters and Setters

    public Long getLvsMessageId() {
        return lvsMessageId;
    }

    public void setLvsMessageId(Long lvsMessageId) {
        this.lvsMessageId = lvsMessageId;
    }

    public LvsUser getLvsSender() {
        return lvsSender;
    }

    public void setLvsSender(LvsUser lvsSender) {
        this.lvsSender = lvsSender;
    }

    public LvsUser getLvsReceiver() {
        return lvsReceiver;
    }

    public void setLvsReceiver(LvsUser lvsReceiver) {
        this.lvsReceiver = lvsReceiver;
    }

    public String getLvsContent() {
        return lvsContent;
    }

    public void setLvsContent(String lvsContent) {
        this.lvsContent = lvsContent;
    }

    public Boolean getLvsIsRead() {
        return lvsIsRead;
    }

    public void setLvsIsRead(Boolean lvsIsRead) {
        this.lvsIsRead = lvsIsRead;
    }

    public Boolean getLvsIsEdited() {
        return lvsIsEdited;
    }

    public void setLvsIsEdited(Boolean lvsIsEdited) {
        this.lvsIsEdited = lvsIsEdited;
    }

    public String getLvsMessageType() {
        return lvsMessageType;
    }

    public void setLvsMessageType(String lvsMessageType) {
        this.lvsMessageType = lvsMessageType;
    }

    public String getLvsAttachmentUrl() {
        return lvsAttachmentUrl;
    }

    public void setLvsAttachmentUrl(String lvsAttachmentUrl) {
        this.lvsAttachmentUrl = lvsAttachmentUrl;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

    public LocalDateTime getLvsUpdatedAt() {
        return lvsUpdatedAt;
    }

    public void setLvsUpdatedAt(LocalDateTime lvsUpdatedAt) {
        this.lvsUpdatedAt = lvsUpdatedAt;
    }

}