package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPromotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface cho quản lý khuyến mãi
 * Xử lý CRUD mã khuyến mãi, kiểm tra hiệu lực, áp dụng
 */
public interface LvsPromotionService {

    // Lấy khuyến mãi theo ID
    LvsPromotion lvsGetPromotionById(Integer lvsPromotionId);

    // Lấy khuyến mãi theo mã
    LvsPromotion lvsGetPromotionByCode(String lvsPromotionCode);

    // Lấy tất cả khuyến mãi
    Page<LvsPromotion> lvsGetAllPromotions(Pageable lvsPageable);

    // Lấy khuyến mãi đang hoạt động
    Page<LvsPromotion> lvsGetActivePromotions(Pageable lvsPageable);

    // Lấy khuyến mãi theo active
    Page<LvsPromotion> lvsGetPromotionsByActive(Boolean lvsIsActive, Pageable lvsPageable);

    // Lưu khuyến mãi
    LvsPromotion lvsSavePromotion(LvsPromotion lvsPromotion);

    // Cập nhật khuyến mãi
    LvsPromotion lvsUpdatePromotion(LvsPromotion lvsPromotion);

    // Xóa khuyến mãi
    void lvsDeletePromotion(Integer lvsPromotionId);

    // Kích hoạt/vô hiệu hóa khuyến mãi
    LvsPromotion lvsTogglePromotionActive(Integer lvsPromotionId);

    // Kiểm tra mã khuyến mãi có tồn tại không
    boolean lvsCheckPromotionCodeExists(String lvsPromotionCode);

    // Kiểm tra mã khuyến mãi có hiệu lực không
    boolean lvsIsPromotionValid(String lvsPromotionCode, Double lvsOrderValue);

    // Kiểm tra mã khuyến mãi có hiệu lực cho user cụ thể không
    boolean lvsIsPromotionValidForUser(String lvsPromotionCode, Double lvsOrderValue, Long lvsUserId);

    // Áp dụng mã khuyến mãi
    Double lvsApplyPromotion(String lvsPromotionCode, Double lvsOrderValue);

    // Tăng số lần sử dụng
    void lvsIncrementUsageCount(Integer lvsPromotionId);

    // Kiểm tra đã sử dụng hết số lần chưa
    boolean lvsIsPromotionUsageLimitReached(Integer lvsPromotionId);

    // Kiểm tra còn trong thời gian hiệu lực không
    boolean lvsIsPromotionInDateRange(Integer lvsPromotionId);

    // Lấy danh sách mã khuyến mãi có hiệu lực
    List<LvsPromotion> lvsGetValidPromotions(Double lvsOrderValue);

    // Tạo mã khuyến mãi tự động
    String lvsGeneratePromotionCode();

    // Lấy đơn hàng sử dụng khuyến mãi
    List<Object[]> lvsGetOrdersByPromotion(Integer lvsPromotionId);
}