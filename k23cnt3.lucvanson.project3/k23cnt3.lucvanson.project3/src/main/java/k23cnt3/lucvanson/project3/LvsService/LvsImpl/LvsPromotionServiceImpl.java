package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPromotion;
import k23cnt3.lucvanson.project3.LvsRepository.LvsPromotionRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsOrderRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Service implementation cho quản lý khuyến mãi
 * Xử lý CRUD mã khuyến mãi, kiểm tra hiệu lực, áp dụng
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsPromotionServiceImpl implements LvsPromotionService {

    private final LvsPromotionRepository lvsPromotionRepository;
    private final LvsOrderRepository lvsOrderRepository;
    private final Random lvsRandom = new Random();

    /**
     * Lấy khuyến mãi theo ID
     * 
     * @param lvsPromotionId ID khuyến mãi
     * @return Khuyến mãi tìm thấy
     */
    @Override
    public LvsPromotion lvsGetPromotionById(Integer lvsPromotionId) {
        return lvsPromotionRepository.findById(lvsPromotionId).orElse(null);
    }

    /**
     * Lấy khuyến mãi theo mã
     * 
     * @param lvsPromotionCode Mã khuyến mãi
     * @return Khuyến mãi tìm thấy
     */
    @Override
    public LvsPromotion lvsGetPromotionByCode(String lvsPromotionCode) {
        return lvsPromotionRepository.findByLvsCode(lvsPromotionCode).orElse(null);
    }

    /**
     * Lấy tất cả khuyến mãi với phân trang
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang khuyến mãi
     */
    @Override
    public Page<LvsPromotion> lvsGetAllPromotions(Pageable lvsPageable) {
        return lvsPromotionRepository.findAll(lvsPageable);
    }

    /**
     * Lấy khuyến mãi đang hoạt động
     * 
     * @param lvsPageable Thông tin phân trang
     * @return Trang khuyến mãi đang hoạt động
     */
    @Override
    public Page<LvsPromotion> lvsGetActivePromotions(Pageable lvsPageable) {
        return lvsPromotionRepository.findByLvsIsActiveTrue(lvsPageable);
    }

    /**
     * Lấy khuyến mãi theo active
     * 
     * @param lvsIsActive Trạng thái active
     * @param lvsPageable Thông tin phân trang
     * @return Trang khuyến mãi
     */
    @Override
    public Page<LvsPromotion> lvsGetPromotionsByActive(Boolean lvsIsActive, Pageable lvsPageable) {
        return lvsPromotionRepository.findByLvsIsActive(lvsIsActive, lvsPageable);
    }

    /**
     * Lưu khuyến mãi
     * 
     * @param lvsPromotion Thông tin khuyến mãi
     * @return Khuyến mãi đã lưu
     */
    @Override
    public LvsPromotion lvsSavePromotion(LvsPromotion lvsPromotion) {
        lvsPromotion.setLvsCreatedAt(LocalDateTime.now());
        return lvsPromotionRepository.save(lvsPromotion);
    }

    /**
     * Cập nhật khuyến mãi
     * 
     * @param lvsPromotion Thông tin khuyến mãi cập nhật
     * @return Khuyến mãi đã cập nhật
     */
    @Override
    public LvsPromotion lvsUpdatePromotion(LvsPromotion lvsPromotion) {
        LvsPromotion lvsExistingPromotion = lvsGetPromotionById(lvsPromotion.getLvsPromotionId());
        if (lvsExistingPromotion != null) {
            lvsExistingPromotion.setLvsCode(lvsPromotion.getLvsCode());
            lvsExistingPromotion.setLvsName(lvsPromotion.getLvsName());
            lvsExistingPromotion.setLvsDiscountType(lvsPromotion.getLvsDiscountType());
            lvsExistingPromotion.setLvsDiscountValue(lvsPromotion.getLvsDiscountValue());
            lvsExistingPromotion.setLvsMinOrderValue(lvsPromotion.getLvsMinOrderValue());
            lvsExistingPromotion.setLvsUsageLimit(lvsPromotion.getLvsUsageLimit());
            lvsExistingPromotion.setLvsStartDate(lvsPromotion.getLvsStartDate());
            lvsExistingPromotion.setLvsEndDate(lvsPromotion.getLvsEndDate());
            lvsExistingPromotion.setLvsIsActive(lvsPromotion.getLvsIsActive());
            lvsExistingPromotion.setLvsDescription(lvsPromotion.getLvsDescription());
            return lvsPromotionRepository.save(lvsExistingPromotion);
        }
        return null;
    }

    /**
     * Xóa khuyến mãi
     * 
     * @param lvsPromotionId ID khuyến mãi
     */
    @Override
    public void lvsDeletePromotion(Integer lvsPromotionId) {
        lvsPromotionRepository.deleteById(lvsPromotionId);
    }

    /**
     * Kích hoạt/vô hiệu hóa khuyến mãi
     * 
     * @param lvsPromotionId ID khuyến mãi
     * @return Khuyến mãi đã cập nhật
     */
    @Override
    public LvsPromotion lvsTogglePromotionActive(Integer lvsPromotionId) {
        LvsPromotion lvsPromotion = lvsGetPromotionById(lvsPromotionId);
        if (lvsPromotion != null) {
            lvsPromotion.setLvsIsActive(!lvsPromotion.getLvsIsActive());
            return lvsPromotionRepository.save(lvsPromotion);
        }
        return null;
    }

    /**
     * Kiểm tra mã khuyến mãi có tồn tại không
     * 
     * @param lvsPromotionCode Mã khuyến mãi
     * @return true nếu tồn tại
     */
    @Override
    public boolean lvsCheckPromotionCodeExists(String lvsPromotionCode) {
        return lvsPromotionRepository.existsByLvsCode(lvsPromotionCode);
    }

    /**
     * Kiểm tra mã khuyến mãi có hiệu lực không
     * 
     * @param lvsPromotionCode Mã khuyến mãi
     * @param lvsOrderValue    Giá trị đơn hàng
     * @return true nếu hiệu lực
     */
    @Override
    public boolean lvsIsPromotionValid(String lvsPromotionCode, Double lvsOrderValue) {
        LvsPromotion lvsPromotion = lvsGetPromotionByCode(lvsPromotionCode);
        if (lvsPromotion == null)
            return false;

        // Kiểm tra active
        if (!Boolean.TRUE.equals(lvsPromotion.getLvsIsActive()))
            return false;

        // Kiểm tra thời gian hiệu lực
        LocalDate lvsToday = LocalDate.now();
        if (lvsToday.isBefore(lvsPromotion.getLvsStartDate()) ||
                lvsToday.isAfter(lvsPromotion.getLvsEndDate())) {
            return false;
        }

        // Kiểm tra giới hạn sử dụng
        if (lvsPromotion.getLvsUsageLimit() != null &&
                lvsPromotion.getLvsUsedCount() >= lvsPromotion.getLvsUsageLimit()) {
            return false;
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (lvsPromotion.getLvsMinOrderValue() != null &&
                lvsOrderValue < lvsPromotion.getLvsMinOrderValue()) {
            return false;
        }

        return true;
    }

    /**
     * Áp dụng mã khuyến mãi
     * 
     * @param lvsPromotionCode Mã khuyến mãi
     * @param lvsOrderValue    Giá trị đơn hàng
     * @return Giá trị sau khi áp dụng
     */

    /**
     * Kiểm tra mã khuyến mãi có hiệu lực cho user cụ thể không
     * 
     * @param lvsPromotionCode Mã khuyến mãi
     * @param lvsOrderValue    Giá trị đơn hàng
     * @param lvsUserId        ID của user
     * @return true nếu hiệu lực
     */
    @Override
    public boolean lvsIsPromotionValidForUser(String lvsPromotionCode, Double lvsOrderValue, Long lvsUserId) {
        LvsPromotion lvsPromotion = lvsGetPromotionByCode(lvsPromotionCode);
        if (lvsPromotion == null)
            return false;

        // Kiểm tra active
        if (!Boolean.TRUE.equals(lvsPromotion.getLvsIsActive()))
            return false;

        // Kiểm tra thời gian hiệu lực
        LocalDate lvsToday = LocalDate.now();
        if (lvsToday.isBefore(lvsPromotion.getLvsStartDate()) ||
                lvsToday.isAfter(lvsPromotion.getLvsEndDate())) {
            return false;
        }

        // IMPORTANT: Kiểm tra user đã sử dụng promotion này chưa
        if (lvsUserId != null) {
            boolean hasUsed = lvsOrderRepository.existsByLvsBuyer_LvsUserIdAndLvsPromotion_LvsPromotionId(
                    lvsUserId, lvsPromotion.getLvsPromotionId());
            if (hasUsed) {
                System.out.println("[PROMOTION] User " + lvsUserId + " has already used promotion: " + lvsPromotionCode);
                return false;
            }
        }

        // IMPORTANT: Kiểm tra giới hạn sử dụng tổng (đếm từ orders thực tế)
        if (lvsPromotion.getLvsUsageLimit() != null) {
            long actualUsageCount = lvsOrderRepository.countByLvsPromotion_LvsPromotionId(
                    lvsPromotion.getLvsPromotionId());
            if (actualUsageCount >= lvsPromotion.getLvsUsageLimit()) {
                System.out.println("[PROMOTION] Promotion " + lvsPromotionCode + " has reached usage limit: " + 
                        actualUsageCount + "/" + lvsPromotion.getLvsUsageLimit());
                return false;
            }
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (lvsPromotion.getLvsMinOrderValue() != null &&
                lvsOrderValue < lvsPromotion.getLvsMinOrderValue()) {
            return false;
        }

        return true;
    }
    @Override
    public Double lvsApplyPromotion(String lvsPromotionCode, Double lvsOrderValue) {
        if (!lvsIsPromotionValid(lvsPromotionCode, lvsOrderValue)) {
            return lvsOrderValue;
        }

        LvsPromotion lvsPromotion = lvsGetPromotionByCode(lvsPromotionCode);
        if (lvsPromotion == null)
            return lvsOrderValue;

        Double lvsDiscount = 0.0;

        if (lvsPromotion.getLvsDiscountType() == LvsPromotion.LvsDiscountType.PERCENT) {
            // Giảm theo phần trăm
            lvsDiscount = lvsOrderValue * (lvsPromotion.getLvsDiscountValue() / 100);
        } else {
            // Giảm cố định
            lvsDiscount = lvsPromotion.getLvsDiscountValue();
        }

        // Đảm bảo không giảm quá giá trị đơn hàng
        if (lvsDiscount > lvsOrderValue) {
            lvsDiscount = lvsOrderValue;
        }

        // Tăng số lần sử dụng
        lvsIncrementUsageCount(lvsPromotion.getLvsPromotionId());

        return lvsOrderValue - lvsDiscount;
    }

    /**
     * Tăng số lần sử dụng
     * 
     * @param lvsPromotionId ID khuyến mãi
     */
    @Override
    public void lvsIncrementUsageCount(Integer lvsPromotionId) {
        LvsPromotion lvsPromotion = lvsGetPromotionById(lvsPromotionId);
        if (lvsPromotion != null) {
            lvsPromotion.setLvsUsedCount(lvsPromotion.getLvsUsedCount() + 1);
            lvsPromotionRepository.save(lvsPromotion);
        }
    }

    /**
     * Kiểm tra đã sử dụng hết số lần chưa
     * 
     * @param lvsPromotionId ID khuyến mãi
     * @return true nếu đã hết
     */
    @Override
    public boolean lvsIsPromotionUsageLimitReached(Integer lvsPromotionId) {
        LvsPromotion lvsPromotion = lvsGetPromotionById(lvsPromotionId);
        if (lvsPromotion != null && lvsPromotion.getLvsUsageLimit() != null) {
            return lvsPromotion.getLvsUsedCount() >= lvsPromotion.getLvsUsageLimit();
        }
        return false;
    }

    /**
     * Kiểm tra còn trong thời gian hiệu lực không
     * 
     * @param lvsPromotionId ID khuyến mãi
     * @return true nếu còn hiệu lực
     */
    @Override
    public boolean lvsIsPromotionInDateRange(Integer lvsPromotionId) {
        LvsPromotion lvsPromotion = lvsGetPromotionById(lvsPromotionId);
        if (lvsPromotion != null) {
            LocalDate lvsToday = LocalDate.now();
            return !lvsToday.isBefore(lvsPromotion.getLvsStartDate()) &&
                    !lvsToday.isAfter(lvsPromotion.getLvsEndDate());
        }
        return false;
    }

    /**
     * Lấy danh sách mã khuyến mãi có hiệu lực
     * 
     * @param lvsOrderValue Giá trị đơn hàng
     * @return Danh sách khuyến mãi
     */
    @Override
    public List<LvsPromotion> lvsGetValidPromotions(Double lvsOrderValue) {
        List<LvsPromotion> lvsAllPromotions = lvsPromotionRepository.findByLvsIsActiveTrue();
        return lvsAllPromotions.stream()
                .filter(p -> lvsIsPromotionValid(p.getLvsCode(), lvsOrderValue))
                .toList();
    }

    /**
     * Tạo mã khuyến mãi tự động
     * 
     * @return Mã khuyến mãi
     */
    @Override
    public String lvsGeneratePromotionCode() {
        String lvsChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder lvsCode = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            lvsCode.append(lvsChars.charAt(lvsRandom.nextInt(lvsChars.length())));
        }

        // Kiểm tra xem mã đã tồn tại chưa
        if (lvsCheckPromotionCodeExists(lvsCode.toString())) {
            return lvsGeneratePromotionCode(); // Đệ quy nếu trùng
        }

        return lvsCode.toString();
    }

    /**
     * Lấy đơn hàng sử dụng khuyến mãi
     * 
     * @param lvsPromotionId ID khuyến mãi
     * @return Danh sách đơn hàng
     */
    @Override
    public List<Object[]> lvsGetOrdersByPromotion(Integer lvsPromotionId) {
        // TODO: Truy vấn đơn hàng sử dụng khuyến mãi
        return List.of();
    }
}