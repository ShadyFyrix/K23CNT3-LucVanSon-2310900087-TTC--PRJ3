package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsPost.LvsPostType;
import k23cnt3.lucvanson.project3.LvsService.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for user-facing blog operations
 * Handles post creation, viewing, editing, and deletion
 */
@Controller
@RequestMapping("/LvsUser/LvsBlog")
@RequiredArgsConstructor
public class LvsUserPostController {

    private final LvsPostService lvsPostService;
    private final LvsCommentService lvsCommentService;
    private final LvsUserService lvsUserService;

    /**
     * List all published blog posts
     */
    @GetMapping("/LvsList")
    public String lvsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "newest") String sort,
            Model model) {

        Pageable lvsPageable;

        // Apply sorting
        switch (sort) {
            case "popular":
                lvsPageable = PageRequest.of(page, size, Sort.by("lvsViewCount").descending());
                break;
            case "comments":
                lvsPageable = PageRequest.of(page, size, Sort.by("lvsCommentCount").descending());
                break;
            default: // newest
                lvsPageable = PageRequest.of(page, size, Sort.by("lvsCreatedAt").descending());
        }

        Page<LvsPost> lvsPosts;

        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            lvsPosts = lvsPostService.lvsSearchPosts(search, lvsPageable);
        } else if (type != null && !type.isEmpty()) {
            lvsPosts = lvsPostService.lvsGetPostsByStatusAndType("PUBLISHED", type, lvsPageable);
        } else {
            lvsPosts = lvsPostService.lvsGetAllPublishedPosts(lvsPageable);
        }

        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsCurrentPage", page);
        model.addAttribute("LvsCurrentType", type);
        model.addAttribute("LvsCurrentSearch", search);
        model.addAttribute("LvsCurrentSort", sort);
        model.addAttribute("LvsPostTypes", LvsPostType.values());

        return "LvsAreas/LvsUsers/LvsBlog/LvsBlogList";
    }

    /**
     * Show create post form
     */
    @GetMapping("/LvsCreate")
    public String lvsShowCreateForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsPostTypes", LvsPostType.values());
        return "LvsAreas/LvsUsers/LvsBlog/LvsBlogCreate";
    }

    /**
     * Create new blog post
     */
    @PostMapping("/LvsCreate")
    public String lvsCreate(
            @RequestParam String lvsTitle,
            @RequestParam String lvsContent,
            @RequestParam String lvsType,
            @RequestParam(required = false) String lvsTags,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsPost lvsPost = new LvsPost();
            lvsPost.setLvsTitle(lvsTitle);
            lvsPost.setLvsContent(lvsContent);
            lvsPost.setLvsType(LvsPostType.valueOf(lvsType));
            lvsPost.setLvsTags(lvsTags);
            lvsPost.setLvsUser(lvsCurrentUser);

            // Set status (DRAFT or PUBLISHED)
            if ("PUBLISHED".equals(lvsStatus)) {
                lvsPost.setLvsStatus(LvsPostStatus.PUBLISHED);
            } else {
                lvsPost.setLvsStatus(LvsPostStatus.DRAFT);
            }

            // Save post with images
            if (images != null && !images.isEmpty()) {
                lvsPost = lvsPostService.lvsSavePostWithImages(lvsPost, images);
            } else {
                lvsPost = lvsPostService.lvsSavePost(lvsPost);
            }

            redirectAttributes.addFlashAttribute("LvsSuccess", "Post created successfully!");
            return "redirect:/LvsUser/LvsBlog/LvsDetail/" + lvsPost.getLvsPostId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error creating post: " + e.getMessage());
            return "redirect:/LvsUser/LvsBlog/LvsCreate";
        }
    }

    /**
     * View post detail with comments
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model,
            HttpSession session) {

        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);
        if (lvsPost == null) {
            return "redirect:/LvsUser/LvsBlog/LvsList";
        }

        // Prevent view count spam - only count once per session
        String viewedKey = "viewed_post_" + id;
        if (session.getAttribute(viewedKey) == null) {
            lvsPostService.lvsIncrementViewCount(id);
            session.setAttribute(viewedKey, true);
        }

        // Get comments
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsComment> lvsComments = lvsCommentService.lvsGetCommentsByPost(id, lvsPageable);

        // Check if current user is the author
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        boolean lvsIsAuthor = lvsCurrentUser != null &&
                lvsPost.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId());

        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsComments", lvsComments);
        model.addAttribute("LvsIsAuthor", lvsIsAuthor);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsBlog/LvsBlogDetail";
    }

    /**
     * Show edit post form
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditForm(
            @PathVariable Long id,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);
        if (lvsPost == null) {
            redirectAttributes.addFlashAttribute("LvsError", "Post not found!");
            return "redirect:/LvsUser/LvsBlog/LvsList";
        }

        // Check authorization
        if (!lvsPost.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            redirectAttributes.addFlashAttribute("LvsError", "You can only edit your own posts!");
            return "redirect:/LvsUser/LvsBlog/LvsDetail/" + id;
        }

        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsPostTypes", LvsPostType.values());
        return "LvsAreas/LvsUsers/LvsBlog/LvsBlogEdit";
    }

    /**
     * Update existing post
     */
    @PostMapping("/LvsUpdate/{id}")
    public String lvsUpdate(
            @PathVariable Long id,
            @RequestParam String lvsTitle,
            @RequestParam String lvsContent,
            @RequestParam String lvsType,
            @RequestParam(required = false) String lvsTags,
            @RequestParam(required = false) String lvsStatus,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);
        if (lvsPost == null || !lvsPost.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            redirectAttributes.addFlashAttribute("LvsError", "Unauthorized!");
            return "redirect:/LvsUser/LvsBlog/LvsList";
        }

        try {
            lvsPost.setLvsTitle(lvsTitle);
            lvsPost.setLvsContent(lvsContent);
            lvsPost.setLvsType(LvsPostType.valueOf(lvsType));
            lvsPost.setLvsTags(lvsTags);

            if ("PUBLISHED".equals(lvsStatus)) {
                lvsPost.setLvsStatus(LvsPostStatus.PUBLISHED);
            } else {
                lvsPost.setLvsStatus(LvsPostStatus.DRAFT);
            }

            lvsPostService.lvsUpdatePost(lvsPost);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Post updated successfully!");
            return "redirect:/LvsUser/LvsBlog/LvsDetail/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error updating post: " + e.getMessage());
            return "redirect:/LvsUser/LvsBlog/LvsEdit/" + id;
        }
    }

    /**
     * Delete post
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDelete(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);
        if (lvsPost == null || !lvsPost.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            redirectAttributes.addFlashAttribute("LvsError", "Unauthorized!");
            return "redirect:/LvsUser/LvsBlog/LvsList";
        }

        try {
            lvsPostService.lvsDeletePost(id);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Post deleted successfully!");
            return "redirect:/LvsUser/LvsBlog/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error deleting post: " + e.getMessage());
            return "redirect:/LvsUser/LvsBlog/LvsDetail/" + id;
        }
    }

    /**
     * View current user's posts with status filter
     */
    @GetMapping("/LvsMyPosts")
    public String lvsMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String status,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size, Sort.by("lvsCreatedAt").descending());
        Page<LvsPost> lvsPosts;

        // Filter by status
        if ("draft".equalsIgnoreCase(status)) {
            lvsPosts = lvsPostService.lvsGetPostsByUserAndStatus(
                    lvsCurrentUser.getLvsUserId(),
                    LvsPost.LvsPostStatus.DRAFT,
                    lvsPageable);
        } else if ("published".equalsIgnoreCase(status)) {
            lvsPosts = lvsPostService.lvsGetPostsByUserAndStatus(
                    lvsCurrentUser.getLvsUserId(),
                    LvsPost.LvsPostStatus.PUBLISHED,
                    lvsPageable);
        } else {
            // All posts (both draft and published)
            lvsPosts = lvsPostService.lvsGetPostsByUser(lvsCurrentUser.getLvsUserId(), lvsPageable);
        }

        model.addAttribute("LvsPosts", lvsPosts);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsBlog/LvsMyPosts";
    }

    /**
     * Toggle like on a post (AJAX endpoint)
     */
    @PostMapping("/LvsLike/{id}")
    @ResponseBody
    public java.util.Map<String, Object> lvsToggleLike(
            @PathVariable Long id,
            HttpSession session) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        // Debug logging
        System.out.println("=== LIKE REQUEST DEBUG ===");
        System.out.println("Post ID: " + id);
        System.out.println("Session ID: " + session.getId());

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        System.out.println("Current User: " + (lvsCurrentUser != null ? lvsCurrentUser.getLvsUsername() : "NULL"));

        if (lvsCurrentUser == null) {
            System.out.println("ERROR: User not found in session!");
            response.put("success", false);
            response.put("message", "Please login to like posts");
            return response;
        }

        try {
            boolean liked = lvsPostService.lvsLikePost(id, lvsCurrentUser.getLvsUserId());
            LvsPost post = lvsPostService.lvsGetPostById(id);

            // Check if post exists
            if (post == null) {
                System.out.println("ERROR: Post not found with ID: " + id);
                response.put("success", false);
                response.put("message", "Post not found");
                return response;
            }

            System.out.println("Like toggled: " + liked + ", New count: " + post.getLvsLikeCount());

            response.put("success", true);
            response.put("liked", liked);
            response.put("likeCount", post.getLvsLikeCount());
            return response;

        } catch (Exception e) {
            System.out.println("ERROR in like: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
}
