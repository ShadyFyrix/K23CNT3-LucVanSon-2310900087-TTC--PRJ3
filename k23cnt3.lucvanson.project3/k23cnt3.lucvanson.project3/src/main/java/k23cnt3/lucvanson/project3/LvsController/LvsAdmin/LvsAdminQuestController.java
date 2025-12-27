package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest;
import k23cnt3.lucvanson.project3.LvsEntity.LvsQuest.LvsQuestType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsQuestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/LvsAdmin/LvsQuest")
public class LvsAdminQuestController {

    @Autowired
    private LvsQuestService lvsQuestService;

    /**
     * List all quests
     */
    @GetMapping("/LvsList")
    public String lvsList(
            @RequestParam(required = false) String lvsType,
            Model model,
            HttpSession session) {

        // Check admin permission
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        List<LvsQuest> quests = lvsQuestService.lvsGetAllQuests();

        // Filter by type if specified
        if (lvsType != null && !lvsType.isEmpty()) {
            quests = quests.stream()
                    .filter(q -> q.getLvsQuestType().name().equals(lvsType))
                    .toList();
        }

        model.addAttribute("lvsQuests", quests);
        model.addAttribute("lvsQuestTypes", LvsQuestType.values());
        model.addAttribute("lvsSelectedType", lvsType);
        model.addAttribute("pageTitle", "Quest Management");

        return "LvsAreas/LvsAdmin/LvsQuest/LvsList";
    }

    /**
     * Show create quest form
     */
    @GetMapping("/LvsCreate")
    public String lvsCreate(Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        model.addAttribute("lvsQuest", new LvsQuest());
        model.addAttribute("lvsQuestTypes", LvsQuestType.values());
        model.addAttribute("pageTitle", "Create Quest");

        return "LvsAreas/LvsAdmin/LvsQuest/LvsCreate";
    }

    /**
     * Save new quest
     */
    @PostMapping("/LvsCreate")
    public String lvsSave(
            @ModelAttribute LvsQuest lvsQuest,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Validation
            if (lvsQuest.getLvsTargetCount() == null || lvsQuest.getLvsTargetCount() <= 0) {
                redirectAttributes.addFlashAttribute("LvsError", "Target count must be positive!");
                return "redirect:/LvsAdmin/LvsQuest/LvsCreate";
            }

            if (lvsQuest.getLvsRewardCoins() == null || lvsQuest.getLvsRewardCoins() < 0) {
                redirectAttributes.addFlashAttribute("LvsError", "Reward coins cannot be negative!");
                return "redirect:/LvsAdmin/LvsQuest/LvsCreate";
            }

            // Save quest
            lvsQuestService.lvsSaveQuest(lvsQuest);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Quest created successfully!");
            return "redirect:/LvsAdmin/LvsQuest/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error creating quest: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsQuest/LvsCreate";
        }
    }

    /**
     * Show edit quest form
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsEdit(@PathVariable Long id, Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        LvsQuest quest = lvsQuestService.lvsGetAllQuests().stream()
                .filter(q -> q.getLvsQuestId().equals(id))
                .findFirst()
                .orElse(null);

        if (quest == null) {
            return "redirect:/LvsAdmin/LvsQuest/LvsList";
        }

        model.addAttribute("lvsQuest", quest);
        model.addAttribute("lvsQuestTypes", LvsQuestType.values());
        model.addAttribute("pageTitle", "Edit Quest");

        return "LvsAreas/LvsAdmin/LvsQuest/LvsEdit";
    }

    /**
     * Update quest
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsUpdate(
            @PathVariable Long id,
            @ModelAttribute LvsQuest lvsQuest,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Validation
            if (lvsQuest.getLvsTargetCount() == null || lvsQuest.getLvsTargetCount() <= 0) {
                redirectAttributes.addFlashAttribute("LvsError", "Target count must be positive!");
                return "redirect:/LvsAdmin/LvsQuest/LvsEdit/" + id;
            }

            if (lvsQuest.getLvsRewardCoins() == null || lvsQuest.getLvsRewardCoins() < 0) {
                redirectAttributes.addFlashAttribute("LvsError", "Reward coins cannot be negative!");
                return "redirect:/LvsAdmin/LvsQuest/LvsEdit/" + id;
            }

            // Set ID to ensure update
            lvsQuest.setLvsQuestId(id);
            lvsQuestService.lvsSaveQuest(lvsQuest);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Quest updated successfully!");
            return "redirect:/LvsAdmin/LvsQuest/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error updating quest: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsQuest/LvsEdit/" + id;
        }
    }

    /**
     * Delete quest
     */
    @GetMapping("/LvsDelete/{id}")
    public String lvsDelete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            lvsQuestService.lvsDeleteQuest(id);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Quest deleted successfully!");
            return "redirect:/LvsAdmin/LvsQuest/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error deleting quest: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsQuest/LvsList";
        }
    }
}
