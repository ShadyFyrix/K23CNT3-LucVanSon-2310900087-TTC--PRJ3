package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent.LvsEventType;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUserEvent;
import k23cnt3.lucvanson.project3.LvsService.LvsEventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/LvsAdmin/LvsEvent")
public class LvsAdminEventController {

    @Autowired
    private LvsEventService lvsEventService;

    /**
     * List all events
     */
    @GetMapping("/LvsList")
    public String lvsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsType,
            Model model,
            HttpSession session) {

        // Check admin permission
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        List<LvsEvent> events;
        if (lvsType != null && !lvsType.isEmpty()) {
            events = lvsEventService.lvsGetEventsByType(LvsEventType.valueOf(lvsType));
        } else {
            events = lvsEventService.lvsGetAllEvents();
        }

        model.addAttribute("lvsEvents", events);
        model.addAttribute("lvsEventTypes", LvsEventType.values());
        model.addAttribute("lvsSelectedType", lvsType);
        model.addAttribute("pageTitle", "Event Management");

        return "LvsAreas/LvsAdmin/LvsEvent/LvsList";
    }

    /**
     * Show create event form
     */
    @GetMapping("/LvsCreate")
    public String lvsCreate(Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        model.addAttribute("lvsEvent", new LvsEvent());
        model.addAttribute("lvsEventTypes", LvsEventType.values());
        model.addAttribute("pageTitle", "Create Event");

        return "LvsAreas/LvsAdmin/LvsEvent/LvsCreate";
    }

    /**
     * Save new event with validation
     */
    @PostMapping("/LvsCreate")
    public String lvsSave(
            @ModelAttribute LvsEvent lvsEvent,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            // Security: Validate reward coins (prevent negative values only)
            if (lvsEvent.getLvsRewardCoins() == null || lvsEvent.getLvsRewardCoins() < 0) {
                redirectAttributes.addFlashAttribute("LvsError", "Reward coins must be positive!");
                return "redirect:/LvsAdmin/LvsEvent/LvsCreate";
            }

            // Set creation timestamp
            lvsEvent.setLvsCreatedAt(LocalDateTime.now());

            // Save event
            lvsEventService.lvsSaveEvent(lvsEvent);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Event created successfully!");
            return "redirect:/LvsAdmin/LvsEvent/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error creating event: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsEvent/LvsCreate";
        }
    }

    /**
     * Show edit event form
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsEdit(@PathVariable Long id, Model model, HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        LvsEvent event = lvsEventService.lvsGetEventById(id);
        if (event == null) {
            return "redirect:/LvsAdmin/LvsEvent/LvsList";
        }

        model.addAttribute("lvsEvent", event);
        model.addAttribute("lvsEventTypes", LvsEventType.values());
        model.addAttribute("pageTitle", "Edit Event");

        return "LvsAreas/LvsAdmin/LvsEvent/LvsEdit";
    }

    /**
     * Update event with validation
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsUpdate(
            @PathVariable Long id,
            @ModelAttribute LvsEvent lvsEvent,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        try {
            LvsEvent existing = lvsEventService.lvsGetEventById(id);
            if (existing == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Event not found!");
                return "redirect:/LvsAdmin/LvsEvent/LvsList";
            }

            // Security: Validate reward coins (prevent negative values only)
            if (lvsEvent.getLvsRewardCoins() == null || lvsEvent.getLvsRewardCoins() < 0) {
                redirectAttributes.addFlashAttribute("LvsError", "Reward coins must be positive!");
                return "redirect:/LvsAdmin/LvsEvent/LvsEdit/" + id;
            }

            // Update fields
            existing.setLvsEventName(lvsEvent.getLvsEventName());
            existing.setLvsDescription(lvsEvent.getLvsDescription());
            existing.setLvsType(lvsEvent.getLvsType());
            existing.setLvsRewardCoins(lvsEvent.getLvsRewardCoins());
            existing.setLvsIsActive(lvsEvent.getLvsIsActive());
            existing.setLvsStartDate(lvsEvent.getLvsStartDate());
            existing.setLvsEndDate(lvsEvent.getLvsEndDate());

            lvsEventService.lvsSaveEvent(existing);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Event updated successfully!");
            return "redirect:/LvsAdmin/LvsEvent/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error updating event: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsEvent/LvsEdit/" + id;
        }
    }

    /**
     * Delete event (with safety check)
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
            LvsEvent event = lvsEventService.lvsGetEventById(id);
            if (event == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Event not found!");
                return "redirect:/LvsAdmin/LvsEvent/LvsList";
            }

            // Admin can delete any event (including system events)
            lvsEventService.lvsDeleteEvent(id);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Event deleted successfully!");
            return "redirect:/LvsAdmin/LvsEvent/LvsList";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Error deleting event: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsEvent/LvsList";
        }
    }

    /**
     * View user event history (for monitoring)
     */
    @GetMapping("/LvsUserHistory")
    public String lvsUserHistory(
            @RequestParam(required = false) Long lvsUserId,
            Model model,
            HttpSession session) {

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null || !currentUser.getLvsRole().name().equals("ADMIN")) {
            return "redirect:/LvsAuth/LvsLogin";
        }

        // TODO: Add user event history view for admin monitoring
        model.addAttribute("pageTitle", "User Event History");

        return "LvsAreas/LvsAdmin/LvsEvent/LvsUserHistory";
    }
}
