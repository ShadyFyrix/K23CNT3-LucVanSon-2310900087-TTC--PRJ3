package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Controller quản lý Danh mục (Category) trong Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị danh sách danh mục với phân trang và thống kê</li>
 * <li>Xem chi tiết thông tin danh mục</li>
 * <li>Tạo mới danh mục</li>
 * <li>Chỉnh sửa thông tin danh mục</li>
 * <li>Xóa danh mục</li>
 * <li>Kích hoạt/vô hiệu hóa danh mục</li>
 * <li>Sắp xếp lại thứ tự hiển thị danh mục</li>
 * </ul>
 * 
 * <p>
 * Bảo mật:
 * </p>
 * <ul>
 * <li>Yêu cầu quyền ADMIN để truy cập tất cả các chức năng</li>
 * <li>Sử dụng @PreAuthorize để bảo vệ toàn bộ controller</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryDetail.html</li>
 * <li>Add: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd.html</li>
 * <li>Edit: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsCategory")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // Bảo vệ toàn bộ controller - chỉ ADMIN mới truy cập được
public class LvsAdminCategoryController {

    /**
     * Service xử lý logic nghiệp vụ cho Category
     * Inject qua @Autowired để Spring tự động khởi tạo
     */
    @Autowired
    private LvsCategoryService lvsCategoryService;

    /**
     * Service xử lý logic nghiệp vụ cho User
     * Dùng để lấy thông tin user hiện tại khi cần
     */
    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Hiển thị danh sách tất cả danh mục với phân trang và thống kê
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy danh sách danh mục theo trang</li>
     * <li>Tính toán số danh mục đang hoạt động</li>
     * <li>Tính tổng số dự án trong tất cả danh mục</li>
     * <li>Truyền dữ liệu ra view để hiển thị</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsCategory/LvsList
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsCategory/LvsList?page=0&size=20
     * </p>
     * 
     * @param page  Số trang hiện tại (bắt đầu từ 0, mặc định = 0)
     * @param size  Số lượng items mỗi trang (mặc định = 20)
     * @param model Model object để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryList
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>LvsCategories: Page&lt;LvsCategory&gt; - Danh sách danh mục có
     *         phân trang</li>
     *         <li>LvsCurrentPage: int - Trang hiện tại</li>
     *         <li>LvsActiveCount: long - Số danh mục đang hoạt động</li>
     *         <li>LvsTotalProjects: long - Tổng số dự án trong tất cả danh mục</li>
     *         </ul>
     */
    @GetMapping("/LvsList")
    public String lvsListCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        // Tạo Pageable object để phân trang
        Pageable lvsPageable = PageRequest.of(page, size);

        // Lấy danh sách danh mục từ database với phân trang
        Page<LvsCategory> lvsCategories = lvsCategoryService.lvsGetAllCategories(lvsPageable);

        // Tính số danh mục đang hoạt động (lvsIsActive = true)
        long lvsActiveCount = lvsCategories.getContent().stream()
                .filter(LvsCategory::getLvsIsActive)
                .count();

        // Tính tổng số dự án trong tất cả danh mục
        // Dùng stream để tránh NullPointerException nếu lvsProjects = null
        long lvsTotalProjects = lvsCategories.getContent().stream()
                .mapToLong(cat -> cat.getLvsProjects() != null ? cat.getLvsProjects().size() : 0)
                .sum();

