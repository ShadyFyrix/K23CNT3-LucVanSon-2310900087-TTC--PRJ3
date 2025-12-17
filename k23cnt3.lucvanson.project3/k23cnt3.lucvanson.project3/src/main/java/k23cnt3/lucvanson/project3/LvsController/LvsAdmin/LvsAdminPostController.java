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

import java.util.List;

/**
 * Controller quáº£n lÃ½ BÃ i viáº¿t (Post) trong Admin Panel
 * 
 * <p>
 * Chá»©c nÄƒng chÃ­nh:
 * </p>
 * <ul>
 * <li>Hiá»ƒn thá»‹ danh sÃ¡ch bÃ i viáº¿t vá»›i phÃ¢n trang, tÃ¬m kiáº¿m vÃ  lá»c</li>
 * <li>Xem chi tiáº¿t bÃ i viáº¿t</li>
 * <li>Chá»‰nh sá»­a bÃ i viáº¿t</li>
 * <li>Duyá»‡t/áº©n/hiá»ƒn thá»‹ bÃ i viáº¿t</li>
 * <li>XÃ³a bÃ i viáº¿t</li>
 * <li>Ghim/bá» ghim bÃ i viáº¿t</li>
 * </ul>
 * 
 * <p>
 * TÃ­nh nÄƒng Ä‘áº·c biá»‡t:
 * </p>
 * <ul>
 * <li><strong>Moderation:</strong> Duyá»‡t, áº©n, hiá»ƒn thá»‹ bÃ i viáº¿t</li>
 * <li><strong>Pin:</strong> Ghim bÃ i viáº¿t quan trá»ng lÃªn Ä‘áº§u</li>
 * <li><strong>Filter:</strong> Lá»c theo status (PENDING, APPROVED, HIDDEN) vÃ 
 * type (DISCUSSION, QUESTION, TUTORIAL)</li>
 * <li><strong>Search:</strong> TÃ¬m kiáº¿m theo tiÃªu Ä‘á» vÃ  ná»™i dung</li>
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
     * Service xá»­ lÃ½ logic nghiá»‡p vá»¥ cho Post
     */
    @Autowired
    private LvsPostService lvsPostService;

    /**
     * Service xá»­ lÃ½ logic nghiá»‡p vá»¥ cho User (Ä‘á»ƒ check quyá»n admin)
     */
    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Hiá»ƒn thá»‹ danh sÃ¡ch bÃ i viáº¿t vá»›i phÃ¢n trang, tÃ¬m kiáº¿m vÃ  lá»c
     * 
     * <p>
     * Chá»©c nÄƒng:
     * </p>
     * <ul>
     * <li>Láº¥y danh sÃ¡ch bÃ i viáº¿t theo trang</li>
     * <li>TÃ¬m kiáº¿m theo keyword (tiÃªu Ä‘á», ná»™i dung)</li>
     * <li>Lá»c theo status (PENDING, APPROVED, HIDDEN)</li>
     * <li>Lá»c theo type (DISCUSSION, QUESTION, TUTORIAL)</li>
     * <li>Káº¿t há»£p nhiá»u Ä‘iá»u kiá»‡n lá»c</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsList
     * </p>
     * 
     * @param page       Sá»‘ trang hiá»‡n táº¡i (máº·c Ä‘á»‹nh = 0)
     * @param size       Sá»‘ items má»—i trang (máº·c Ä‘á»‹nh = 20)
     * @param lvsStatus  Status Ä‘á»ƒ lá»c (optional)
     * @param lvsType    Type Ä‘á»ƒ lá»c (optional)
     * @param lvsKeyword Tá»« khÃ³a tÃ¬m kiáº¿m (optional)
     * @param model      Model Ä‘á»ƒ truyá»n dá»¯ liá»‡u ra view
     * @param session    HttpSession Ä‘á»ƒ check quyá»n admin
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

        // Táº¡o Pageable object
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsPost> lvsPosts;

        // Xá»­ lÃ½ theo thá»© tá»± Æ°u tiÃªn
        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            // TÃ¬m kiáº¿m theo keyword
            lvsPosts = lvsPostService.lvsSearchPosts(lvsKeyword, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty() && lvsType != null && !lvsType.isEmpty()) {
            // Lá»c theo cáº£ status vÃ  type
            lvsPosts = lvsPostService.lvsGetPostsByStatusAndType(lvsStatus, lvsType, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            // Lá»c chá»‰ theo status
            lvsPosts = lvsPostService.lvsGetPostsByStatus(lvsStatus, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty()) {
            // Lá»c chá»‰ theo type
            lvsPosts = lvsPostService.lvsGetPostsByType(lvsType, lvsPageable);
        } else {
            // Láº¥y táº¥t cáº£
            lvsPosts = lvsPostService.lvsGetAllPosts(lvsPageable);
        }

        // Truyá»n dá»¯ liá»‡u ra view
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
     * Hiá»ƒn thá»‹ chi tiáº¿t bÃ i viáº¿t
     * 
     * <p>
     * Chá»©c nÄƒng:
     * </p>
     * <ul>
     * <li>Láº¥y thÃ´ng tin Ä‘áº§y Ä‘á»§ cá»§a bÃ i viáº¿t</li>
     * <li>Hiá»ƒn thá»‹ tÃ¡c giáº£, thá»i gian Ä‘Äƒng</li>
     * <li>Hiá»ƒn thá»‹ sá»‘ lÆ°á»£t xem, like, comment</li>
     * <li>Hiá»ƒn thá»‹ tráº¡ng thÃ¡i duyá»‡t</li>
     * </ul>
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsDetail/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t cáº§n xem
     * @param model   Model Ä‘á»ƒ truyá»n dá»¯ liá»‡u ra view
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @return Template path: LvsAreas/LvsAdmin/LvsPost/LvsDetail
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewPostDetail(@PathVariable Long id,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        // Láº¥y thÃ´ng tin bÃ i viáº¿t
        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);

        // Náº¿u khÃ´ng tÃ¬m tháº¥y, redirect vá» list
        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsPost/LvsList";
        }

        // Truyá»n dá»¯ liá»‡u ra view
        model.addAttribute("LvsPost", lvsPost);

        return "LvsAreas/LvsAdmin/LvsPost/LvsDetail";
    }

    /**
     * Duyá»‡t bÃ i viáº¿t (chuyá»ƒn status sang APPROVED)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsApprove/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t cáº§n duyá»‡t
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @param model   Model Ä‘á»ƒ truyá»n thÃ´ng bÃ¡o
     * @return Redirect vá» trang chi tiáº¿t
     */
    @PostMapping("/LvsApprove/{id}")
    public String lvsApprovePost(@PathVariable Long id,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsApprovePost(id);
            model.addAttribute("LvsSuccess", "ÄÃ£ duyá»‡t bÃ i viáº¿t!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * áº¨n bÃ i viáº¿t (chuyá»ƒn status sang HIDDEN)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsHide/{id}
     * </p>
     * 
     * @param id        ID cá»§a bÃ i viáº¿t cáº§n áº©n
     * @param lvsReason LÃ½ do áº©n (optional)
     * @param session   HttpSession Ä‘á»ƒ check quyá»n admin
     * @param model     Model Ä‘á»ƒ truyá»n thÃ´ng bÃ¡o
     * @return Redirect vá» trang chi tiáº¿t
     */
    @PostMapping("/LvsHide/{id}")
    public String lvsHidePost(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsHidePost(id, lvsReason);
            model.addAttribute("LvsSuccess", "ÄÃ£ áº©n bÃ i viáº¿t!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Hiá»ƒn thá»‹ láº¡i bÃ i viáº¿t (chuyá»ƒn status tá»« HIDDEN sang APPROVED)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsShow/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t cáº§n hiá»ƒn thá»‹
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @param model   Model Ä‘á»ƒ truyá»n thÃ´ng bÃ¡o
     * @return Redirect vá» trang chi tiáº¿t
     */
    @PostMapping("/LvsShow/{id}")
    public String lvsShowPost(@PathVariable Long id,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsShowPost(id);
            model.addAttribute("LvsSuccess", "ÄÃ£ hiá»ƒn thá»‹ bÃ i viáº¿t!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * XÃ³a bÃ i viáº¿t
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsDelete/{id}
     * </p>
     * 
     * @param id        ID cá»§a bÃ i viáº¿t cáº§n xÃ³a
     * @param lvsReason LÃ½ do xÃ³a (optional)
     * @param session   HttpSession Ä‘á»ƒ check quyá»n admin
     * @param model     Model Ä‘á»ƒ truyá»n thÃ´ng bÃ¡o
     * @return Redirect vá» trang danh sÃ¡ch
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeletePost(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            lvsPostService.lvsDeletePost(id, lvsReason);
            model.addAttribute("LvsSuccess", "ÄÃ£ xÃ³a bÃ i viáº¿t!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPost/LvsList";
    }

    /**
     * Ghim bÃ i viáº¿t (Ä‘Ã¡nh dáº¥u quan trá»ng, hiá»ƒn thá»‹ Æ°u tiÃªn)
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsPin/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t cáº§n ghim
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @return Redirect vá» trang chi tiáº¿t
     */
    @PostMapping("/LvsPin/{id}")
    public String lvsPinPost(@PathVariable Long id) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        lvsPostService.lvsPinPost(id);

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Bá» ghim bÃ i viáº¿t
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsUnpin/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t cáº§n bá» ghim
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @return Redirect vá» trang chi tiáº¿t
     */
    @PostMapping("/LvsUnpin/{id}")
    public String lvsUnpinPost(@PathVariable Long id) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        lvsPostService.lvsUnpinPost(id);

        return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
    }

    /**
     * Hiá»ƒn thá»‹ form chá»‰nh sá»­a bÃ i viáº¿t
     * 
     * <p>
     * URL: GET /LvsAdmin/LvsPost/LvsEdit/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t cáº§n chá»‰nh sá»­a
     * @param model   Model Ä‘á»ƒ truyá»n dá»¯ liá»‡u ra view
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @return Template path: LvsAreas/LvsAdmin/LvsPost/LvsEdit
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditPostForm(@PathVariable Long id,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        // Láº¥y thÃ´ng tin bÃ i viáº¿t
        LvsPost lvsPost = lvsPostService.lvsGetPostById(id);

        // Náº¿u khÃ´ng tÃ¬m tháº¥y, redirect vá» list
        if (lvsPost == null) {
            return "redirect:/LvsAdmin/LvsPost/LvsList";
        }

        // Truyá»n dá»¯ liá»‡u ra view
        model.addAttribute("LvsPost", lvsPost);
        model.addAttribute("LvsPostTypes", LvsPost.LvsPostType.values());

        return "LvsAreas/LvsAdmin/LvsPost/LvsEdit";
    }

    /**
     * Xá»­ lÃ½ submit form chá»‰nh sá»­a bÃ i viáº¿t
     * 
     * <p>
     * URL: POST /LvsAdmin/LvsPost/LvsEdit/{id}
     * </p>
     * 
     * @param id      ID cá»§a bÃ i viáº¿t Ä‘ang chá»‰nh sá»­a
     * @param lvsPost Object LvsPost Ä‘Æ°á»£c binding tá»« form
     * @param session HttpSession Ä‘á»ƒ check quyá»n admin
     * @param model   Model Ä‘á»ƒ truyá»n thÃ´ng bÃ¡o
     * @return Redirect vá» detail náº¿u thÃ nh cÃ´ng, hoáº·c edit náº¿u lá»—i
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditPost(@PathVariable Long id,
            @ModelAttribute LvsPost lvsPost,
            Model model) {
        // Kiá»ƒm tra quyá»n admin
        // TODO: Fix session parameter - Temporarily commented out
        // if (!lvsUserService.lvsIsAdmin(session)) { return "redirect:/LvsAuth/LvsLogin.html"; }

        try {
            // Set ID Ä‘á»ƒ Ä‘áº£m báº£o update Ä‘Ãºng record
            lvsPost.setLvsPostId(id);
            lvsPostService.lvsUpdatePost(lvsPost);

            model.addAttribute("LvsSuccess", "Cáº­p nháº­t bÃ i viáº¿t thÃ nh cÃ´ng!");
            return "redirect:/LvsAdmin/LvsPost/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsPost/LvsEdit";
        }
    }
}