package k23cnt3.lucvanson.project3.LvsController.LvsUser;

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
 * Controller quản lý bình luận cho người dùng
 * Xử lý viết, chỉnh sửa, xóa, thích bình luận
 */
@Controller
@RequestMapping("/LvsUser/LvsComment")
public class LvsUserCommentController {

    @Autowired
    private LvsCommentService lvsCommentService;

    @Autowired
    private LvsPostService lvsPostService;

    @Autowired
    private LvsUserService lvsUserService;

    // Thêm bình luận mới
    @PostMapping("/LvsAdd")
    public String lvsAddComment(@RequestParam Long lvsPostId,
                                @RequestParam String lvsContent,
                                @RequestParam(required = false) Long lvsParentId,
                                HttpSession session,
                                Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsPost lvsPost = lvsPostService.lvsGetPostById(lvsPostId);
            LvsComment lvsComment = new LvsComment();
            lvsComment.setLvsPost(lvsPost);
            lvsComment.setLvsUser(lvsCurrentUser);
            lvsComment.setLvsContent(lvsContent);

            if (lvsParentId != null) {
                LvsComment lvsParent = lvsCommentService.lvsGetCommentById(lvsParentId);
                lvsComment.setLvsParent(lvsParent);
            }

            lvsCommentService.lvsSaveComment(lvsComment);

            model.addAttribute("LvsSuccess", "Đã thêm bình luận!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi thêm bình luận: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsPost/LvsDetail/" + lvsPostId;
    }

    // Chỉnh sửa bình luận
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditComment(@PathVariable Long id,
                                 @RequestParam String lvsContent,
                                 HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsComment lvsComment = lvsCommentService.lvsGetCommentById(id);

        if (lvsCurrentUser != null &&
                lvsComment.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {

            lvsComment.setLvsContent(lvsContent);
            lvsComment.setLvsIsEdited(true);
            lvsCommentService.lvsSaveComment(lvsComment);
        }

        return "redirect:/LvsUser/LvsPost/LvsDetail/" + lvsComment.getLvsPost().getLvsPostId();
    }

    // Xóa bình luận
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteComment(@PathVariable Long id,
                                   HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsComment lvsComment = lvsCommentService.lvsGetCommentById(id);

        if (lvsCurrentUser != null &&
                lvsComment.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {

            Long lvsPostId = lvsComment.getLvsPost().getLvsPostId();
            lvsCommentService.lvsDeleteComment(id);

            return "redirect:/LvsUser/LvsPost/LvsDetail/" + lvsPostId;
        }

        return "redirect:/LvsUser/LvsDashboard";
    }

    // Thích bình luận
    @PostMapping("/LvsLike/{id}")
    @ResponseBody
    public String lvsLikeComment(@PathVariable Long id, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "{\"success\": false, \"message\": \"Vui lòng đăng nhập\"}";
        }

        boolean lvsLiked = lvsCommentService.lvsLikeComment(id, lvsCurrentUser.getLvsUserId());
        if (lvsLiked) {
            return "{\"success\": true, \"message\": \"Đã thích bình luận\"}";
        } else {
            return "{\"success\": false, \"message\": \"Đã có lỗi xảy ra\"}";
        }
    }

    // Xem bình luận của tôi
    @GetMapping("/LvsMyComments")
    public String lvsViewMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByUser(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsUser/LvsMyComments";
    }

    // Xem trả lời cho bình luận của tôi
    @GetMapping("/LvsReplies")
    public String lvsViewRepliesToMe(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsReplies = lvsCommentService.lvsGetRepliesToUser(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsReplies", lvsReplies);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsUser/LvsCommentReplies";
    }

    // Báo cáo bình luận
    @PostMapping("/LvsReport/{id}")
    public String lvsReportComment(@PathVariable Long id,
                                   @RequestParam String lvsReason,
                                   HttpSession session,
                                   Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCommentService.lvsReportComment(id, lvsCurrentUser.getLvsUserId(), lvsReason);
            model.addAttribute("LvsSuccess", "Đã gửi báo cáo thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsDashboard";
    }
}