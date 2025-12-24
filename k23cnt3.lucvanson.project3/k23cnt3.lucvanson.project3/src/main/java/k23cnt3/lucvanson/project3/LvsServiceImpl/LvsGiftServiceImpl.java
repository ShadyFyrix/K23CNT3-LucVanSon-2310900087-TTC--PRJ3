package k23cnt3.lucvanson.project3.LvsServiceImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsGift;
import k23cnt3.lucvanson.project3.LvsEntity.LvsProject;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrderItem;
import k23cnt3.lucvanson.project3.LvsEntity.LvsMessage;
import k23cnt3.lucvanson.project3.LvsRepository.LvsGiftRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsOrderRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsGiftService;
import k23cnt3.lucvanson.project3.LvsService.LvsUserService;
import k23cnt3.lucvanson.project3.LvsService.LvsProjectService;
import k23cnt3.lucvanson.project3.LvsService.LvsFollowService;
import k23cnt3.lucvanson.project3.LvsService.LvsOrderService;
import k23cnt3.lucvanson.project3.LvsService.LvsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LvsGiftServiceImpl implements LvsGiftService {

    @Autowired
    private LvsGiftRepository lvsGiftRepository;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsFollowService lvsFollowService;

    @Autowired
    private LvsOrderRepository lvsOrderRepository;

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsMessageService lvsMessageService;

    @Override
    @Transactional
    public LvsGift lvsSendGift(Long buyerId, Long recipientId, Long projectId, String message) {
        // 1. Validate buyer, recipient, and project exist
        LvsUser buyer = lvsUserService.lvsGetUserById(buyerId);
        LvsUser recipient = lvsUserService.lvsGetUserById(recipientId);
        LvsProject project = lvsProjectService.lvsGetProjectById(projectId);

        if (buyer == null) {
            throw new IllegalArgumentException("Buyer not found");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient not found");
        }
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        // 2. Validate recipient is a follower of buyer
        boolean isFollower = lvsFollowService.lvsIsFollowing(recipientId, buyerId);
        if (!isFollower) {
            throw new IllegalArgumentException("You can only gift to your followers");
        }

        // 3. Check if buyer has enough coins
        if (buyer.getLvsCoin() < project.getLvsPrice()) {
            throw new IllegalArgumentException("Insufficient coins. You need " + project.getLvsPrice() + " coins.");
        }

        // 4. Check if recipient already owns the project
        boolean alreadyOwns = lvsOrderRepository
                .existsByLvsUserLvsUserIdAndLvsOrderItemsLvsProjectLvsProjectIdAndLvsStatus(
                        recipientId, projectId, LvsOrder.LvsOrderStatus.COMPLETED);

        if (alreadyOwns) {
            throw new IllegalArgumentException("Recipient already owns this project");
        }

        // 5. Check if pending gift already exists
        boolean pendingGiftExists = lvsGiftRepository
                .existsByLvsSenderLvsUserIdAndLvsRecipientLvsUserIdAndLvsProjectLvsProjectIdAndLvsStatus(
                        buyerId, recipientId, projectId, LvsGift.LvsGiftStatus.PENDING);

        if (pendingGiftExists) {
            throw new IllegalArgumentException("You already have a pending gift for this project to this user");
        }

        // 6. Deduct coins from buyer
        buyer.setLvsCoin(buyer.getLvsCoin() - project.getLvsPrice());
        lvsUserService.lvsUpdateUser(buyer);

        // 7. Create ORDER for buyer (gift purchase)
        LvsOrder senderOrder;
        try {
            senderOrder = lvsOrderService.lvsPurchaseProject(projectId, buyerId);
        } catch (Exception e) {
            // If order creation fails, refund buyer
            buyer.setLvsCoin(buyer.getLvsCoin() + project.getLvsPrice());
            lvsUserService.lvsUpdateUser(buyer);
            throw new IllegalArgumentException("Failed to create order: " + e.getMessage());
        }

        // 8. Create gift record
        LvsGift gift = new LvsGift();
        gift.setLvsSender(buyer);
        gift.setLvsRecipient(recipient);
        gift.setLvsProject(project);
        gift.setLvsGiftMessage(message);
        gift.setLvsStatus(LvsGift.LvsGiftStatus.PENDING);
        gift.setLvsCreatedAt(LocalDateTime.now());
        gift.setLvsOrder(senderOrder); // Track sender's order for refund/cancellation
        gift = lvsGiftRepository.save(gift);

        // 9. Send MESSAGE notification to recipient
        LvsMessage notification = new LvsMessage();
        notification.setLvsSender(buyer);
        notification.setLvsReceiver(recipient);
        notification.setLvsMessageType("GIFT");
        notification.setLvsGift(gift);
        notification.setLvsContent("🎁 You received a gift: " + project.getLvsProjectName() +
                (message != null && !message.isEmpty() ? "\n💌 Message: " + message : ""));
        notification.setLvsIsRead(false);
        lvsMessageService.lvsSaveMessage(notification);

        return gift;
    }

    @Override
    @Transactional
    public void lvsAcceptGift(Long giftId, Long userId) {
        LvsGift gift = lvsGiftRepository.findById(giftId)
                .orElseThrow(() -> new IllegalArgumentException("Gift not found"));

        // Validate user is the recipient
        if (!gift.getLvsRecipient().getLvsUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only accept gifts sent to you");
        }

        // Validate gift is still pending
        if (gift.getLvsStatus() != LvsGift.LvsGiftStatus.PENDING) {
            throw new IllegalArgumentException("This gift has already been responded to");
        }

        // Check again if user already owns the project
        boolean alreadyOwns = lvsOrderRepository
                .existsByLvsUserLvsUserIdAndLvsOrderItemsLvsProjectLvsProjectIdAndLvsStatus(
                        userId, gift.getLvsProject().getLvsProjectId(), LvsOrder.LvsOrderStatus.COMPLETED);

        if (alreadyOwns) {
            // Cancel the gift instead of accepting
            gift.setLvsStatus(LvsGift.LvsGiftStatus.CANCELLED);
            gift.setLvsRespondedAt(LocalDateTime.now());
            lvsGiftRepository.save(gift);
            throw new IllegalArgumentException("You already own this project. Gift has been cancelled.");
        }

        // Create FREE order for recipient (they don't pay - sender already did)
        // We need to manually create order instead of using lvsPurchaseProject
        // because lvsPurchaseProject would charge the recipient
        try {
            LvsOrder recipientOrder = new LvsOrder();
            recipientOrder.setLvsBuyer(gift.getLvsRecipient());
            recipientOrder.setLvsStatus(LvsOrder.LvsOrderStatus.COMPLETED);
            recipientOrder.setLvsTotalAmount(0.0); // FREE - it's a gift!
            recipientOrder.setLvsCreatedAt(LocalDateTime.now());
            recipientOrder.setLvsUpdatedAt(LocalDateTime.now());

            // Create order item
            LvsOrderItem item = new LvsOrderItem();
            item.setLvsOrder(recipientOrder);
            item.setLvsProject(gift.getLvsProject());
            item.setLvsUnitPrice(0.0); // FREE
            item.setLvsQuantity(1);

            recipientOrder.setLvsOrderItems(java.util.List.of(item));
            lvsOrderRepository.save(recipientOrder);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to add project to your library: " + e.getMessage());
        }

        gift.setLvsStatus(LvsGift.LvsGiftStatus.ACCEPTED);
        gift.setLvsRespondedAt(LocalDateTime.now());
        lvsGiftRepository.save(gift);

        // Send notification to sender that gift was accepted
        LvsMessage acceptNotification = new LvsMessage();
        acceptNotification.setLvsSender(gift.getLvsRecipient());
        acceptNotification.setLvsReceiver(gift.getLvsSender());
        acceptNotification.setLvsMessageType("GIFT_RESPONSE");
        acceptNotification.setLvsGift(gift);
        acceptNotification.setLvsContent("✅ " + gift.getLvsRecipient().getLvsUsername() +
                " accepted your gift: " + gift.getLvsProject().getLvsProjectName());
        acceptNotification.setLvsIsRead(false);
        lvsMessageService.lvsSaveMessage(acceptNotification);
    }

    @Override
    @Transactional
    public void lvsRejectGift(Long giftId, Long userId) {
        LvsGift gift = lvsGiftRepository.findById(giftId)
                .orElseThrow(() -> new IllegalArgumentException("Gift not found"));

        // Validate user is the recipient
        if (!gift.getLvsRecipient().getLvsUserId().equals(userId)) {
            throw new IllegalArgumentException("You can only reject gifts sent to you");
        }

        // Validate gift is still pending
        if (gift.getLvsStatus() != LvsGift.LvsGiftStatus.PENDING) {
            throw new IllegalArgumentException("This gift has already been responded to");
        }

        // Refund coins to sender
        LvsUser sender = gift.getLvsSender();
        Double refundAmount = gift.getLvsProject().getLvsPrice();
        sender.setLvsCoin(sender.getLvsCoin() + refundAmount);
        lvsUserService.lvsUpdateUser(sender);

        // Cancel sender's order
        if (gift.getLvsOrder() != null) {
            LvsOrder senderOrder = gift.getLvsOrder();
            senderOrder.setLvsStatus(LvsOrder.LvsOrderStatus.CANCELLED);
            senderOrder.setLvsUpdatedAt(LocalDateTime.now());
            lvsOrderRepository.save(senderOrder);
        }

        // Update gift status
        gift.setLvsStatus(LvsGift.LvsGiftStatus.REJECTED);
        gift.setLvsRespondedAt(LocalDateTime.now());
        lvsGiftRepository.save(gift);

        // Send notification to sender that gift was rejected
        LvsMessage rejectNotification = new LvsMessage();
        rejectNotification.setLvsSender(gift.getLvsRecipient());
        rejectNotification.setLvsReceiver(gift.getLvsSender());
        rejectNotification.setLvsMessageType("GIFT_RESPONSE");
        rejectNotification.setLvsGift(gift);
        rejectNotification.setLvsContent("❌ " + gift.getLvsRecipient().getLvsUsername() +
                " declined your gift: " + gift.getLvsProject().getLvsProjectName() +
                ". You have been refunded " + refundAmount.intValue() + " coins.");
        rejectNotification.setLvsIsRead(false);
        lvsMessageService.lvsSaveMessage(rejectNotification);
    }

    @Override
    public Page<LvsGift> lvsGetGiftsSent(Long userId, Pageable pageable) {
        return lvsGiftRepository.findByLvsSenderLvsUserId(userId, pageable);
    }

    @Override
    public Page<LvsGift> lvsGetGiftsReceived(Long userId, Pageable pageable) {
        return lvsGiftRepository.findByLvsRecipientLvsUserId(userId, pageable);
    }

    @Override
    public int lvsGetPendingGiftsCount(Long userId) {
        return lvsGiftRepository.countByLvsRecipientLvsUserIdAndLvsStatus(
                userId, LvsGift.LvsGiftStatus.PENDING);
    }
}
