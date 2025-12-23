package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsGift;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
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
            lvsGiftService.lvsAcceptGift(giftId, currentUser.getLvsUserId());
            redirectAttributes.addFlashAttribute("success", "Gift accepted! Project added to your library.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/LvsUser/LvsGift/LvsMy?tab=received";
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
            lvsGiftService.lvsRejectGift(giftId, currentUser.getLvsUserId());
            redirectAttributes.addFlashAttribute("success", "Gift rejected.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/LvsUser/LvsGift/LvsMy?tab=received";
    }
}
