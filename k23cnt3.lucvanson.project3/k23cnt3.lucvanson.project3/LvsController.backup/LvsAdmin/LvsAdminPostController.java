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
 * Controller quản lý bài viết cho LvsAdmin
 * Xử lý duyệt, ẩn, xóa bài viết
 */
@Controller
@RequestMapping("/LvsAdmin/LvsPost")
public class LvsAdminPostController {

    @Autowired
    private LvsPostService lvsPostService;

    @Autowired
    private LvsUserService lvsUserService;

    // Danh sách bài viết
    @GetMapping("/LvsList")
    public String lvsListPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsType,
            @RequestParam(required = false) String lvsKeyword,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsPost> lvsPosts;

        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsPosts = lvsPostService.lvsSearchPosts(lvsKeyword, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty() && lvsType != null && !lvsType.isEmpty()) {
            lvsPosts = lvsPostService.lvsGetPostsByStatusAndType(lvsStatus, lvsType, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsPosts = lvsPostService.lvsGetPostsByStatus(lvsStatus, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty()) {
            lvsPosts = lvsPostService.lvsGetPostsByType(lvsType, lvsPageable);
        } else {
            lvsPosts = lvsPostService.lvsGetAllPosts(lvsPageable);
        }

        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsStatuses", LvsPost.LvsPostStatus.values());
        model.addAttribute("LvsTypes", LvsPost.LvsPostType.values());
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsSelectedType", lvsType);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsPostList";
    }

    // Xem chi tiết bài viết
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewPostDetail(@PathVariable Long id,
                                    Model model,
                                    HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);

        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsPost/LvsList";
        }

        model.addAttribute("LvsPost", lvsPost);

        return "LvsAdmin/LvsPostDetail";
    }

    // Duyệt bài viết
    @PostMapping("/LvsApprove/{id}")
    public String lvsApprovePost(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPostService.lvsApprovePost(id);
            model.addAttribute("LvsSuccess", "Đã duyệt bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    // Ẩn bài viết
    @PostMapping("/LvsHide/{id}")
    public String lvsHidePost(@PathVariable Long id,
                              @RequestParam(required = false) String lvsReason,
                              HttpSession session,
                              Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPostService.lvsHidePost(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã ẩn bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    // Hiển thị bài viết
    @PostMapping("/LvsShow/{id}")
    public String lvsShowPost(@PathVariable Long id,
                              HttpSession session,
                              Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPostService.lvsShowPost(id);
            model.addAttribute("LvsSuccess", "Đã hiển thị bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    // Xóa bài viết
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeletePost(@PathVariable Long id,
                                @RequestParam(required = false) String lvsReason,
                                HttpSession session,
                                Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPostService.lvsDeletePost(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã xóa bài viết!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsList";
    }

    // Ghim bài viết
    @PostMapping("/LvsPin/{id}")
    public String lvsPinPost(@PathVariable Long id,
                             HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsPostService.lvsPinPost(id);

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    // Bỏ ghim bài viết
    @PostMapping("/LvsUnpin/{id}")
    public String lvsUnpinPost(@PathVariable Long id,
                               HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsPostService.lvsUnpinPost(id);

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    // Chỉnh sửa bài viết
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditPostForm(@PathVariable Long id,
                                      Model model,
                                      HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);

        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsPost/LvsList";
        }

        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsPostTypes", LvsPost.LvsPostType.values());

        return "LvsAdmin/LvsPostEdit";
    }

    // Xử lý chỉnh sửa bài viết
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditPost(@PathVariable Long id,
                              @ModelAttribute LvsPost lvsPost,
                              HttpSession session,
                              Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPost.setLvsPostId(id);
            lvsPostService.lvsUpdatePost(lvsPost);

            model.addAttribute("LvsSuccess", "Cập nhật bài viết thành công!");
            return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsPostEdit";
        }
    }
}