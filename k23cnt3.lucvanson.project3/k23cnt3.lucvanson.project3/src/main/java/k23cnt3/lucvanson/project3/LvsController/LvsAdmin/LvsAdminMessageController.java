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
import java.util.Map;

/**
 * Controller quản lý Tin nhắn (Message) trong Admin Panel
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsMessage")
public class LvsAdminMessageController {

    @Autowired
    private LvsMessageService lvsMessageService;

    @Autowired
    private LvsUserService lvsUserService;

    @GetMapping("/LvsList")
    public String lvsListMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long lvsUserId,
            Model model,
            HttpSession session) {

        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsMessage> lvsMessages;

        if (lvsUserId != null) {
            lvsMessages = lvsMessageService.lvsGetMessagesByUser(lvsUserId, lvsPageable);
        } else {
            lvsMessages = lvsMessageService.lvsGetAllMessages(lvsPageable);
        }

        model.addAttribute("LvsMessages", lvsMessages);
        model.addAttribute("LvsUserId", lvsUserId);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsMessage/LvsList";
    }

    @GetMapping("/LvsDetail/{id}")
    public String lvsViewMessageDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsMessage lvsMessage = lvsMessageService.lvsGetMessageById(id);
        if (lvsMessage == null) {
            return "redirect:/LvsAdmin/LvsMessage/LvsList";
        }

        model.addAttribute("LvsMessage", lvsMessage);
        return "LvsAreas/LvsAdmin/LvsMessage/LvsDetail";
    }

    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteMessage(@PathVariable Long id, HttpSession session, Model model) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsMessageService.lvsDeleteMessage(id);
            model.addAttribute("LvsSuccess", "Đã xóa tin nhắn!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsMessage/LvsList";
    }

    @GetMapping("/LvsConversation")
    public String lvsViewConversation(@RequestParam Long lvsUserId1, @RequestParam Long lvsUserId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model, HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsMessage> lvsMessages = lvsMessageService.lvsGetConversation(lvsUserId1, lvsUserId2, lvsPageable);

        LvsUser lvsUser1 = lvsUserService.lvsGetUserById(lvsUserId1);
        LvsUser lvsUser2 = lvsUserService.lvsGetUserById(lvsUserId2);

        model.addAttribute("LvsMessages", lvsMessages);
        model.addAttribute("LvsUser1", lvsUser1);
        model.addAttribute("LvsUser2", lvsUser2);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsMessage/LvsConversation";
    }

    @GetMapping("/LvsSearch")
    public String lvsSearchMessages(@RequestParam String lvsKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model, HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsMessage> lvsMessages = lvsMessageService.lvsSearchMessages(lvsKeyword, lvsPageable);

        model.addAttribute("LvsMessages", lvsMessages);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsMessage/LvsSearch";
    }

    @PostMapping("/LvsDeleteConversation")
    public String lvsDeleteConversation(@RequestParam Long lvsUserId1, @RequestParam Long lvsUserId2,
            HttpSession session, Model model) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsMessageService.lvsDeleteConversation(lvsUserId1, lvsUserId2);
            model.addAttribute("LvsSuccess", "Đã xóa cuộc trò chuyện!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsMessage/LvsList";
    }

    @GetMapping("/LvsStatistics")
    public String lvsViewMessageStatistics(Model model, HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Long lvsTotalMessages = lvsMessageService.lvsCountTotalMessages();
        Long lvsTodayMessages = lvsMessageService.lvsCountTodayMessages();
        Long lvsUnreadMessages = lvsMessageService.lvsCountUnreadMessages();
        Map<String, Long> lvsMessagesByType = lvsMessageService.lvsGetMessagesByType();

        model.addAttribute("LvsTotalMessages", lvsTotalMessages);
        model.addAttribute("LvsTodayMessages", lvsTodayMessages);
        model.addAttribute("LvsUnreadMessages", lvsUnreadMessages);
        model.addAttribute("LvsMessagesByType", lvsMessagesByType);

        return "LvsAreas/LvsAdmin/LvsMessage/LvsStatistics";
    }
}