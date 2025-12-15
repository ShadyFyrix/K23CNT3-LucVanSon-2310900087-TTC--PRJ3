package k23cnt3.lucvanson.project3.LvsService;

import java.util.Map;

/**
 * Service interface cho xử lý thanh toán
 * Xử lý thanh toán qua nhiều cổng thanh toán
 */
public interface LvsPaymentService {

    // Tạo thanh toán
    Map<String, Object> lvsCreatePayment(Long lvsOrderId, String lvsPaymentMethod);

    // Xử lý thanh toán
    boolean lvsProcessPayment(String lvsPaymentId, String lvsPaymentMethod);

    // Hoàn tiền
    boolean lvsRefundPayment(String lvsPaymentId, Double lvsAmount, String lvsReason);

    // Kiểm tra trạng thái thanh toán
    Map<String, Object> lvsCheckPaymentStatus(String lvsPaymentId);

    // Tạo thanh toán VNPay
    Map<String, Object> lvsCreateVNPayPayment(Long lvsOrderId, Double lvsAmount, String lvsBankCode);

    // Tạo thanh toán Momo
    Map<String, Object> lvsCreateMomoPayment(Long lvsOrderId, Double lvsAmount);

    // Tạo thanh toán Zalopay
    Map<String, Object> lvsCreateZaloPayPayment(Long lvsOrderId, Double lvsAmount);

    // Tạo thanh toán bằng coin
    boolean lvsCreateCoinPayment(Long lvsUserId, Long lvsOrderId);

    // Xác minh chữ ký VNPay
    boolean lvsVerifyVNPaySignature(Map<String, String> lvsParams);

    // Xác minh chữ ký Momo
    boolean lvsVerifyMomoSignature(Map<String, String> lvsParams);

    // Lấy lịch sử thanh toán
    Map<String, Object> lvsGetPaymentHistory(Long lvsUserId, int lvsPage, int lvsSize);

    // Tính phí thanh toán
    Double lvsCalculatePaymentFee(Double lvsAmount, String lvsPaymentMethod);

    // Kiểm tra cổng thanh toán có sẵn
    boolean lvsIsPaymentMethodAvailable(String lvsPaymentMethod);

    // Lấy danh sách phương thức thanh toán
    Map<String, String> lvsGetAvailablePaymentMethods();
}