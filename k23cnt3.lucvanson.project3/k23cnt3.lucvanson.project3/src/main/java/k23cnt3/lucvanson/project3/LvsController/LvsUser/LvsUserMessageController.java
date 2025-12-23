package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller quản lý tin nhắn cho người dùng
 * Xử lý gửi, nhận, xóa tin nhắn realtime
 */
@Controller
@RequestMapping("/LvsUser/LvsMessage")
public class LvsUserMessageController {

    @Autowired
    private LvsMessageService lvsMessageService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // Trang hộp thư đến
    @GetMapping("/LvsInbox")
    public String lvsViewInbox(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        List<LvsMessage> lvsMessages = lvsMessageService.lvsGetInboxMessages(
                lvsCurrentUser.getLvsUserId());
        List<k23cnt3.lucvanson.project3.LvsDTO.LvsConversationDTO> lvsConversations = lvsMessageService
                .lvsGetConversationsWithLatestMessage(lvsCurrentUser.getLvsUserId());

        model.addAttribute("LvsMessages", lvsMessages);
        model.addAttribute("LvsConversations", lvsConversations);
        model.addAttribute("LvsUnreadCount", lvsMessageService.lvsGetUnreadCount(
                lvsCurrentUser.getLvsUserId()));

        return "LvsAreas/LvsUsers/LvsMessages/LvsMessageInbox";
    }

    // Trang hộp thư đi
    @GetMapping("/LvsSent")
    public String lvsViewSentMessages(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        List<LvsMessage> lvsSentMessages = lvsMessageService.lvsGetSentMessages(
                lvsCurrentUser.getLvsUserId());

        model.addAttribute("LvsMessages", lvsSentMessages);

        return "LvsAreas/LvsUsers/LvsMessages/LvsMessageSent";
    }

    // Xem cuộc trò chuyện với người dùng cụ thể
    @GetMapping("/LvsConversation/{userId}")
    public String lvsViewConversation(@PathVariable Long userId,
            Model model,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsUser lvsOtherUser = lvsUserService.lvsGetUserById(userId);
        if (lvsOtherUser == null) {
            return "redirect:/LvsUser/LvsMessage/LvsInbox";
        }

        // Lấy tin nhắn giữa 2 người
        Pageable lvsPageable = PageRequest.of(0, 100); // Get last 100 messages
        List<LvsMessage> lvsMessages = lvsMessageService.lvsGetConversation(
                lvsCurrentUser.getLvsUserId(), userId, lvsPageable).getContent();

        // Đánh dấu đã đọc
        lvsMessageService.lvsMarkAsRead(lvsCurrentUser.getLvsUserId(), userId);

        model.addAttribute("LvsCurrentUser", lvsCurrentUser);
        model.addAttribute("LvsOtherUser", lvsOtherUser);
        model.addAttribute("LvsMessages", lvsMessages);
        model.addAttribute("LvsNewMessage", new LvsMessage());

        return "LvsAreas/LvsUsers/LvsMessages/LvsMessageConversation";
    }

    // Gửi tin nhắn (HTTP POST)
    @PostMapping("/LvsSend")
    public String lvsSendMessage(@RequestParam Long lvsReceiverId,
            @RequestParam String lvsContent,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsMessage lvsMessage = new LvsMessage();
            lvsMessage.setLvsSender(lvsCurrentUser);
            lvsMessage.setLvsReceiver(lvsUserService.lvsGetUserById(lvsReceiverId));
            lvsMessage.setLvsContent(lvsContent);
            lvsMessage.setLvsMessageType("TEXT");

            lvsMessageService.lvsSendMessage(lvsMessage);

            model.addAttribute("LvsSuccess", "Đã gửi tin nhắn!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi gửi tin nhắn: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsMessage/LvsConversation/" + lvsReceiverId;
    }

    // Gửi tin nhắn realtime (WebSocket)
    @MessageMapping("/LvsChat")
    @SendTo("/LvsTopic/LvsMessages")
    public LvsMessage lvsSendRealTimeMessage(LvsMessage lvsMessage) {
        lvsMessage.setLvsCreatedAt(LocalDateTime.now());
        LvsMessage lvsSavedMessage = lvsMessageService.lvsSendMessage(lvsMessage);

        // Gửi tin nhắn đến người nhận cụ thể (if WebSocket is configured)
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSendToUser(
                    lvsSavedMessage.getLvsReceiver().getLvsUserId().toString(),
                    "/LvsQueue/LvsMessages",
                    lvsSavedMessage);
        }

        return lvsSavedMessage;
    }

    // Đánh dấu tin nhắn đã đọc
    @PostMapping("/LvsMarkAsRead")
    @ResponseBody
    public String lvsMarkMessageAsRead(@RequestParam Long lvsMessageId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "{\"success\": false}";
        }

        boolean lvsSuccess = lvsMessageService.lvsMarkMessageAsRead(lvsMessageId);
        return "{\"success\": " + lvsSuccess + "}";
    }

    // Xóa tin nhắn
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteMessage(@PathVariable Long id,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsMessageService.lvsDeleteMessage(id, lvsCurrentUser.getLvsUserId());

        return "redirect:/LvsUser/LvsMessage/LvsInbox";
    }

    // Xóa toàn bộ cuộc trò chuyện
    @PostMapping("/LvsDeleteConversation/{userId}")
    public String lvsDeleteConversation(@PathVariable Long userId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsMessageService.lvsDeleteConversation(
                lvsCurrentUser.getLvsUserId(), userId);

        return "redirect:/LvsUser/LvsMessage/LvsInbox";
    }

    // Gửi file đính kèm
    @PostMapping("/LvsSendAttachment")
    public String lvsSendAttachment(@RequestParam Long lvsReceiverId,
            @RequestParam String lvsContent,
            @RequestParam String lvsAttachmentUrl,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsMessage lvsMessage = new LvsMessage();
            lvsMessage.setLvsSender(lvsCurrentUser);
            lvsMessage.setLvsReceiver(lvsUserService.lvsGetUserById(lvsReceiverId));
            lvsMessage.setLvsContent(lvsContent);
            lvsMessage.setLvsAttachmentUrl(lvsAttachmentUrl);
            lvsMessage.setLvsMessageType("FILE");

            lvsMessageService.lvsSendMessage(lvsMessage);

            model.addAttribute("LvsSuccess", "Đã gửi file đính kèm!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi gửi file: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsMessage/LvsConversation/" + lvsReceiverId;
    }

    // Tìm kiếm tin nhắn
    @GetMapping("/LvsSearch")
    public String lvsSearchMessages(@RequestParam String lvsKeyword,
            Model model,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        List<LvsMessage> lvsMessages = lvsMessageService.lvsSearchMessages(
                lvsCurrentUser.getLvsUserId(), lvsKeyword);

        model.addAttribute("LvsMessages", lvsMessages);
        model.addAttribute("LvsKeyword", lvsKeyword);

        return "LvsAreas/LvsUsers/LvsMessages/LvsMessageSearch";
    }
}