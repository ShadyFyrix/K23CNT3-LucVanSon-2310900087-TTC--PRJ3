package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsService.LvsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Simplified test controller (NO SESSION CHECK)
 */
@Controller
@RequestMapping("/test-admin")
public class LvsTestAdminController {

    @Autowired
    private LvsEventService lvsEventService;

    @GetMapping("/events")
    public String testEventList(Model model) {
        try {
            List<LvsEvent> events = lvsEventService.lvsGetAllEvents();
            model.addAttribute("lvsEvents", events);
            model.addAttribute("lvsEventTypes", LvsEvent.LvsEventType.values());
            model.addAttribute("pageTitle", "Test Event List");

            return "LvsAreas/LvsAdmin/LvsEvent/LvsList";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
