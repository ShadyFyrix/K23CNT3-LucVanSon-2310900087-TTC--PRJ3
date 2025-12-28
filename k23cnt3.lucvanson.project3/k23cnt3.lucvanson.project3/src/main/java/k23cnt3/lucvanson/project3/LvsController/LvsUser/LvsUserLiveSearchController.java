package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsService.LvsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller cho live search autocomplete
 * Trả về JSON data cho AJAX requests
 */
@RestController
@RequestMapping("/LvsUser")
public class LvsUserLiveSearchController {

    @Autowired
    private LvsSearchService lvsSearchService;

    /**
     * API endpoint cho live search
     * Trả về top 5 projects và top 5 users matching keyword
     */
    @GetMapping("/api/search")
    public Map<String, Object> lvsLiveSearch(@RequestParam("q") String lvsKeyword) {
        // Limit 5 results per category for autocomplete
        return lvsSearchService.lvsGlobalSearch(lvsKeyword, 5);
    }
}
