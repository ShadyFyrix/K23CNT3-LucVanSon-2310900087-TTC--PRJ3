package k23cnt3.lucvanson.project3.LvsController.LvsUser;

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
 * Controller quản lý đơn hàng cho người dùng
 * Xử lý xem, thanh toán, hủy đơn hàng
 */
@Controller
@RequestMapping("/LvsUser/LvsOrder")
public class LvsUserOrderController {

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsTransactionService lvsTransactionService;

    // Xem danh sách đơn hàng của tôi
    @GetMapping("/LvsMyOrders")
    public String lvsViewMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String lvsStatus,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsOrder> lvsOrders;

        if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsOrders = lvsOrderService.lvsGetOrdersByUserAndStatus(
                    lvsCurrentUser.getLvsUserId(), lvsStatus, lvsPageable);
        } else {
            lvsOrders = lvsOrderService.lvsGetOrdersByUser(
                    lvsCurrentUser.getLvsUserId(), lvsPageable);
        }

        model.addAttribute("LvsOrders", lvsOrders);
        model.addAttribute("LvsStatuses", LvsOrder.LvsOrderStatus.values());
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsOrders/LvsMyOrders";
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewOrderDetail(@PathVariable Long id,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);

        // Kiểm tra quyền xem đơn hàng
        if (lvsOrder == null ||
                !lvsOrder.getLvsBuyer().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsOrder/LvsMyOrders";
        }

        List<LvsOrderItem> lvsOrderItems = lvsOrder.getLvsOrderItems();

        model.addAttribute("LvsOrder", lvsOrder);
        model.addAttribute("LvsOrderItems", lvsOrderItems);

        return "LvsAreas/LvsUsers/LvsOrders/LvsOrderDetail";
    }

    // Mua ngay (tạo đơn hàng trực tiếp)
    @PostMapping("/LvsBuyNow")
    public String lvsBuyNow(@RequestParam Long lvsProjectId,
            @RequestParam(defaultValue = "1") Integer lvsQuantity,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra số dư coin
            Double lvsRequiredCoin = lvsOrderService.lvsCalculateOrderTotal(
                    lvsCurrentUser.getLvsUserId(), lvsProjectId, lvsQuantity);

            if (lvsRequiredCoin > lvsCurrentUser.getLvsCoin()) {
                model.addAttribute("LvsError", "Số coin không đủ để mua!");
                return "redirect:/LvsUser/LvsProject/LvsDetail/" + lvsProjectId;
            }

            // Tạo đơn hàng
            LvsOrder lvsOrder = lvsOrderService.lvsCreateOrderFromProject(
                    lvsCurrentUser.getLvsUserId(), lvsProjectId, lvsQuantity);

            if (lvsOrder != null) {
                // Thanh toán ngay
                boolean lvsPaymentSuccess = lvsOrderService.lvsProcessPayment(lvsOrder.getLvsOrderId());

                if (lvsPaymentSuccess) {
                    model.addAttribute("LvsSuccess", "Mua hàng thành công!");
                    return "redirect:/LvsUser/LvsOrder/LvsDetail/" + lvsOrder.getLvsOrderId();
                } else {
                    model.addAttribute("LvsError", "Thanh toán thất bại!");
                    return "redirect:/LvsUser/LvsProject/LvsDetail/" + lvsProjectId;
                }
            } else {
                model.addAttribute("LvsError", "Tạo đơn hàng thất bại!");
                return "redirect:/LvsUser/LvsProject/LvsDetail/" + lvsProjectId;
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsUser/LvsProject/LvsDetail/" + lvsProjectId;
        }
    }

    // Thanh toán đơn hàng
    @PostMapping("/LvsPay/{id}")
    public String lvsPayOrder(@PathVariable Long id,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            boolean lvsSuccess = lvsOrderService.lvsProcessPayment(id);

            if (lvsSuccess) {
                model.addAttribute("LvsSuccess", "Thanh toán thành công!");
            } else {
                model.addAttribute("LvsError", "Thanh toán thất bại!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsOrder/LvsDetail/" + id;
    }

    // Hủy đơn hàng
    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelOrder(@PathVariable Long id,
            @RequestParam(required = false) String lvsReason,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            boolean lvsSuccess = lvsOrderService.lvsCancelOrder(id, lvsReason);

            if (lvsSuccess) {
                model.addAttribute("LvsSuccess", "Hủy đơn hàng thành công!");
            } else {
                model.addAttribute("LvsError", "Không thể hủy đơn hàng!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsOrder/LvsDetail/" + id;
    }

    // Xác nhận đã nhận hàng (hoàn tất đơn hàng)
    @PostMapping("/LvsComplete/{id}")
    public String lvsCompleteOrder(@PathVariable Long id,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            boolean lvsSuccess = lvsOrderService.lvsCompleteOrder(id);

            if (lvsSuccess) {
                model.addAttribute("LvsSuccess", "Đã xác nhận nhận hàng!");
            } else {
                model.addAttribute("LvsError", "Không thể xác nhận!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:LvsAreas/LvsUser/LvsOrder/LvsDetail/" + id;
    }

    // Xuất hóa đơn PDF
    @GetMapping("/LvsInvoice/{id}")
    public String lvsGenerateInvoice(@PathVariable Long id,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);

        if (lvsOrder == null ||
                !lvsOrder.getLvsBuyer().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:LvsAreas/LvsUser/LvsOrder/LvsMyOrders";
        }

        model.addAttribute("LvsOrder", lvsOrder);

        return "LvsAreas/LvsUsers/LvsOrders/LvsInvoicePdf"; // Trả về view PDF
    }

    // Yêu cầu hoàn tiền
    @PostMapping("/LvsRequestRefund/{id}")
    public String lvsRequestRefund(@PathVariable Long id,
            @RequestParam String lvsReason,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            boolean lvsSuccess = lvsOrderService.lvsRequestRefund(id, lvsReason);

            if (lvsSuccess) {
                model.addAttribute("LvsSuccess", "Đã gửi yêu cầu hoàn tiền!");
            } else {
                model.addAttribute("LvsError", "Không thể gửi yêu cầu hoàn tiền!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsOrder/LvsDetail/" + id;
    }
}