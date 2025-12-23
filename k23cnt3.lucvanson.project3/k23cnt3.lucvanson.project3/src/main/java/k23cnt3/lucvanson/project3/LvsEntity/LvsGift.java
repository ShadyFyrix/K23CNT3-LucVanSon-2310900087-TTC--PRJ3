package k23cnt3.lucvanson.project3.LvsEntity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "LvsGift")
public class LvsGift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LvsGiftId")
    private Long lvsGiftId;

    @ManyToOne
    @JoinColumn(name = "LvsSenderId", nullable = false)
    private LvsUser lvsSender;

    @ManyToOne
    @JoinColumn(name = "LvsRecipientId", nullable = false)
    private LvsUser lvsRecipient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LvsProjectId", nullable = false)
    private LvsProject lvsProject;

    @Column(name = "LvsGiftMessage", length = 500)
    private String lvsGiftMessage;

    @Column(name = "LvsStatus")
    @Enumerated(EnumType.STRING)
    private LvsGiftStatus lvsStatus = LvsGiftStatus.PENDING;

    @Column(name = "LvsCreatedAt")
    private LocalDateTime lvsCreatedAt;

    @Column(name = "LvsRespondedAt")
    private LocalDateTime lvsRespondedAt;

    public enum LvsGiftStatus {
        PENDING, ACCEPTED, REJECTED, CANCELLED
    }

    // Constructors
    public LvsGift() {
        this.lvsCreatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLvsGiftId() {
        return lvsGiftId;
    }

    public void setLvsGiftId(Long lvsGiftId) {
        this.lvsGiftId = lvsGiftId;
    }

    public LvsUser getLvsSender() {
        return lvsSender;
    }

    public void setLvsSender(LvsUser lvsSender) {
        this.lvsSender = lvsSender;
    }

    public LvsUser getLvsRecipient() {
        return lvsRecipient;
    }

    public void setLvsRecipient(LvsUser lvsRecipient) {
        this.lvsRecipient = lvsRecipient;
    }

    public LvsProject getLvsProject() {
        return lvsProject;
    }

    public void setLvsProject(LvsProject lvsProject) {
        this.lvsProject = lvsProject;
    }

    public String getLvsGiftMessage() {
        return lvsGiftMessage;
    }

    public void setLvsGiftMessage(String lvsGiftMessage) {
        this.lvsGiftMessage = lvsGiftMessage;
    }

    public LvsGiftStatus getLvsStatus() {
        return lvsStatus;
    }

    public void setLvsStatus(LvsGiftStatus lvsStatus) {
        this.lvsStatus = lvsStatus;
    }

    public LocalDateTime getLvsCreatedAt() {
        return lvsCreatedAt;
    }

    public void setLvsCreatedAt(LocalDateTime lvsCreatedAt) {
        this.lvsCreatedAt = lvsCreatedAt;
    }

    public LocalDateTime getLvsRespondedAt() {
        return lvsRespondedAt;
    }

    public void setLvsRespondedAt(LocalDateTime lvsRespondedAt) {
        this.lvsRespondedAt = lvsRespondedAt;
    }
}
