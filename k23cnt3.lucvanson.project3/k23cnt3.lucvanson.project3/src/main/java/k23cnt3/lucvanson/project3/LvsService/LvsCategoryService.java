package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface cho quản lý danh mục
 * Xử lý CRUD danh mục, sắp xếp, kích hoạt/vô hiệu hóa
 */
public interface LvsCategoryService {

    // Lấy danh mục theo ID
    LvsCategory lvsGetCategoryById(Integer lvsCategoryId);

    // Lấy tất cả danh mục
    List<LvsCategory> lvsGetAllCategories();

    // Lấy tất cả danh mục với phân trang
    Page<LvsCategory> lvsGetAllCategories(Pageable lvsPageable);

    // Lấy danh mục đang hoạt động
    List<LvsCategory> lvsGetActiveCategories();

    // Lưu danh mục
    LvsCategory lvsSaveCategory(LvsCategory lvsCategory);

    // Cập nhật danh mục
    LvsCategory lvsUpdateCategory(LvsCategory lvsCategory);

    // Xóa danh mục
    void lvsDeleteCategory(Integer lvsCategoryId);

    // Kích hoạt/vô hiệu hóa danh mục
    LvsCategory lvsToggleCategoryActive(Integer lvsCategoryId);

    // Sắp xếp lại thứ tự danh mục
    void lvsReorderCategories(List<Integer> lvsCategoryIds);

    // Tăng số lượng dự án trong danh mục
    void lvsIncrementProjectCount(Integer lvsCategoryId);

    // Giảm số lượng dự án trong danh mục
    void lvsDecrementProjectCount(Integer lvsCategoryId);

    // Lấy số lượng dự án trong danh mục
    Integer lvsGetProjectCount(Integer lvsCategoryId);

    // Kiểm tra tên danh mục đã tồn tại chưa
    boolean lvsCheckCategoryNameExists(String lvsCategoryName);
}