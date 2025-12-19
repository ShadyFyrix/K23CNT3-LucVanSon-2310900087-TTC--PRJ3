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

import java.util.List;

/**
 * Controller quản lý Bình luận (Comment) trong Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị danh sách bình luận với phân trang và lọc</li>
 * <li>Xem chi tiết bình luận và replies</li>
 * <li>Chỉnh sửa nội dung bình luận</li>
 * <li>Duyệt/ẩn bình luận</li>
 * <li>Xóa bình luận</li>
 * <li>Xem bình luận theo bài viết hoặc user</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsComment/LvsList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsComment/LvsDetail.html</li>
 * <li>Edit: LvsAreas/LvsAdmin/LvsComment/LvsEdit.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsComment")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminCommentController {

    @Autowired
    private LvsCommentService lvsCommentService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsPostService lvsPostService;

    /**
     * Hiển thị form tạo comment
     */
    @GetMapping("/LvsCreate")
    public String lvsShowCreateForm(Model model) {
        model.addAttribute("LvsPosts", lvsPostService.lvsGetAllPosts(PageRequest.of(0, 100)).getContent());
        model.addAttribute("LvsUsers", lvsUserService.lvsGetAllUsers(PageRequest.of(0, 100)).getContent());
        return "LvsAreas/LvsAdmin/LvsComment/LvsCreate";
    }

    /**
     * Xử lý tạo comment
     */
    @PostMapping("/LvsCreate")
    public String lvsCreateComment(
            @RequestParam Long lvsPostId,
            @RequestParam Long lvsUserId,
            @RequestParam String lvsContent,
            @RequestParam(required = false) Boolean lvsIsApproved,
            @RequestParam(value = "images", required = false) org.springframework.web.multipart.MultipartFile[] images,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            LvsPost post = lvsPostService.lvsGetPostById(lvsPostId);
            LvsUser user = lvsUserService.lvsGetUserById(lvsUserId);

            LvsComment comment = new LvsComment();
            comment.setLvsPost(post);
            comment.setLvsUser(user);
            comment.setLvsContent(lvsContent);
            comment.setLvsIsApproved(lvsIsApproved != null ? lvsIsApproved : false);

            lvsCommentService.lvsSaveCommentWithImages(comment, images);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Tạo bình luận thành công!");
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsComment/LvsCreate";
        }
    }

    /**
     * Hiển thị danh sách bình luận
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsComment/LvsList
     * </p>
     * 
     * @param page          Số trang (mặc định = 0)
     * @param size          Số items/trang (mặc định = 50)
     * @param lvsIsApproved Lọc theo trạng thái duyệt (optional)
     * @param lvsPostId     Lọc theo bài viết (optional)
     * @param model         Model
     * @param session       HttpSession
     * @return Template path
     */
    @GetMapping("/LvsList")
    public String lvsListComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Boolean lvsIsApproved,
            @RequestParam(required = false) Long lvsPostId,
            Model model) {
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments;

        if (lvsPostId != null) {
            lvsComments = lvsCommentService.lvsGetCommentsByPost(lvsPostId, lvsPageable);
        } else if (lvsIsApproved != null) {
            lvsComments = lvsCommentService.lvsGetCommentsByApproval(lvsIsApproved, lvsPageable);
        } else {
            lvsComments = lvsCommentService.lvsGetAllComments(lvsPageable);
        }

        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsIsApproved", lvsIsApproved);
        model.addAttribute("LvsPostId", lvsPostId);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsComment/LvsList";
    }

    /**
     * Xem chi tiết bình luận
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewCommentDetail(@PathVariable Long id,
            Model model) {
        LvsComment lvsComment = lvsCommentService.lvsGetCommentById(id);

        if (lvsComment == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        List<LvsComment> lvsReplies = lvsComment.getLvsReplies();

        model.addAttribute("LvsComment", lvsComment);
        model.addAttribute("LvsReplies", lvsReplies);

        return "LvsAreas/LvsAdmin/LvsComment/LvsDetail";
    }

    /**
     * Duyệt bình luận
     */
    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveComment(@PathVariable Long id,
            Model model) {
        try {
            lvsCommentService.lvsApproveComment(id);
            model.addAttribute("LvsSuccess", "Đã duyệt bình luận!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsComment/LvsDetail/" + id;
    }

    /**
     * Ẩn bình luận
     */
    @PostMapping("/LvsHide/{id}")
    public String lvsHideComment(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            Model model) {
        try {
            lvsCommentService.lvsHideComment(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã ẩn bình luận!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsComment/LvsDetail/" + id;
    }

    /**
     * Xóa bình luận
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteComment(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== DELETE COMMENT START ===");
            System.out.println("Comment ID: " + id);
            System.out.println("Reason: " + lvsReason);

            lvsCommentService.lvsDeleteComment(id, lvsReason);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Đã xóa bình luận!");
            System.out.println("=== DELETE COMMENT SUCCESS ===");
        } catch (Exception e) {
            System.err.println("=== DELETE COMMENT ERROR ===");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsComment/LvsList";
    }

    /**
     * Hiển thị form chỉnh sửa
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditCommentForm(@PathVariable Long id,
            Model model) {
        LvsComment lvsComment = lvsCommentService.lvsGetCommentById(id);

        if (lvsComment == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        model.addAttribute("LvsComment", lvsComment);

        return "LvsAreas/LvsAdmin/LvsComment/LvsEdit";
    }

    /**
     * Xử lý chỉnh sửa
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditComment(@PathVariable Long id,
            @ModelAttribute LvsComment lvsComment,
            @RequestParam(value = "images", required = false) org.springframework.web.multipart.MultipartFile[] images,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== EDIT COMMENT START ===");
            System.out.println("Comment ID: " + id);
            System.out.println("Content: " + lvsComment.getLvsContent());
            System.out.println("Images count: " + (images != null ? images.length : 0));

            // USE UPDATE instead of SAVE to preserve User/Post
            lvsCommentService.lvsUpdateComment(id, lvsComment.getLvsContent());
            System.out.println("Comment updated successfully");

            // Add new images if provided
            if (images != null && images.length > 0) {
                System.out.println("Adding " + images.length + " new images...");
                lvsCommentService.lvsAddImagesToComment(id, images);
                System.out.println("Images added successfully");
            }

            redirectAttributes.addFlashAttribute("LvsSuccess", "Cập nhật bình luận thành công!");
            System.out.println("=== EDIT COMMENT SUCCESS ===");
            return "redirect:/LvsAdmin/LvsComment/LvsDetail/" + id;
        } catch (Exception e) {
            System.err.println("=== EDIT COMMENT ERROR ===");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsComment/LvsEdit/" + id;
        }
    }

    /**
     * Xóa ảnh của comment
     */
    @PostMapping("/LvsDeleteImage/{imageId}")
    public String lvsDeleteCommentImage(@PathVariable Long imageId,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Long commentId = lvsCommentService.lvsDeleteCommentImage(imageId);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Đã xóa ảnh!");
            return "redirect:/LvsAdmin/LvsComment/LvsEdit/" + commentId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }
    }

    /**
     * Xem bình luận của bài viết
     */
    @GetMapping("/LvsPost/{postId}")
    public String lvsViewPostComments(@PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {
        LvsPost lvsPost = lvsPostService.lvsGetPostById(postId);
        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByPost(postId, lvsPageable);

        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsComment/LvsPost";
    }

    /**
     * Xem bình luận của user
     */
    @GetMapping("/LvsUser/{userId}")
    public String lvsViewUserComments(@PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {
        LvsUser lvsUser = lvsUserService.lvsGetUserById(userId);
        if (lvsUser == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByUser(userId, lvsPageable);

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsComment/LvsUser";
    }
}