        // Truyền dữ liệu ra view
        model.addAttribute("LvsCategories", lvsCategories);
        model.addAttribute("LvsCurrentPage", page);
        model.addAttribute("LvsActiveCount", lvsActiveCount);
        model.addAttribute("LvsTotalProjects", lvsTotalProjects);

        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryList";
    }

    /**
     * Hiển thị form thêm danh mục mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Tạo object LvsCategory rỗng</li>
     * <li>Truyền object ra view để binding với form</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsCategory/LvsAdd
     * </p>
     * 
     * @param model Model object để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>LvsCategory: LvsCategory - Object rỗng để binding với form</li>
     *         </ul>
     */
    @GetMapping("/LvsAdd")
    public String lvsShowAddCategoryForm(Model model) {
        // Tạo object rỗng để binding với form
        model.addAttribute("LvsCategory", new LvsCategory());
        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd";
    }

    /**
     * Xử lý submit form thêm danh mục mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nhận dữ liệu từ form</li>
     * <li>Set trạng thái mặc định là active</li>
     * <li>Lưu vào database</li>
     * <li>Redirect đến trang chi tiết nếu thành công</li>
     * <li>Hiển thị lại form với thông báo lỗi nếu thất bại</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsCategory/LvsAdd
     * </p>
     * 
     * @param lvsCategory Object LvsCategory được binding từ form
     * @param model       Model object để truyền thông báo
     * @return Redirect đến detail page nếu thành công, hoặc trở lại form nếu lỗi
     * 
     *         <p>
     *         Success: redirect:/LvsAdmin/LvsCategory/LvsDetail/{id}
     *         </p>
     *         <p>
     *         Error: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd
     *         </p>
     */
    @PostMapping("/LvsAdd")
    public String lvsAddCategory(@ModelAttribute LvsCategory lvsCategory,
            Model model) {

        try {
            // Set trạng thái mặc định là active khi tạo mới
            lvsCategory.setLvsIsActive(true);

            // Lưu vào database qua service layer
            LvsCategory lvsSavedCategory = lvsCategoryService.lvsSaveCategory(lvsCategory);

            // Thêm thông báo thành công
            model.addAttribute("LvsSuccess", "Thêm danh mục thành công!");

            // Redirect đến trang chi tiết của danh mục vừa tạo
            return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + lvsSavedCategory.getLvsCategoryId();
        } catch (Exception e) {
            // Nếu có lỗi, thêm thông báo lỗi và hiển thị lại form
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryAdd";
        }
    }

    /**
     * Hiển thị chi tiết thông tin danh mục
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin danh mục theo ID</li>
     * <li>Lấy danh sách dự án thuộc danh mục</li>
     * <li>Tính số lượng dự án</li>
     * <li>Hiển thị thông tin chi tiết</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsCategory/LvsDetail/{id}
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsCategory/LvsDetail/1
     * </p>
     * 
     * @param id    ID của danh mục cần xem
     * @param model Model object để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryDetail
     *         hoặc redirect về list nếu không tìm thấy
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>LvsCategory: LvsCategory - Thông tin danh mục</li>
     *         <li>LvsProjects: List&lt;LvsProject&gt; - Danh sách dự án thuộc danh
     *         mục</li>
     *         <li>LvsProjectCount: int - Số lượng dự án</li>
     *         </ul>
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewCategoryDetail(@PathVariable Integer id,
            Model model) {

        // Lấy thông tin danh mục từ database
        LvsCategory lvsCategory = lvsCategoryService.lvsGetCategoryById(id);

        // Nếu không tìm thấy, redirect về trang danh sách
        if (lvsCategory == null) {
            return "redirect:/LvsAdmin/LvsCategory/LvsList";
        }

        // Lấy danh sách dự án thuộc danh mục này
        List<LvsProject> lvsProjects = lvsCategory.getLvsProjects();

        // Tính số lượng dự án (kiểm tra null để tránh NullPointerException)
        int lvsProjectCount = lvsProjects != null ? lvsProjects.size() : 0;

        // Truyền dữ liệu ra view
        model.addAttribute("LvsCategory", lvsCategory);
        model.addAttribute("LvsProjects", lvsProjects);
        model.addAttribute("LvsProjectCount", lvsProjectCount);

        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryDetail";
    }

    /**
     * Hiển thị form chỉnh sửa danh mục
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin danh mục hiện tại theo ID</li>
     * <li>Truyền object ra view để binding với form</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsCategory/LvsEdit/{id}
     * </p>
     * <p>
     * Ví dụ: /LvsAdmin/LvsCategory/LvsEdit/1
     * </p>
     * 
     * @param id    ID của danh mục cần chỉnh sửa
     * @param model Model object để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit
     *         hoặc redirect về list nếu không tìm thấy
     * 
     *         <p>
     *         Model attributes:
     *         </p>
     *         <ul>
     *         <li>LvsCategory: LvsCategory - Thông tin danh mục hiện tại</li>
     *         </ul>
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditCategoryForm(@PathVariable Integer id,
            Model model) {

        // Lấy thông tin danh mục từ database
        LvsCategory lvsCategory = lvsCategoryService.lvsGetCategoryById(id);

        // Nếu không tìm thấy, redirect về trang danh sách
        if (lvsCategory == null) {
            return "redirect:/LvsAdmin/LvsCategory/LvsList";
        }

        // Truyền object ra view để binding với form
        model.addAttribute("LvsCategory", lvsCategory);

        return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit";
    }

    /**
     * Xử lý submit form chỉnh sửa danh mục
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nhận dữ liệu từ form</li>
     * <li>Set ID cho object (đảm bảo update đúng record)</li>
     * <li>Lưu thay đổi vào database</li>
     * <li>Redirect đến trang chi tiết nếu thành công</li>
     * <li>Hiển thị lại form với thông báo lỗi nếu thất bại</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsCategory/LvsEdit/{id}
     * </p>
     * 
     * @param id          ID của danh mục đang chỉnh sửa
     * @param lvsCategory Object LvsCategory được binding từ form
     * @param model       Model object để truyền thông báo
     * @return Redirect đến detail page nếu thành công, hoặc trở lại form nếu lỗi
     * 
     *         <p>
     *         Success: redirect:/LvsAdmin/LvsCategory/LvsDetail/{id}
     *         </p>
     *         <p>
     *         Error: LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit
     *         </p>
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditCategory(@PathVariable Integer id,
            @ModelAttribute LvsCategory lvsCategory,
            Model model) {

        try {
            // Set ID để đảm bảo update đúng record trong database
            lvsCategory.setLvsCategoryId(id);

            // Lưu thay đổi vào database qua service layer
            lvsCategoryService.lvsSaveCategory(lvsCategory);

            // Thêm thông báo thành công
            model.addAttribute("LvsSuccess", "Cập nhật danh mục thành công!");

            // Redirect đến trang chi tiết
            return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + id;
        } catch (Exception e) {
            // Nếu có lỗi, thêm thông báo lỗi và hiển thị lại form
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsCategory/LvsCategoryEdit";
        }
    }

    /**
     * Xóa danh mục
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Xóa danh mục khỏi database</li>
     * <li>Hiển thị thông báo thành công/lỗi</li>
     * <li>Redirect về trang danh sách</li>
     * </ul>
     * 
     * <p>
     * Lưu ý:
     * </p>
     * <ul>
     * <li>Cần kiểm tra ràng buộc với dự án trước khi xóa</li>
     * <li>Có thể cần xóa cascade hoặc set null cho foreign key</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsCategory/LvsDelete/{id}
     * </p>
     * 
     * @param id    ID của danh mục cần xóa
     * @param model Model object để truyền thông báo
     * @return Redirect về trang danh sách
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteCategory(@PathVariable Integer id,
            Model model) {
        try {
            // Xóa danh mục qua service layer
            lvsCategoryService.lvsDeleteCategory(id);

            // Thêm thông báo thành công
            model.addAttribute("LvsSuccess", "Đã xóa danh mục!");
        } catch (Exception e) {
            // Nếu có lỗi (ví dụ: còn dự án thuộc danh mục), hiển thị thông báo lỗi
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        // Redirect về trang danh sách
        return "redirect:/LvsAdmin/LvsCategory/LvsList";
    }

    /**
     * Kích hoạt/vô hiệu hóa danh mục
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Toggle trạng thái lvsIsActive của danh mục</li>
     * <li>Nếu đang active -> set inactive</li>
     * <li>Nếu đang inactive -> set active</li>
     * <li>Redirect về trang chi tiết</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsCategory/LvsToggleActive/{id}
     * </p>
     * 
     * @param id ID của danh mục cần toggle
     * @return Redirect về trang chi tiết của danh mục
     */
    @PostMapping("/LvsToggleActive/{id}")
    public String lvsToggleCategoryActive(@PathVariable Integer id) {
        // Toggle trạng thái active qua service layer
        lvsCategoryService.lvsToggleCategoryActive(id);

        // Redirect về trang chi tiết để xem kết quả
        return "redirect:/LvsAdmin/LvsCategory/LvsDetail/" + id;
    }

    /**
     * Sắp xếp lại thứ tự hiển thị danh mục
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nhận danh sách ID theo thứ tự mới</li>
     * <li>Cập nhật lvsSortOrder cho từng danh mục</li>
     * <li>Redirect về trang danh sách</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsCategory/LvsReorder
     * </p>
     * <p>
     * Request body: lvsCategoryIds=[1,3,2,4]
     * </p>
     * 
     * @param lvsCategoryIds Danh sách ID theo thứ tự mới
     * @return Redirect về trang danh sách
     */
    @PostMapping("/LvsReorder")
    public String lvsReorderCategories(@RequestParam List<Integer> lvsCategoryIds) {
        // Cập nhật thứ tự qua service layer
        lvsCategoryService.lvsReorderCategories(lvsCategoryIds);

        // Redirect về trang danh sách để xem kết quả
        return "redirect:/LvsAdmin/LvsCategory/LvsList";
    }

    /**
     * Helper method để lấy thông tin user hiện tại đang đăng nhập
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy Authentication từ SecurityContext</li>
     * <li>Lấy username từ Authentication</li>
     * <li>Query database để lấy thông tin user đầy đủ</li>
     * </ul>
     * 
     * <p>
     * Sử dụng:
     * </p>
     * <ul>
     * <li>Khi cần ghi log ai đã thực hiện hành động</li>
     * <li>Khi cần kiểm tra quyền hạn cụ thể</li>
     * <li>Khi cần hiển thị thông tin user trong view</li>
     * </ul>
     * 
     * @return LvsUser object nếu user đã đăng nhập, null nếu chưa đăng nhập
     */
    private LvsUser getCurrentUser() {
        // Lấy Authentication object từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem user đã đăng nhập chưa
        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy username
            String username = authentication.getName();

            // Query database để lấy thông tin user đầy đủ
            return lvsUserService.lvsGetUserByUsername(username);
        }

        // Trả về null nếu chưa đăng nhập
        return null;
    }
}