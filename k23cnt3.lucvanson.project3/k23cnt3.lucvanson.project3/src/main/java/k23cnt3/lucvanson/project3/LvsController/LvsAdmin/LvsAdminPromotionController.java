package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import k23cnt3.lucvanson.project3.LvsRepository.LvsOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller quản lý Khuyến mãi (Promotion) trong Admin Panel
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsPromotion")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminPromotionController {

    @Autowired
    private LvsPromotionService lvsPromotionService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsOrderRepository lvsOrderRepository;

    @GetMapping("/LvsList")
    public String lvsListPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean lvsIsActive,
            Model model) {
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsPromotion> lvsPromotions;

        if (lvsIsActive != null) {
            lvsPromotions = lvsPromotionService.lvsGetPromotionsByActive(lvsIsActive, lvsPageable);
        } else {
            lvsPromotions = lvsPromotionService.lvsGetAllPromotions(lvsPageable);
        }

        // Calculate actual usage count from orders (same as user controller)
        lvsPromotions.forEach(promo -> {
            long actualCount = lvsOrderRepository.countByLvsPromotion_LvsPromotionId(
                    promo.getLvsPromotionId());
            promo.setLvsUsedCount((int) actualCount);
        });

        model.addAttribute("LvsPromotions", lvsPromotions);
        model.addAttribute("LvsIsActive", lvsIsActive);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsPromotion/LvsList";
    }

    @GetMapping("/LvsAdd")
    public String lvsShowAddPromotionForm(Model model) {
        model.addAttribute("LvsPromotion", new LvsPromotion());
        model.addAttribute("LvsDiscountTypes", LvsPromotion.LvsDiscountType.values());

        return "LvsAreas/LvsAdmin/LvsPromotion/LvsCreate";
    }

    @PostMapping("/LvsAdd")
    public String lvsAddPromotion(@ModelAttribute LvsPromotion lvsPromotion, Model model) {
        try {
            lvsPromotion.setLvsIsActive(true);
            LvsPromotion lvsSavedPromotion = lvsPromotionService.lvsSavePromotion(lvsPromotion);

            model.addAttribute("LvsSuccess", "Thêm khuyến mãi thành công!");
            return "redirect:/LvsAdmin/LvsPromotion/LvsDetail/" + lvsSavedPromotion.getLvsPromotionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsPromotion/LvsCreate";
        }
    }

    @GetMapping("/LvsDetail/{id}")
    public String lvsViewPromotionDetail(@PathVariable Integer id, Model model) {
        LvsPromotion lvsPromotion = lvsPromotionService.lvsGetPromotionById(id);
        if (lvsPromotion == null) {
            return "redirect:/LvsAdmin/LvsPromotion/LvsList";
        }

        model.addAttribute("LvsPromotion", lvsPromotion);
        return "LvsAreas/LvsAdmin/LvsPromotion/LvsDetail";
    }

    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditPromotionForm(@PathVariable Integer id, Model model) {
        LvsPromotion lvsPromotion = lvsPromotionService.lvsGetPromotionById(id);
        if (lvsPromotion == null) {
            return "redirect:/LvsAdmin/LvsPromotion/LvsList";
        }

        model.addAttribute("LvsPromotion", lvsPromotion);
        model.addAttribute("LvsDiscountTypes", LvsPromotion.LvsDiscountType.values());

        return "LvsAreas/LvsAdmin/LvsPromotion/LvsEdit";
    }

    @PostMapping("/LvsEdit/{id}")
    public String lvsEditPromotion(@PathVariable Integer id, @ModelAttribute LvsPromotion lvsPromotion, Model model) {
        try {
            lvsPromotion.setLvsPromotionId(id);
            lvsPromotionService.lvsSavePromotion(lvsPromotion);

            model.addAttribute("LvsSuccess", "Cập nhật khuyến mãi thành công!");
            return "redirect:/LvsAdmin/LvsPromotion/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsPromotion/LvsEdit";
        }
    }

    @PostMapping("/LvsDelete/{id}")
    public String lvsDeletePromotion(@PathVariable Integer id, Model model) {
        try {
            lvsPromotionService.lvsDeletePromotion(id);
            model.addAttribute("LvsSuccess", "Đã xóa khuyến mãi!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPromotion/LvsList";
    }

    @PostMapping("/LvsToggleActive/{id}")
    public String lvsTogglePromotionActive(@PathVariable Integer id) {
        lvsPromotionService.lvsTogglePromotionActive(id);
        return "redirect:/LvsAdmin/LvsPromotion/LvsDetail/" + id;
    }

    @GetMapping("/LvsCheckCode")
    @ResponseBody
    public String lvsCheckPromotionCode(@RequestParam String lvsCode) {
        boolean lvsExists = lvsPromotionService.lvsCheckPromotionCodeExists(lvsCode);
        return "{\"exists\": " + lvsExists + "}";
    }

    @GetMapping("/LvsStatistics/{id}")
    public String lvsViewPromotionStatistics(@PathVariable Integer id, Model model) {
        LvsPromotion lvsPromotion = lvsPromotionService.lvsGetPromotionById(id);
        if (lvsPromotion == null) {
            return "redirect:/LvsAdmin/LvsPromotion/LvsList";
        }

        int lvsUsageCount = lvsPromotion.getLvsUsedCount();
        int lvsUsageLimit = lvsPromotion.getLvsUsageLimit();
        double lvsUsageRate = (lvsUsageLimit > 0) ? (double) lvsUsageCount / lvsUsageLimit * 100 : 0;

        model.addAttribute("LvsPromotion", lvsPromotion);
        model.addAttribute("LvsUsageCount", lvsUsageCount);
        model.addAttribute("LvsUsageLimit", lvsUsageLimit);
        model.addAttribute("LvsUsageRate", lvsUsageRate);

        return "LvsAreas/LvsAdmin/LvsPromotion/LvsStatistics";
    }
}
