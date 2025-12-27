package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsCart
 * Xử lý truy vấn liên quan đến giỏ hàng
 */
@Repository
public interface LvsCartRepository extends JpaRepository<LvsCart, Long> {

    // Tìm giỏ hàng theo user
    Optional<LvsCart> findByLvsUser_LvsUserId(Long lvsUserId);

    // Kiểm tra user đã có giỏ hàng chưa
    boolean existsByLvsUser_LvsUserId(Long lvsUserId);

    // Xóa giỏ hàng theo user
    void deleteByLvsUser_LvsUserId(Long lvsUserId);

    // Cập nhật tổng tiền
    @Modifying
    @Query("UPDATE LvsCart c SET c.lvsTotalPrice = :totalPrice, c.lvsFinalPrice = :finalPrice WHERE c.lvsCartId = :cartId")
    void updateCartTotals(@Param("cartId") Long cartId, @Param("totalPrice") Double totalPrice,
            @Param("finalPrice") Double finalPrice);

    // Cập nhật mã khuyến mãi
    @Modifying
    @Query("UPDATE LvsCart c SET c.lvsPromotionCode = :promotionCode WHERE c.lvsCartId = :cartId")
    void updatePromotionCode(@Param("cartId") Long cartId, @Param("promotionCode") String promotionCode);

    // Cập nhật discount amount
    @Modifying
    @Query("UPDATE LvsCart c SET c.lvsDiscountAmount = :discountAmount, c.lvsFinalPrice = c.lvsTotalPrice - :discountAmount WHERE c.lvsCartId = :cartId")
    void updateDiscountAmount(@Param("cartId") Long cartId, @Param("discountAmount") Double discountAmount);

    // Reset giỏ hàng
    @Modifying
    @Query("UPDATE LvsCart c SET c.lvsTotalItems = 0, c.lvsTotalPrice = 0.0, c.lvsDiscountAmount = 0.0, c.lvsFinalPrice = 0.0, c.lvsPromotionCode = NULL WHERE c.lvsCartId = :cartId")
    void resetCart(@Param("cartId") Long cartId);

    // Đếm số giỏ hàng
    @Query("SELECT COUNT(c) FROM LvsCart c")
    Long countAllCarts();

    // Lấy tổng giá trị tất cả giỏ hàng
    @Query("SELECT SUM(c.lvsTotalPrice) FROM LvsCart c")
    Double getTotalCartValue();

    // Tìm giỏ hàng có mã khuyến mãi
    List<LvsCart> findByLvsPromotionCodeIsNotNull();

    // Tìm giỏ hàng có tổng tiền lớn hơn
    List<LvsCart> findByLvsTotalPriceGreaterThan(Double minPrice);

    // Tìm giỏ hàng theo user với eager loading items AND gift recipients
    @Query("SELECT c FROM LvsCart c LEFT JOIN FETCH c.lvsCartItems ci LEFT JOIN FETCH ci.lvsGiftRecipient WHERE c.lvsUser.lvsUserId = :userId")
    Optional<LvsCart> findByUserIdWithItems(@Param("userId") Long userId);

    // Tìm giỏ hàng theo ID với eager loading items
    @Query("SELECT c FROM LvsCart c LEFT JOIN FETCH c.lvsCartItems WHERE c.lvsCartId = :cartId")
    Optional<LvsCart> findByIdWithItems(@Param("cartId") Long cartId);
}