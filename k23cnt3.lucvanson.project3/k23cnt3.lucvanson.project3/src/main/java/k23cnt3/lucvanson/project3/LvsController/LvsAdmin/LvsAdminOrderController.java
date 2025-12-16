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

import java.util.List;

/**
 * Controller quản lý Đơn hàng (Order) trong Admin Panel
 * 
 * <p>
 * Chức năng chính:
 * </p>
 * <ul>
 * <li>Hiển thị danh sách đơn hàng với phân trang, tìm kiếm và lọc</li>
 * <li>Xem chi tiết đơn hàng và items</li>
 * <li>Cập nhật trạng thái đơn hàng</li>
 * <li>Hủy đơn hàng</li>
 * <li>Hoàn tiền</li>
 * <li>Xuất hóa đơn</li>
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
public class LvsAdminOrderController {

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Hiển thị danh sách đơn hàng
     */
    @GetMapping("/LvsList")
    public String lvsListOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model,
            HttpSession session) {

        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

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
     * Xem chi tiết đơn hàng
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewOrderDetail(@PathVariable Long id,
            Model model,
            HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

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
     * Cập nhật trạng thái đơn hàng
     */
    @PostMapping("/LvsUpdateStatus/{id}")
    public String lvsUpdateOrderStatus(@PathVariable Long id,
            @RequestParam LvsOrder.LvsOrderStatus lvsStatus,
            @RequestParam(required = false) String lvsNotes,
            HttpSession session,
            Model model) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsOrderService.lvsUpdateOrderStatus(id, lvsStatus, lvsNotes);
            model.addAttribute("LvsSuccess", "Đã cập nhật trạng thái đơn hàng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
    }

    /**
     * Hủy đơn hàng
     */
    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelOrder(@PathVariable Long id,
            @RequestParam String lvsReason,
            HttpSession session,
            Model model) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsOrderService.lvsCancelOrderByAdmin(id, lvsReason);
            model.addAttribute("LvsSuccess", "Đã hủy đơn hàng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
    }

    /**
     * Hoàn tiền đơn hàng
     */
    @PostMapping("/LvsRefund/{id}")
    public String lvsRefundOrder(@PathVariable Long id,
            @RequestParam Double lvsAmount,
            @RequestParam String lvsReason,
            HttpSession session,
            Model model) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsOrderService.lvsRefundOrder(id, lvsAmount, lvsReason, lvsAdmin.getLvsUserId());

            model.addAttribute("LvsSuccess", "Đã hoàn tiền đơn hàng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
    }

    /**
     * Xuất hóa đơn
     */
    @GetMapping("/LvsInvoice/{id}")
    public String lvsGenerateInvoice(@PathVariable Long id,
            Model model,
            HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);

        if (lvsOrder == null) {
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }

        model.addAttribute("LvsOrder", lvsOrder);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsInvoice";
    }

    /**
     * Tìm kiếm đơn hàng theo user
     */
    @GetMapping("/LvsSearchByUser")
    public String lvsSearchOrdersByUser(@RequestParam Long lvsUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model,
            HttpSession session) {
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsOrder> lvsOrders = lvsOrderService.lvsGetOrdersByUser(lvsUserId, lvsPageable);

        LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);

        model.addAttribute("LvsOrders", lvsOrders);
        model.addAttribute("LvsUser", lvsUser);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsUser";
    }
}