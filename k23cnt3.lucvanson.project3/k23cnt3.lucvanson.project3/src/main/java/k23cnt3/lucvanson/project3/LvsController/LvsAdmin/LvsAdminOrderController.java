package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Controller quáº£n lÃ½ ÄÆ¡n hÃ ng (Order) trong Admin Panel
 * 
 * <p>
 * Chá»©c nÄƒng chÃ­nh:
 * </p>
 * <ul>
 * <li>Hiá»ƒn thá»‹ danh sÃ¡ch Ä‘Æ¡n hÃ ng vá»›i phÃ¢n trang, tÃ¬m kiáº¿m vÃ 
 * lá»c</li>
 * <li>Xem chi tiáº¿t Ä‘Æ¡n hÃ ng vÃ  items</li>
 * <li>Cáº­p nháº­t tráº¡ng thÃ¡i Ä‘Æ¡n hÃ ng</li>
 * <li>Há»§y Ä‘Æ¡n hÃ ng</li>
 * <li>HoÃ n tiá»n</li>
 * <li>Xuáº¥t hÃ³a Ä‘Æ¡n</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsOrder/LvsList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsOrder/LvsDetail.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsOrder")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminOrderController {

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Hiá»ƒn thá»‹ danh sÃ¡ch Ä‘Æ¡n hÃ ng
     */
    @GetMapping("/LvsList")
    public String lvsListOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model) {
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsOrder> lvsOrders;

        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsOrders = lvsOrderService.lvsSearchOrders(lvsKeyword, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsOrders = lvsOrderService.lvsGetOrdersByStatus(lvsStatus, lvsPageable);
        } else {
            lvsOrders = lvsOrderService.lvsGetAllOrders(lvsPageable);
        }

        model.addAttribute("LvsOrders", lvsOrders);
        model.addAttribute("LvsStatuses", LvsOrder.LvsOrderStatus.values());
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsList";
    }

    /**
     * Xem chi tiáº¿t Ä‘Æ¡n hÃ ng
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewOrderDetail(@PathVariable Long id,
            Model model) {
        LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);

        if (lvsOrder == null) {
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }

        List<LvsOrderItem> lvsOrderItems = lvsOrder.getLvsOrderItems();

        model.addAttribute("LvsOrder", lvsOrder);
        model.addAttribute("LvsOrderItems", lvsOrderItems);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsDetail";
    }

    /**
     * Cáº­p nháº­t tráº¡ng thÃ¡i Ä‘Æ¡n hÃ ng
     */
    @PostMapping("/LvsUpdateStatus/{id}")
    public String lvsUpdateOrderStatus(@PathVariable Long id,
            @RequestParam LvsOrder.LvsOrderStatus lvsStatus,
            @RequestParam(required = false) String lvsNotes,
            Model model) {
        try {
            lvsOrderService.lvsUpdateOrderStatus(id, lvsStatus, lvsNotes);
            model.addAttribute("LvsSuccess", "ÄÃ£ cáº­p nháº­t tráº¡ng thÃ¡i Ä‘Æ¡n hÃ ng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
    }

    /**
     * Há»§y Ä‘Æ¡n hÃ ng
     */
    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelOrder(@PathVariable Long id,
            @RequestParam String lvsReason,
            Model model) {
        try {
            lvsOrderService.lvsCancelOrderByAdmin(id, lvsReason);
            model.addAttribute("LvsSuccess", "ÄÃ£ há»§y Ä‘Æ¡n hÃ ng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
    }

    /**
     * HoÃ n tiá»n Ä‘Æ¡n hÃ ng
     */
    @PostMapping("/LvsRefund/{id}")
    public String lvsRefundOrder(@PathVariable Long id,
            @RequestParam Double lvsAmount,
            @RequestParam String lvsReason,
            Model model) {
        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsOrderService.lvsRefundOrder(id, lvsAmount, lvsReason, lvsAdmin.getLvsUserId());

            model.addAttribute("LvsSuccess", "ÄÃ£ hoÃ n tiá»n Ä‘Æ¡n hÃ ng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lá»—i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
    }

    /**
     * Xuáº¥t hÃ³a Ä‘Æ¡n
     */
    @GetMapping("/LvsInvoice/{id}")
    public String lvsGenerateInvoice(@PathVariable Long id,
            Model model) {
        LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);

        if (lvsOrder == null) {
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }

        model.addAttribute("LvsOrder", lvsOrder);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsInvoice";
    }

    /**
     * TÃ¬m kiáº¿m Ä‘Æ¡n hÃ ng theo user
     */
    @GetMapping("/LvsSearchByUser")
    public String lvsSearchOrdersByUser(@RequestParam Long lvsUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsOrder> lvsOrders = lvsOrderService.lvsGetOrdersByUser(lvsUserId, lvsPageable);

        LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);

        model.addAttribute("LvsOrders", lvsOrders);
        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsUser";
    }
}
