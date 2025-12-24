package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsGift;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsGiftRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/LvsUser/LvsGift")
public class LvsGiftController {

    @Autowired
    private LvsGiftService lvsGiftService;

    @Autowired
    private LvsGiftRepository lvsGiftRepository;

    // My Gifts Page
    @GetMapping("/LvsMy")
    public String lvsMyGifts(@RequestParam(defaultValue = "sent") String tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model,
            HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        PageRequest pageable = PageRequest.of(page, size);

        if ("sent".equals(tab)) {
            Page<LvsGift> giftsSent = lvsGiftService.lvsGetGiftsSent(currentUser.getLvsUserId(), pageable);
            model.addAttribute("LvsGifts", giftsSent);
        } else {
            Page<LvsGift> giftsReceived = lvsGiftService.lvsGetGiftsReceived(currentUser.getLvsUserId(), pageable);
            model.addAttribute("LvsGifts", giftsReceived);
        }

        model.addAttribute("LvsActiveTab", tab);
        return "LvsAreas/LvsUsers/LvsGifts/LvsMyGifts";
    }

    // Send Gift
    @PostMapping("/LvsSend")
    public String lvsSendGift(@RequestParam Long projectId,
            @RequestParam Long recipientId,
            @RequestParam(required = false) String message,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            lvsGiftService.lvsSendGift(currentUser.getLvsUserId(), recipientId, projectId, message);
            redirectAttributes.addFlashAttribute("success", "Gift sent successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/LvsUser/LvsProject/LvsDetail/" + projectId;
    }

    // Accept Gift
    @PostMapping("/LvsAccept/{giftId}")
    public String lvsAcceptGift(@PathVariable Long giftId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Get gift before accepting to get sender info
            LvsGift gift = lvsGiftRepository.findById(giftId)
                    .orElseThrow(() -> new IllegalArgumentException("Gift not found"));
            lvsGiftService.lvsAcceptGift(giftId, currentUser.getLvsUserId());
            redirectAttributes.addFlashAttribute("success", "Gift accepted! Project added to your library.");
            // Redirect back to conversation with sender
            return "redirect:/LvsUser/LvsMessage/LvsConversation/" + gift.getLvsSender().getLvsUserId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/LvsUser/LvsMessage/LvsInbox";
        }
    }

    // Reject Gift
    @PostMapping("/LvsReject/{giftId}")
    public String lvsRejectGift(@PathVariable Long giftId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Get gift before rejecting to get sender info
            LvsGift gift = lvsGiftRepository.findById(giftId)
                    .orElseThrow(() -> new IllegalArgumentException("Gift not found"));
            lvsGiftService.lvsRejectGift(giftId, currentUser.getLvsUserId());
            redirectAttributes.addFlashAttribute("success", "Gift rejected.");
            // Redirect back to conversation with sender
            return "redirect:/LvsUser/LvsMessage/LvsConversation/" + gift.getLvsSender().getLvsUserId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/LvsUser/LvsMessage/LvsInbox";
        }
    }
}
