package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction;
import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction.LvsTransactionStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction.LvsTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý giao dịch
 * Xử lý nạp, rút, thanh toán, hoàn tiền
 */
public interface LvsTransactionService {

    // Lấy giao dịch theo ID
    LvsTransaction lvsGetTransactionById(Long lvsTransactionId);

    // Lấy tất cả giao dịch
    Page<LvsTransaction> lvsGetAllTransactions(Pageable lvsPageable);

    // Tìm kiếm giao dịch
    Page<LvsTransaction> lvsSearchTransactions(String lvsKeyword, Pageable lvsPageable);

    // Lấy giao dịch theo user
    Page<LvsTransaction> lvsGetTransactionsByUser(Long lvsUserId, Pageable lvsPageable);

    // Lấy giao dịch theo type
    Page<LvsTransaction> lvsGetTransactionsByType(String lvsType, Pageable lvsPageable);

    // Lấy giao dịch theo status
    Page<LvsTransaction> lvsGetTransactionsByStatus(String lvsStatus, Pageable lvsPageable);

    // Lấy giao dịch theo user và type
    Page<LvsTransaction> lvsGetTransactionsByUserAndType(Long lvsUserId, String lvsType, Pageable lvsPageable);

    // Lấy giao dịch theo user và status
    Page<LvsTransaction> lvsGetTransactionsByUserAndStatus(Long lvsUserId, String lvsStatus, Pageable lvsPageable);

    // Lấy giao dịch theo type và status
    Page<LvsTransaction> lvsGetTransactionsByTypeAndStatus(String lvsType, String lvsStatus, Pageable lvsPageable);

    // Lấy giao dịch theo user, type và status
    Page<LvsTransaction> lvsGetTransactionsByUserAndTypeAndStatus(Long lvsUserId, String lvsType, String lvsStatus,
            Pageable lvsPageable);

    // Lưu giao dịch
    LvsTransaction lvsSaveTransaction(LvsTransaction lvsTransaction);

    // Cập nhật giao dịch
    LvsTransaction lvsUpdateTransaction(LvsTransaction lvsTransaction);

    // Xóa giao dịch
    void lvsDeleteTransaction(Long lvsTransactionId);

    // Tạo yêu cầu nạp tiền
    LvsTransaction lvsCreateDepositRequest(Long lvsUserId, Double lvsAmount, String lvsPaymentMethod);

    // Tạo yêu cầu rút tiền
    LvsTransaction lvsCreateWithdrawalRequest(Long lvsUserId, Double lvsAmount);

    // Duyệt nạp tiền
    LvsTransaction lvsApproveDeposit(Long lvsTransactionId, Long lvsAdminId);

    // Duyệt rút tiền
    LvsTransaction lvsApproveWithdrawal(Long lvsTransactionId, Long lvsAdminId);

    // Từ chối giao dịch
    LvsTransaction lvsRejectTransaction(Long lvsTransactionId, Long lvsAdminId, String lvsReason);

    // Hủy giao dịch
    void lvsCancelTransaction(Long lvsTransactionId);

    // Xử lý thanh toán đơn hàng
    boolean lvsProcessOrderPayment(Long lvsOrderId, Long lvsUserId);

    // Xử lý bán dự án
    boolean lvsProcessSaleTransaction(Long lvsOrderId, Long lvsSellerId);

    // Xử lý hoàn tiền đơn hàng (tạo 2 giao dịch REFUND)
    boolean lvsProcessRefundTransaction(Long lvsOrderId, Long lvsBuyerId, Long lvsSellerId, String lvsReason);

    // Đếm tổng số giao dịch
    Long lvsCountTotalTransactions();

    // Đếm số giao dịch theo trạng thái
    Long lvsCountTransactionsByStatus(String lvsStatus);

    // Đếm số giao dịch đang chờ xử lý
    Long lvsCountPendingTransactions();

    // Lấy tổng doanh thu
    Double lvsGetTotalRevenue();

    // Lấy tổng doanh thu theo khoảng thời gian
    Double lvsGetTotalRevenue(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy thống kê doanh thu 30 ngày gần nhất
    Map<String, Double> lvsGetRevenueStatsLast30Days();

    // Lấy dữ liệu biểu đồ doanh thu
    Map<String, Object> lvsGetRevenueChartData();

    // Lấy lịch sử giao dịch gần đây
    List<LvsTransaction> lvsGetRecentTransactions(Long lvsUserId, int lvsLimit);

    // Kiểm tra số dư
    Double lvsGetUserBalance(Long lvsUserId);

    // Chuyển đổi Balance sang Coin
    LvsTransaction lvsConvertBalanceToCoin(Long lvsUserId, Double lvsAmount);
}