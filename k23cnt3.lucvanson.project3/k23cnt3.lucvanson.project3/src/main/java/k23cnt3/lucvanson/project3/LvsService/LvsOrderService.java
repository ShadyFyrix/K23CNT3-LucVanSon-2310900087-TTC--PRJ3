package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder.LvsOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý đơn hàng
 * Xử lý tạo, thanh toán, hủy, hoàn tiền đơn hàng
 */
public interface LvsOrderService {

    // Lấy đơn hàng theo ID
    LvsOrder lvsGetOrderById(Long lvsOrderId);

    // Lấy đơn hàng theo mã
    LvsOrder lvsGetOrderByCode(String lvsOrderCode);

    // Lấy tất cả đơn hàng
    Page<LvsOrder> lvsGetAllOrders(Pageable lvsPageable);

    // Tìm kiếm đơn hàng
    Page<LvsOrder> lvsSearchOrders(String lvsKeyword, Pageable lvsPageable);

    // Lấy đơn hàng theo user
    Page<LvsOrder> lvsGetOrdersByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy đơn hàng theo status
    Page<LvsOrder> lvsGetOrdersByStatus(String lvsStatus, Pageable lvsPageable);

    // Lấy đơn hàng theo user và status
    Page<LvsOrder> lvsGetOrdersByUserAndStatus(Long lvsUserId, String lvsStatus, Pageable lvsPageable);

    // Tạo đơn hàng từ dự án
    LvsOrder lvsCreateOrderFromProject(Long lvsUserId, Long lvsProjectId, Integer lvsQuantity);

    // Tạo đơn hàng từ giỏ hàng
    LvsOrder lvsCreateOrderFromCart(Long lvsUserId);

    // Lưu đơn hàng
    LvsOrder lvsSaveOrder(LvsOrder lvsOrder);

    // Cập nhật đơn hàng
    LvsOrder lvsUpdateOrder(LvsOrder lvsOrder);

    // Xóa đơn hàng
    void lvsDeleteOrder(Long lvsOrderId);

    // Thanh toán đơn hàng
    boolean lvsProcessPayment(Long lvsOrderId);

    // Hủy đơn hàng
    boolean lvsCancelOrder(Long lvsOrderId, String lvsReason);

    // Hủy đơn hàng bởi LvsAdmin
    boolean lvsCancelOrderByAdmin(Long lvsOrderId, String lvsReason);

    // Hoàn thành đơn hàng
    boolean lvsCompleteOrder(Long lvsOrderId);

    // Hoàn tiền đơn hàng
    boolean lvsRefundOrder(Long lvsOrderId, Double lvsAmount, String lvsReason, Long lvsAdminId);

    // Yêu cầu hoàn tiền
    boolean lvsRequestRefund(Long lvsOrderId, String lvsReason);

    // Cập nhật trạng thái đơn hàng
    LvsOrder lvsUpdateOrderStatus(Long lvsOrderId, LvsOrderStatus lvsStatus, String lvsNotes);

    // Tính tổng tiền đơn hàng
    Double lvsCalculateOrderTotal(Long lvsUserId, Long lvsProjectId, Integer lvsQuantity);

    // Tạo mã đơn hàng duy nhất
    String lvsGenerateOrderCode();

    // Đếm tổng số đơn hàng
    Long lvsCountTotalOrders();

    // Đếm số đơn hàng theo trạng thái
    Long lvsCountOrdersByStatus(LvsOrderStatus lvsStatus);

    // Đếm số đơn hàng mới trong khoảng thời gian
    Long lvsCountNewOrders(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy tổng doanh thu
    Double lvsGetTotalRevenue();

    // Lấy doanh thu theo khoảng thời gian
    Double lvsGetTotalRevenue(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy đơn hàng chờ thanh toán
    List<LvsOrder> lvsGetPendingOrders(Long lvsUserId);

    // Lấy thống kê đơn hàng
    Map<String, Long> lvsGetOrderStats();

    // Xuất hóa đơn PDF
    byte[] lvsGenerateInvoicePdf(Long lvsOrderId);
}