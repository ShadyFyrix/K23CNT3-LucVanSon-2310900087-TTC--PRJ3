package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsCart;
import k23cnt3.lucvanson.project3.LvsEntity.LvsCartItem;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder;

import java.util.List;

/**
 * Service interface cho quản lý giỏ hàng
 * Xử lý thêm, xóa, cập nhật giỏ hàng, thanh toán
 */
public interface LvsCartService {

    // Lấy giỏ hàng theo user ID
    LvsCart lvsGetCartByUserId(Long lvsUserId);

    // Lấy giỏ hàng theo cart ID
    LvsCart lvsGetCartById(Long lvsCartId);

    // Lưu giỏ hàng
    LvsCart lvsSaveCart(LvsCart lvsCart);

    // Thêm sản phẩm vào giỏ hàng
    LvsCartItem lvsAddToCart(Long lvsUserId, Long lvsProjectId, Integer lvsQuantity);

    // Cập nhật số lượng sản phẩm
    LvsCartItem lvsUpdateCartItemQuantity(Long lvsCartItemId, Integer lvsQuantity);

    // Xóa sản phẩm khỏi giỏ hàng
    void lvsRemoveCartItem(Long lvsCartItemId);

    // Chọn/bỏ chọn sản phẩm
    LvsCartItem lvsToggleCartItemSelection(Long lvsCartItemId, Boolean lvsIsSelected);

    // Áp dụng mã khuyến mãi
    boolean lvsApplyPromotion(Long lvsUserId, String lvsPromotionCode);

    // Xóa mã khuyến mãi
    void lvsRemovePromotion(Long lvsUserId);

    // Thanh toán giỏ hàng
    LvsOrder lvsCheckout(Long lvsUserId);

    // Xóa toàn bộ giỏ hàng
    void lvsClearCart(Long lvsUserId);

    // Tính toán tổng tiền giỏ hàng
    void lvsCalculateCartTotal(LvsCart lvsCart);

    // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
    boolean lvsIsProjectInCart(Long lvsUserId, Long lvsProjectId);

    // Lấy số lượng sản phẩm trong giỏ hàng
    Integer lvsGetCartItemCount(Long lvsUserId);

    // Lấy tổng giá trị giỏ hàng
    Double lvsGetCartTotalValue(Long lvsUserId);

    // Lấy danh sách sản phẩm đã chọn
    List<LvsCartItem> lvsGetSelectedCartItems(Long lvsUserId);

    // Di chuyển giỏ hàng sang đơn hàng
    LvsOrder lvsConvertCartToOrder(Long lvsUserId);

    // Sao chép giỏ hàng từ user này sang user khác
    void lvsCopyCart(Long lvsFromUserId, Long lvsToUserId);
}