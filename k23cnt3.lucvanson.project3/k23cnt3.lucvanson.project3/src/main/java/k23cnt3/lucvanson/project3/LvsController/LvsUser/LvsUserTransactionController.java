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

        return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionHistory";
    }

    // Xem chi tiết giao dịch
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewTransactionDetail(@PathVariable Long id,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(id);

        // Kiểm tra transaction có tồn tại không
        if (lvsTransaction == null) {
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        }

        // Kiểm tra quyền truy cập
        if (!lvsTransaction.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        }

        model.addAttribute("LvsTransaction", lvsTransaction);

        return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionDetail";
    }

    // Tạo yêu cầu nạp coin
    @GetMapping("/LvsDeposit")
    public String lvsShowDepositForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsTransaction", new LvsTransaction());
        model.addAttribute("LvsCurrentBalance", lvsCurrentUser.getLvsCoin());

        return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionDeposit";
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

            // If DEMO payment, redirect to mock payment gateway
            if ("DEMO".equals(lvsPaymentMethod)) {
                return "redirect:/LvsUser/LvsTransaction/LvsMockPayment/" + lvsTransaction.getLvsTransactionId();
            }

            // Otherwise, wait for admin approval
            model.addAttribute("LvsSuccess", "Đã tạo yêu cầu nạp tiền! Chờ LvsAdmin duyệt.");
            return "redirect:/LvsUser/LvsTransaction/LvsDetail/" + lvsTransaction.getLvsTransactionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionDeposit";
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

        return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionWithdraw";
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
                return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionWithdraw";
            }

            LvsTransaction lvsTransaction = lvsTransactionService.lvsCreateWithdrawalRequest(
                    lvsCurrentUser.getLvsUserId(), lvsAmount);

            lvsTransaction.setLvsPaymentInfo(lvsPaymentInfo);
            lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            model.addAttribute("LvsSuccess", "Đã tạo yêu cầu rút tiền! Chờ LvsAdmin duyệt.");
            return "redirect:/LvsUser/LvsTransaction/LvsDetail/" + lvsTransaction.getLvsTransactionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsTransactions/LvsTransactionWithdraw";
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

    // Hiển thị form chuyển đổi Balance sang Coin
    @GetMapping("/LvsConvert")
    public String lvsShowConvertForm(Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Refresh user data
        lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());

        model.addAttribute("LvsUser", lvsCurrentUser);
        model.addAttribute("LvsMinAmount", 1000.0);

        return "LvsAreas/LvsUsers/LvsTransactions/LvsBalanceToCoins";
    }

    // Xử lý chuyển đổi Balance sang Coin
    @PostMapping("/LvsConvert")
    public String lvsProcessConvert(@RequestParam Double lvsAmount,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            System.out.println(">>> CONVERSION: Starting conversion for user " + lvsCurrentUser.getLvsUsername()
                    + ", amount: " + lvsAmount);

            LvsTransaction lvsTransaction = lvsTransactionService.lvsConvertBalanceToCoin(
                    lvsCurrentUser.getLvsUserId(), lvsAmount);

            System.out.println(">>> CONVERSION: Transaction created with ID: " + lvsTransaction.getLvsTransactionId());

            // Refresh user data in session
            lvsCurrentUser = lvsUserService.lvsGetUserById(lvsCurrentUser.getLvsUserId());
            session.setAttribute("LvsCurrentUser", lvsCurrentUser);

            String redirectUrl = "redirect:LvsDetail/" + lvsTransaction.getLvsTransactionId();
            System.out.println(">>> CONVERSION: Redirecting to: " + redirectUrl);

            return redirectUrl;
        } catch (Exception e) {
            System.out.println(">>> CONVERSION ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            model.addAttribute("LvsUser", lvsCurrentUser);
            model.addAttribute("LvsMinAmount", 1000.0);
            return "LvsAreas/LvsUsers/LvsTransactions/LvsBalanceToCoins";
        }
    }

    // ===== MOCK PAYMENT GATEWAY ENDPOINTS =====

    // Hiển thị trang mock payment gateway
    @GetMapping("/LvsMockPayment/{id}")
    public String lvsShowMockPaymentGateway(@PathVariable Long id, Model model, HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(id);
        if (lvsTransaction == null
                || !lvsTransaction.getLvsUser().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        }

        model.addAttribute("LvsTransactionId", lvsTransaction.getLvsTransactionId());
        model.addAttribute("LvsAmount", lvsTransaction.getLvsAmount());
        model.addAttribute("LvsUser", lvsCurrentUser);

        return "LvsAreas/LvsUsers/LvsTransactions/LvsMockPaymentGateway";
    }

    // Xử lý thanh toán thành công
    @PostMapping("/LvsPaymentSuccess")
    public String lvsProcessPaymentSuccess(@RequestParam Long lvsTransactionId, HttpSession session, Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            System.out.println(">>> MOCK PAYMENT: Processing success for transaction " + lvsTransactionId);

            LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(lvsTransactionId);
            if (lvsTransaction == null) {
                model.addAttribute("LvsError", "Giao dịch không tồn tại!");
                return "redirect:/LvsUser/LvsTransaction/LvsHistory";
            }

            // Approve the transaction and add coins
            lvsTransaction.setLvsStatus(LvsTransaction.LvsTransactionStatus.SUCCESS);
            lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            // Add coins to user
            lvsCurrentUser.setLvsCoin(lvsCurrentUser.getLvsCoin() + lvsTransaction.getLvsAmount());
            lvsUserService.lvsUpdateUser(lvsCurrentUser);

            // Refresh session
            session.setAttribute("LvsCurrentUser", lvsCurrentUser);

            System.out.println(">>> MOCK PAYMENT: Success! Added " + lvsTransaction.getLvsAmount() + " coins to user");

            model.addAttribute("LvsSuccess",
                    "Thanh toán thành công! Đã nạp " + lvsTransaction.getLvsAmount() + " coin vào tài khoản.");
            return "redirect:/LvsUser/LvsTransaction/LvsDetail/" + lvsTransactionId;
        } catch (Exception e) {
            System.out.println(">>> MOCK PAYMENT ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("LvsError", "Lỗi xử lý thanh toán: " + e.getMessage());
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        }
    }

    // Xử lý hủy thanh toán
    @PostMapping("/LvsPaymentCancel")
    public String lvsProcessPaymentCancel(@RequestParam Long lvsTransactionId, HttpSession session, Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            System.out.println(">>> MOCK PAYMENT: Cancelling transaction " + lvsTransactionId);

            LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(lvsTransactionId);
            if (lvsTransaction == null) {
                return "redirect:/LvsUser/LvsTransaction/LvsHistory";
            }

            // Cancel the transaction
            lvsTransaction.setLvsStatus(LvsTransaction.LvsTransactionStatus.FAILED);
            lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            System.out.println(">>> MOCK PAYMENT: Transaction cancelled");

            model.addAttribute("LvsError", "Đã hủy thanh toán.");
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        } catch (Exception e) {
            System.out.println(">>> MOCK PAYMENT ERROR: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/LvsUser/LvsTransaction/LvsHistory";
        }
    }
}
