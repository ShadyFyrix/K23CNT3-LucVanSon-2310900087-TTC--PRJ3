package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder.LvsOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface cho entity LvsOrder
 * Xử lý truy vấn liên quan đến đơn hàng
 */
@Repository
public interface LvsOrderRepository extends JpaRepository<LvsOrder, Long> {

        // Tìm đơn hàng theo ID với eager loading
        @Query("SELECT o FROM LvsOrder o LEFT JOIN FETCH o.lvsBuyer LEFT JOIN FETCH o.lvsOrderItems WHERE o.lvsOrderId = :id")
        Optional<LvsOrder> findByIdWithDetails(@Param("id") Long id);

        // Tìm tất cả đơn hàng với eager loading buyer
        @Query("SELECT DISTINCT o FROM LvsOrder o LEFT JOIN FETCH o.lvsBuyer")
        List<LvsOrder> findAllWithBuyer();

        // Tìm đơn hàng theo mã
        Optional<LvsOrder> findByLvsOrderCode(String lvsOrderCode);

        // Tìm đơn hàng theo buyer
        List<LvsOrder> findByLvsBuyer_LvsUserId(Long lvsUserId);

        Page<LvsOrder> findByLvsBuyer_LvsUserId(Long lvsUserId, Pageable pageable);

        // Tìm đơn hàng theo status
        List<LvsOrder> findByLvsStatus(LvsOrderStatus lvsStatus);

        Page<LvsOrder> findByLvsStatus(LvsOrderStatus lvsStatus, Pageable pageable);

        // Tìm đơn hàng theo buyer và status
        List<LvsOrder> findByLvsBuyer_LvsUserIdAndLvsStatus(Long lvsUserId, LvsOrderStatus lvsStatus);

        Page<LvsOrder> findByLvsBuyer_LvsUserIdAndLvsStatus(Long lvsUserId, LvsOrderStatus lvsStatus,
                        Pageable pageable);

        // Tìm đơn hàng đã thanh toán
        List<LvsOrder> findByLvsIsPaidTrue();

        Page<LvsOrder> findByLvsIsPaidTrue(Pageable pageable);

        // Tìm đơn hàng chưa thanh toán
        List<LvsOrder> findByLvsIsPaidFalse();

        Page<LvsOrder> findByLvsIsPaidFalse(Pageable pageable);

        // Tìm kiếm đơn hàng theo keyword
        @Query("SELECT o FROM LvsOrder o WHERE " +
                        "o.lvsOrderCode LIKE %:keyword% OR " +
                        "o.lvsPromotionCode LIKE %:keyword% OR " +
                        "o.lvsNotes LIKE %:keyword%")
        Page<LvsOrder> searchOrders(@Param("keyword") String keyword, Pageable pageable);

        // Đếm đơn hàng theo status
        Long countByLvsStatus(LvsOrderStatus lvsStatus);

        // Đếm đơn hàng theo buyer
        Long countByLvsBuyer_LvsUserId(Long lvsUserId);

        // Đếm đơn hàng trong khoảng thời gian
        Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Đếm đơn hàng đã thanh toán trong khoảng thời gian
        Long countByLvsIsPaidTrueAndLvsPaidAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Lấy đơn hàng mới nhất
        List<LvsOrder> findByOrderByLvsCreatedAtDesc();

        Page<LvsOrder> findByOrderByLvsCreatedAtDesc(Pageable pageable);

        // Lấy đơn hàng theo giá trị
        Page<LvsOrder> findByOrderByLvsFinalAmountDesc(Pageable pageable);

        // Cập nhật status
        @Modifying
        @Query("UPDATE LvsOrder o SET o.lvsStatus = :status WHERE o.lvsOrderId = :orderId")
        void updateStatus(@Param("orderId") Long orderId, @Param("status") LvsOrderStatus status);

        // Cập nhật payment status
        @Modifying
        @Query("UPDATE LvsOrder o SET o.lvsIsPaid = :paid, o.lvsPaidAt = :paidAt WHERE o.lvsOrderId = :orderId")
        void updatePaymentStatus(@Param("orderId") Long orderId, @Param("paid") Boolean paid,
                        @Param("paidAt") LocalDateTime paidAt);

        // Cập nhật discount amount
        @Modifying
        @Query("UPDATE LvsOrder o SET o.lvsDiscountAmount = :discountAmount, o.lvsFinalAmount = o.lvsTotalAmount - :discountAmount WHERE o.lvsOrderId = :orderId")
        void updateDiscountAmount(@Param("orderId") Long orderId, @Param("discountAmount") Double discountAmount);

        // Lấy tổng doanh thu
        @Query("SELECT SUM(o.lvsFinalAmount) FROM LvsOrder o WHERE o.lvsStatus = 'COMPLETED' AND o.lvsIsPaid = true")
        Double getTotalRevenue();

        // Lấy doanh thu trong khoảng thời gian
        @Query("SELECT SUM(o.lvsFinalAmount) FROM LvsOrder o WHERE o.lvsStatus = 'COMPLETED' AND o.lvsIsPaid = true AND o.lvsPaidAt BETWEEN :startDate AND :endDate")
        Double getRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        // Lấy đơn hàng có mã khuyến mãi
        List<LvsOrder> findByLvsPromotionCodeIsNotNull();

        // Lấy đơn hàng theo payment method
        List<LvsOrder> findByLvsPaymentMethod(String lvsPaymentMethod);

        // Tìm đơn hàng có total amount lớn hơn
        List<LvsOrder> findByLvsTotalAmountGreaterThan(Double minAmount);

        // Tìm đơn hàng có final amount lớn hơn
        List<LvsOrder> findByLvsFinalAmountGreaterThan(Double minAmount);

        // Lấy đơn hàng đang chờ xử lý
        List<LvsOrder> findByLvsStatusIn(List<LvsOrderStatus> statuses);

        // Kiểm tra order code đã tồn tại chưa
        boolean existsByLvsOrderCode(String lvsOrderCode);

        // Tạo order code mới
        @Query(value = "SELECT CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(COALESCE(MAX(SUBSTRING(lvs_order_code, 12)), 0) + 1, 5, '0')) "
                        +
                        "FROM lvs_order WHERE DATE(lvs_created_at) = CURDATE()", nativeQuery = true)
        String generateOrderCode();

        // Tìm kiếm theo order code hoặc buyer username
        Page<LvsOrder> findByLvsOrderCodeContainingOrLvsBuyer_LvsUsernameContaining(String orderCode, String username,
                        Pageable pageable);

        // Check if user owns a project (via completed order)
        @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM LvsOrder o " +
                        "JOIN o.lvsOrderItems oi " +
                        "WHERE o.lvsBuyer.lvsUserId = :userId " +
                        "AND oi.lvsProject.lvsProjectId = :projectId " +
                        "AND o.lvsStatus = :status")
        boolean existsByLvsUserLvsUserIdAndLvsOrderItemsLvsProjectLvsProjectIdAndLvsStatus(
                        @Param("userId") Long userId,
                        @Param("projectId") Long projectId,
                        @Param("status") LvsOrderStatus status);

        /**
         * Count downloads (completed orders) for a specific project
         */
        @Query("SELECT COUNT(DISTINCT o) FROM LvsOrder o " +
                        "JOIN o.lvsOrderItems oi " +
                        "WHERE oi.lvsProject.lvsProjectId = :projectId " +
                        "AND o.lvsStatus = 'COMPLETED'")
        long countByLvsProject_LvsProjectId(@Param("projectId") Long projectId);
}
