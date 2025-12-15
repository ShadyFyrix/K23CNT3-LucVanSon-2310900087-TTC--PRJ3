package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controller quản lý khuyến mãi cho LvsAdmin
 * Xử lý thêm, sửa, xóa mã khuyến mãi
 */
@Controller
@RequestMapping("/LvsAdmin/LvsPromotion")
public class LvsAdminPromotionController {

    @Autowired
    private LvsPromotionService lvsPromotionService;

    @Autowired
    private LvsUserService lvsUserService;

    // Danh sách khuyến mãi
    @GetMapping("/LvsList")
    public String lvsListPromotions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean lvsIsActive,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsPromotion> lvsPromotions;

        if (lvsIsActive != null) {
            lvsPromotions = lvsPromotionService.lvsGetPromotionsByActive(lvsIsActive, lvsPageable);
        } else {
            lvsPromotions = lvsPromotionService.lvsGetAllPromotions(lvsPageable);
        }

        model.addAttribute("LvsPromotions", lvsPromotions);
        model.addAttribute("LvsIsActive", lvsIsActive);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsPromotionList";
    }

    // Thêm khuyến mãi mới
    @GetMapping("/LvsAdd")
    public String lvsShowAddPromotionForm(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsPromotion", new LvsPromotion());
        model.addAttribute("LvsDiscountTypes", LvsPromotion.LvsDiscountType.values());

        return "LvsAdmin/LvsPromotionAdd";
    }

    // Xử lý thêm khuyến mãi
    @PostMapping("/LvsAdd")
    public String lvsAddPromotion(@ModelAttribute LvsPromotion lvsPromotion,
                                  HttpSession session,
                                  Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPromotion.setLvsIsActive(true);
            LvsPromotion lvsSavedPromotion = lvsPromotionService.lvsSavePromotion(lvsPromotion);

            model.addAttribute("LvsSuccess", "Thêm khuyến mãi thành công!");
            return "redirect:/LvsAdmin/LvsPromotion/LvsDetail/" + lvsSavedPromotion.getLvsPromotionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsPromotionAdd";
        }
    }

    // Xem chi tiết khuyến mãi
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewPromotionDetail(@PathVariable Integer id,
                                         Model model,
                                         HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPromotion lvsPromotion = lvsPromotionService.lvsGetPromotionById(id);

        if (lvsPromotion == null) {
            return "redirect:/LvsAdmin/LvsPromotion/LvsList";
        }

        model.addAttribute("LvsPromotion", lvsPromotion);

        return "LvsAdmin/LvsPromotionDetail";
    }

    // Chỉnh sửa khuyến mãi
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditPromotionForm(@PathVariable Integer id,
                                           Model model,
                                           HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPromotion lvsPromotion = lvsPromotionService.lvsGetPromotionById(id);

        if (lvsPromotion == null) {
            return "redirect:/LvsAdmin/LvsPromotion/LvsList";
        }

        model.addAttribute("LvsPromotion", lvsPromotion);
        model.addAttribute("LvsDiscountTypes", LvsPromotion.LvsDiscountType.values());

        return "LvsAdmin/LvsPromotionEdit";
    }

    // Xử lý chỉnh sửa khuyến mãi
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditPromotion(@PathVariable Integer id,
                                   @ModelAttribute LvsPromotion lvsPromotion,
                                   HttpSession session,
                                   Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPromotion.setLvsPromotionId(id);
            lvsPromotionService.lvsSavePromotion(lvsPromotion);

            model.addAttribute("LvsSuccess", "Cập nhật khuyến mãi thành công!");
            return "redirect:/LvsAdmin/LvsPromotion/LvsDetail/" + id;
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsPromotionEdit";
        }
    }

    // Xóa khuyến mãi
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeletePromotion(@PathVariable Integer id,
                                     HttpSession session,
                                     Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsPromotionService.lvsDeletePromotion(id);
            model.addAttribute("LvsSuccess", "Đã xóa khuyến mãi!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsPromotion/LvsList";
    }

    // Kích hoạt/vô hiệu hóa khuyến mãi
    @PostMapping("/LvsToggleActive/{id}")
    public String lvsTogglePromotionActive(@PathVariable Integer id,
                                           HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsPromotionService.lvsTogglePromotionActive(id);

        return "redirect:/LvsAdmin/LvsPromotion/LvsDetail/" + id;
    }

    // Kiểm tra mã khuyến mãi
    @GetMapping("/LvsCheckCode")
    @ResponseBody
    public String lvsCheckPromotionCode(@RequestParam String lvsCode) {
        boolean lvsExists = lvsPromotionService.lvsCheckPromotionCodeExists(lvsCode);
        return "{\"exists\": " + lvsExists + "}";
    }

    // Xem thống kê sử dụng khuyến mãi
    @GetMapping("/LvsStatistics/{id}")
    public String lvsViewPromotionStatistics(@PathVariable Integer id,
                                             Model model,
                                             HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsPromotion lvsPromotion = lvsPromotionService.lvsGetPromotionById(id);

        if (lvsPromotion == null) {
            return "redirect:/LvsAdmin/LvsPromotion/LvsList";
        }

        // Lấy thống kê sử dụng
        int lvsUsageCount = lvsPromotion.getLvsUsedCount();
        int lvsUsageLimit = lvsPromotion.getLvsUsageLimit();
        double lvsUsageRate = (lvsUsageLimit > 0) ?
                (double) lvsUsageCount / lvsUsageLimit * 100 : 0;

        // Lấy danh sách đơn hàng sử dụng khuyến mãi
        // List<LvsOrder> lvsOrders = lvsPromotionService.lvsGetOrdersByPromotion(id);

        model.addAttribute("LvsPromotion", lvsPromotion);
        model.addAttribute("LvsUsageCount", lvsUsageCount);
        model.addAttribute("LvsUsageLimit", lvsUsageLimit);
        model.addAttribute("LvsUsageRate", lvsUsageRate);
        // model.addAttribute("LvsOrders", lvsOrders);

        return "LvsAdmin/LvsPromotionStatistics";
    }
}