package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Controller quản lý giỏ hàng cho người dùng
 * Xử lý thêm, xóa, cập nhật giỏ hàng
 */
@Controller
@RequestMapping("/LvsUser/LvsCart")
public class LvsUserCartController {

    @Autowired
    private LvsCartService lvsCartService;

    @Autowired
    private LvsPromotionService lvsPromotionService;

    @Autowired
    private LvsUserService lvsUserService;

    /**
     * Xem giỏ hàng của user hiện tại
     * Hiển thị danh sách sản phẩm trong giỏ, tổng tiền, khuyến mãi
     * 
     * @param model   Model để truyền dữ liệu
     * @param session HttpSession để lấy thông tin user
     * @return Template giỏ hàng, hoặc redirect đến login nếu chưa đăng nhập
     */
    @GetMapping("/LvsView")
    public String lvsViewCart(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsUser/LvsLogin";
        }

        LvsCart lvsCart = lvsCartService.lvsGetCartByUserId(lvsCurrentUser.getLvsUserId());

        if (lvsCart == null) {
            lvsCart = new LvsCart();
            lvsCart.setLvsUser(lvsCurrentUser);
            lvsCart = lvsCartService.lvsSaveCart(lvsCart);
        }

        List<LvsCartItem> lvsCartItems = lvsCart.getLvsCartItems();

        model.addAttribute("LvsCart", lvsCart);
        model.addAttribute("LvsCartItems", lvsCartItems);

        return "LvsAreas/LvsUsers/LvsCartView";
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/LvsAddItem")
    public String lvsAddToCart(@RequestParam Long lvsProjectId,
            @RequestParam(defaultValue = "1") Integer lvsQuantity,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsCartService.lvsAddToCart(lvsCurrentUser.getLvsUserId(), lvsProjectId, lvsQuantity);
            model.addAttribute("LvsSuccess", "Đã thêm vào giỏ hàng!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PostMapping("/LvsUpdateQuantity")
    public String lvsUpdateQuantity(@RequestParam Long lvsCartItemId,
            @RequestParam Integer lvsQuantity,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCartService.lvsUpdateCartItemQuantity(lvsCartItemId, lvsQuantity);

        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/LvsRemoveItem")
    public String lvsRemoveFromCart(@RequestParam Long lvsCartItemId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCartService.lvsRemoveCartItem(lvsCartItemId);

        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Chọn/bỏ chọn sản phẩm để thanh toán
    @PostMapping("/LvsToggleSelect")
    public String lvsToggleSelect(@RequestParam Long lvsCartItemId,
            @RequestParam Boolean lvsIsSelected,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCartService.lvsToggleCartItemSelection(lvsCartItemId, lvsIsSelected);

        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Áp dụng mã khuyến mãi
    @PostMapping("/LvsApplyPromotion")
    public String lvsApplyPromotion(@RequestParam String lvsPromotionCode,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            boolean lvsSuccess = lvsCartService.lvsApplyPromotion(
                    lvsCurrentUser.getLvsUserId(), lvsPromotionCode);

            if (lvsSuccess) {
                model.addAttribute("LvsSuccess", "Áp dụng mã khuyến mãi thành công!");
            } else {
                model.addAttribute("LvsError", "Mã khuyến mãi không hợp lệ!");
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", e.getMessage());
        }

        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Xóa mã khuyến mãi
    @PostMapping("/LvsRemovePromotion")
    public String lvsRemovePromotion(HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCartService.lvsRemovePromotion(lvsCurrentUser.getLvsUserId());

        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Thanh toán giỏ hàng
    @PostMapping("/LvsCheckout")
    public String lvsCheckout(HttpSession session, Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra số dư coin
            LvsCart lvsCart = lvsCartService.lvsGetCartByUserId(lvsCurrentUser.getLvsUserId());
            if (lvsCart.getLvsFinalPrice() > lvsCurrentUser.getLvsCoin()) {
                model.addAttribute("LvsError", "Số coin không đủ để thanh toán!");
                return "redirect:/LvsUser/LvsCart/LvsView";
            }

            // Tạo đơn hàng từ giỏ hàng
            LvsOrder lvsOrder = lvsCartService.lvsCheckout(lvsCurrentUser.getLvsUserId());

            if (lvsOrder != null) {
                model.addAttribute("LvsSuccess", "Thanh toán thành công!");
                return "redirect:/LvsUser/LvsOrder/LvsDetail/" + lvsOrder.getLvsOrderId();
            } else {
                model.addAttribute("LvsError", "Thanh toán thất bại!");
                return "redirect:/LvsUser/LvsCart/LvsView";
            }
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsUser/LvsCart/LvsView";
        }
    }

    // Xóa toàn bộ giỏ hàng
    @PostMapping("/LvsClear")
    public String lvsClearCart(HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        lvsCartService.lvsClearCart(lvsCurrentUser.getLvsUserId());

        return "redirect:/LvsUser/LvsCart/LvsView";
    }
}