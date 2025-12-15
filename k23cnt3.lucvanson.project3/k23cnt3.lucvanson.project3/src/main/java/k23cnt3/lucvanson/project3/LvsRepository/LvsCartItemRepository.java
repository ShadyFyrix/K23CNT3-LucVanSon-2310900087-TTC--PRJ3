package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsCartItem
 * Xử lý truy vấn liên quan đến mục giỏ hàng
 */
@Repository
public interface LvsCartItemRepository extends JpaRepository<LvsCartItem, Long> {

    // Tìm cart item theo cart
    List<LvsCartItem> findByLvsCart_LvsCartId(Long lvsCartId);

    // Tìm cart item theo cart và project
    Optional<LvsCartItem> findByLvsCart_LvsCartIdAndLvsProject_LvsProjectId(Long lvsCartId, Long lvsProjectId);

    // Tìm cart item theo cart và selected
    List<LvsCartItem> findByLvsCart_LvsCartIdAndLvsIsSelectedTrue(Long lvsCartId);

    // Đếm cart item theo cart
    Long countByLvsCart_LvsCartId(Long lvsCartId);

    // Đếm cart item selected theo cart
    Long countByLvsCart_LvsCartIdAndLvsIsSelectedTrue(Long lvsCartId);

    // Xóa cart item theo cart
    void deleteByLvsCart_LvsCartId(Long lvsCartId);

    // Xóa cart item theo cart và project
    void deleteByLvsCart_LvsCartIdAndLvsProject_LvsProjectId(Long lvsCartId, Long lvsProjectId);

    // Cập nhật quantity
    @Modifying
    @Query("UPDATE LvsCartItem ci SET ci.lvsQuantity = :quantity, ci.lvsItemTotal = ci.lvsUnitPrice * :quantity WHERE ci.lvsCartItemId = :itemId")
    void updateQuantity(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);

    // Cập nhật selected status
    @Modifying
    @Query("UPDATE LvsCartItem ci SET ci.lvsIsSelected = :selected WHERE ci.lvsCartItemId = :itemId")
    void updateSelectedStatus(@Param("itemId") Long itemId, @Param("selected") Boolean selected);

    // Cập nhật unit price
    @Modifying
    @Query("UPDATE LvsCartItem ci SET ci.lvsUnitPrice = :unitPrice, ci.lvsItemTotal = :unitPrice * ci.lvsQuantity WHERE ci.lvsCartItemId = :itemId")
    void updateUnitPrice(@Param("itemId") Long itemId, @Param("unitPrice") Double unitPrice);

    // Tính tổng tiền cart item theo cart
    @Query("SELECT SUM(ci.lvsItemTotal) FROM LvsCartItem ci WHERE ci.lvsCart.lvsCartId = :cartId")
    Double getCartTotal(@Param("cartId") Long cartId);

    // Tính tổng tiền cart item selected theo cart
    @Query("SELECT SUM(ci.lvsItemTotal) FROM LvsCartItem ci WHERE ci.lvsCart.lvsCartId = :cartId AND ci.lvsIsSelected = true")
    Double getSelectedCartTotal(@Param("cartId") Long cartId);

    // Tính tổng số lượng cart item theo cart
    @Query("SELECT SUM(ci.lvsQuantity) FROM LvsCartItem ci WHERE ci.lvsCart.lvsCartId = :cartId")
    Integer getTotalItemCount(@Param("cartId") Long cartId);

    // Kiểm tra project đã có trong cart chưa
    boolean existsByLvsCart_LvsCartIdAndLvsProject_LvsProjectId(Long lvsCartId, Long lvsProjectId);

    // Lấy cart item theo user và project
    @Query("SELECT ci FROM LvsCartItem ci WHERE ci.lvsCart.lvsUser.lvsUserId = :userId AND ci.lvsProject.lvsProjectId = :projectId")
    Optional<LvsCartItem> findByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    // Xóa cart item theo user và project
    @Modifying
    @Query("DELETE FROM LvsCartItem ci WHERE ci.lvsCart.lvsUser.lvsUserId = :userId AND ci.lvsProject.lvsProjectId = :projectId")
    void deleteByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);
}