package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.LvsEvent;
import k23cnt3.lucvanson.project3.LvsRepository.LvsEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Simple test controller to verify Event system is working
 * Access: http://localhost:8080/lvsforum/test/events
 */
@RestController
@RequestMapping("/test")
public class LvsTestEventController {

    @Autowired
    private LvsEventRepository lvsEventRepository;

    @GetMapping("/events")
    public String testEvents() {
        try {
            List<LvsEvent> events = lvsEventRepository.findAll();

            if (events.isEmpty()) {
                return "✅ Database connected! No events found. Tables are empty.";
            }

            StringBuilder result = new StringBuilder("✅ Database connected! Found " + events.size() + " events:\n\n");
            for (LvsEvent event : events) {
                result.append("ID: ").append(event.getLvsEventId())
                        .append(", Name: ").append(event.getLvsEventName())
                        .append(", Type: ").append(event.getLvsType())
                        .append(", Coins: ").append(event.getLvsRewardCoins())
                        .append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "❌ ERROR: " + e.getMessage() + "\n\nStack trace: " + e.toString();
        }
    }

    @GetMapping("/db-info")
    public String testDatabase() {
        try {
            long count = lvsEventRepository.count();
            return "✅ Database connection OK! Event count: " + count;
        } catch (Exception e) {
            return "❌ Database connection FAILED: " + e.getMessage();
        }
    }
}
