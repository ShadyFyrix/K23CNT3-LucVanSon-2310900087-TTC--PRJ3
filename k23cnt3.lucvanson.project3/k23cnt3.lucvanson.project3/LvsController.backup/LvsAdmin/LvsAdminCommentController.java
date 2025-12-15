package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Controller quản lý bình luận cho LvsAdmin
 * Xử lý duyệt, ẩn, xóa bình luận
 */
@Controller
@RequestMapping("/LvsAdmin/LvsComment")
public class LvsAdminCommentController {

    @Autowired
    private LvsCommentService lvsCommentService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsPostService lvsPostService;

    // Danh sách bình luận
    @GetMapping("/LvsList")
    public String lvsListComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Boolean lvsIsApproved,
            @RequestParam(required = false) Long lvsPostId,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

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

        return "LvsAdmin/LvsCommentList";
    }

    // Xem chi tiết bình luận
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewCommentDetail(@PathVariable Long id,
                                       Model model,
                                       HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsComment lvsComment = lvsCommentService.lvsGetCommentById(id);

        if (lvsComment == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        List<LvsComment> lvsReplies = lvsComment.getLvsReplies();

        model.addAttribute("LvsComment", lvsComment);
        model.addAttribute("LvsReplies", lvsReplies);

        return "LvsAdmin/LvsCommentDetail";
    }

    // Duyệt bình luận
    @PostMapping("/LvsApprove/{id}")
    public String lvsApproveComment(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCommentService.lvsApproveComment(id);
            model.addAttribute("LvsSuccess", "Đã duyệt bình luận!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsComment/LvsDetail/" + id;
    }

    // Ẩn bình luận
    @PostMapping("/LvsHide/{id}")
    public String lvsHideComment(@PathVariable Long id,
                                 @RequestParam(required = false) String lvsReason,
                                 HttpSession session,
                                 Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCommentService.lvsHideComment(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã ẩn bình luận!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsComment/LvsDetail/" + id;
    }

    // Xóa bình luận
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteComment(@PathVariable Long id,
                                   @RequestParam(required = false) String lvsReason,
                                   HttpSession session,
                                   Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCommentService.lvsDeleteComment(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã xóa bình luận!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsComment/LvsList";
    }

    // Chỉnh sửa bình luận
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditCommentForm(@PathVariable Long id,
                                         Model model,
                                         HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsComment lvsComment = lvsCommentService.lvsGetCommentById(id);

        if (lvsComment == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        model.addAttribute("LvsComment", lvsComment);

        return "LvsAdmin/LvsCommentEdit";
    }

    // Xử lý chỉnh sửa bình luận
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditComment(@PathVariable Long id,
                                 @ModelAttribute LvsComment lvsComment,
                                 HttpSession session,
                                 Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsComment.setLvsCommentId(id);
            lvsComment.setLvsIsEdited(true);
            lvsCommentService.lvsSaveComment(lvsComment);

            model.addAttribute("LvsSuccess", "Cập nhật bình luận thành công!");
            return "redirect:/LvsAdmin/LvsComment/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsCommentEdit";
        }
    }

    // Xem bình luận của bài viết cụ thể
    @GetMapping("/LvsPost/{postId}")
    public String lvsViewPostComments(@PathVariable Long postId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "50") int size,
                                      Model model,
                                      HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPost lvsPost = lvsPostService.lvsGetPostById(postId);
        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByPost(postId, lvsPageable);

        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsCommentPost";
    }

    // Xem bình luận của người dùng cụ thể
    @GetMapping("/LvsUser/{userId}")
    public String lvsViewUserComments(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "50") int size,
                                      Model model,
                                      HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsUser lvsUser = lvsUserService.lvsGetUserById(userId);
        if (lvsUser == null) {
            return "redirect:/LvsAdmin/LvsComment/LvsList";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByUser(userId, lvsPageable);

        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsCommentUser";
    }
}