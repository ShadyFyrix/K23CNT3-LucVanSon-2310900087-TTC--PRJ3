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
 * Controller quản lý báo cáo cho người dùng
 * Xử lý gửi, xem báo cáo
 */
@Controller
@RequestMapping("/LvsUser/LvsReport")
public class LvsUserReportController {

    @Autowired
    private LvsReportService lvsReportService;

    @Autowired
    private LvsUserService lvsUserService;

    // Xem báo cáo của tôi
    @GetMapping("/LvsMyReports")
    public String lvsViewMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model) {

        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReport> lvsReports = lvsReportService.lvsGetReportsByUser(
                lvsCurrentUser.getLvsUserId(), lvsPageable);

        model.addAttribute("LvsReports", lvsReports);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsUsers/LvsReports/LvsMyReports";
    }

    // Gửi báo cáo
    @GetMapping("/LvsCreate")
    public String lvsShowCreateReportForm(@RequestParam LvsReport.LvsReportType lvsReportType,
            @RequestParam Long lvsTargetId,
            Model model,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsReport", new LvsReport());
        model.addAttribute("LvsReportType", lvsReportType);
        model.addAttribute("LvsTargetId", lvsTargetId);

        return "LvsAreas/LvsUsers/LvsReports/LvsReportCreate";
    }

    // Xử lý gửi báo cáo
    @PostMapping("/LvsCreate")
    public String lvsCreateReport(@ModelAttribute LvsReport lvsReport,
            @RequestParam LvsReport.LvsReportType lvsReportType,
            @RequestParam Long lvsTargetId,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (lvsCurrentUser == null) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsReport.setLvsReporter(lvsCurrentUser);
            lvsReport.setLvsReportType(lvsReportType);
            lvsReport.setLvsTargetId(lvsTargetId);
            lvsReport.setLvsStatus(LvsReport.LvsReportStatus.PENDING);

            lvsReportService.lvsSaveReport(lvsReport);

            model.addAttribute("LvsSuccess", "Đã gửi báo cáo thành công!");
            return "redirect:/LvsUser/LvsReport/LvsMyReports";
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi khi gửi báo cáo: " + e.getMessage());
            return "LvsAreas/LvsUsers/LvsReports/LvsReportCreate";
        }
    }

    // Xem chi tiết báo cáo
    @GetMapping("/LvsDetail/{id}")
    public String lvsViewReportDetail(@PathVariable Long id,
            HttpSession session,
            Model model) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsReport lvsReport = lvsReportService.lvsGetReportById(id);

        if (lvsCurrentUser == null ||
                !lvsReport.getLvsReporter().getLvsUserId().equals(lvsCurrentUser.getLvsUserId())) {
            return "redirect:/LvsUser/LvsReport/LvsMyReports";
        }

        model.addAttribute("LvsReport", lvsReport);

        return "LvsAreas/LvsUsers/LvsReports/LvsReportDetail";
    }

    // Hủy báo cáo
    @PostMapping("/LvsCancel/{id}")
    public String lvsCancelReport(@PathVariable Long id,
            HttpSession session) {
        LvsUser lvsCurrentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        LvsReport lvsReport = lvsReportService.lvsGetReportById(id);

        if (lvsCurrentUser != null &&
                lvsReport.getLvsReporter().getLvsUserId().equals(lvsCurrentUser.getLvsUserId()) &&
                lvsReport.getLvsStatus() == LvsReport.LvsReportStatus.PENDING) {

            lvsReportService.lvsDeleteReport(id);
        }

        return "redirect:/LvsUser/LvsReport/LvsMyReports";
    }
}