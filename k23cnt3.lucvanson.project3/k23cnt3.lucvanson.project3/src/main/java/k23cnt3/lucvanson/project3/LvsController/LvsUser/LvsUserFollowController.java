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

import java.util.ArrayList;
import java.util.List;

/**
 * Controller quản lý theo dõi người dùng
 * Xử lý follow, unfollow, xem người theo dõi
 */
@Controller
@RequestMapping("/LvsUser/LvsFollow")
public class LvsUserFollowController {

    @Autowired
    private LvsFollowService lvsFollowService;

    @Autowired
    private LvsUserService lvsUserService;

    // Theo dõi người dùng
    @PostMapping("/LvsFollowUser")
    @ResponseBody
    public String lvsFollowUser(@RequestParam Long lvsUserId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "{\"success\": false, \"message\": \"Vui lòng đăng nhập\"}";
        }

        try {
            boolean lvsSuccess = lvsFollowService.lvsFollowUser(
                    lvsCurrentUser.getLvsUserId(), lvsUserId);

            if (lvsSuccess) {
                return "{\"success\": true, \"message\": \"Đã theo dõi\"}";
            } else {
                return "{\"success\": false, \"message\": \"Đã xảy ra lỗi\"}";
            }
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // Bỏ theo dõi
    @PostMapping("/LvsUnfollowUser")
    @ResponseBody
    public String lvsUnfollowUser(@RequestParam Long lvsUserId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "{\"success\": false, \"message\": \"Vui lòng đăng nhập\"}";
        }

        try {
            boolean lvsSuccess = lvsFollowService.lvsUnfollowUser(
                    lvsCurrentUser.getLvsUserId(), lvsUserId);

            if (lvsSuccess) {
                return "{\"success\": true, \"message\": \"Đã bỏ theo dõi\"}";
            } else {
                return "{\"success\": false, \"message\": \"Đã xảy ra lỗi\"}";
            }
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    // Xem danh sách người theo dõi tôi
    @GetMapping("/LvsMyFollowers")
    public String lvsViewMyFollowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsUser> lvsFollowers = lvsFollowService.lvsGetFollowers(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsFollowers", lvsFollowers);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsFollow/LvsFollowersList";
    }

    // Xem danh sách người tôi đang theo dõi
    @GetMapping("/LvsMyFollowing")
    public String lvsViewMyFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsUser> lvsFollowing = lvsFollowService.lvsGetFollowing(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsFollowing", lvsFollowing);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsFollow/LvsFollowingList";
    }

    // Xem gợi ý người dùng để theo dõi
    @GetMapping("/LvsSuggestions")
    public String lvsViewSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        // TODO: Fix method signature - cannot find symbol
        // Page<LvsUser> lvsSuggestions = lvsFollowService.lvsGetFollowSuggestions(
        // lvsCurrentUser.getLvsUserId(), lvsPageable);
        List<LvsUser> lvsSuggestions = new ArrayList<>();

        model.addAttribute("LvsSuggestions", lvsSuggestions);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsFollow/LvsFollowSuggestions";
    }

    // Tìm kiếm người dùng để theo dõi
    @GetMapping("/LvsSearch")
    public String lvsSearchUsersToFollow(
            @RequestParam String lvsKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsUser> lvsUsers = lvsUserService.lvsSearchUsers(lvsKeyword, lvsPageable);

        // Loại bỏ chính mình khỏi kết quả
        List<LvsUser> lvsFilteredUsers = lvsUsers.getContent().stream()
                .filter(user -> !user.getLvsUserId().equals(lvsCurrentUser.getLvsUserId()))
                .toList();

        // Kiểm tra đã follow chưa
        for (LvsUser user : lvsFilteredUsers) {
            boolean lvsIsFollowing = lvsFollowService.lvsIsFollowing(
                    lvsCurrentUser.getLvsUserId(), user.getLvsUserId());
            // TODO: Add setLvsFollowing method to LvsUser entity
            // user.setLvsFollowing(lvsIsFollowing);
        }

        model.addAttribute("LvsUsers", lvsUsers);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsFollow/LvsFollowSearch";
    }

    // Xóa người theo dõi (chặn)
    @PostMapping("/LvsRemoveFollower")
    public String lvsRemoveFollower(@RequestParam Long lvsFollowerId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsFollowService.lvsRemoveFollower(lvsCurrentUser.getLvsUserId(), lvsFollowerId);

        return "redirect:/LvsUser/LvsFollow/LvsMyFollowers";
    }
}