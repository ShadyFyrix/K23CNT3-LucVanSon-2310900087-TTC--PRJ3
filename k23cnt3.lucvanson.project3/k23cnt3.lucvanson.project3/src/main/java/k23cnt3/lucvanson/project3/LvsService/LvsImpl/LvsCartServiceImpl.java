package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsRepository.*;
import k23cnt3.lucvanson.project3.LvsService.LvsCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    /**
     * Lấy giỏ hàng theo user ID
     * 
     * @param lvsUserId ID người dùng
     * @return Giỏ hàng tìm thấy
     */
    @Override
    public LvsCart lvsGetCartByUserId(Long lvsUserId) {
        return lvsCartRepository.findByLvsUser_LvsUserId(lvsUserId).orElseGet(() -> {
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
    public LvsCart lvsSaveCart(LvsCart lvsCart) {
        lvsCart.setLvsUpdatedAt(LocalDateTime.now());
        lvsCalculateCartTotal(lvsCart);
        return lvsCartRepository.save(lvsCart);
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

        if (lvsExistingItem != null) {
            // Nếu đã có, cập nhật số lượng
            lvsExistingItem.setLvsQuantity(lvsExistingItem.getLvsQuantity() + lvsQuantity);
            lvsExistingItem.setLvsUnitPrice(lvsProject.getLvsPrice());
        } else {
            // Nếu chưa có, tạo mới
            lvsExistingItem = new LvsCartItem();
            lvsExistingItem.setLvsCart(lvsCart);
            lvsExistingItem.setLvsProject(lvsProject);
            lvsExistingItem.setLvsQuantity(lvsQuantity);
            lvsExistingItem.setLvsUnitPrice(lvsProject.getLvsPrice());
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
    public void lvsRemoveCartItem(Long lvsCartItemId) {
        LvsCartItem lvsCartItem = lvsCartItemRepository.findById(lvsCartItemId).orElse(null);
        if (lvsCartItem != null) {
            LvsCart lvsCart = lvsCartItem.getLvsCart();
            lvsCartItemRepository.deleteById(lvsCartItemId);
            lvsCalculateCartTotal(lvsCart);
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
    public LvsCartItem lvsToggleCartItemSelection(Long lvsCartItemId, Boolean lvsIsSelected) {
        LvsCartItem lvsCartItem = lvsCartItemRepository.findById(lvsCartItemId).orElse(null);
        if (lvsCartItem != null) {
            lvsCartItem.setLvsIsSelected(lvsIsSelected);
            return lvsCartItemRepository.save(lvsCartItem);
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
        if (lvsCart != null) {
            // TODO: Kiểm tra mã khuyến mãi hợp lệ
            lvsCart.setLvsPromotionCode(lvsPromotionCode);
            // TODO: Tính toán giảm giá
            lvsCartRepository.save(lvsCart);
            return true;
        }
        return false;
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
            lvsCart.setLvsPromotionCode(null);
            lvsCart.setLvsDiscountAmount(0.0);
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
        lvsCart.setLvsFinalPrice(lvsTotalPrice - lvsCart.getLvsDiscountAmount());
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

        // Tạo đơn hàng mới
        LvsOrder lvsOrder = new LvsOrder();
        lvsOrder.setLvsBuyer(lvsCart.getLvsUser());
        lvsOrder.setLvsOrderCode(lvsGenerateOrderCode());
        lvsOrder.setLvsStatus(LvsOrder.LvsOrderStatus.PENDING);
        lvsOrder.setLvsPaymentMethod("COIN");
        lvsOrder.setLvsCreatedAt(LocalDateTime.now());
        lvsOrder.setLvsUpdatedAt(LocalDateTime.now());

        // Thêm các item vào đơn hàng
        for (LvsCartItem lvsCartItem : lvsSelectedItems) {
            LvsOrderItem lvsOrderItem = new LvsOrderItem();
            lvsOrderItem.setLvsOrder(lvsOrder);
            lvsOrderItem.setLvsProject(lvsCartItem.getLvsProject());
            lvsOrderItem.setLvsSeller(lvsCartItem.getLvsProject().getLvsUser());
            lvsOrderItem.setLvsQuantity(lvsCartItem.getLvsQuantity());
            lvsOrderItem.setLvsUnitPrice(lvsCartItem.getLvsUnitPrice());
            lvsOrderItem.setLvsCreatedAt(LocalDateTime.now());

            lvsOrder.getLvsOrderItems().add(lvsOrderItem);
        }

        // Tính toán tổng tiền (calculateAmounts() will be called automatically by
        // @PrePersist)
        lvsOrder.setLvsTotalAmount(lvsCart.getLvsTotalPrice());
        lvsOrder.setLvsDiscountAmount(lvsCart.getLvsDiscountAmount());
        lvsOrder.setLvsFinalAmount(lvsCart.getLvsFinalPrice());

        // Lưu đơn hàng
        LvsOrder lvsSavedOrder = lvsOrderRepository.save(lvsOrder);

        // Xóa các item đã chọn khỏi giỏ hàng
        for (LvsCartItem lvsCartItem : lvsSelectedItems) {
            lvsCartItemRepository.delete(lvsCartItem);
        }

        // Cập nhật lại giỏ hàng
        lvsCalculateCartTotal(lvsCart);

        return lvsSavedOrder;
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
     * 
     * @return Mã đơn hàng
     */
    private String lvsGenerateOrderCode() {
        return "ORD-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }
}