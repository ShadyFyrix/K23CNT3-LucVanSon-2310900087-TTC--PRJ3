package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface cho entity LvsOrderItem
 * Xử lý truy vấn liên quan đến mục đơn hàng
 */
@Repository
public interface LvsOrderItemRepository extends JpaRepository<LvsOrderItem, Long> {

    // Tìm order item theo order
    List<LvsOrderItem> findByLvsOrder_LvsOrderId(Long lvsOrderId);

    // Tìm order item theo project
    List<LvsOrderItem> findByLvsProject_LvsProjectId(Long lvsProjectId);

    // Tìm order item theo seller
    List<LvsOrderItem> findByLvsSeller_LvsUserId(Long lvsUserId);

    // Tìm order item theo order và project
    List<LvsOrderItem> findByLvsOrder_LvsOrderIdAndLvsProject_LvsProjectId(Long lvsOrderId, Long lvsProjectId);

    // Đếm order item theo order
    Long countByLvsOrder_LvsOrderId(Long lvsOrderId);

    // Đếm order item theo project
    Long countByLvsProject_LvsProjectId(Long lvsProjectId);

    // Đếm order item theo seller
    Long countByLvsSeller_LvsUserId(Long lvsUserId);

    // Tính tổng tiền order item theo order
    @Query("SELECT SUM(oi.lvsItemTotal) FROM LvsOrderItem oi WHERE oi.lvsOrder.lvsOrderId = :orderId")
    Double getOrderTotal(@Param("orderId") Long orderId);

    // Tính tổng tiền order item theo project
    @Query("SELECT SUM(oi.lvsItemTotal) FROM LvsOrderItem oi WHERE oi.lvsProject.lvsProjectId = :projectId")
    Double getProjectRevenue(@Param("projectId") Long projectId);

    // Tính tổng tiền order item theo seller
    @Query("SELECT SUM(oi.lvsItemTotal) FROM LvsOrderItem oi WHERE oi.lvsSeller.lvsUserId = :sellerId")
    Double getSellerRevenue(@Param("sellerId") Long sellerId);

    // Đếm số lượng bán theo project
    @Query("SELECT SUM(oi.lvsQuantity) FROM LvsOrderItem oi WHERE oi.lvsProject.lvsProjectId = :projectId")
    Integer getTotalSoldQuantity(@Param("projectId") Long projectId);

    // Lấy danh sách project đã bán bởi seller
    @Query("SELECT DISTINCT oi.lvsProject FROM LvsOrderItem oi WHERE oi.lvsSeller.lvsUserId = :sellerId")
    List<Object[]> getSoldProjectsBySeller(@Param("sellerId") Long sellerId);

    // Kiểm tra user đã mua project chưa
    @Query("SELECT COUNT(oi) > 0 FROM LvsOrderItem oi WHERE oi.lvsOrder.lvsBuyer.lvsUserId = :userId AND oi.lvsProject.lvsProjectId = :projectId")
    boolean hasUserPurchasedProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    // Lấy order item theo buyer
    @Query("SELECT oi FROM LvsOrderItem oi WHERE oi.lvsOrder.lvsBuyer.lvsUserId = :userId")
    List<LvsOrderItem> findByBuyer(@Param("userId") Long userId);

    // Lấy tổng số project đã bán
    @Query("SELECT COUNT(DISTINCT oi.lvsProject.lvsProjectId) FROM LvsOrderItem oi")
    Long countDistinctSoldProjects();

    // Lấy top project bán chạy
    @Query("SELECT oi.lvsProject.lvsProjectId, SUM(oi.lvsQuantity) as totalQuantity " +
            "FROM LvsOrderItem oi " +
            "GROUP BY oi.lvsProject.lvsProjectId " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> getTopSellingProjects();

    // Lấy top seller
    @Query("SELECT oi.lvsSeller.lvsUserId, SUM(oi.lvsItemTotal) as totalRevenue " +
            "FROM LvsOrderItem oi " +
            "GROUP BY oi.lvsSeller.lvsUserId " +
            "ORDER BY totalRevenue DESC")
    List<Object[]> getTopSellers();
}