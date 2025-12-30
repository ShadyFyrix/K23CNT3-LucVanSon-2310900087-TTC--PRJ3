package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsRepository.*;
import k23cnt3.lucvanson.project3.LvsService.LvsCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation cho quản lý giỏ hàng
 * Xử lý thêm, xóa, cập nhật giỏ hàng, thanh toán
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsCartServiceImpl implements LvsCartService {

    private final LvsCartRepository lvsCartRepository;
    private final LvsCartItemRepository lvsCartItemRepository;
    private final LvsUserRepository lvsUserRepository;
    private final LvsProjectRepository lvsProjectRepository;
    private final LvsOrderRepository lvsOrderRepository;
    private final LvsOrderItemRepository lvsOrderItemRepository;
    private final LvsPromotionRepository lvsPromotionRepository;
    private final LvsMessageRepository lvsMessageRepository;
    private final LvsGiftRepository lvsGiftRepository;

    /**
     * Lấy giỏ hàng theo user ID
     * 
     * @param lvsUserId ID người dùng
     * @return Giỏ hàng tìm thấy
     */
    @Override
    public LvsCart lvsGetCartByUserId(Long lvsUserId) {
        return lvsCartRepository.findByUserIdWithItems(lvsUserId).orElseGet(() -> {
            // Nếu chưa có giỏ hàng, tạo mới
            LvsUser lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);
            if (lvsUser != null) {
                LvsCart lvsNewCart = new LvsCart();
                lvsNewCart.setLvsUser(lvsUser);
                lvsNewCart.setLvsCreatedAt(LocalDateTime.now());
                lvsNewCart.setLvsUpdatedAt(LocalDateTime.now());
                return lvsCartRepository.save(lvsNewCart);
            }
            return null;
        });
    }

    /**
     * Lấy giỏ hàng theo cart ID
     * 
     * @param lvsCartId ID giỏ hàng
     * @return Giỏ hàng tìm thấy
     */
    @Override
    public LvsCart lvsGetCartById(Long lvsCartId) {
        return lvsCartRepository.findById(lvsCartId).orElse(null);
    }

    /**
     * Lưu giỏ hàng
     * 
     * @param lvsCart Thông tin giỏ hàng
     * @return Giỏ hàng đã lưu
     */
    @Override
    @org.springframework.transaction.annotation.Transactional
    public LvsCart lvsSaveCart(LvsCart lvsCart) {
        lvsCart.setLvsUpdatedAt(LocalDateTime.now());
        lvsCalculateCartTotal(lvsCart);

        // Explicitly set the calculated values to ensure they're tracked
        lvsCart.setLvsTotalItems(lvsCart.getLvsTotalItems());
        lvsCart.setLvsTotalPrice(lvsCart.getLvsTotalPrice());
        lvsCart.setLvsFinalPrice(lvsCart.getLvsFinalPrice());

        LvsCart saved = lvsCartRepository.save(lvsCart);
        lvsCartRepository.flush(); // Force immediate persistence
        return saved;
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * 
     * @param lvsUserId    ID người dùng
     * @param lvsProjectId ID dự án
     * @param lvsQuantity  Số lượng
     * @return Item giỏ hàng đã thêm
     */

    @Override
    public LvsCartItem lvsAddToCart(Long lvsUserId, Long lvsProjectId, Integer lvsQuantity) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        LvsProject lvsProject = lvsProjectRepository.findById(lvsProjectId).orElse(null);

        if (lvsCart == null || lvsProject == null) {
            return null;
        }

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        LvsCartItem lvsExistingItem = lvsCartItemRepository
                .findByLvsCart_LvsCartIdAndLvsProject_LvsProjectId(lvsCart.getLvsCartId(), lvsProjectId)
                .orElse(null);

        // ✅ FIX: Use final price (after owner discount) instead of original price
        Double lvsItemPrice = lvsProject.getLvsFinalPrice() != null ? lvsProject.getLvsFinalPrice()
                : lvsProject.getLvsPrice();

        if (lvsExistingItem != null) {
            // Nếu đã có, cập nhật số lượng
            lvsExistingItem.setLvsQuantity(lvsExistingItem.getLvsQuantity() + lvsQuantity);
            lvsExistingItem.setLvsUnitPrice(lvsItemPrice); // ✅ Use discounted price
        } else {
            // Nếu chưa có, tạo mới
            lvsExistingItem = new LvsCartItem();
            lvsExistingItem.setLvsCart(lvsCart);
            lvsExistingItem.setLvsProject(lvsProject);
            lvsExistingItem.setLvsQuantity(lvsQuantity);
            lvsExistingItem.setLvsUnitPrice(lvsItemPrice); // ✅ Use discounted price
            lvsExistingItem.setLvsIsSelected(true);
            lvsExistingItem.setLvsAddedAt(LocalDateTime.now());
        }

        LvsCartItem lvsSavedItem = lvsCartItemRepository.save(lvsExistingItem);
        lvsCalculateCartTotal(lvsCart);

        return lvsSavedItem;
    }

    /**
     * Cập nhật số lượng sản phẩm
     * 
     * @param lvsCartItemId ID item giỏ hàng
     * @param lvsQuantity   Số lượng mới
     * @return Item đã cập nhật
     */
    @Override
    public LvsCartItem lvsUpdateCartItemQuantity(Long lvsCartItemId, Integer lvsQuantity) {
        LvsCartItem lvsCartItem = lvsCartItemRepository.findById(lvsCartItemId).orElse(null);
        if (lvsCartItem != null) {
            lvsCartItem.setLvsQuantity(lvsQuantity);
            LvsCartItem lvsUpdatedItem = lvsCartItemRepository.save(lvsCartItem);
            lvsCalculateCartTotal(lvsCartItem.getLvsCart());
            return lvsUpdatedItem;
        }
        return null;
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * 
     * @param lvsCartItemId ID item giỏ hàng
     */
    @Override
    @Transactional
    public void lvsRemoveCartItem(Long lvsCartItemId) {
        LvsCartItem lvsCartItem = lvsCartItemRepository.findById(lvsCartItemId).orElse(null);
        if (lvsCartItem != null) {
            Long cartId = lvsCartItem.getLvsCart().getLvsCartId();

            // Delete item
            lvsCartItemRepository.deleteById(lvsCartItemId);
            lvsCartItemRepository.flush(); // Force delete to DB

            // Refresh cart with eager loading to get updated items list
            LvsCart lvsCart = lvsCartRepository.findByIdWithItems(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            // Recalculate totals
            lvsCalculateCartTotal(lvsCart);
            lvsCartRepository.save(lvsCart);
        }
    }

    /**
     * Chọn/bỏ chọn sản phẩm
     * 
     * @param lvsCartItemId ID item giỏ hàng
     * @param lvsIsSelected Trạng thái chọn
     * @return Item đã cập nhật
     */
    @Override
    @Transactional
    public LvsCartItem lvsToggleCartItemSelection(Long lvsCartItemId, Boolean lvsIsSelected) {
        LvsCartItem lvsCartItem = lvsCartItemRepository.findById(lvsCartItemId).orElse(null);
        if (lvsCartItem != null) {
            lvsCartItem.setLvsIsSelected(lvsIsSelected);
            LvsCartItem savedItem = lvsCartItemRepository.save(lvsCartItem);
            lvsCartItemRepository.flush(); // Force save to DB

            // Refresh cart with eager loading to get updated items
            LvsCart lvsCart = lvsCartRepository.findByIdWithItems(
                    lvsCartItem.getLvsCart().getLvsCartId()).orElseThrow(() -> new RuntimeException("Cart not found"));

            // Recalculate cart totals based on selected items
            lvsCalculateCartTotal(lvsCart);
            lvsCartRepository.save(lvsCart);

            return savedItem;
        }
        return null;
    }

    /**
     * Áp dụng mã khuyến mãi
     * 
     * @param lvsUserId        ID người dùng
     * @param lvsPromotionCode Mã khuyến mãi
     * @return true nếu áp dụng thành công
     */
    @Override
    public boolean lvsApplyPromotion(Long lvsUserId, String lvsPromotionCode) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        if (lvsCart == null) {
            return false;
        }

        // 1. Find promotion by code
        LvsPromotion lvsPromotion = lvsPromotionRepository
                .findByLvsCode(lvsPromotionCode)
                .orElse(null);

        if (lvsPromotion == null) {
            throw new RuntimeException("Mã khuyến mãi không tồn tại");
        }

        // 2. Check if active
        if (!lvsPromotion.getLvsIsActive()) {
            throw new RuntimeException("Mã khuyến mãi đã bị vô hiệu hóa");
        }

        // 3. Check date range
        LocalDateTime now = LocalDateTime.now();
        if (now.toLocalDate().isBefore(lvsPromotion.getLvsStartDate())) {
            throw new RuntimeException("Mã khuyến mãi chưa có hiệu lực");
        }
        if (now.toLocalDate().isAfter(lvsPromotion.getLvsEndDate())) {
            throw new RuntimeException("Mã khuyến mãi đã hết hạn");
        }

        // 4. IMPORTANT: Check if user has already used this promotion
        boolean hasUsed = lvsOrderRepository.existsByLvsBuyer_LvsUserIdAndLvsPromotion_LvsPromotionId(
                lvsUserId, lvsPromotion.getLvsPromotionId());
        if (hasUsed) {
            throw new RuntimeException("Bạn đã sử dụng mã khuyến mãi này rồi!");
        }

        // 5. Check usage limit (count from actual orders)
        if (lvsPromotion.getLvsUsageLimit() != null) {
            long actualUsageCount = lvsOrderRepository.countByLvsPromotion_LvsPromotionId(
                    lvsPromotion.getLvsPromotionId());
            if (actualUsageCount >= lvsPromotion.getLvsUsageLimit()) {
                throw new RuntimeException("Mã khuyến mãi đã hết lượt sử dụng!");
            }
        }

        // 6. Check minimum order value
        if (lvsPromotion.getLvsMinOrderValue() != null &&
                lvsCart.getLvsTotalPrice() < lvsPromotion.getLvsMinOrderValue()) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " +
                    lvsPromotion.getLvsMinOrderValue() + " coins");
        }

        // 7. Calculate discount
        double discount = 0.0;
        if (lvsPromotion.getLvsDiscountType() == LvsPromotion.LvsDiscountType.PERCENT) {
            discount = lvsCart.getLvsTotalPrice() * (lvsPromotion.getLvsDiscountValue() / 100.0);
        } else {
            discount = lvsPromotion.getLvsDiscountValue();
        }

        // 8. Apply promotion to cart
        lvsCart.setLvsPromotion(lvsPromotion);
        lvsCart.setLvsPromotionCode(lvsPromotionCode);
        lvsCart.setLvsPromotionDiscount(discount);

        // 9. Save cart
        lvsCartRepository.save(lvsCart);

        return true;
    }

    /**
     * Xóa mã khuyến mãi
     * 
     * @param lvsUserId ID người dùng
     */
    @Override
    public void lvsRemovePromotion(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        if (lvsCart != null) {
            lvsCart.setLvsPromotion(null); // ✅ Clear promotion object
            lvsCart.setLvsPromotionCode(null); // ✅ Clear promotion code
            lvsCart.setLvsPromotionDiscount(0.0); // ✅ Clear promotion discount
            lvsCartRepository.save(lvsCart);
        }
    }

    /**
     * Thanh toán giỏ hàng
     * 
     * @param lvsUserId ID người dùng
     * @return Đơn hàng đã tạo
     */
    @Override
    public LvsOrder lvsCheckout(Long lvsUserId) {
        return lvsConvertCartToOrder(lvsUserId);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * 
     * @param lvsUserId ID người dùng
     */
    @Override
    public void lvsClearCart(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        if (lvsCart != null) {
            lvsCartItemRepository.deleteAll(lvsCart.getLvsCartItems());
            lvsCart.getLvsCartItems().clear();
            lvsCart.setLvsTotalItems(0);
            lvsCart.setLvsTotalPrice(0.0);
            lvsCart.setLvsDiscountAmount(0.0);
            lvsCart.setLvsFinalPrice(0.0);
            lvsCart.setLvsUpdatedAt(LocalDateTime.now());
            lvsCartRepository.save(lvsCart);
        }
    }

    /**
     * Tính toán tổng tiền giỏ hàng
     * 
     * @param lvsCart Giỏ hàng cần tính
     */
    @Override
    public void lvsCalculateCartTotal(LvsCart lvsCart) {
        List<LvsCartItem> lvsItems = lvsCart.getLvsCartItems();

        int lvsTotalItems = 0;
        double lvsTotalPrice = 0.0;

        for (LvsCartItem lvsItem : lvsItems) {
            if (Boolean.TRUE.equals(lvsItem.getLvsIsSelected())) {
                lvsTotalItems += lvsItem.getLvsQuantity();
                lvsTotalPrice += lvsItem.getLvsItemTotal();
            }
        }

        lvsCart.setLvsTotalItems(lvsTotalItems);
        lvsCart.setLvsTotalPrice(lvsTotalPrice);

        // ✅ FIX: Recalculate or remove promotion when cart changes
        if (lvsCart.getLvsPromotion() != null) {
            LvsPromotion promotion = lvsCart.getLvsPromotion();

            // Check if cart still meets minimum order value
            if (promotion.getLvsMinOrderValue() != null && lvsTotalPrice < promotion.getLvsMinOrderValue()) {
                // Cart no longer meets minimum → Remove promotion
                lvsCart.setLvsPromotion(null);
                lvsCart.setLvsPromotionCode(null);
                lvsCart.setLvsPromotionDiscount(0.0);
            } else {
                // Recalculate promotion discount based on new total
                double newDiscount = 0.0;
                if (promotion.getLvsDiscountType() == LvsPromotion.LvsDiscountType.PERCENT) {
                    newDiscount = lvsTotalPrice * (promotion.getLvsDiscountValue() / 100.0);
                } else {
                    newDiscount = promotion.getLvsDiscountValue();
                }
                lvsCart.setLvsPromotionDiscount(newDiscount);
            }
        }

        // ✅ FIX: Calculate final price manually since @PrePersist only runs on INSERT
        // Final price = Total - Discount - Promotion Discount
        double finalPrice = lvsTotalPrice
                - (lvsCart.getLvsDiscountAmount() != null ? lvsCart.getLvsDiscountAmount() : 0.0)
                - (lvsCart.getLvsPromotionDiscount() != null ? lvsCart.getLvsPromotionDiscount() : 0.0);
        lvsCart.setLvsFinalPrice(finalPrice);

        lvsCart.setLvsUpdatedAt(LocalDateTime.now());

    }

    /**
     * Kiểm tra sản phẩm đã có trong giỏ hàng chưa
     * 
     * @param lvsUserId    ID người dùng
     * @param lvsProjectId ID dự án
     * @return true nếu đã có trong giỏ
     */
    @Override
    public boolean lvsIsProjectInCart(Long lvsUserId, Long lvsProjectId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        if (lvsCart != null) {
            return lvsCartItemRepository
                    .existsByLvsCart_LvsCartIdAndLvsProject_LvsProjectId(lvsCart.getLvsCartId(), lvsProjectId);
        }
        return false;
    }

    /**
     * Lấy số lượng sản phẩm trong giỏ hàng
     * 
     * @param lvsUserId ID người dùng
     * @return Số lượng sản phẩm
     */
    @Override
    public Integer lvsGetCartItemCount(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        return lvsCart != null ? lvsCart.getLvsTotalItems() : 0;
    }

    /**
     * Lấy tổng giá trị giỏ hàng
     * 
     * @param lvsUserId ID người dùng
     * @return Tổng giá trị
     */
    @Override
    public Double lvsGetCartTotalValue(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        return lvsCart != null ? lvsCart.getLvsFinalPrice() : 0.0;
    }

    /**
     * Lấy danh sách sản phẩm đã chọn
     * 
     * @param lvsUserId ID người dùng
     * @return Danh sách item đã chọn
     */
    @Override
    public List<LvsCartItem> lvsGetSelectedCartItems(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        if (lvsCart != null) {
            return lvsCart.getLvsCartItems().stream()
                    .filter(item -> Boolean.TRUE.equals(item.getLvsIsSelected()))
                    .toList();
        }
        return List.of();
    }

    /**
     * Di chuyển giỏ hàng sang đơn hàng
     * 
     * @param lvsUserId ID người dùng
     * @return Đơn hàng đã tạo
     */
    @Override
    public LvsOrder lvsConvertCartToOrder(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        List<LvsCartItem> lvsSelectedItems = lvsGetSelectedCartItems(lvsUserId);

        if (lvsCart == null || lvsSelectedItems.isEmpty()) {
            return null;
        }

        // Null check for cart user
        if (lvsCart.getLvsUser() == null) {
            throw new RuntimeException("Cart has no user!");
        }

        // Tạo đơn hàng mới
        LvsOrder lvsOrder = new LvsOrder();
        lvsOrder.setLvsBuyer(lvsCart.getLvsUser());
        lvsOrder.setLvsOrderCode(lvsGenerateOrderCode());
        lvsOrder.setLvsStatus(LvsOrder.LvsOrderStatus.PENDING);
        lvsOrder.setLvsPaymentMethod("COIN");
        lvsOrder.setLvsCreatedAt(LocalDateTime.now());
        lvsOrder.setLvsUpdatedAt(LocalDateTime.now());

        // Thêm các item vào đơn hàng
        List<LvsCartItem> lvsGiftItems = new ArrayList<>();

        for (LvsCartItem lvsCartItem : lvsSelectedItems) {
            // Separate gift items for special handling
            if (Boolean.TRUE.equals(lvsCartItem.getLvsIsGift())) {
                lvsGiftItems.add(lvsCartItem);
                continue; // Process gifts separately
            }

            // Null check for project
            if (lvsCartItem.getLvsProject() == null) {
                throw new RuntimeException("Cart item has no project!");
            }

            LvsOrderItem lvsOrderItem = new LvsOrderItem();
            lvsOrderItem.setLvsOrder(lvsOrder);
            lvsOrderItem.setLvsProject(lvsCartItem.getLvsProject());

            // Better error handling for null seller
            LvsUser lvsSeller = lvsCartItem.getLvsProject().getLvsUser();
            if (lvsSeller == null) {
                throw new RuntimeException("Project has no seller!");
            }
            lvsOrderItem.setLvsSeller(lvsSeller);

            lvsOrderItem.setLvsQuantity(lvsCartItem.getLvsQuantity());
            lvsOrderItem.setLvsUnitPrice(lvsCartItem.getLvsUnitPrice());
            lvsOrderItem.setLvsCreatedAt(LocalDateTime.now());

            lvsOrder.getLvsOrderItems().add(lvsOrderItem);
        }

        // Check if order has any regular items
        if (lvsOrder.getLvsOrderItems().isEmpty() && lvsGiftItems.isEmpty()) {
            throw new RuntimeException("No items to checkout!");
        }

        // Save regular order if it has items OR if there are gift items
        LvsOrder lvsSavedOrder = null;
        if (!lvsOrder.getLvsOrderItems().isEmpty() || !lvsGiftItems.isEmpty()) {
            // ✅ FIX: Calculate totals INCLUDING gift items (sender pays upfront)
            double regularItemsTotal = lvsOrder.getLvsOrderItems().stream()
                    .mapToDouble(item -> item.getLvsUnitPrice() * item.getLvsQuantity())
                    .sum();

            // Calculate gift items total
            double giftItemsTotal = lvsGiftItems.stream()
                    .mapToDouble(item -> item.getLvsUnitPrice() * item.getLvsQuantity())
                    .sum();

            double totalBeforeDiscount = regularItemsTotal + giftItemsTotal;

            // Apply promotion discount to ENTIRE cart (regular + gifts)
            double promotionDiscount = 0.0;
            if (lvsCart.getLvsPromotion() != null && lvsCart.getLvsTotalPrice() > 0) {
                promotionDiscount = lvsCart.getLvsPromotionDiscount() != null ? lvsCart.getLvsPromotionDiscount() : 0.0;
            }

            lvsOrder.setLvsTotalAmount(totalBeforeDiscount);
            lvsOrder.setLvsDiscountAmount(lvsCart.getLvsDiscountAmount()); // Project discounts
            lvsOrder.setLvsFinalAmount(totalBeforeDiscount - lvsCart.getLvsDiscountAmount() - promotionDiscount);
            lvsOrder.setLvsPromotionDiscount(promotionDiscount);

            // Copy promotion from cart to order
            if (lvsCart.getLvsPromotion() != null) {
                lvsOrder.setLvsPromotion(lvsCart.getLvsPromotion());
                lvsOrder.setLvsPromotionCode(lvsCart.getLvsPromotionCode());
            }

            // Add note if this includes gifts
            if (!lvsGiftItems.isEmpty()) {
                String note = lvsOrder.getLvsOrderItems().isEmpty()
                        ? "Gift purchase - " + lvsGiftItems.size() + " gift(s) sent"
                        : "Order includes " + lvsGiftItems.size() + " gift(s)";
                lvsOrder.setLvsNotes(note);
            }

            lvsSavedOrder = lvsOrderRepository.save(lvsOrder);
        }

        // ✅ FIX: Process gift items with PENDING status (recipient must accept/reject)
        for (LvsCartItem lvsGiftItem : lvsGiftItems) {
            LvsUser lvsRecipient = lvsGiftItem.getLvsGiftRecipient();
            if (lvsRecipient == null) {
                throw new RuntimeException("Gift item has no recipient!");
            }

            // Create LvsGift record with PENDING status
            LvsGift gift = new LvsGift();
            gift.setLvsSender(lvsCart.getLvsUser());
            gift.setLvsRecipient(lvsRecipient);
            gift.setLvsProject(lvsGiftItem.getLvsProject());
            gift.setLvsGiftMessage("Gift from cart checkout");
            gift.setLvsStatus(LvsGift.LvsGiftStatus.PENDING);
            gift.setLvsCreatedAt(LocalDateTime.now());
            gift.setLvsOrder(lvsSavedOrder); // Link to sender's order for refund if rejected
            gift = lvsGiftRepository.save(gift);

            // Send notification to recipient with accept/reject options
            try {
                LvsMessage lvsGiftMessage = new LvsMessage();
                lvsGiftMessage.setLvsSender(lvsCart.getLvsUser());
                lvsGiftMessage.setLvsReceiver(lvsRecipient);
                lvsGiftMessage.setLvsMessageType("GIFT");
                lvsGiftMessage.setLvsGift(gift);
                lvsGiftMessage
                        .setLvsContent("🎁 You received a gift: " + lvsGiftItem.getLvsProject().getLvsProjectName());
                lvsGiftMessage.setLvsIsRead(false);
                lvsGiftMessage.setLvsCreatedAt(LocalDateTime.now());
                lvsMessageRepository.save(lvsGiftMessage);
            } catch (Exception e) {
                // Log but don't fail if message sending fails
                System.err.println("Failed to send gift notification: " + e.getMessage());
            }
        }

        // Xóa các item đã chọn khỏi giỏ hàng
        for (LvsCartItem lvsCartItem : lvsSelectedItems) {
            lvsCartItemRepository.delete(lvsCartItem);
        }
        lvsCartItemRepository.flush(); // ✅ Force immediate deletion

        // Cập nhật lại giỏ hàng
        lvsCalculateCartTotal(lvsCart);
        lvsCartRepository.save(lvsCart); // ✅ Save cart after recalculation
        lvsCartRepository.flush(); // ✅ Force immediate save

        return lvsSavedOrder; // Return regular order (or null if only gifts)

    }

    /**
     * Sao chép giỏ hàng từ user này sang user khác
     * 
     * @param lvsFromUserId ID user nguồn
     * @param lvsToUserId   ID user đích
     */
    @Override
    public void lvsCopyCart(Long lvsFromUserId, Long lvsToUserId) {
        LvsCart lvsFromCart = lvsGetCartByUserId(lvsFromUserId);
        LvsCart lvsToCart = lvsGetCartByUserId(lvsToUserId);

        if (lvsFromCart != null && lvsToCart != null) {
            // Xóa giỏ hàng hiện tại của user đích
            lvsCartItemRepository.deleteAll(lvsToCart.getLvsCartItems());

            // Sao chép các item
            for (LvsCartItem lvsFromItem : lvsFromCart.getLvsCartItems()) {
                LvsCartItem lvsNewItem = new LvsCartItem();
                lvsNewItem.setLvsCart(lvsToCart);
                lvsNewItem.setLvsProject(lvsFromItem.getLvsProject());
                lvsNewItem.setLvsQuantity(lvsFromItem.getLvsQuantity());
                lvsNewItem.setLvsUnitPrice(lvsFromItem.getLvsUnitPrice());
                lvsNewItem.setLvsIsSelected(lvsFromItem.getLvsIsSelected());
                lvsNewItem.setLvsAddedAt(LocalDateTime.now());

                lvsCartItemRepository.save(lvsNewItem);
            }

            // Cập nhật tổng tiền
            lvsCalculateCartTotal(lvsToCart);
        }
    }

    /**
     * Tạo mã đơn hàng duy nhất
     * Format: ORD-{timestamp_last_10}-{random_3}
     * Max length: 4 + 10 + 1 + 3 = 18 chars (fits in VARCHAR(20))
     * 
     * @return Mã đơn hàng
     */
    private String lvsGenerateOrderCode() {
        long timestamp = System.currentTimeMillis();
        String shortTimestamp = String.valueOf(timestamp).substring(3); // Last 10 digits
        int random = (int) (Math.random() * 1000);
        return "ORD-" + shortTimestamp + "-" + String.format("%03d", random);
    }

    /**
     * Bỏ chọn tất cả sản phẩm trong giỏ hàng
     * 
     * @param lvsUserId ID người dùng
     */
    @Override
    public void lvsUnselectAllItems(Long lvsUserId) {
        LvsCart lvsCart = lvsGetCartByUserId(lvsUserId);
        if (lvsCart != null && lvsCart.getLvsCartItems() != null) {
            for (LvsCartItem item : lvsCart.getLvsCartItems()) {
                item.setLvsIsSelected(false);
                lvsCartItemRepository.save(item);
            }
        }
    }
}