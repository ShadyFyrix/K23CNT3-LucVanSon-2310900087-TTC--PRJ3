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
 * Controller quản lý giao dịch cho LvsAdmin
 * Xử lý duyệt nạp/rút coin
 */
@Controller
@RequestMapping("/LvsAdmin/LvsTransaction")
public class LvsAdminTransactionController {

    @Autowired
    private LvsTransactionService lvsTransactionService;

    @Autowired
    private LvsUserService lvsUserService;

    // Danh sách giao dịch
    @GetMapping("/LvsList")
    public String lvsListTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String lvsType,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model,
            HttpSession session) {

        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsTransaction> lvsTransactions;

        if (lvsKeyword != null && !lvsKeyword.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsSearchTransactions(lvsKeyword, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty() && lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByTypeAndStatus(
                    lvsType, lvsStatus, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByType(lvsType, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByStatus(lvsStatus, lvsPageable);
        } else {
            lvsTransactions = lvsTransactionService.lvsGetAllTransactions(lvsPageable);
        }

        model.addAttribute("LvsTransactions", lvsTransactions);
        model.addAttribute("LvsTypes", LvsTransaction.LvsTransactionType.values());
        model.addAttribute("LvsStatuses", LvsTransaction.LvsTransactionStatus.values());
        model.addAttribute("LvsSelectedType", lvsType);
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsKeyword", lvsKeyword);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAdmin/LvsTransactionList";
    }

    // Xem chi tiết giao dịch
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewTransactionDetail(@PathVariable Long id,
                                           Model model,
                                           HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(id);

        if (lvsTransaction == null) {
            return "redirect:/LvsAdmin/LvsTransaction/LvsList";
        }

        model.addAttribute("LvsTransaction", lvsTransaction);

        return "LvsAdmin/LvsTransactionDetail";
    }

    // Duyệt giao dịch nạp coin
    @PostMapping("/LvsApproveDeposit/{id}")
    public String lvsApproveDeposit(@PathVariable Long id,
                                    HttpSession session,
                                    Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsTransactionService.lvsApproveDeposit(id, lvsAdmin.getLvsUserId());

            model.addAttribute("LvsSuccess", "Đã duyệt nạp tiền!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + id;
    }

    // Duyệt giao dịch rút coin
    @PostMapping("/LvsApproveWithdrawal/{id}")
    public String lvsApproveWithdrawal(@PathVariable Long id,
                                       HttpSession session,
                                       Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsTransactionService.lvsApproveWithdrawal(id, lvsAdmin.getLvsUserId());

            model.addAttribute("LvsSuccess", "Đã duyệt rút tiền!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + id;
    }

    // Từ chối giao dịch
    @PostMapping("/LvsReject/{id}")
    public String lvsRejectTransaction(@PathVariable Long id,
                                       @RequestParam String lvsReason,
                                       HttpSession session,
                                       Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsTransactionService.lvsRejectTransaction(id, lvsAdmin.getLvsUserId(), lvsReason);

            model.addAttribute("LvsSuccess", "Đã từ chối giao dịch!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + id;
    }

    // Hủy giao dịch
    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelTransaction(@PathVariable Long id,
                                       HttpSession session,
                                       Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsTransactionService.lvsCancelTransaction(id);
            model.addAttribute("LvsSuccess", "Đã hủy giao dịch!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsList";
    }

    // Thêm giao dịch thủ công (LvsAdmin)
    @GetMapping("/LvsAdd")
    public String lvsShowAddTransactionForm(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        List<LvsUser> lvsUsers = lvsUserService.lvsGetAllUsers();

        model.addAttribute("LvsTransaction", new LvsTransaction());
        model.addAttribute("LvsUsers", lvsUsers);
        model.addAttribute("LvsTransactionTypes", LvsTransaction.LvsTransactionType.values());

        return "LvsAdmin/LvsTransactionAdd";
    }

    // Xử lý thêm giao dịch
    @PostMapping("/LvsAdd")
    public String lvsAddTransaction(@ModelAttribute LvsTransaction lvsTransaction,
                                    @RequestParam Long lvsUserId,
                                    HttpSession session,
                                    Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");

            lvsTransaction.setLvsUser(lvsUser);
            lvsTransaction.setLvsAdminApprover(lvsAdmin);
            lvsTransaction.setLvsStatus(LvsTransaction.LvsTransactionStatus.SUCCESS);
            lvsTransaction.setLvsApprovedAt(java.time.LocalDateTime.now());

            LvsTransaction lvsSavedTransaction = lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            // Cập nhật số dư user
            if (lvsTransaction.getLvsType() == LvsTransaction.LvsTransactionType.DEPOSIT) {
                lvsUser.setLvsCoin(lvsUser.getLvsCoin() + lvsTransaction.getLvsAmount());
            } else if (lvsTransaction.getLvsType() == LvsTransaction.LvsTransactionType.WITHDRAWAL) {
                lvsUser.setLvsBalance(lvsUser.getLvsBalance() - lvsTransaction.getLvsAmount());
            }
            lvsUserService.lvsUpdateUser(lvsUser);

            model.addAttribute("LvsSuccess", "Thêm giao dịch thành công!");
            return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + lvsSavedTransaction.getLvsTransactionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsTransactionAdd";
        }
    }
}