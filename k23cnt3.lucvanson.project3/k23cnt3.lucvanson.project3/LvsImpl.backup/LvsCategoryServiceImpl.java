package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsCategory;
import k23cnt3.lucvanson.project3.LvsRepository.LvsCategoryRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation cho quản lý danh mục
 * Xử lý CRUD danh mục, sắp xếp, kích hoạt/vô hiệu hóa
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsCategoryServiceImpl implements LvsCategoryService {

    private final LvsCategoryRepository lvsCategoryRepository;

    /**
     * Lấy danh mục theo ID
     * @param lvsCategoryId ID danh mục
     * @return Danh mục tìm thấy
     */
    @Override
    public LvsCategory lvsGetCategoryById(Integer lvsCategoryId) {
        return lvsCategoryRepository.findById(lvsCategoryId).orElse(null);
    }

    /**
     * Lấy tất cả danh mục
     * @return Danh sách danh mục
     */
    @Override
    public List<LvsCategory> lvsGetAllCategories() {
        return lvsCategoryRepository.findAll();
    }

    /**
     * Lấy tất cả danh mục với phân trang
     * @param lvsPageable Thông tin phân trang
     * @return Trang danh mục
     */
    @Override
    public Page<LvsCategory> lvsGetAllCategories(Pageable lvsPageable) {
        return lvsCategoryRepository.findAll(lvsPageable);
    }

    /**
     * Lấy danh mục đang hoạt động
     * @return Danh sách danh mục đang hoạt động
     */
    @Override
    public List<LvsCategory> lvsGetActiveCategories() {
        return lvsCategoryRepository.findByLvsIsActiveTrue();
    }

    /**
     * Lưu danh mục
     * @param lvsCategory Thông tin danh mục
     * @return Danh mục đã lưu
     */
    @Override
    public LvsCategory lvsSaveCategory(LvsCategory lvsCategory) {
        lvsCategory.setLvsCreatedAt(LocalDateTime.now());
        return lvsCategoryRepository.save(lvsCategory);
    }

    /**
     * Cập nhật danh mục
     * @param lvsCategory Thông tin danh mục cập nhật
     * @return Danh mục đã cập nhật
     */
    @Override
    public LvsCategory lvsUpdateCategory(LvsCategory lvsCategory) {
        LvsCategory lvsExistingCategory = lvsGetCategoryById(lvsCategory.getLvsCategoryId());
        if (lvsExistingCategory != null) {
            lvsExistingCategory.setLvsCategoryName(lvsCategory.getLvsCategoryName());
            lvsExistingCategory.setLvsDescription(lvsCategory.getLvsDescription());
            lvsExistingCategory.setLvsIcon(lvsCategory.getLvsIcon());
            lvsExistingCategory.setLvsColor(lvsCategory.getLvsColor());
            lvsExistingCategory.setLvsSortOrder(lvsCategory.getLvsSortOrder());
            lvsExistingCategory.setLvsIsActive(lvsCategory.getLvsIsActive());
            return lvsCategoryRepository.save(lvsExistingCategory);
        }
        return null;
    }

    /**
     * Xóa danh mục
     * @param lvsCategoryId ID danh mục
     */
    @Override
    public void lvsDeleteCategory(Integer lvsCategoryId) {
        lvsCategoryRepository.deleteById(lvsCategoryId);
    }

    /**
     * Kích hoạt/vô hiệu hóa danh mục
     * @param lvsCategoryId ID danh mục
     * @return Danh mục đã cập nhật
     */
    @Override
    public LvsCategory lvsToggleCategoryActive(Integer lvsCategoryId) {
        LvsCategory lvsCategory = lvsGetCategoryById(lvsCategoryId);
        if (lvsCategory != null) {
            lvsCategory.setLvsIsActive(!lvsCategory.getLvsIsActive());
            return lvsCategoryRepository.save(lvsCategory);
        }
        return null;
    }

    /**
     * Sắp xếp lại thứ tự danh mục
     * @param lvsCategoryIds Danh sách ID danh mục theo thứ tự mới
     */
    @Override
    public void lvsReorderCategories(List<Integer> lvsCategoryIds) {
        for (int i = 0; i < lvsCategoryIds.size(); i++) {
            Integer lvsCategoryId = lvsCategoryIds.get(i);
            LvsCategory lvsCategory = lvsGetCategoryById(lvsCategoryId);
            if (lvsCategory != null) {
                lvsCategory.setLvsSortOrder(i);
                lvsCategoryRepository.save(lvsCategory);
            }
        }
    }

    /**
     * Tăng số lượng dự án trong danh mục
     * @param lvsCategoryId ID danh mục
     */
    @Override
    public void lvsIncrementProjectCount(Integer lvsCategoryId) {
        LvsCategory lvsCategory = lvsGetCategoryById(lvsCategoryId);
        if (lvsCategory != null) {
            lvsCategory.setLvsProjectCount(lvsCategory.getLvsProjectCount() + 1);
            lvsCategoryRepository.save(lvsCategory);
        }
    }

    /**
     * Giảm số lượng dự án trong danh mục
     * @param lvsCategoryId ID danh mục
     */
    @Override
    public void lvsDecrementProjectCount(Integer lvsCategoryId) {
        LvsCategory lvsCategory = lvsGetCategoryById(lvsCategoryId);
        if (lvsCategory != null && lvsCategory.getLvsProjectCount() > 0) {
            lvsCategory.setLvsProjectCount(lvsCategory.getLvsProjectCount() - 1);
            lvsCategoryRepository.save(lvsCategory);
        }
    }

    /**
     * Lấy số lượng dự án trong danh mục
     * @param lvsCategoryId ID danh mục
     * @return Số lượng dự án
     */
    @Override
    public Integer lvsGetProjectCount(Integer lvsCategoryId) {
        LvsCategory lvsCategory = lvsGetCategoryById(lvsCategoryId);
        return lvsCategory != null ? lvsCategory.getLvsProjectCount() : 0;
    }

    /**
     * Kiểm tra tên danh mục đã tồn tại chưa
     * @param lvsCategoryName Tên danh mục cần kiểm tra
     * @return true nếu đã tồn tại
     */
    @Override
    public boolean lvsCheckCategoryNameExists(String lvsCategoryName) {
        return lvsCategoryRepository.existsByLvsCategoryName(lvsCategoryName);
    }
}