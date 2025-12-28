package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * Controller quÃ¡ÂºÂ£n lÃƒÂ½ Giao dÃ¡Â»â€¹ch (Transaction) trong Admin Panel
 * 
 * <p>
 * ChÃ¡Â»Â©c nÃ„Æ’ng chÃƒÂ­nh:
 * </p>
 * <ul>
 * <li>HiÃ¡Â»Æ’n thÃ¡Â»â€¹ danh sÃƒÂ¡ch giao dÃ¡Â»â€¹ch vÃ¡Â»â€ºi phÃƒÂ¢n trang
 * vÃƒÂ  lÃ¡Â»Âc</li>
 * <li>Xem chi tiÃ¡ÂºÂ¿t giao dÃ¡Â»â€¹ch</li>
 * <li>DuyÃ¡Â»â€¡t giao dÃ¡Â»â€¹ch nÃ¡ÂºÂ¡p coin</li>
 * <li>DuyÃ¡Â»â€¡t giao dÃ¡Â»â€¹ch rÃƒÂºt coin</li>
 * <li>TÃ¡Â»Â« chÃ¡Â»â€˜i/hÃ¡Â»Â§y giao dÃ¡Â»â€¹ch</li>
 * <li>ThÃƒÂªm giao dÃ¡Â»â€¹ch thÃ¡Â»Â§ cÃƒÂ´ng</li>
 * </ul>
 * 
 * <p>
 * Template paths:
 * </p>
 * <ul>
 * <li>List: LvsAreas/LvsAdmin/LvsTransaction/LvsList.html</li>
 * <li>Detail: LvsAreas/LvsAdmin/LvsTransaction/LvsDetail.html</li>
 * <li>Create: LvsAreas/LvsAdmin/LvsTransaction/LvsCreate.html</li>
 * </ul>
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsTransaction")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminTransactionController {

    @Autowired
    private LvsTransactionService lvsTransactionService;

    @Autowired
    private LvsUserService lvsUserService;

    /**
     * HiÃ¡Â»Æ’n thÃ¡Â»â€¹ danh sÃƒÂ¡ch giao dÃ¡Â»â€¹ch
     */
    @GetMapping("/LvsList")
    public String lvsListTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String lvsType,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsKeyword,
            Model model) {
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

        return "LvsAreas/LvsAdmin/LvsTransaction/LvsList";
    }

    @GetMapping("/LvsDetail/{id}")
    public String lvsViewTransactionDetail(@PathVariable Long id, Model model) {
        LvsTransaction lvsTransaction = lvsTransactionService.lvsGetTransactionById(id);
        if (lvsTransaction == null) {
            return "redirect:/LvsAdmin/LvsTransaction/LvsList";
        }

        model.addAttribute("LvsTransaction", lvsTransaction);
        return "LvsAreas/LvsAdmin/LvsTransaction/LvsDetail";
    }

    @PostMapping("/LvsApproveDeposit/{id}")
    public String lvsApproveDeposit(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            if (lvsAdmin == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Vui lòng đăng nhập lại!");
                return "redirect:/lvsforum/LvsLogin";
            }

            lvsTransactionService.lvsApproveDeposit(id, lvsAdmin.getLvsUserId());
            redirectAttributes.addFlashAttribute("LvsSuccess", "Đã duyệt nạp tiền!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + id;
    }

    @PostMapping("/LvsApproveWithdrawal/{id}")
    public String lvsApproveWithdrawal(@PathVariable Long id, HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            if (lvsAdmin == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Vui lòng đăng nhập lại!");
                return "redirect:/lvsforum/LvsLogin";
            }

            lvsTransactionService.lvsApproveWithdrawal(id, lvsAdmin.getLvsUserId());
            redirectAttributes.addFlashAttribute("LvsSuccess", "Đã duyệt rút tiền!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + id;
    }

    @PostMapping("/LvsReject/{id}")
    public String lvsRejectTransaction(@PathVariable Long id, @RequestParam String lvsReason,
            HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            if (lvsAdmin == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Vui lòng đăng nhập lại!");
                return "redirect:/lvsforum/LvsLogin";
            }

            lvsTransactionService.lvsRejectTransaction(id, lvsAdmin.getLvsUserId(), lvsReason);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Đã từ chối giao dịch!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + id;
    }

    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lvsTransactionService.lvsCancelTransaction(id);
            redirectAttributes.addFlashAttribute("LvsSuccess", "Đã hủy giao dịch!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsTransaction/LvsList";
    }

    @GetMapping("/LvsCreate")
    public String lvsShowAddTransactionForm(Model model) {
        Page<LvsUser> lvsUsersPage = lvsUserService.lvsGetAllUsers(Pageable.unpaged());
        List<LvsUser> lvsUsers = lvsUsersPage.getContent();
        model.addAttribute("LvsTransaction", new LvsTransaction());
        model.addAttribute("LvsUsers", lvsUsers);
        model.addAttribute("LvsTransactionTypes", LvsTransaction.LvsTransactionType.values());

        return "LvsAreas/LvsAdmin/LvsTransaction/LvsCreate";
    }

    @PostMapping("/LvsCreate")
    public String lvsAddTransaction(@ModelAttribute LvsTransaction lvsTransaction,
            @RequestParam Long lvsUserId, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        try {
            LvsUser lvsUser = lvsUserService.lvsGetUserById(lvsUserId);
            LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");

            if (lvsAdmin == null) {
                redirectAttributes.addFlashAttribute("LvsError", "Vui lòng đăng nhập lại!");
                return "redirect:/lvsforum/LvsLogin";
            }

            lvsTransaction.setLvsUser(lvsUser);
            lvsTransaction.setLvsAdminApprover(lvsAdmin);
            lvsTransaction.setLvsStatus(LvsTransaction.LvsTransactionStatus.SUCCESS);
            lvsTransaction.setLvsApprovedAt(java.time.LocalDateTime.now());

            LvsTransaction lvsSavedTransaction = lvsTransactionService.lvsSaveTransaction(lvsTransaction);

            if (lvsTransaction.getLvsType() == LvsTransaction.LvsTransactionType.DEPOSIT) {
                lvsUser.setLvsCoin(lvsUser.getLvsCoin() + lvsTransaction.getLvsAmount());
            } else if (lvsTransaction.getLvsType() == LvsTransaction.LvsTransactionType.WITHDRAWAL) {
                lvsUser.setLvsBalance(lvsUser.getLvsBalance() - lvsTransaction.getLvsAmount());
            }
            lvsUserService.lvsUpdateUser(lvsUser);

            redirectAttributes.addFlashAttribute("LvsSuccess", "Thêm giao dịch thành công!");
            return "redirect:/LvsAdmin/LvsTransaction/LvsDetail/" + lvsSavedTransaction.getLvsTransactionId();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsTransaction/LvsCreate";
        }
    }
}
