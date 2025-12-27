package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import k23cnt3.lucvanson.project3.LvsRepository.LvsCartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;

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

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Autowired
    private LvsCartItemRepository lvsCartItemRepository;

    @Autowired
    private LvsFollowService lvsFollowService;

    @Autowired
    private LvsOrderService lvsOrderService;

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

        // Recalculate totals to ensure they're up-to-date
        lvsCartService.lvsCalculateCartTotal(lvsCart);
        lvsCartService.lvsSaveCart(lvsCart);

        model.addAttribute("LvsCart", lvsCart);
        model.addAttribute("LvsCartItems", lvsCartItems);
        if (lvsCart.getLvsPromotion() != null) {
            model.addAttribute("LvsAppliedPromotion", lvsCart.getLvsPromotion());
        }
        // Get followers for gift recipient selection
        try {
            org.springframework.data.domain.Page<LvsUser> followersPage = lvsFollowService.lvsGetFollowers(
                    lvsCurrentUser.getLvsUserId(),
                    org.springframework.data.domain.PageRequest.of(0, 100));
            java.util.List<LvsUser> followers = followersPage.getContent();
            model.addAttribute("LvsFollowers", followers);
        } catch (Exception e) {
            // If followers loading fails, set empty list
            model.addAttribute("LvsFollowers", new java.util.ArrayList<>());
        }
        return "LvsAreas/LvsUsers/LvsCartView";
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/LvsAddItem")
    public String lvsAddToCart(@RequestParam Long lvsProjectId,
            @RequestParam(defaultValue = "1") Integer lvsQuantity,
            @RequestParam(defaultValue = "false") Boolean lvsAutoSelect, // For Buy Now
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsLogin";
        }

        try {
            LvsCartItem addedItem = lvsCartService.lvsAddToCart(
                    lvsCurrentUser.getLvsUserId(),
                    lvsProjectId,
                    lvsQuantity);

            // If auto-select (from Buy Now button)
            if (lvsAutoSelect && addedItem != null) {
                // Unselect all other items first
                lvsCartService.lvsUnselectAllItems(lvsCurrentUser.getLvsUserId());
                // Then select only this new item
                lvsCartService.lvsToggleCartItemSelection(
                        addedItem.getLvsCartItemId(),
                        true);
            }

            redirectAttributes.addFlashAttribute("success", "Added to cart successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
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
            RedirectAttributes redirectAttributes) { // ✅ Changed from Model to RedirectAttributes
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            boolean lvsSuccess = lvsCartService.lvsApplyPromotion(
                    lvsCurrentUser.getLvsUserId(), lvsPromotionCode);

            if (lvsSuccess) {
                redirectAttributes.addFlashAttribute("success", "Áp dụng mã khuyến mãi thành công!"); // ✅ Use
                                                                                                      // addFlashAttribute
            } else {
                redirectAttributes.addFlashAttribute("error", "Mã khuyến mãi không hợp lệ!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("promotionError", e.getMessage()); // ✅ Show specific error
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
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
    /**
     * TEST ENDPOINT - Verify routing works
     */
    @PostMapping("/LvsCheckoutTest")
    public String lvsCheckoutTest(HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("========================================");
        System.out.println("TEST ENDPOINT CALLED!");
        System.out.println("========================================");
        redirectAttributes.addFlashAttribute("success", "Test endpoint works!");
        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    @PostMapping("/LvsProcessCheckout")
    public String lvsProcessCheckout(HttpSession session) {
        System.out.println("========================================");
        System.out.println("CHECKOUT METHOD CALLED!");
        System.out.println("========================================");

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            System.out.println("DEBUG: No current user in session");
            return "redirect:/LvsLogin";
        }

        try {
            System.out.println("DEBUG: Starting checkout for user " + lvsCurrentUser.getLvsUserId());

            // Kiểm tra số dư coin
            LvsCart lvsCart = lvsCartService.lvsGetCartByUserId(lvsCurrentUser.getLvsUserId());
            System.out.println("DEBUG: Cart loaded: " + (lvsCart != null));

            // Null check for final price
            Double finalPrice = lvsCart.getLvsFinalPrice();
            System.out.println("DEBUG: Final price: " + finalPrice);
            if (finalPrice == null) {
                finalPrice = 0.0;
            }

            if (finalPrice > lvsCurrentUser.getLvsCoin()) {
                session.setAttribute("errorMessage", "Insufficient coins!");
                return "redirect:/LvsUser/LvsCart/LvsView";
            }

            System.out.println("DEBUG: Calling lvsCheckout...");
            System.out.println("DEBUG: User ID: " + lvsCurrentUser.getLvsUserId());
            System.out.println("DEBUG: Cart final price: " + finalPrice);

            // Tạo đơn hàng từ giỏ hàng
            LvsOrder lvsOrder = lvsCartService.lvsCheckout(lvsCurrentUser.getLvsUserId());
            System.out.println("DEBUG: Order created: " + (lvsOrder != null));

            if (lvsOrder != null) {
                // Process payment (deduct coins, add revenue, create transaction)
                System.out.println("DEBUG: Processing payment for order " + lvsOrder.getLvsOrderId());
                boolean paymentSuccess = lvsOrderService.lvsProcessPayment(lvsOrder.getLvsOrderId());
                System.out.println("DEBUG: Payment success: " + paymentSuccess);

                if (!paymentSuccess) {
                    session.setAttribute("errorMessage", "Payment processing failed!");
                    return "redirect:/LvsUser/LvsCart/LvsView";
                }

                // Update session with new coin balance
                LvsUser lvsUpdatedUser = lvsUserRepository.findById(lvsCurrentUser.getLvsUserId()).orElse(null);
                if (lvsUpdatedUser != null) {
                    session.setAttribute("LvsCurrentUser", lvsUpdatedUser);
                }

                session.setAttribute("successMessage", "Checkout successful! Order #" + lvsOrder.getLvsOrderCode());
                return "redirect:/LvsUser/LvsOrder/LvsDetail/" + lvsOrder.getLvsOrderId();
            } else {
                session.setAttribute("errorMessage", "Checkout failed!");
                return "redirect:/LvsUser/LvsCart/LvsView";
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log to console
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            System.out.println("DEBUG: Exception caught: " + errorMsg);
            session.setAttribute("errorMessage", "Error: " + errorMsg);
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

    // Thêm sản phẩm vào giỏ hàng như quà tặng
    @PostMapping("/LvsAddGiftItem")
    public String lvsAddGiftItem(
            @RequestParam Long lvsProjectId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsLogin";
        }
        try {
            // Add to cart
            LvsCartItem item = lvsCartService.lvsAddToCart(
                    lvsCurrentUser.getLvsUserId(),
                    lvsProjectId,
                    1);
            // Mark as gift
            item.setLvsIsGift(true);
            lvsCartItemRepository.save(item);

            // Recalculate cart total to include the gift item
            LvsCart cart = lvsCartService.lvsGetCartByUserId(lvsCurrentUser.getLvsUserId());
            lvsCartService.lvsCalculateCartTotal(cart);
            lvsCartService.lvsSaveCart(cart); // Save the updated totals

            redirectAttributes.addFlashAttribute("success", "Added to cart as gift!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/LvsUser/LvsCart/LvsView";
    }

    // Đặt người nhận quà
    @PostMapping("/LvsSetRecipient")
    public String lvsSetRecipient(
            @RequestParam Long lvsCartItemId,
            @RequestParam Long lvsRecipientId,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsLogin";
        }
        LvsCartItem item = lvsCartItemRepository.findById(lvsCartItemId).orElse(null);
        LvsUser recipient = lvsUserRepository.findById(lvsRecipientId).orElse(null);
        if (item != null && recipient != null) {
            item.setLvsGiftRecipient(recipient);
            lvsCartItemRepository.save(item);
        }
        return "redirect:/LvsUser/LvsCart/LvsView";
    }
}