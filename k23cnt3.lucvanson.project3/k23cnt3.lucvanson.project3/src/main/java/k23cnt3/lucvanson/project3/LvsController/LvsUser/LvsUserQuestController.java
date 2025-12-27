package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserQuest;
import k23cnt3.lucvanson.project3.LvsService.LvsQuestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/LvsUser/LvsQuest")
public class LvsUserQuestController {

    @Autowired
    private LvsQuestService lvsQuestService;

    /**
     * Quest Dashboard - View all quests and progress
     */
    @GetMapping("/LvsDashboard")
    public String lvsDashboard(Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // Get user's quest progress
        List<LvsUserQuest> activeQuests = lvsQuestService.lvsGetUserActiveQuests(currentUser);
        List<LvsUserQuest> completedQuests = lvsQuestService.lvsGetUserCompletedQuests(currentUser);
        List<LvsUserQuest> unclaimedQuests = lvsQuestService.lvsGetUnclaimedQuests(currentUser);

        model.addAttribute("lvsActiveQuests", activeQuests);
        model.addAttribute("lvsCompletedQuests", completedQuests);
        model.addAttribute("lvsUnclaimedQuests", unclaimedQuests);
        model.addAttribute("pageTitle", "Quest Dashboard");

        return "LvsAreas/LvsUsers/LvsQuests/LvsDashboard";
    }

    /**
     * Claim single quest reward
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
            lvsQuestService.lvsClaimQuestReward(id);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Quest reward claimed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error claiming reward: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsQuest/LvsDashboard";
    }

    /**
     * Claim all quest rewards
     */
    @PostMapping("/LvsClaimAll")
    public String lvsClaimAll(RedirectAttributes redirectAttributes, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            lvsQuestService.lvsClaimAllQuestRewards(currentUser);
            redirectAttributes.addFlashAttribute("LvsSuccess", "All rewards claimed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error claiming rewards: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsQuest/LvsDashboard";
    }
}
