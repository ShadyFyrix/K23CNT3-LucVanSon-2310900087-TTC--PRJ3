package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction;
import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction.LvsTransactionStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction.LvsTransactionType;
import k23cnt3.lucvanson.project3.LvsRepository.LvsTransactionRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsOrderRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation cho quản lý giao dịch
 * Xử lý nạp, rút, thanh toán, hoàn tiền
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsTransactionServiceImpl implements LvsTransactionService {

    private final LvsTransactionRepository lvsTransactionRepository;
    private final LvsUserRepository lvsUserRepository;
    private final LvsOrderRepository lvsOrderRepository;

    /**
     * Lấy giao dịch theo ID
     * 
     * @param lvsTransactionId ID giao dịch
     * @return Giao dịch tìm thấy
     */
    @Override
    public LvsTransaction lvsGetTransactionById(Long lvsTransactionId) {
        return lvsTransactionRepository.findById(lvsTransactionId).orElse(null);
    }

    /**
     * Lấy tất cả giao dịch với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetAllTransactions(Pageable lvsPageable) {
        return lvsTransactionRepository.findAll(lvsPageable);
    }

    /**
     * Tìm kiếm giao dịch theo keyword
     * 
     * @param lvsKeyword  Từ khóa tìm kiếm
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch tìm thấy
     */
    @Override
    public Page<LvsTransaction> lvsSearchTransactions(String lvsKeyword, Pageable lvsPageable) {
        return lvsTransactionRepository.findByLvsDescriptionContaining(lvsKeyword, lvsPageable);
    }

    /**
     * Lấy giao dịch theo user
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsTransactionRepository.findByLvsUser_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy giao dịch theo type
     * 
     * @param lvsType     Loại giao dịch
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByType(String lvsType, Pageable lvsPageable) {
        LvsTransactionType lvsTransactionType = LvsTransactionType.valueOf(lvsType.toUpperCase());
        return lvsTransactionRepository.findByLvsType(lvsTransactionType, lvsPageable);
    }

    /**
     * Lấy giao dịch theo status
     * 
     * @param lvsStatus   Trạng thái giao dịch
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByStatus(String lvsStatus, Pageable lvsPageable) {
        LvsTransactionStatus lvsTransactionStatus = LvsTransactionStatus.valueOf(lvsStatus.toUpperCase());
        return lvsTransactionRepository.findByLvsStatus(lvsTransactionStatus, lvsPageable);
    }

    /**
     * Lấy giao dịch theo user và type
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsType     Loại giao dịch
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByUserAndType(Long lvsUserId, String lvsType, Pageable lvsPageable) {
        LvsTransactionType lvsTransactionType = LvsTransactionType.valueOf(lvsType.toUpperCase());
        return lvsTransactionRepository.findByLvsUser_LvsUserIdAndLvsType(lvsUserId, lvsTransactionType, lvsPageable);
    }

    /**
     * Lấy giao dịch theo user và status
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsStatus   Trạng thái giao dịch
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByUserAndStatus(Long lvsUserId, String lvsStatus,
            Pageable lvsPageable) {
        LvsTransactionStatus lvsTransactionStatus = LvsTransactionStatus.valueOf(lvsStatus.toUpperCase());
        return lvsTransactionRepository.findByLvsUser_LvsUserIdAndLvsStatus(lvsUserId, lvsTransactionStatus,
                lvsPageable);
    }

    /**
     * Lấy giao dịch theo type và status
     * 
     * @param lvsType     Loại giao dịch
     * @param lvsStatus   Trạng thái giao dịch
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByTypeAndStatus(String lvsType, String lvsStatus,
            Pageable lvsPageable) {
        LvsTransactionType lvsTransactionType = LvsTransactionType.valueOf(lvsType.toUpperCase());
        LvsTransactionStatus lvsTransactionStatus = LvsTransactionStatus.valueOf(lvsStatus.toUpperCase());
        return lvsTransactionRepository.findByLvsTypeAndLvsStatus(lvsTransactionType, lvsTransactionStatus,
                lvsPageable);
    }

    /**
     * Lấy giao dịch theo user, type và status
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsType     Loại giao dịch
     * @param lvsStatus   Trạng thái giao dịch
     * @param lvsPageable Thông tin phân trang
     * @return Trang giao dịch
     */
    @Override
    public Page<LvsTransaction> lvsGetTransactionsByUserAndTypeAndStatus(Long lvsUserId, String lvsType,
            String lvsStatus, Pageable lvsPageable) {
        LvsTransactionType lvsTransactionType = LvsTransactionType.valueOf(lvsType.toUpperCase());
        LvsTransactionStatus lvsTransactionStatus = LvsTransactionStatus.valueOf(lvsStatus.toUpperCase());
        return lvsTransactionRepository.findByLvsUser_LvsUserIdAndLvsTypeAndLvsStatus(
                lvsUserId, lvsTransactionType, lvsTransactionStatus, lvsPageable);
    }

    /**
     * Lưu giao dịch
     * 
     * @param lvsTransaction Thông tin giao dịch
     * @return Giao dịch đã lưu
     */
    @Override
    public LvsTransaction lvsSaveTransaction(LvsTransaction lvsTransaction) {
        lvsTransaction.setLvsCreatedAt(LocalDateTime.now());
        return lvsTransactionRepository.save(lvsTransaction);
    }

    /**
     * Cập nhật giao dịch
     * 
     * @param lvsTransaction Thông tin giao dịch cập nhật
     * @return Giao dịch đã cập nhật
     */
    @Override
    public LvsTransaction lvsUpdateTransaction(LvsTransaction lvsTransaction) {
        LvsTransaction lvsExistingTransaction = lvsGetTransactionById(lvsTransaction.getLvsTransactionId());
        if (lvsExistingTransaction != null) {
            lvsExistingTransaction.setLvsStatus(lvsTransaction.getLvsStatus());
            lvsExistingTransaction.setLvsDescription(lvsTransaction.getLvsDescription());
            lvsExistingTransaction.setLvsPaymentInfo(lvsTransaction.getLvsPaymentInfo());
            lvsExistingTransaction.setLvsAdminNote(lvsTransaction.getLvsAdminNote());

            if (lvsTransaction.getLvsStatus() == LvsTransactionStatus.SUCCESS) {
                lvsExistingTransaction.setLvsApprovedAt(LocalDateTime.now());
            }

            return lvsTransactionRepository.save(lvsExistingTransaction);
        }
        return null;
    }

    /**
     * Xóa giao dịch
     * 
     * @param lvsTransactionId ID giao dịch
     */
    @Override
    public void lvsDeleteTransaction(Long lvsTransactionId) {
        lvsTransactionRepository.deleteById(lvsTransactionId);
    }

    /**
     * Tạo yêu cầu nạp tiền
     * 
     * @param lvsUserId        ID người dùng
     * @param lvsAmount        Số tiền nạp
     * @param lvsPaymentMethod Phương thức thanh toán
     * @return Giao dịch đã tạo
     */
    @Override
    public LvsTransaction lvsCreateDepositRequest(Long lvsUserId, Double lvsAmount, String lvsPaymentMethod) {
        var lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);
        if (lvsUser == null)
            return null;

        LvsTransaction lvsTransaction = new LvsTransaction();
        lvsTransaction.setLvsUser(lvsUser);
        lvsTransaction.setLvsType(LvsTransactionType.DEPOSIT);
        lvsTransaction.setLvsAmount(lvsAmount);
        lvsTransaction.setLvsStatus(LvsTransactionStatus.PENDING);
        lvsTransaction.setLvsDescription("Yêu cầu nạp tiền qua " + lvsPaymentMethod);
        lvsTransaction.setLvsPaymentInfo(lvsPaymentMethod);
        lvsTransaction.setLvsCreatedAt(LocalDateTime.now());

        return lvsTransactionRepository.save(lvsTransaction);
    }

    /**
     * Tạo yêu cầu rút tiền
     * 
     * @param lvsUserId ID người dùng
     * @param lvsAmount Số tiền rút
     * @return Giao dịch đã tạo
     */
    @Override
    public LvsTransaction lvsCreateWithdrawalRequest(Long lvsUserId, Double lvsAmount) {
        var lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);
        if (lvsUser == null)
            return null;

        // Kiểm tra số dư
        if (lvsUser.getLvsBalance() < lvsAmount) {
            throw new RuntimeException("Số dư không đủ để rút");
        }

        LvsTransaction lvsTransaction = new LvsTransaction();
        lvsTransaction.setLvsUser(lvsUser);
        lvsTransaction.setLvsType(LvsTransactionType.WITHDRAWAL);
        lvsTransaction.setLvsAmount(lvsAmount);
        lvsTransaction.setLvsStatus(LvsTransactionStatus.PENDING);
        lvsTransaction.setLvsDescription("Yêu cầu rút tiền");
        lvsTransaction.setLvsCreatedAt(LocalDateTime.now());

        // Tạm giữ số dư
        lvsUser.setLvsBalance(lvsUser.getLvsBalance() - lvsAmount);
        lvsUserRepository.save(lvsUser);

        return lvsTransactionRepository.save(lvsTransaction);
    }

    /**
     * Duyệt nạp tiền
     * 
     * @param lvsTransactionId ID giao dịch
     * @param lvsAdminId       ID LvsAdmin duyệt
     * @return Giao dịch đã duyệt
     */
    @Override
    public LvsTransaction lvsApproveDeposit(Long lvsTransactionId, Long lvsAdminId) {
        LvsTransaction lvsTransaction = lvsGetTransactionById(lvsTransactionId);
        if (lvsTransaction != null && lvsTransaction.getLvsType() == LvsTransactionType.DEPOSIT) {
            // Cộng coin cho user
            var lvsUser = lvsTransaction.getLvsUser();
            lvsUser.setLvsCoin(lvsUser.getLvsCoin() + lvsTransaction.getLvsAmount());
            lvsUserRepository.save(lvsUser);

            // Cập nhật trạng thái giao dịch
            var lvsAdmin = lvsUserRepository.findById(lvsAdminId).orElse(null);
            lvsTransaction.setLvsStatus(LvsTransactionStatus.SUCCESS);
            lvsTransaction.setLvsAdminApprover(lvsAdmin);
            lvsTransaction.setLvsApprovedAt(LocalDateTime.now());

            return lvsTransactionRepository.save(lvsTransaction);
        }
        return null;
    }

    /**
     * Duyệt rút tiền
     * 
     * @param lvsTransactionId ID giao dịch
     * @param lvsAdminId       ID LvsAdmin duyệt
     * @return Giao dịch đã duyệt
     */
    @Override
    public LvsTransaction lvsApproveWithdrawal(Long lvsTransactionId, Long lvsAdminId) {
        LvsTransaction lvsTransaction = lvsGetTransactionById(lvsTransactionId);
        if (lvsTransaction != null && lvsTransaction.getLvsType() == LvsTransactionType.WITHDRAWAL) {
            // Số dư đã được tạm giữ, chỉ cần cập nhật trạng thái
            var lvsAdmin = lvsUserRepository.findById(lvsAdminId).orElse(null);
            lvsTransaction.setLvsStatus(LvsTransactionStatus.SUCCESS);
            lvsTransaction.setLvsAdminApprover(lvsAdmin);
            lvsTransaction.setLvsApprovedAt(LocalDateTime.now());

            return lvsTransactionRepository.save(lvsTransaction);
        }
        return null;
    }

    /**
     * Từ chối giao dịch
     * 
     * @param lvsTransactionId ID giao dịch
     * @param lvsAdminId       ID LvsAdmin từ chối
     * @param lvsReason        Lý do từ chối
     * @return Giao dịch đã từ chối
     */
    @Override
    public LvsTransaction lvsRejectTransaction(Long lvsTransactionId, Long lvsAdminId, String lvsReason) {
        LvsTransaction lvsTransaction = lvsGetTransactionById(lvsTransactionId);
        if (lvsTransaction != null) {
            var lvsAdmin = lvsUserRepository.findById(lvsAdminId).orElse(null);
            lvsTransaction.setLvsStatus(LvsTransactionStatus.FAILED);
            lvsTransaction.setLvsAdminApprover(lvsAdmin);
            lvsTransaction.setLvsApprovedAt(LocalDateTime.now());
            lvsTransaction.setLvsAdminNote(lvsReason);

            // Nếu là rút tiền, hoàn lại số dư
            if (lvsTransaction.getLvsType() == LvsTransactionType.WITHDRAWAL) {
                var lvsUser = lvsTransaction.getLvsUser();
                lvsUser.setLvsBalance(lvsUser.getLvsBalance() + lvsTransaction.getLvsAmount());
                lvsUserRepository.save(lvsUser);
            }

            return lvsTransactionRepository.save(lvsTransaction);
        }
        return null;
    }

    /**
     * Hủy giao dịch
     * 
     * @param lvsTransactionId ID giao dịch
     */
    @Override
    public void lvsCancelTransaction(Long lvsTransactionId) {
        LvsTransaction lvsTransaction = lvsGetTransactionById(lvsTransactionId);
        if (lvsTransaction != null && lvsTransaction.getLvsStatus() == LvsTransactionStatus.PENDING) {
            lvsTransaction.setLvsStatus(LvsTransactionStatus.FAILED);

            // Nếu là rút tiền, hoàn lại số dư
            if (lvsTransaction.getLvsType() == LvsTransactionType.WITHDRAWAL) {
                var lvsUser = lvsTransaction.getLvsUser();
                lvsUser.setLvsBalance(lvsUser.getLvsBalance() + lvsTransaction.getLvsAmount());
                lvsUserRepository.save(lvsUser);
            }

            lvsTransactionRepository.save(lvsTransaction);
        }
    }

    /**
     * Xử lý thanh toán đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     * @param lvsUserId  ID người dùng
     * @return true nếu thành công
     */
    @Override
    public boolean lvsProcessOrderPayment(Long lvsOrderId, Long lvsUserId) {
        var lvsOrder = lvsOrderRepository.findById(lvsOrderId).orElse(null);
        var lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);

        if (lvsOrder == null || lvsUser == null)
            return false;

        // Kiểm tra số dư
        if (lvsUser.getLvsCoin() < lvsOrder.getLvsFinalAmount()) {
            return false;
        }

        // Trừ coin của người mua
        lvsUser.setLvsCoin(lvsUser.getLvsCoin() - lvsOrder.getLvsFinalAmount());
        lvsUserRepository.save(lvsUser);

        // Tạo giao dịch PURCHASE cho người mua (số âm = mất tiền)
        LvsTransaction lvsTransaction = new LvsTransaction();
        lvsTransaction.setLvsUser(lvsUser);
        lvsTransaction.setLvsType(LvsTransactionType.PURCHASE);
        lvsTransaction.setLvsAmount(-lvsOrder.getLvsFinalAmount()); // Số âm vì người mua mất tiền
        lvsTransaction.setLvsStatus(LvsTransactionStatus.SUCCESS);
        lvsTransaction.setLvsDescription("Mua hàng - Đơn hàng #" + lvsOrder.getLvsOrderCode());
        lvsTransaction.setLvsOrder(lvsOrder);
        lvsTransaction.setLvsCreatedAt(LocalDateTime.now());

        lvsTransactionRepository.save(lvsTransaction);

        // Cập nhật trạng thái đơn hàng
        lvsOrder.setLvsIsPaid(true);
        lvsOrder.setLvsPaidAt(LocalDateTime.now());
        lvsOrderRepository.save(lvsOrder);

        return true;
    }

    /**
     * Xử lý bán dự án
     * 
     * @param lvsOrderId  ID đơn hàng
     * @param lvsSellerId ID người bán
     * @return true nếu thành công
     */
    @Override
    public boolean lvsProcessSaleTransaction(Long lvsOrderId, Long lvsSellerId) {
        var lvsOrder = lvsOrderRepository.findById(lvsOrderId).orElse(null);
        var lvsSeller = lvsUserRepository.findById(lvsSellerId).orElse(null);

        if (lvsOrder == null || lvsSeller == null)
            return false;

        // Tính doanh thu cho người bán
        double lvsRevenue = lvsOrder.getLvsFinalAmount() * 0.8; // Giả sử platform giữ 20%

        // Cộng doanh thu cho người bán
        lvsSeller.setLvsBalance(lvsSeller.getLvsBalance() + lvsRevenue);
        lvsUserRepository.save(lvsSeller);

        // Tạo giao dịch SALE cho người bán (số dương = nhận tiền)
        LvsTransaction lvsTransaction = new LvsTransaction();
        lvsTransaction.setLvsUser(lvsSeller);
        lvsTransaction.setLvsType(LvsTransactionType.SALE);
        lvsTransaction.setLvsAmount(lvsRevenue); // Số dương vì người bán nhận tiền
        lvsTransaction.setLvsStatus(LvsTransactionStatus.SUCCESS);
        lvsTransaction.setLvsDescription(
                "Bán hàng - Đơn hàng #" + lvsOrder.getLvsOrderCode() + " (Doanh thu: " + lvsRevenue + " coin)");
        lvsTransaction.setLvsOrder(lvsOrder);
        lvsTransaction.setLvsCreatedAt(LocalDateTime.now());

        lvsTransactionRepository.save(lvsTransaction);

        return true;
    }

    /**
     * Xử lý hoàn tiền đơn hàng
     * Tạo 2 giao dịch REFUND: 1 cho người mua (nhận lại tiền), 1 cho người bán (mất
     * tiền)
     * 
     * @param lvsOrderId  ID đơn hàng
     * @param lvsBuyerId  ID người mua
     * @param lvsSellerId ID người bán
     * @param lvsReason   Lý do hoàn tiền
     * @return true nếu thành công
     */
    @Override
    public boolean lvsProcessRefundTransaction(Long lvsOrderId, Long lvsBuyerId, Long lvsSellerId, String lvsReason) {
        var lvsOrder = lvsOrderRepository.findById(lvsOrderId).orElse(null);
        var lvsBuyer = lvsUserRepository.findById(lvsBuyerId).orElse(null);
        var lvsSeller = lvsUserRepository.findById(lvsSellerId).orElse(null);

        if (lvsOrder == null || lvsBuyer == null || lvsSeller == null)
            return false;

        // Tính số tiền hoàn lại
        Double lvsRefundAmount = lvsOrder.getLvsFinalAmount();
        Double lvsSellerRevenue = lvsRefundAmount * 0.8; // Người bán đã nhận 80%

        // Hoàn tiền cho người mua
        lvsBuyer.setLvsCoin(lvsBuyer.getLvsCoin() + lvsRefundAmount);
        lvsUserRepository.save(lvsBuyer);

        // Trừ tiền người bán
        lvsSeller.setLvsBalance(lvsSeller.getLvsBalance() - lvsSellerRevenue);
        lvsUserRepository.save(lvsSeller);

        // Tạo giao dịch REFUND cho người mua (số dương = nhận lại tiền)
        LvsTransaction lvsBuyerRefund = new LvsTransaction();
        lvsBuyerRefund.setLvsUser(lvsBuyer);
        lvsBuyerRefund.setLvsType(LvsTransactionType.REFUND);
        lvsBuyerRefund.setLvsAmount(lvsRefundAmount); // Số dương vì nhận lại tiền
        lvsBuyerRefund.setLvsStatus(LvsTransactionStatus.SUCCESS);
        lvsBuyerRefund
                .setLvsDescription("Hoàn tiền - Đơn hàng #" + lvsOrder.getLvsOrderCode() + " - Lý do: " + lvsReason);
        lvsBuyerRefund.setLvsOrder(lvsOrder);
        lvsBuyerRefund.setLvsCreatedAt(LocalDateTime.now());
        lvsTransactionRepository.save(lvsBuyerRefund);

        // Tạo giao dịch REFUND cho người bán (số âm = mất tiền)
        LvsTransaction lvsSellerRefund = new LvsTransaction();
        lvsSellerRefund.setLvsUser(lvsSeller);
        lvsSellerRefund.setLvsType(LvsTransactionType.REFUND);
        lvsSellerRefund.setLvsAmount(-lvsSellerRevenue); // Số âm vì mất tiền
        lvsSellerRefund.setLvsStatus(LvsTransactionStatus.SUCCESS);
        lvsSellerRefund.setLvsDescription(
                "Hoàn tiền cho khách - Đơn hàng #" + lvsOrder.getLvsOrderCode() + " - Lý do: " + lvsReason);
        lvsSellerRefund.setLvsOrder(lvsOrder);
        lvsSellerRefund.setLvsCreatedAt(LocalDateTime.now());
        lvsTransactionRepository.save(lvsSellerRefund);

        return true;
    }

    /**
     * Đếm tổng số giao dịch
     * 
     * @return Tổng số giao dịch
     */
    @Override
    public Long lvsCountTotalTransactions() {
        return lvsTransactionRepository.count();
    }

    /**
     * Đếm số giao dịch theo trạng thái
     * 
     * @param lvsStatus Trạng thái cần đếm
     * @return Số giao dịch
     */
    @Override
    public Long lvsCountTransactionsByStatus(String lvsStatus) {
        LvsTransactionStatus lvsTransactionStatus = LvsTransactionStatus.valueOf(lvsStatus.toUpperCase());
        return lvsTransactionRepository.countByLvsStatus(lvsTransactionStatus);
    }

    /**
     * Đếm số giao dịch đang chờ xử lý
     * 
     * @return Số giao dịch chờ xử lý
     */
    @Override
    public Long lvsCountPendingTransactions() {
        return lvsTransactionRepository.countByLvsStatus(LvsTransactionStatus.PENDING);
    }

    /**
     * Lấy tổng doanh thu
     * 
     * @return Tổng doanh thu
     */
    @Override
    public Double lvsGetTotalRevenue() {
        return lvsTransactionRepository.sumAmountByTypeAndStatus(
                LvsTransactionType.PURCHASE, LvsTransactionStatus.SUCCESS);
    }

    /**
     * Lấy tổng doanh thu theo khoảng thời gian
     * 
     * @param lvsStartDate Ngày bắt đầu
     * @param lvsEndDate   Ngày kết thúc
     * @return Tổng doanh thu
     */
    @Override
    public Double lvsGetTotalRevenue(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        return lvsTransactionRepository.sumAmountByTypeAndStatusAndDateRange(
                LvsTransactionType.PURCHASE, LvsTransactionStatus.SUCCESS,
                lvsStartDate.atStartOfDay(), lvsEndDate.atTime(23, 59, 59));
    }

    /**
     * Lấy thống kê doanh thu 30 ngày gần nhất
     * 
     * @return Map thống kê doanh thu
     */
    @Override
    public Map<String, Double> lvsGetRevenueStatsLast30Days() {
        Map<String, Double> lvsStats = new HashMap<>();
        LocalDate lvsEndDate = LocalDate.now();
        LocalDate lvsStartDate = lvsEndDate.minusDays(30);

        for (LocalDate lvsDate = lvsStartDate; !lvsDate.isAfter(lvsEndDate); lvsDate = lvsDate.plusDays(1)) {
            Double lvsRevenue = lvsTransactionRepository.sumAmountByTypeAndStatusAndDateRange(
                    LvsTransactionType.PURCHASE, LvsTransactionStatus.SUCCESS,
                    lvsDate.atStartOfDay(), lvsDate.atTime(23, 59, 59));
            lvsStats.put(lvsDate.toString(), lvsRevenue != null ? lvsRevenue : 0.0);
        }

        return lvsStats;
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu
     * 
     * @return Dữ liệu biểu đồ
     */
    @Override
    public Map<String, Object> lvsGetRevenueChartData() {
        Map<String, Object> lvsChartData = new HashMap<>();
        Map<String, Double> lvsStats = lvsGetRevenueStatsLast30Days();

        lvsChartData.put("labels", lvsStats.keySet());
        lvsChartData.put("data", lvsStats.values());

        return lvsChartData;
    }

    /**
     * Lấy lịch sử giao dịch gần đây
     * 
     * @param lvsUserId ID người dùng
     * @param lvsLimit  Giới hạn số lượng
     * @return Danh sách giao dịch gần đây
     */
    @Override
    public List<LvsTransaction> lvsGetRecentTransactions(Long lvsUserId, int lvsLimit) {
        // TODO: Fix repository method - expects Pageable not int
        // return
        // lvsTransactionRepository.findTopByLvsUser_LvsUserIdOrderByLvsCreatedAtDesc(lvsUserId,
        // lvsLimit);
        return lvsTransactionRepository.findTopByLvsUser_LvsUserIdOrderByLvsCreatedAtDesc(
                lvsUserId,
                org.springframework.data.domain.PageRequest.of(0, lvsLimit));
    }

    /**
     * Kiểm tra số dư
     * 
     * @param lvsUserId ID người dùng
     * @return Số dư
     */
    @Override
    public Double lvsGetUserBalance(Long lvsUserId) {
        var lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);
        return lvsUser != null ? lvsUser.getLvsCoin() : 0.0;
    }

    /**
     * Chuyển đổi Balance (doanh thu) sang Coin
     * 
     * @param lvsUserId ID người dùng
     * @param lvsAmount Số tiền cần chuyển đổi
     * @return Giao dịch đã tạo
     */
    @Override
    public LvsTransaction lvsConvertBalanceToCoin(Long lvsUserId, Double lvsAmount) {
        var lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);
        if (lvsUser == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        // Kiểm tra số dư Balance
        if (lvsUser.getLvsBalance() < lvsAmount) {
            throw new RuntimeException("Số dư doanh thu không đủ để chuyển đổi");
        }

        // Kiểm tra số tiền tối thiểu (ví dụ: 1000 coin)
        if (lvsAmount < 1000.0) {
            throw new RuntimeException("Số tiền chuyển đổi tối thiểu là 1,000 coin");
        }

        // Trừ Balance
        lvsUser.setLvsBalance(lvsUser.getLvsBalance() - lvsAmount);

        // Cộng Coin
        lvsUser.setLvsCoin(lvsUser.getLvsCoin() + lvsAmount);

        lvsUserRepository.save(lvsUser);

        // Tạo giao dịch BALANCE_TO_COIN
        LvsTransaction lvsTransaction = new LvsTransaction();
        lvsTransaction.setLvsUser(lvsUser);
        lvsTransaction.setLvsType(LvsTransactionType.BALANCE_TO_COIN);
        lvsTransaction.setLvsAmount(lvsAmount);
        lvsTransaction.setLvsStatus(LvsTransactionStatus.SUCCESS);
        lvsTransaction.setLvsDescription("Chuyển đổi doanh thu sang coin: " + lvsAmount + " coin");
        lvsTransaction.setLvsCreatedAt(LocalDateTime.now());

        return lvsTransactionRepository.save(lvsTransaction);
    }
}