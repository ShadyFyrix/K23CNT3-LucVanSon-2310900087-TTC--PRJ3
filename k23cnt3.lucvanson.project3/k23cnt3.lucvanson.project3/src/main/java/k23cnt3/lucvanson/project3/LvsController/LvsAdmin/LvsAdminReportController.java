package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

/**
 * Controller quÃ¡ÂºÂ£n lÃƒÂ½ BÃƒÂ¡o cÃƒÂ¡o (Report) trong Admin Panel
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsReport")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminReportController {

    @Autowired
    private LvsReportService lvsReportService;

    @Autowired
    private LvsUserService lvsUserService;

    @GetMapping("/LvsList")
    public String lvsListReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String lvsStatus,
            @RequestParam(required = false) String lvsType,
            Model model) {
        Pageable lvsPageable = PageRequest.of(page, size);
        Page<LvsReport> lvsReports;

        if (lvsStatus != null && !lvsStatus.isEmpty() && lvsType != null && !lvsType.isEmpty()) {
            lvsReports = lvsReportService.lvsGetReportsByStatusAndType(lvsStatus, lvsType, lvsPageable);
        } else if (lvsStatus != null && !lvsStatus.isEmpty()) {
            lvsReports = lvsReportService.lvsGetReportsByStatus(lvsStatus, lvsPageable);
        } else if (lvsType != null && !lvsType.isEmpty()) {
            lvsReports = lvsReportService.lvsGetReportsByType(lvsType, lvsPageable);
        } else {
            lvsReports = lvsReportService.lvsGetAllReports(lvsPageable);
        }

        model.addAttribute("LvsReports", lvsReports);
        model.addAttribute("LvsStatuses", LvsReport.LvsReportStatus.values());
        model.addAttribute("LvsTypes", LvsReport.LvsReportType.values());
        model.addAttribute("LvsSelectedStatus", lvsStatus);
        model.addAttribute("LvsSelectedType", lvsType);
        model.addAttribute("LvsCurrentPage", page);

        return "LvsAreas/LvsAdmin/LvsReport/LvsList";
    }

    @GetMapping("/LvsDetail/{id}")
    public String lvsViewReportDetail(@PathVariable Long id, Model model) {
        LvsReport lvsReport = lvsReportService.lvsGetReportById(id);
        if (lvsReport == null) {
            return "redirect:/LvsAdmin/LvsReport/LvsList";
        }

        Object lvsTarget = null;
        switch (lvsReport.getLvsReportType()) {
            case USER:
                lvsTarget = lvsUserService.lvsGetUserById(lvsReport.getLvsTargetId());
                break;
            case PROJECT:
            case POST:
            default:
                break;
        }

        model.addAttribute("LvsReport", lvsReport);
        model.addAttribute("LvsTarget", lvsTarget);

        return "LvsAreas/LvsAdmin/LvsReport/LvsDetail";
    }

    @PostMapping("/LvsHandle/{id}")
    public String lvsHandleReport(@PathVariable Long id,
            @RequestParam LvsReport.LvsReportStatus lvsStatus,
            @RequestParam(required = false) String lvsActionTaken,
            @RequestParam(required = false) String lvsAdminNote, Model model) {
        try {
            // TODO: Fix session parameter - Temporarily commented out
            // LvsUser lvsAdmin = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsReportService.lvsHandleReport(id, 1L /* Hardcoded admin ID temporarily */, lvsStatus,
                    lvsActionTaken, lvsAdminNote);
            model.addAttribute("LvsSuccess", "Ã„ÂÃƒÂ£ xÃ¡Â»Â­ lÃƒÂ½ bÃƒÂ¡o cÃƒÂ¡o!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "LÃ¡Â»â€”i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReport/LvsDetail/" + id;
    }

    @PostMapping("/LvsUpdateStatus/{id}")
    public String lvsUpdateReportStatus(@PathVariable Long id,
            @RequestParam LvsReport.LvsReportStatus lvsStatus, Model model) {
        try {
            lvsReportService.lvsUpdateReportStatus(id, lvsStatus);
            model.addAttribute("LvsSuccess", "Ã„ÂÃƒÂ£ cÃ¡ÂºÂ­p nhÃ¡ÂºÂ­t trÃ¡ÂºÂ¡ng thÃƒÂ¡i!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "LÃ¡Â»â€”i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReport/LvsDetail/" + id;
    }

    @PostMapping("/LvsAssign/{id}")
    public String lvsAssignReport(@PathVariable Long id, @RequestParam Long lvsAdminId, Model model) {
        try {
            lvsReportService.lvsAssignReport(id, lvsAdminId);
            model.addAttribute("LvsSuccess", "Ã„ÂÃƒÂ£ gÃƒÂ¡n admin xÃ¡Â»Â­ lÃƒÂ½!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "LÃ¡Â»â€”i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReport/LvsDetail/" + id;
    }

    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteReport(@PathVariable Long id, Model model) {
        try {
            lvsReportService.lvsDeleteReport(id);
            model.addAttribute("LvsSuccess", "Ã„ÂÃƒÂ£ xÃƒÂ³a bÃƒÂ¡o cÃƒÂ¡o!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "LÃ¡Â»â€”i: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsReport/LvsList";
    }

    @GetMapping("/LvsStatistics")
    public String lvsViewReportStatistics(Model model) {
        Map<String, Long> lvsStatsByType = lvsReportService.lvsGetReportStatsByType();
        Map<String, Long> lvsStatsByStatus = lvsReportService.lvsGetReportStatsByStatus();
        Map<String, Long> lvsStatsByTime = lvsReportService.lvsGetReportStatsByTime();

        model.addAttribute("LvsStatsByType", lvsStatsByType);
        model.addAttribute("LvsStatsByStatus", lvsStatsByStatus);
        model.addAttribute("LvsStatsByTime", lvsStatsByTime);

        return "LvsAreas/LvsAdmin/LvsReport/LvsStatistics";
    }
}
