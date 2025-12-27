package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserEvent;
import k23cnt3.lucvanson.project3.LvsService.LvsEventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/LvsUser/LvsEvent")
public class LvsUserEventController {

    @Autowired
    private LvsEventService lvsEventService;

    /**
     * Daily check-in page
     */
    @GetMapping("/LvsDailyCheckIn")
    public String lvsDailyCheckIn(Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // Get login streak
        int streak = lvsEventService.lvsGetLoginStreak(currentUser);

        // Get event history
        List<LvsUserEvent> history = lvsEventService.lvsGetUserEventHistory(currentUser);

        model.addAttribute("lvsStreak", streak);
        model.addAttribute("lvsHistory", history);
        model.addAttribute("pageTitle", "Daily Check-In");

        return "LvsAreas/LvsUsers/LvsEvents/LvsDailyCheckIn";
    }

    /**
     * Process daily login (with anti-spam protection)
     */
    @PostMapping("/LvsCheckIn")
    public String lvsCheckIn(RedirectAttributes redirectAttributes, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Process daily login (service handles duplicate check)
            LvsUserEvent result = lvsEventService.lvsProcessDailyLogin(currentUser);

            if (result == null) {
                redirectAttributes.addFlashAttribute("LvsError", "You have already checked in today!");
            } else {
                // Update session with new coin balance
                currentUser.setLvsCoin(currentUser.getLvsCoin() + result.getLvsCoinsEarned());
                session.setAttribute("LvsCurrentUser", currentUser);

                redirectAttributes.addFlashAttribute("LvsSuccess",
                        "Check-in successful! You earned " + result.getLvsCoinsEarned() + " coins!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error processing check-in: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsEvent/LvsDailyCheckIn";
    }

    /**
     * View all available events/tasks
     */
    @GetMapping("/LvsTasks")
    public String lvsTasks(Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // Get active events
        List<LvsEvent> activeEvents = lvsEventService.lvsGetActiveEvents();

        // Get user's unclaimed rewards
        List<LvsUserEvent> unclaimedRewards = lvsEventService.lvsGetUnclaimedRewards(currentUser);

        model.addAttribute("lvsEvents", activeEvents);
        model.addAttribute("lvsUnclaimedRewards", unclaimedRewards);
        model.addAttribute("pageTitle", "Daily Tasks");

        return "LvsAreas/LvsUsers/LvsEvents/LvsTasks";
    }

    /**
     * Claim a specific reward
     */
    @PostMapping("/LvsClaimReward/{id}")
    public String lvsClaimReward(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Get user event
            List<LvsUserEvent> unclaimed = lvsEventService.lvsGetUnclaimedRewards(currentUser);
            LvsUserEvent userEvent = unclaimed.stream()
                    .filter(ue -> ue.getLvsUserEventId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (userEvent == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Reward not found or already claimed!");
                return "redirect:/LvsUser/LvsEvent/LvsTasks";
            }

            // Claim reward
            lvsEventService.lvsClaimReward(userEvent);

            // Update session
            currentUser.setLvsCoin(currentUser.getLvsCoin() + userEvent.getLvsCoinsEarned());
            session.setAttribute("LvsCurrentUser", currentUser);

            redirectAttributes.addFlashAttribute("LvsSuccess",
                    "Claimed " + userEvent.getLvsCoinsEarned() + " coins!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error claiming reward: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsEvent/LvsTasks";
    }

    /**
     * Claim all unclaimed rewards
     */
    @PostMapping("/LvsClaimAll")
    public String lvsClaimAll(RedirectAttributes redirectAttributes, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            List<LvsUserEvent> unclaimed = lvsEventService.lvsGetUnclaimedRewards(currentUser);

            if (unclaimed.isEmpty()) {
                redirectAttributes.addFlashAttribute("LvsError", "No rewards to claim!");
                return "redirect:/LvsUser/LvsEvent/LvsTasks";
            }

            int totalCoins = unclaimed.stream()
                    .mapToInt(LvsUserEvent::getLvsCoinsEarned)
                    .sum();

            lvsEventService.lvsClaimAllRewards(currentUser);

            // Update session
            currentUser.setLvsCoin(currentUser.getLvsCoin() + totalCoins);
            session.setAttribute("LvsCurrentUser", currentUser);

            redirectAttributes.addFlashAttribute("LvsSuccess",
                    "Claimed all rewards! Total: " + totalCoins + " coins!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error claiming rewards: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsEvent/LvsTasks";
    }

    /**
     * View event history
     */
    @GetMapping("/LvsHistory")
    public String lvsHistory(Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        List<LvsUserEvent> history = lvsEventService.lvsGetUserEventHistory(currentUser);

        model.addAttribute("lvsHistory", history);
        model.addAttribute("pageTitle", "Event History");

        return "LvsAreas/LvsUsers/LvsEvents/LvsHistory";
    }
}
