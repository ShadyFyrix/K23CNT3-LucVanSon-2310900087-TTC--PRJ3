package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/LvsAdmin/LvsOrder")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminOrderController {

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsTransactionService lvsTransactionService;

    /**
     * Hiển thị danh sách đơn hàng với phân trang
     */
    @GetMapping("/LvsList")
    public String lvsListOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsKeyword,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<LvsOrder> lvsOrders;

            if (lvsKeyword != null && !lvsKeyword.trim().isEmpty()) {
                lvsOrders = lvsOrderService.lvsSearchOrders(lvsKeyword, pageable);
                model.addAttribute("lvsKeyword", lvsKeyword);
            } else {
                lvsOrders = lvsOrderService.lvsGetAllOrders(pageable);
            }

            model.addAttribute("lvsOrders", lvsOrders);
            model.addAttribute("lvsCurrentPage", page);
            return "LvsAreas/LvsAdmin/LvsOrder/LvsList";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsOrder/LvsList";
        }
    }

    /**
     * Hiển thị chi tiết đơn hàng
     */
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewOrderDetail(@PathVariable Long id, Model model) {
        try {
            LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);
            if (lvsOrder == null) {
                model.addAttribute("LvsError", "Không tìm thấy đơn hàng!");
                return "redirect:/LvsAdmin/LvsOrder/LvsList";
            }
            model.addAttribute("lvsOrder", lvsOrder);
            return "LvsAreas/LvsAdmin/LvsOrder/LvsDetail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }
    }

    /**
     * Hiển thị form chỉnh sửa đơn hàng
     */
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditForm(@PathVariable Long id, Model model) {
        try {
            LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);
            if (lvsOrder == null) {
                model.addAttribute("LvsError", "Không tìm thấy đơn hàng!");
                return "redirect:/LvsAdmin/LvsOrder/LvsList";
            }
            model.addAttribute("lvsOrder", lvsOrder);
            return "LvsAreas/LvsAdmin/LvsOrder/LvsEdit";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }
    }

    /**
     * Xử lý cập nhật đơn hàng
     */
    @PostMapping("/LvsEdit/{id}")
    public String lvsUpdateOrder(
            @PathVariable Long id,
            @RequestParam String lvsStatus,
            @RequestParam(required = false) String lvsNotes,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);
            if (lvsOrder == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Không tìm thấy đơn hàng!");
                return "redirect:/LvsAdmin/LvsOrder/LvsList";
            }

            LvsOrder.LvsOrderStatus oldStatus = lvsOrder.getLvsStatus();
            LvsOrder.LvsOrderStatus newStatus = LvsOrder.LvsOrderStatus.valueOf(lvsStatus);

            lvsOrder.setLvsStatus(newStatus);
            lvsOrder.setLvsNotes(lvsNotes);

            // Nếu chuyển từ PENDING sang COMPLETED, trừ tiền
            if (oldStatus != LvsOrder.LvsOrderStatus.COMPLETED &&
                    newStatus == LvsOrder.LvsOrderStatus.COMPLETED) {
                LvsUser lvsBuyer = lvsOrder.getLvsBuyer();
                Double currentCoin = lvsBuyer.getLvsCoin();
                if (currentCoin == null)
                    currentCoin = 0.0;

                if (currentCoin < lvsOrder.getLvsFinalAmount()) {
                    redirectAttributes.addFlashAttribute("LvsError",
                            "Khách hàng không đủ coin! Cần: " + lvsOrder.getLvsFinalAmount() +
                                    " coins, Có: " + currentCoin + " coins");
                    return "redirect:/LvsAdmin/LvsOrder/LvsEdit/" + id;
                }

                // Xử lý thanh toán qua Transaction Service
                // Tạo PURCHASE transaction cho người mua
                lvsTransactionService.lvsProcessOrderPayment(lvsOrder.getLvsOrderId(), lvsBuyer.getLvsUserId());

                // Tạo SALE transaction cho từng người bán
                for (LvsOrderItem item : lvsOrder.getLvsOrderItems()) {
                    LvsUser projectOwner = item.getLvsProject().getLvsUser();
                    lvsTransactionService.lvsProcessSaleTransaction(lvsOrder.getLvsOrderId(),
                            projectOwner.getLvsUserId());
                }

                // Đánh dấu đơn hàng đã thanh toán
                lvsOrder.setLvsIsPaid(true);
            }

            // Nếu chuyển sang REFUNDED, hoàn tiền
            if (newStatus == LvsOrder.LvsOrderStatus.REFUNDED) {
                // Kiểm tra đơn hàng đã thanh toán chưa
                if (!lvsOrder.getLvsIsPaid()) {
                    redirectAttributes.addFlashAttribute("LvsError",
                            "Không thể hoàn tiền cho đơn hàng chưa thanh toán!");
                    return "redirect:/LvsAdmin/LvsOrder/LvsEdit/" + id;
                }

                // Kiểm tra không hoàn tiền 2 lần
                if (oldStatus == LvsOrder.LvsOrderStatus.REFUNDED) {
                    redirectAttributes.addFlashAttribute("LvsError",
                            "Đơn hàng này đã được hoàn tiền rồi!");
                    return "redirect:/LvsAdmin/LvsOrder/LvsEdit/" + id;
                }

                // Xử lý hoàn tiền qua Transaction Service
                LvsUser lvsBuyer = lvsOrder.getLvsBuyer();

                // Tạo REFUND transactions cho từng người bán
                for (LvsOrderItem item : lvsOrder.getLvsOrderItems()) {
                    LvsUser projectOwner = item.getLvsProject().getLvsUser();
                    String refundReason = lvsNotes != null ? lvsNotes : "Hoàn tiền theo yêu cầu";
                    lvsTransactionService.lvsProcessRefundTransaction(
                            lvsOrder.getLvsOrderId(),
                            lvsBuyer.getLvsUserId(),
                            projectOwner.getLvsUserId(),
                            refundReason);
                }

                redirectAttributes.addFlashAttribute("LvsSuccess",
                        "Đã hoàn " + lvsOrder.getLvsFinalAmount() + " coins về tài khoản " + lvsBuyer.getLvsUsername());
            }

            lvsOrderService.lvsUpdateOrder(lvsOrder);

            // Chỉ thêm thông báo success chung nếu chưa có thông báo success cụ thể
            if (newStatus != LvsOrder.LvsOrderStatus.REFUNDED) {
                redirectAttributes.addFlashAttribute("LvsSuccess", "Cập nhật đơn hàng thành công!");
            }

            return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsOrder/LvsEdit/" + id;
        }
    }

    /**
     * Xóa đơn hàng
     */
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            LvsOrder lvsOrder = lvsOrderService.lvsGetOrderById(id);
            if (lvsOrder == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Không tìm thấy đơn hàng!");
                return "redirect:/LvsAdmin/LvsOrder/LvsList";
            }

            // Nếu đơn hàng đã COMPLETED, hoàn lại tiền
            if (lvsOrder.getLvsStatus() == LvsOrder.LvsOrderStatus.COMPLETED) {
                LvsUser lvsBuyer = lvsOrder.getLvsBuyer();
                Double currentCoin = lvsBuyer.getLvsCoin();
                if (currentCoin == null)
                    currentCoin = 0.0;
                lvsBuyer.setLvsCoin(currentCoin + lvsOrder.getLvsFinalAmount());
                lvsUserService.lvsUpdateUser(lvsBuyer);

                // Trừ doanh thu từ chủ project
                for (LvsOrderItem item : lvsOrder.getLvsOrderItems()) {
                    LvsUser projectOwner = item.getLvsProject().getLvsUser();
                    Double ownerRevenue = projectOwner.getLvsBalance();
                    if (ownerRevenue == null)
                        ownerRevenue = 0.0;
                    projectOwner.setLvsBalance(ownerRevenue - item.getLvsItemTotal());
                    lvsUserService.lvsUpdateUser(projectOwner);
                }
            }

            lvsOrderService.lvsDeleteOrder(id);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Xóa đơn hàng thành công!");
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi khi xóa: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }
    }

    /**
     * Hiển thị form tạo đơn hàng mới
     */
    @GetMapping("/LvsCreate")
    public String lvsShowCreateForm(Model model) {
        // Lấy danh sách users - pass Page object
        Pageable lvsUserPageable = PageRequest.of(0, 1000);
        Page<LvsUser> lvsUsers = lvsUserService.lvsGetAllUsers(lvsUserPageable);

        // Lấy danh sách projects với eager loading để tránh LazyInitializationException
        Pageable lvsProjectPageable = PageRequest.of(0, 1000);
        Page<LvsProject> lvsProjects = lvsProjectService.lvsGetAllProjectsWithCategoryAndUser(lvsProjectPageable);

        // Pass Page objects directly, template will use .content
        model.addAttribute("lvsUsers", lvsUsers);
        model.addAttribute("lvsProjects", lvsProjects);

        return "LvsAreas/LvsAdmin/LvsOrder/LvsCreate";
    }

    /**
     * Xử lý tạo đơn hàng mới - Patreon-style (multiple projects)
     */
    @PostMapping("/LvsCreate")
    public String lvsCreateOrder(
            @RequestParam Long lvsBuyerId,
            @RequestParam(required = false) java.util.List<Long> lvsProjectIds,
            @RequestParam(required = false) String lvsNotes,
            @RequestParam String lvsStatus,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Validate: at least one project selected
            if (lvsProjectIds == null || lvsProjectIds.isEmpty()) {
                model.addAttribute("LvsError", "Vui lòng chọn ít nhất một dự án!");
                return lvsShowCreateForm(model);
            }

            // Get buyer
            LvsUser lvsBuyer = lvsUserService.lvsGetUserById(lvsBuyerId);
            if (lvsBuyer == null) {
                model.addAttribute("LvsError", "Không tìm thấy khách hàng!");
                return lvsShowCreateForm(model);
            }

            // Create order
            LvsOrder lvsOrder = new LvsOrder();
            lvsOrder.setLvsBuyer(lvsBuyer);
            lvsOrder.setLvsNotes(lvsNotes);
            lvsOrder.setLvsStatus(LvsOrder.LvsOrderStatus.valueOf(lvsStatus));
            lvsOrder.setLvsOrderCode("ORD-" + System.currentTimeMillis());

            // Create order items for each selected project
            java.util.List<LvsOrderItem> lvsOrderItems = new java.util.ArrayList<>();
            double lvsTotal = 0.0;

            for (Long lvsProjectId : lvsProjectIds) {
                LvsProject lvsProject = lvsProjectService.lvsGetProjectById(lvsProjectId);
                if (lvsProject == null) {
                    model.addAttribute("LvsError", "Không tìm thấy dự án ID: " + lvsProjectId);
                    return lvsShowCreateForm(model);
                }

                // Create order item (quantity always = 1 for ownership purchase)
                LvsOrderItem lvsOrderItem = new LvsOrderItem();
                lvsOrderItem.setLvsOrder(lvsOrder);
                lvsOrderItem.setLvsProject(lvsProject);
                lvsOrderItem.setLvsQuantity(1); // Always 1 for project ownership
                // ✅ USE FINAL PRICE (with discount if active)
                lvsOrderItem.setLvsUnitPrice(lvsProject.getLvsFinalPrice());
                lvsOrderItem.setLvsItemTotal(lvsProject.getLvsFinalPrice());

                lvsOrderItems.add(lvsOrderItem);
                lvsTotal += lvsProject.getLvsFinalPrice();
            }

            lvsOrder.setLvsOrderItems(lvsOrderItems);
            lvsOrder.setLvsTotalAmount(lvsTotal);
            lvsOrder.setLvsFinalAmount(lvsTotal);

            // Check coin balance if status is COMPLETED
            LvsOrder.LvsOrderStatus orderStatus = LvsOrder.LvsOrderStatus.valueOf(lvsStatus);
            if (orderStatus == LvsOrder.LvsOrderStatus.COMPLETED) {
                Double currentCoin = lvsBuyer.getLvsCoin();
                if (currentCoin == null)
                    currentCoin = 0.0;

                if (currentCoin < lvsTotal) {
                    model.addAttribute("LvsError",
                            "Khách hàng không đủ coin! Cần: " + lvsTotal + " coins, Có: " + currentCoin + " coins");
                    return lvsShowCreateForm(model);
                }
            }

            // Save order
            LvsOrder lvsSavedOrder = lvsOrderService.lvsSaveOrder(lvsOrder);

            // Deduct coins and distribute revenue if status is COMPLETED
            if (orderStatus == LvsOrder.LvsOrderStatus.COMPLETED) {
                // Xử lý thanh toán qua Transaction Service
                // Tạo PURCHASE transaction cho người mua
                lvsTransactionService.lvsProcessOrderPayment(lvsSavedOrder.getLvsOrderId(), lvsBuyer.getLvsUserId());

                // Tạo SALE transaction cho từng người bán
                for (LvsOrderItem item : lvsOrderItems) {
                    LvsUser projectOwner = item.getLvsProject().getLvsUser();
                    lvsTransactionService.lvsProcessSaleTransaction(lvsSavedOrder.getLvsOrderId(),
                            projectOwner.getLvsUserId());
                }

                // Đánh dấu đơn hàng đã thanh toán
                lvsSavedOrder.setLvsIsPaid(true);
                lvsOrderService.lvsUpdateOrder(lvsSavedOrder);
            }

            redirectAttributes.addFlashAttribute("LvsSuccess",
                    "Tạo đơn hàng thành công! Mã đơn: " + lvsSavedOrder.getLvsOrderCode() +
                            " - " + lvsProjectIds.size() + " dự án");
            return "redirect:/LvsAdmin/LvsOrder/LvsDetail/" + lvsSavedOrder.getLvsOrderId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return lvsShowCreateForm(model);
        }
    }

    /**
     * Backfill revenue for existing completed orders
     * This fixes orders that were completed but never distributed revenue to
     * sellers
     */
    @GetMapping("/LvsBackfillRevenue")
    public String lvsBackfillRevenue(RedirectAttributes redirectAttributes) {
        try {
            // Get all completed and paid orders
            Pageable pageable = PageRequest.of(0, 1000); // Get up to 1000 orders
            Page<LvsOrder> allOrders = lvsOrderService.lvsGetAllOrders(pageable);

            int processedCount = 0;
            int skippedCount = 0;
            double totalRevenue = 0.0;

            for (LvsOrder order : allOrders.getContent()) {
                // Only process COMPLETED orders (regardless of lvsIsPaid flag for backfill)
                if (order.getLvsStatus() == LvsOrder.LvsOrderStatus.COMPLETED) {

                    // Process each order item
                    for (LvsOrderItem item : order.getLvsOrderItems()) {
                        // Try to get seller from item first, fallback to project owner
                        LvsUser projectOwner = item.getLvsSeller();
                        if (projectOwner == null && item.getLvsProject() != null) {
                            projectOwner = item.getLvsProject().getLvsUser();
                        }

                        if (projectOwner == null) {
                            continue; // Skip if no seller found
                        }

                        Double itemTotal = item.getLvsItemTotal();
                        if (itemTotal == null || itemTotal == 0.0) {
                            continue; // Skip items with no value
                        }

                        // Add revenue to project owner
                        Double currentBalance = projectOwner.getLvsBalance();
                        if (currentBalance == null)
                            currentBalance = 0.0;

                        projectOwner.setLvsBalance(currentBalance + itemTotal);
                        lvsUserService.lvsUpdateUser(projectOwner);

                        totalRevenue += itemTotal;
                    }

                    processedCount++;
                }
            }

            redirectAttributes.addFlashAttribute("LvsSuccess",
                    "Backfill hoàn tất! Đã xử lý " + processedCount + " đơn hàng. " +
                            "Tổng doanh thu phân phối: " + totalRevenue + " VND");

            return "redirect:/LvsAdmin/LvsOrder/LvsList";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("LvsError",
                    "Lỗi khi backfill revenue: " + e.getMessage());
            return "redirect:/LvsAdmin/LvsOrder/LvsList";
        }
    }
}
