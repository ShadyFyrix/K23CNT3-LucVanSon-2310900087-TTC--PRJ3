package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsEntity.LvsOrder.LvsOrderStatus;
import k23cnt3.lucvanson.project3.LvsRepository.*;
import k23cnt3.lucvanson.project3.LvsService.LvsOrderService;
import k23cnt3.lucvanson.project3.LvsService.LvsCartService;
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
 * Service implementation cho quản lý đơn hàng
 * Xử lý tạo, thanh toán, hủy, hoàn tiền đơn hàng
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsOrderServiceImpl implements LvsOrderService {

    private final LvsOrderRepository lvsOrderRepository;
    private final LvsUserRepository lvsUserRepository;
    private final LvsProjectRepository lvsProjectRepository;
    private final LvsOrderItemRepository lvsOrderItemRepository;
    private final LvsTransactionRepository lvsTransactionRepository;
    private final LvsCartService lvsCartService;

    /**
     * Lấy đơn hàng theo ID
     * 
     * @param lvsOrderId ID đơn hàng
     * @return Đơn hàng tìm thấy
     */
    @Override
    public LvsOrder lvsGetOrderById(Long lvsOrderId) {
        return lvsOrderRepository.findByIdWithDetails(lvsOrderId).orElse(null);
    }

    /**
     * Lấy đơn hàng theo mã
     * 
     * @param lvsOrderCode Mã đơn hàng
     * @return Đơn hàng tìm thấy
     */
    @Override
    public LvsOrder lvsGetOrderByCode(String lvsOrderCode) {
        return lvsOrderRepository.findByLvsOrderCode(lvsOrderCode).orElse(null);
    }

    /**
     * Lấy tất cả đơn hàng với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang đơn hàng
     */
    @Override
    public Page<LvsOrder> lvsGetAllOrders(Pageable lvsPageable) {
        return lvsOrderRepository.findAll(lvsPageable);
    }

    /**
     * Tìm kiếm đơn hàng theo keyword
     * 
     * @param lvsKeyword  Từ khóa tìm kiếm
     * @param lvsPageable Thông tin phân trang
     * @return Trang đơn hàng tìm thấy
     */
    @Override
    public Page<LvsOrder> lvsSearchOrders(String lvsKeyword, Pageable lvsPageable) {
        return lvsOrderRepository.findByLvsOrderCodeContainingOrLvsBuyer_LvsUsernameContaining(
                lvsKeyword, lvsKeyword, lvsPageable);
    }

    /**
     * Lấy đơn hàng theo user
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsPageable Thông tin phân trang
     * @return Trang đơn hàng
     */
    @Override
    public Page<LvsOrder> lvsGetOrdersByUser(Long lvsUserId, Pageable lvsPageable) {
        return lvsOrderRepository.findByLvsBuyer_LvsUserId(lvsUserId, lvsPageable);
    }

    /**
     * Lấy đơn hàng theo status
     * 
     * @param lvsStatus   Trạng thái cần lọc
     * @param lvsPageable Thông tin phân trang
     * @return Trang đơn hàng
     */
    @Override
    public Page<LvsOrder> lvsGetOrdersByStatus(String lvsStatus, Pageable lvsPageable) {
        LvsOrderStatus lvsOrderStatus = LvsOrderStatus.valueOf(lvsStatus.toUpperCase());
        return lvsOrderRepository.findByLvsStatus(lvsOrderStatus, lvsPageable);
    }

    /**
     * Lấy đơn hàng theo user và status
     * 
     * @param lvsUserId   ID người dùng
     * @param lvsStatus   Trạng thái cần lọc
     * @param lvsPageable Thông tin phân trang
     * @return Trang đơn hàng
     */
    @Override
    public Page<LvsOrder> lvsGetOrdersByUserAndStatus(Long lvsUserId, String lvsStatus, Pageable lvsPageable) {
        LvsOrderStatus lvsOrderStatus = LvsOrderStatus.valueOf(lvsStatus.toUpperCase());
        return lvsOrderRepository.findByLvsBuyer_LvsUserIdAndLvsStatus(lvsUserId, lvsOrderStatus, lvsPageable);
    }

    /**
     * Tạo đơn hàng từ dự án
     * 
     * @param lvsUserId    ID người dùng
     * @param lvsProjectId ID dự án
     * @param lvsQuantity  Số lượng
     * @return Đơn hàng đã tạo
     */
    @Override
    public LvsOrder lvsCreateOrderFromProject(Long lvsUserId, Long lvsProjectId, Integer lvsQuantity) {
        LvsUser lvsBuyer = lvsUserRepository.findById(lvsUserId).orElse(null);
        LvsProject lvsProject = lvsProjectRepository.findById(lvsProjectId).orElse(null);

        if (lvsBuyer == null || lvsProject == null) {
            return null;
        }

        // Tạo đơn hàng mới
        LvsOrder lvsOrder = new LvsOrder();
        lvsOrder.setLvsBuyer(lvsBuyer);
        lvsOrder.setLvsOrderCode(lvsGenerateOrderCode());
        lvsOrder.setLvsStatus(LvsOrderStatus.PENDING);
        lvsOrder.setLvsPaymentMethod("COIN");
        lvsOrder.setLvsCreatedAt(LocalDateTime.now());
        lvsOrder.setLvsUpdatedAt(LocalDateTime.now());

        // Tạo order item
        LvsOrderItem lvsOrderItem = new LvsOrderItem();
        lvsOrderItem.setLvsOrder(lvsOrder);
        lvsOrderItem.setLvsProject(lvsProject);
        lvsOrderItem.setLvsSeller(lvsProject.getLvsUser());
        lvsOrderItem.setLvsQuantity(lvsQuantity);
        lvsOrderItem.setLvsUnitPrice(lvsProject.getLvsPrice());
        lvsOrderItem.setLvsCreatedAt(LocalDateTime.now());

        lvsOrder.getLvsOrderItems().add(lvsOrderItem);

        // Tính toán tổng tiền (calculateAmounts() will be called automatically by
        // @PrePersist)
        lvsOrder.setLvsTotalAmount(lvsOrderItem.getLvsQuantity() * lvsOrderItem.getLvsUnitPrice());
        lvsOrder.setLvsFinalAmount(lvsOrder.getLvsTotalAmount());

        return lvsOrderRepository.save(lvsOrder);
    }

    /**
     * Tạo đơn hàng từ giỏ hàng
     * 
     * @param lvsUserId ID người dùng
     * @return Đơn hàng đã tạo
     */
    @Override
    public LvsOrder lvsCreateOrderFromCart(Long lvsUserId) {
        // Delegate to CartService to convert cart to order
        return lvsCartService.lvsConvertCartToOrder(lvsUserId);
    }

    /**
     * Lưu đơn hàng
     * 
     * @param lvsOrder Thông tin đơn hàng
     * @return Đơn hàng đã lưu
     */
    @Override
    public LvsOrder lvsSaveOrder(LvsOrder lvsOrder) {
        lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
        return lvsOrderRepository.save(lvsOrder);
    }

    /**
     * Cập nhật đơn hàng
     * 
     * @param lvsOrder Thông tin đơn hàng cập nhật
     * @return Đơn hàng đã cập nhật
     */
    @Override
    public LvsOrder lvsUpdateOrder(LvsOrder lvsOrder) {
        LvsOrder lvsExistingOrder = lvsGetOrderById(lvsOrder.getLvsOrderId());
        if (lvsExistingOrder != null) {
            // Chỉ cập nhật các trường cho phép
            lvsExistingOrder.setLvsStatus(lvsOrder.getLvsStatus());
            lvsExistingOrder.setLvsPaymentMethod(lvsOrder.getLvsPaymentMethod());
            lvsExistingOrder.setLvsPromotionCode(lvsOrder.getLvsPromotionCode());
            lvsExistingOrder.setLvsNotes(lvsOrder.getLvsNotes());
            lvsExistingOrder.setLvsUpdatedAt(LocalDateTime.now());

            if (Boolean.TRUE.equals(lvsOrder.getLvsIsPaid())) {
                lvsExistingOrder.setLvsIsPaid(true);
                lvsExistingOrder.setLvsPaidAt(LocalDateTime.now());
            }

            return lvsOrderRepository.save(lvsExistingOrder);
        }
        return null;
    }

    /**
     * Xóa đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     */
    @Override
    public void lvsDeleteOrder(Long lvsOrderId) {
        lvsOrderRepository.deleteById(lvsOrderId);
    }

    /**
     * Thanh toán đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     * @return true nếu thanh toán thành công
     */
    @Override
    public boolean lvsProcessPayment(Long lvsOrderId) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null) {
            // Kiểm tra số dư của người mua
            LvsUser lvsBuyer = lvsOrder.getLvsBuyer();
            if (lvsBuyer.getLvsCoin() >= lvsOrder.getLvsFinalAmount()) {
                // Trừ coin của người mua
                lvsBuyer.setLvsCoin(lvsBuyer.getLvsCoin() - lvsOrder.getLvsFinalAmount());
                lvsBuyer.setLvsUpdatedAt(LocalDateTime.now());
                lvsUserRepository.save(lvsBuyer);

                // Cộng doanh thu cho người bán
                for (LvsOrderItem lvsItem : lvsOrder.getLvsOrderItems()) {
                    LvsUser lvsSeller = lvsItem.getLvsSeller();
                    Double lvsRevenue = lvsItem.getLvsItemTotal();
                    lvsSeller.setLvsBalance(lvsSeller.getLvsBalance() + lvsRevenue);
                    lvsSeller.setLvsUpdatedAt(LocalDateTime.now());
                    lvsUserRepository.save(lvsSeller);
                }

                // Cập nhật trạng thái đơn hàng
                lvsOrder.setLvsStatus(LvsOrderStatus.COMPLETED);
                lvsOrder.setLvsIsPaid(true);
                lvsOrder.setLvsPaidAt(LocalDateTime.now());
                lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
                lvsOrderRepository.save(lvsOrder);

                // Tạo giao dịch thanh toán
                LvsTransaction lvsTransaction = new LvsTransaction();
                lvsTransaction.setLvsUser(lvsBuyer);
                lvsTransaction.setLvsType(LvsTransaction.LvsTransactionType.PURCHASE);
                lvsTransaction.setLvsAmount(lvsOrder.getLvsFinalAmount());
                lvsTransaction.setLvsStatus(LvsTransaction.LvsTransactionStatus.SUCCESS);
                lvsTransaction.setLvsDescription("Thanh toán đơn hàng " + lvsOrder.getLvsOrderCode());
                lvsTransaction.setLvsOrder(lvsOrder);
                lvsTransaction.setLvsCreatedAt(LocalDateTime.now());
                lvsTransactionRepository.save(lvsTransaction);

                return true;
            }
        }
        return false;
    }

    /**
     * Hủy đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     * @param lvsReason  Lý do hủy
     * @return true nếu hủy thành công
     */
    @Override
    public boolean lvsCancelOrder(Long lvsOrderId, String lvsReason) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null && lvsOrder.getLvsStatus() == LvsOrderStatus.PENDING) {
            lvsOrder.setLvsStatus(LvsOrderStatus.CANCELLED);
            lvsOrder.setLvsNotes("Hủy bởi người mua: " + lvsReason);
            lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
            lvsOrderRepository.save(lvsOrder);
            return true;
        }
        return false;
    }

    /**
     * Hủy đơn hàng bởi LvsAdmin
     * 
     * @param lvsOrderId ID đơn hàng
     * @param lvsReason  Lý do hủy
     * @return true nếu hủy thành công
     */
    @Override
    public boolean lvsCancelOrderByAdmin(Long lvsOrderId, String lvsReason) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null) {
            lvsOrder.setLvsStatus(LvsOrderStatus.CANCELLED);
            lvsOrder.setLvsNotes("Hủy bởi LvsAdmin: " + lvsReason);
            lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
            lvsOrderRepository.save(lvsOrder);

            // Hoàn tiền nếu đã thanh toán
            if (Boolean.TRUE.equals(lvsOrder.getLvsIsPaid())) {
                lvsRefundOrder(lvsOrderId, lvsOrder.getLvsFinalAmount(), "Hủy bởi LvsAdmin", null);
            }

            return true;
        }
        return false;
    }

    /**
     * Hoàn thành đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     * @return true nếu hoàn thành thành công
     */
    @Override
    public boolean lvsCompleteOrder(Long lvsOrderId) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null && lvsOrder.getLvsStatus() == LvsOrderStatus.PROCESSING) {
            lvsOrder.setLvsStatus(LvsOrderStatus.COMPLETED);
            lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
            lvsOrderRepository.save(lvsOrder);

            // Tăng số lượt mua cho các dự án
            for (LvsOrderItem lvsItem : lvsOrder.getLvsOrderItems()) {
                LvsProject lvsProject = lvsItem.getLvsProject();
                lvsProject.setLvsPurchaseCount(lvsProject.getLvsPurchaseCount() + lvsItem.getLvsQuantity());
                lvsProjectRepository.save(lvsProject);
            }

            return true;
        }
        return false;
    }

    /**
     * Hoàn tiền đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     * @param lvsAmount  Số tiền hoàn
     * @param lvsReason  Lý do hoàn tiền
     * @param lvsAdminId ID LvsAdmin thực hiện
     * @return true nếu hoàn tiền thành công
     */
    @Override
    public boolean lvsRefundOrder(Long lvsOrderId, Double lvsAmount, String lvsReason, Long lvsAdminId) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null && Boolean.TRUE.equals(lvsOrder.getLvsIsPaid())) {
            // Hoàn coin cho người mua
            LvsUser lvsBuyer = lvsOrder.getLvsBuyer();
            lvsBuyer.setLvsCoin(lvsBuyer.getLvsCoin() + lvsAmount);
            lvsBuyer.setLvsUpdatedAt(LocalDateTime.now());
            lvsUserRepository.save(lvsBuyer);

            // Trừ doanh thu của người bán
            for (LvsOrderItem lvsItem : lvsOrder.getLvsOrderItems()) {
                LvsUser lvsSeller = lvsItem.getLvsSeller();
                lvsSeller.setLvsBalance(lvsSeller.getLvsBalance() - lvsItem.getLvsItemTotal());
                lvsSeller.setLvsUpdatedAt(LocalDateTime.now());
                lvsUserRepository.save(lvsSeller);
            }

            // Cập nhật trạng thái đơn hàng
            lvsOrder.setLvsStatus(LvsOrderStatus.REFUNDED);
            lvsOrder.setLvsNotes("Hoàn tiền: " + lvsReason);
            lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
            lvsOrderRepository.save(lvsOrder);

            // Tạo giao dịch hoàn tiền
            LvsTransaction lvsTransaction = new LvsTransaction();
            lvsTransaction.setLvsUser(lvsBuyer);
            lvsTransaction.setLvsType(LvsTransaction.LvsTransactionType.REFUND);
            lvsTransaction.setLvsAmount(lvsAmount);
            lvsTransaction.setLvsStatus(LvsTransaction.LvsTransactionStatus.SUCCESS);
            lvsTransaction.setLvsDescription("Hoàn tiền đơn hàng " + lvsOrder.getLvsOrderCode() + ": " + lvsReason);
            lvsTransaction.setLvsOrder(lvsOrder);
            if (lvsAdminId != null) {
                LvsUser lvsAdmin = lvsUserRepository.findById(lvsAdminId).orElse(null);
                lvsTransaction.setLvsAdminApprover(lvsAdmin);
            }
            lvsTransaction.setLvsCreatedAt(LocalDateTime.now());
            lvsTransactionRepository.save(lvsTransaction);

            return true;
        }
        return false;
    }

    /**
     * Yêu cầu hoàn tiền
     * 
     * @param lvsOrderId ID đơn hàng
     * @param lvsReason  Lý do yêu cầu
     * @return true nếu gửi yêu cầu thành công
     */
    @Override
    public boolean lvsRequestRefund(Long lvsOrderId, String lvsReason) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null && Boolean.TRUE.equals(lvsOrder.getLvsIsPaid())) {
            // TODO: Gửi thông báo cho LvsAdmin về yêu cầu hoàn tiền
            lvsOrder.setLvsNotes("Yêu cầu hoàn tiền: " + lvsReason);
            lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
            lvsOrderRepository.save(lvsOrder);
            return true;
        }
        return false;
    }

    /**
     * Cập nhật trạng thái đơn hàng
     * 
     * @param lvsOrderId ID đơn hàng
     * @param lvsStatus  Trạng thái mới
     * @param lvsNotes   Ghi chú
     * @return Đơn hàng đã cập nhật
     */
    @Override
    public LvsOrder lvsUpdateOrderStatus(Long lvsOrderId, LvsOrderStatus lvsStatus, String lvsNotes) {
        LvsOrder lvsOrder = lvsGetOrderById(lvsOrderId);
        if (lvsOrder != null) {
            lvsOrder.setLvsStatus(lvsStatus);
            lvsOrder.setLvsNotes(lvsNotes);
            lvsOrder.setLvsUpdatedAt(LocalDateTime.now());
            return lvsOrderRepository.save(lvsOrder);
        }
        return null;
    }

    /**
     * Tính tổng tiền đơn hàng
     * 
     * @param lvsUserId    ID người dùng
     * @param lvsProjectId ID dự án
     * @param lvsQuantity  Số lượng
     * @return Tổng tiền
     */
    @Override
    public Double lvsCalculateOrderTotal(Long lvsUserId, Long lvsProjectId, Integer lvsQuantity) {
        LvsProject lvsProject = lvsProjectRepository.findById(lvsProjectId).orElse(null);
        if (lvsProject != null) {
            return lvsProject.getLvsPrice() * lvsQuantity;
        }
        return 0.0;
    }

    /**
     * Tạo mã đơn hàng duy nhất
     * 
     * @return Mã đơn hàng
     */
    @Override
    public String lvsGenerateOrderCode() {
        return "ORD-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }

    /**
     * Đếm tổng số đơn hàng
     * 
     * @return Tổng số đơn hàng
     */
    @Override
    public Long lvsCountTotalOrders() {
        return lvsOrderRepository.count();
    }

    /**
     * Đếm số đơn hàng theo trạng thái
     * 
     * @param lvsStatus Trạng thái cần đếm
     * @return Số đơn hàng
     */
    @Override
    public Long lvsCountOrdersByStatus(LvsOrderStatus lvsStatus) {
        return lvsOrderRepository.countByLvsStatus(lvsStatus);
    }

    /**
     * Đếm số đơn hàng mới trong khoảng thời gian
     * 
     * @param lvsStartDate Ngày bắt đầu
     * @param lvsEndDate   Ngày kết thúc
     * @return Số đơn hàng mới
     */
    @Override
    public Long lvsCountNewOrders(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        return lvsOrderRepository.countByLvsCreatedAtBetween(
                lvsStartDate.atStartOfDay(), lvsEndDate.atTime(23, 59, 59));
    }

    /**
     * Lấy tổng doanh thu
     * 
     * @return Tổng doanh thu
     */
    @Override
    public Double lvsGetTotalRevenue() {
        // TODO: Add calculateTotalRevenue() method to LvsOrderRepository
        // return lvsOrderRepository.calculateTotalRevenue();
        return 0.0;
    }

    /**
     * Lấy doanh thu theo khoảng thời gian
     * 
     * @param lvsStartDate Ngày bắt đầu
     * @param lvsEndDate   Ngày kết thúc
     * @return Tổng doanh thu
     */
    @Override
    public Double lvsGetTotalRevenue(LocalDate lvsStartDate, LocalDate lvsEndDate) {
        // TODO: Add calculateTotalRevenueBetween() method to LvsOrderRepository
        // return lvsOrderRepository.calculateTotalRevenueBetween(
        // lvsStartDate.atStartOfDay(), lvsEndDate.atTime(23, 59, 59));
        return 0.0;
    }

    /**
     * Lấy đơn hàng chờ thanh toán
     * 
     * @param lvsUserId ID người dùng
     * @return Danh sách đơn hàng chờ thanh toán
     */
    @Override
    public List<LvsOrder> lvsGetPendingOrders(Long lvsUserId) {
        return lvsOrderRepository.findByLvsBuyer_LvsUserIdAndLvsStatus(lvsUserId, LvsOrderStatus.PENDING);
    }

    /**
     * Lấy thống kê đơn hàng
     * 
     * @return Map thống kê đơn hàng
     */
    @Override
    public Map<String, Long> lvsGetOrderStats() {
        Map<String, Long> lvsStats = new HashMap<>();

        for (LvsOrderStatus lvsStatus : LvsOrderStatus.values()) {
            Long lvsCount = lvsOrderRepository.countByLvsStatus(lvsStatus);
            lvsStats.put(lvsStatus.name(), lvsCount);
        }

        return lvsStats;
    }

    /**
     * Xuất hóa đơn PDF
     * 
     * @param lvsOrderId ID đơn hàng
     * @return Byte array của file PDF
     */
    @Override
    public byte[] lvsGenerateInvoicePdf(Long lvsOrderId) {
        // TODO: Triển khai logic xuất PDF
        // Sử dụng thư viện như iText hoặc Apache PDFBox
        return new byte[0];
    }
}