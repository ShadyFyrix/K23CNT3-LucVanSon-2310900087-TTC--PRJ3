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
 * Controller quản lý giao dịch cho người dùng
 * Xử lý xem lịch sử giao dịch, nạp/rút coin
 */
@Controller
@RequestMapping("/LvsUser/LvsTransaction")
public class LvsUserTransactionController {

    @Autowired
    private LvsTransactionService lvsTransactionService;

    @Autowired
    private LvsUserService lvsUserService;

    // Xem lịch sử giao dịch
    @GetMapping("/LvsHistory")
    public String lvsViewTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lvsType,
            @RequestParam(required = false) String lvsStatus,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsTransaction> lvsTransactions;

        if (lvsType != null && !lvsType.isEmpty() && lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByUserAndTypeAndStatus(
                    lvsCurrentUser.getLvsUserId(), lvsType, lvsStatus, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByUserAndType(
                    lvsCurrentUser.getLvsUserId(), lvsType, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByUserAndStatus(
                    lvsCurrentUser.getLvsUserId(), lvsStatus, lvsPageable);
        } else {
            lvsTransactions = lvsTransactionService.lvsGetTransactionsByUser(
                    lvsCurrentUser.getLvsUserId(), lvsPageable);
        }

        model.addAttribute("LvsTransactions", lvsTransactions);
        model.addAttribute("LvsTypes", LvsTransaction.LvsTransactionType.values());
        model.addAttribute("LvsStatuses", LvsTransaction.LvsTransactionStatus.values());
        model.addAttribute("LvsSelectedType", lvsType);
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsUser/LvsTransactionHistory";
    }

    // Xem chi tiết giao dịch
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewTransactionDetail(@PathVariable Long id,
                                           HttpSession session,
                                           Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(id);

        if (lvsCurrentUser == null ||
                !lvsTransaction.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        }

        model.addAttribute("LvsTransaction", lvsTransaction);

        return "LvsUser/LvsTransactionDetail";
    }

    // Tạo yêu cầu nạp coin
    @GetMapping("/LvsDeposit")
    public String lvsShowDepositForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsTransaction", new LvsTransaction());

        return "LvsUser/LvsTransactionDeposit";
    }

    // Xử lý yêu cầu nạp coin
    @PostMapping("/LvsDeposit")
    public String lvsProcessDeposit(@RequestParam Double lvsAmount,
                                    @RequestParam String lvsPaymentMethod,
                                    @RequestParam(required = false) String lvsPaymentInfo,
                                    HttpSession session,
                                    Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            LvsTransaction lvsTransaction = lvsTransactionService.lvsCreateDepositRequest(
                    lvsCurrentUser.getLvsUserId(), lvsAmount, lvsPaymentMethod);

            lvsTransaction.setLvsPaymentInfo(lvsPaymentInfo);
            lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            model.addAttribute("LvsSuccess", "Đã tạo yêu cầu nạp tiền! Chờ LvsAdmin duyệt.");
            return "redirect:/LvsUser/LvsTransaction/LvsDetail/" + lvsTransaction.getLvsTransactionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsUser/LvsTransactionDeposit";
        }
    }

    // Tạo yêu cầu rút coin
    @GetMapping("/LvsWithdraw")
    public String lvsShowWithdrawForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsUser", lvsCurrentUser);
        model.addAttribute("LvsTransaction", new LvsTransaction());

        return "LvsUser/LvsTransactionWithdraw";
    }

    // Xử lý yêu cầu rút coin
    @PostMapping("/LvsWithdraw")
    public String lvsProcessWithdraw(@RequestParam Double lvsAmount,
                                     @RequestParam String lvsPaymentInfo,
                                     HttpSession session,
                                     Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            // Kiểm tra số dư
            if (lvsAmount > lvsCurrentUser.getLvsBalance()) {
                model.addAttribute("LvsError", "Số dư không đủ!");
                return "LvsUser/LvsTransactionWithdraw";
            }

            LvsTransaction lvsTransaction = lvsTransactionService.lvsCreateWithdrawalRequest(
                    lvsCurrentUser.getLvsUserId(), lvsAmount);

            lvsTransaction.setLvsPaymentInfo(lvsPaymentInfo);
            lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            model.addAttribute("LvsSuccess", "Đã tạo yêu cầu rút tiền! Chờ LvsAdmin duyệt.");
            return "redirect:/LvsUser/LvsTransaction/LvsDetail/" + lvsTransaction.getLvsTransactionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsUser/LvsTransactionWithdraw";
        }
    }

    // Hủy yêu cầu giao dịch (chỉ khi đang pending)
    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelTransaction(@PathVariable Long id,
                                       HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(id);

        if (lvsCurrentUser != null &&
                lvsTransaction.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId()) &&
                lvsTransaction.getLvsStatus() == LvsTransaction.LvsTransactionStatus.PENDING) {

            lvsTransactionService.lvsCancelTransaction(id);
        }

        return "redirect:/LvsUser/LvsTransaction/LvsHistory";
    }
}