package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.LvsPromotion;
import k23cnt3.lucvanson.project3.LvsService.LvsPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller xử lý các request liên quan đến promotion từ phía user
 * Hiển thị danh sách promotion đang hoạt động
 */
@Controller
@RequestMapping("/lvsforum/LvsPromotion")
public class LvsPromotionController {

    @Autowired
    private LvsPromotionService lvsPromotionService;

    /**
     * Hiển thị danh sách promotion đang hoạt động
     * GET /lvsforum/LvsPromotion/LvsList
     */
    @GetMapping("/LvsList")
    public String lvsShowPromotions(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Create pageable with 12 items per page, sorted by creation date descending
        Pageable pageable = PageRequest.of(page, 12, Sort.by("lvsCreatedAt").descending());

        // Get active promotions
        Page<LvsPromotion> promotionsPage = lvsPromotionService.lvsGetActivePromotions(pageable);

        // Add to model
        model.addAttribute("LvsPromotions", promotionsPage.getContent());
        model.addAttribute("LvsCurrentPage", page);
        model.addAttribute("LvsTotalPages", promotionsPage.getTotalPages());
        model.addAttribute("LvsTotalItems", promotionsPage.getTotalElements());

        return "LvsAreas/LvsUsers/LvsPromotion/LvsList";
    }
}
