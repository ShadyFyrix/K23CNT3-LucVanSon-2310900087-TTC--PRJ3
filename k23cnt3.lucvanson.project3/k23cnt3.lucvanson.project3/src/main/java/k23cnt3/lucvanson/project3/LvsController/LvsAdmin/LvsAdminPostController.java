package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller quản lý Bài viết (Post) trong Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị danh sách bài viết với phân trang, tìm kiếm và lọc</li>
 * <li>Xem chi tiết bài viết</li>
 * <li>Chỉnh sửa bài viết</li>
 * <li>Duyệt/Ẩn/hiển thị bài viết</li>
 * <li>Xóa bài viết</li>
 * <li>Ghim/bỏ ghim bài viết</li>
 * </ul>
 * 
 * <p>
 * Tính năng đặc biệt:
 * </p>
 * <ul>
 * <li><strong>Moderation:</strong> Duyệt, ẩn, hiển thị bài viết</li>
 * <li><strong>Pin:</strong> Ghim bài viết quan trọng lên đầu</li>
 * <li><strong>Filter:</strong> Lọc theo status (PENDING, APPROVED, HIDDEN) và
 * type (DISCUSSION, QUESTION, TUTORIAL)</li>
 * <li><strong>Search:</strong> Tìm kiếm theo tiêu đề và nội dung</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsPost/LvsList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsPost/LvsDetail.html</li>
 * <li>Edit: LvsAreas/LvsAdmin/LvsPost/LvsEdit.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsPost")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminPostController {

    /**
     * Service xử lý logic nghiệp vụ cho Post
     */
    @Autowired
    private LvsPostService lvsPostService;

    /**
     * Service xử lý logic nghiệp vụ cho User (để check quyền admin)
     */
    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Service xử lý logic nghiệp vụ cho Comment
     */
    @Autowired
    private LvsCommentService lvsCommentService;

    /**
     * Hiển thị danh sách bài viết với phân trang, tìm kiếm và lọc
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy danh sách bài viết theo trang</li>
     * <li>Tìm kiếm theo keyword (tiêu đề, nội dung)</li>
     * <li>Lọc theo status (PENDING, APPROVED, HIDDEN)</li>
     * <li>Lọc theo type (DISCUSSION, QUESTION, TUTORIAL)</li>
     * <li>Kết hợp nhiều điều kiện lọc</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsList
     * </p>
     * 
     * @param page       Số trang hiện tại (mặc định = 0)
     * @param size       Số items mỗi trang (mặc định = 20)
     * @param lvsStatus  Status để lọc (optional)
     * @param lvsType    Type để lọc (optional)
     * @param lvsKeyword Từ khóa tìm kiếm (optional)
     * @param model      Model để truyền dữ liệu ra view
     * @param session    HttpSession để check quyền admin
     * @return Template path: LvsAreas/LvsAdmin/LvsPost/LvsList
     */
    @GetMapping("/LvsList")
    public String lvsListPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsType,
            @RequestParam(required = false) String lvsKeyword,
            Model model) {

        // Tạo Pageable object
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsPost> lvsPosts;

        // Xử lý theo thứ tự ưu tiên
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            // Tìm kiếm theo keyword
            lvsPosts = lvsPostService.lvsSearchPosts(lvsKeyword, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty() && lvsType != null && !lvsType.isEmpty()) {
            // Lọc theo cả status và type
            lvsPosts = lvsPostService.lvsGetPostsByStatusAndType(lvsStatus, lvsType, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            // Lọc chỉ theo status
            lvsPosts = lvsPostService.lvsGetPostsByStatus(lvsStatus, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty()) {
            // Lọc chỉ theo type
            lvsPosts = lvsPostService.lvsGetPostsByType(lvsType, lvsPageable);
        } else {
            // Lấy tất cả
            lvsPosts = lvsPostService.lvsGetAllPosts(lvsPageable);
        }

        // Truyền dữ liệu ra view
        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsStatuses", LvsPost.LvsPostStatus.values());
        model.addAttribute("LvsTypes", LvsPost.LvsPostType.values());
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsSelectedType", lvsType);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsPost/LvsList";
    }

    /**
     * Hiển thị form tạo bài viết mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Hiển thị form nhập thông tin bài viết</li>
     * <li>Cung cấp các loại bài viết để chọn</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsCreate
     * </p>
     * 
     * @param model Model để truyền dữ liệu ra view
     * @return Template path: LvsAreas/LvsAdmin/LvsPost/LvsCreate
     */
    @GetMapping("/LvsCreate")
    public String lvsShowCreatePostForm(Model model) {
        // Truyền danh sách loại bài viết ra view
        model.addAttribute("LvsPostTypes", LvsPost.LvsPostType.values());

        return "LvsAreas/LvsAdmin/LvsPost/LvsCreate";
    }

    /**
     * Xử lý submit form tạo bài viết mới
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Nhận dữ liệu từ form</li>
     * <li>Tạo bài viết mới trong database</li>
     * <li>Redirect về danh sách hoặc hiển thị lại form nếu lỗi</li>
     * </ul>
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsCreate
     * </p>
     * 
     * @param lvsPost Object LvsPost được binding từ form
     * @param model   Model để truyền thông báo
     * @param session HttpSession để lấy thông tin user đang đăng nhập
     * @return Redirect về list nếu thành công, hoặc create nếu lỗi
     */
    @PostMapping("/LvsCreate")
    public String lvsCreatePost(
            @ModelAttribute LvsPost lvsPost,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Model model,
            HttpSession session) {

        try {
            // Lấy user đang đăng nhập từ session
            LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

            if (lvsCurrentUser == null) {
                model.addAttribute("LvsError", "Vui lòng đăng nhập!");
                return "redirect:/LvsUser/LvsLogin";
            }

            // Set user cho bài viết
            lvsPost.setLvsUser(lvsCurrentUser);

            // Set status mặc định là PUBLISHED (vì admin tạo)
            lvsPost.setLvsStatus(LvsPost.LvsPostStatus.PUBLISHED);

            // Tạo bài viết kèm ảnh
            LvsPost lvsCreatedPost = lvsPostService.lvsSavePostWithImages(lvsPost, images);

            model.addAttribute("LvsSuccess", "Tạo bài viết thành công!");
            return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + lvsCreatedPost.getLvsPostId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            model.addAttribute("LvsPostTypes", LvsPost.LvsPostType.values());
            return "LvsAreas/LvsAdmin/LvsPost/LvsCreate";
        }
    }

    /**
     * Xóa một ảnh của bài viết
     */
    @PostMapping("/LvsDeleteImage/{imageId}")
    public String lvsDeletePostImage(
            @PathVariable Long imageId,
            @RequestParam Long postId,
            @RequestParam(required = false) Boolean returnToEdit) {
        lvsPostService.lvsDeletePostImage(imageId);

        // Redirect về edit nếu xóa từ trang edit
        if (returnToEdit != null && returnToEdit) {
            return "redirect:/LvsAdmin/LvsPost/LvsEdit/" + postId;
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + postId;
    }

    /**
     * Hiển thị chi tiết bài viết
     * 
     * <p>
     * Chức năng:
     * </p>
     * <ul>
     * <li>Lấy thông tin đầy đủ của bài viết</li>
     * <li>Hiển thị tác giả, thời gian đăng</li>
     * <li>Hiển thị số lượt xem, like, comment</li>
     * <li>Hiển thị trạng thái duyệt</li>
     * <li>Hiển thị danh sách bình luận của bài viết</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsDetail/{id}
     * </p>
     * 
     * @param id      ID của bài viết cần xem
     * @param page    Số trang của comments (mặc định = 0)
     * @param size    Số comments mỗi trang (mặc định = 10)
     * @param model   Model để truyền dữ liệu ra view
     * @param session HttpSession để check quyền admin
     * @return Template path: LvsAreas/LvsAdmin/LvsPost/LvsDetail
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewPostDetail(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        // Lấy thông tin bài viết
        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);

        // Nếu không tìm thấy, redirect về list
        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsPost/LvsList";
        }

        // Lấy danh sách bình luận của bài viết với phân trang
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByPost(id, lvsPageable);

        // Truyền dữ liệu ra view
        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsPost/LvsDetail";
    }

    /**
     * Duyệt bài viết (chuyển status sang APPROVED)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsApprove/{id}
     * </p>
     * 
     * @param id      ID của bài viết cần duyệt
     * @param session HttpSession để check quyền admin
     * @param model   Model để truyền thông báo
     * @return Redirect về trang chi tiết
     */
    @PostMapping("/LvsApprove/{id}")
    public String lvsApprovePost(@PathVariable Long id,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsApprovePost(id);
            model.addAttribute("LvsSuccess", "Đã duyệt bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Ẩn bài viết (chuyển status sang HIDDEN)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsHide/{id}
     * </p>
     * 
     * @param id        ID của bài viết cần ẩn
     * @param lvsReason Lý do ẩn (optional)
     * @param session   HttpSession để check quyền admin
     * @param model     Model để truyền thông báo
     * @return Redirect về trang chi tiết
     */
    @PostMapping("/LvsHide/{id}")
    public String lvsHidePost(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsHidePost(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã ẩn bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Hiển thị lại bài viết (chuyển status từ HIDDEN sang APPROVED)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsShow/{id}
     * </p>
     * 
     * @param id      ID của bài viết cần hiển thị
     * @param session HttpSession để check quyền admin
     * @param model   Model để truyền thông báo
     * @return Redirect về trang chi tiết
     */
    @PostMapping("/LvsShow/{id}")
    public String lvsShowPost(@PathVariable Long id,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsShowPost(id);
            model.addAttribute("LvsSuccess", "Đã hiển thị bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Xóa bài viết
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsDelete/{id}
     * </p>
     * 
     * @param id        ID của bài viết cần xóa
     * @param lvsReason Lý do xóa (optional)
     * @param session   HttpSession để check quyền admin
     * @param model     Model để truyền thông báo
     * @return Redirect về trang danh sách
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeletePost(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsDeletePost(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã xóa bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsList";
    }

    /**
     * Ghim bài viết (đánh dấu quan trọng, hiển thị ưu tiên)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsPin/{id}
     * </p>
     * 
     * @param id      ID của bài viết cần ghim
     * @param session HttpSession để check quyền admin
     * @return Redirect về trang chi tiết
     */
    @PostMapping("/LvsPin/{id}")
    public String lvsPinPost(@PathVariable Long id) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        lvsPostService.lvsPinPost(id);

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Bỏ ghim bài viết
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsUnpin/{id}
     * </p>
     * 
     * @param id      ID của bài viết cần bỏ ghim
     * @param session HttpSession để check quyền admin
     * @return Redirect về trang chi tiết
     */
    @PostMapping("/LvsUnpin/{id}")
    public String lvsUnpinPost(@PathVariable Long id) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        lvsPostService.lvsUnpinPost(id);

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Hiển thị form chỉnh sửa bài viết
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsEdit/{id}
     * </p>
     * 
     * @param id      ID của bài viết cần chỉnh sửa
     * @param model   Model để truyền dữ liệu ra view
     * @param session HttpSession để check quyền admin
     * @return Template path: LvsAreas/LvsAdmin/LvsPost/LvsEdit
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditPostForm(@PathVariable Long id,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        // Lấy thông tin bài viết
        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);

        // Nếu không tìm thấy, redirect về list
        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsPost/LvsList";
        }

        // Truyền dữ liệu ra view
        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsPostTypes", LvsPost.LvsPostType.values());

        return "LvsAreas/LvsAdmin/LvsPost/LvsEdit";
    }

    /**
     * Xử lý submit form chỉnh sửa bài viết
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsEdit/{id}
     * </p>
     * 
     * @param id      ID của bài viết đang chỉnh sửa
     * @param lvsPost Object LvsPost được binding từ form
     * @param session HttpSession để check quyền admin
     * @param model   Model để truyền thông báo
     * @return Redirect về detail nếu thành công, hoặc edit nếu lỗi
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditPost(@PathVariable Long id,
            @ModelAttribute LvsPost lvsPost,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Model model) {
        // Kiểm tra quyền admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return
        // "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            // Set ID để đảm bảo update đúng record
            lvsPost.setLvsPostId(id);
            lvsPostService.lvsUpdatePost(lvsPost);

            // Thêm ảnh mới nếu có
            if (images != null && !images.isEmpty()) {
                lvsPostService.lvsAddImagesToPost(id, images);
            }

            model.addAttribute("LvsSuccess", "Cập nhật bài viết thành công!");
            return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            model.addAttribute("LvsPost", lvsPost);
            model.addAttribute("LvsPostTypes", LvsPost.LvsPostType.values());
            return "LvsAreas/LvsAdmin/LvsPost/LvsEdit";
        }
    }
}