package k23cnt3.lucvanson.project3.LvsController.LvsModeration;

import jakarta.servlet.http.HttpSession;
import k23cnt3.lucvanson.project3.LvsEntity.LvsReport;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsService.LvsReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/LvsModeration")
public class LvsModerationController {

    @Autowired
    private LvsReportService lvsReportService;

    @Autowired
    private k23cnt3.lucvanson.project3.LvsService.LvsProjectService lvsProjectService;

    @Autowired
    private k23cnt3.lucvanson.project3.LvsService.LvsPostService lvsPostService;

    @Autowired
    private k23cnt3.lucvanson.project3.LvsService.LvsCommentService lvsCommentService;

    @Autowired
    private k23cnt3.lucvanson.project3.LvsService.LvsReviewService lvsReviewService;

    @Autowired
    private k23cnt3.lucvanson.project3.LvsService.LvsUserService lvsUserService;

    /**
     * Check if current user is ADMIN or MODERATOR
     */
    private boolean lvsIsModerator(HttpSession session) {
        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        if (currentUser == null) {
            return false;
        }
        LvsUser.LvsRole role = currentUser.getLvsRole();
        return role == LvsUser.LvsRole.ADMIN || role == LvsUser.LvsRole.MODERATOR;
    }

    /**
     * Moderation Dashboard
     */
    @GetMapping("/LvsDashboard")
    public String lvsDashboard(Model model, HttpSession session) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");

        // Get statistics
        List<LvsReport> allReports = lvsReportService.lvsGetAllReports();
        List<LvsReport> pendingReports = lvsReportService.lvsGetReportsByStatus(LvsReport.LvsReportStatus.PENDING);
        List<LvsReport> underReviewReports = lvsReportService
                .lvsGetReportsByStatus(LvsReport.LvsReportStatus.UNDER_REVIEW);

        // Get recent reports (last 10)
        List<LvsReport> recentReports = allReports.stream()
                .sorted((r1, r2) -> r2.getLvsCreatedAt().compareTo(r1.getLvsCreatedAt()))
                .limit(10)
                .toList();

        model.addAttribute("totalReports", allReports.size());
        model.addAttribute("pendingCount", pendingReports.size());
        model.addAttribute("underReviewCount", underReviewReports.size());
        model.addAttribute("recentReports", recentReports);
        model.addAttribute("pageTitle", "Moderation Dashboard");

        return "LvsAreas/LvsModeration/LvsDashboard";
    }

    /**
     * Reports List with Filters
     */
    @GetMapping("/LvsReports")
    public String lvsReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            Model model,
            HttpSession session) {

        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        List<LvsReport> reports;

        // Filter by status
        if (status != null && !status.isEmpty()) {
            try {
                LvsReport.LvsReportStatus reportStatus = LvsReport.LvsReportStatus.valueOf(status);
                reports = lvsReportService.lvsGetReportsByStatus(reportStatus);
            } catch (IllegalArgumentException e) {
                reports = lvsReportService.lvsGetAllReports();
            }
        }
        // Filter by type
        else if (type != null && !type.isEmpty()) {
            try {
                LvsReport.LvsReportType reportType = LvsReport.LvsReportType.valueOf(type);
                reports = lvsReportService.lvsGetReportsByType(reportType);
            } catch (IllegalArgumentException e) {
                reports = lvsReportService.lvsGetAllReports();
            }
        }
        // No filter - get all
        else {
            reports = lvsReportService.lvsGetAllReports();
        }

        model.addAttribute("reports", reports);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedType", type);
        model.addAttribute("pageTitle", "Reports Management");

        return "LvsAreas/LvsModeration/LvsReports";
    }

    /**
     * Report Detail
     */
    @GetMapping("/LvsReport/LvsDetail/{id}")
    public String lvsReportDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        LvsReport report = lvsReportService.lvsGetReportById(id);
        if (report == null) {
            return "redirect:/LvsModeration/LvsReports";
        }

        model.addAttribute("report", report);
        model.addAttribute("pageTitle", "Report Detail");

        return "LvsAreas/LvsModeration/LvsReportDetail";
    }

    /**
     * Resolve Report
     */
    @PostMapping("/LvsReport/LvsResolve/{id}")
    public String lvsResolveReport(
            @PathVariable Long id,
            @RequestParam String adminNote,
            @RequestParam String actionTaken,
            @RequestParam String newStatus,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
            LvsReport.LvsReportStatus status = LvsReport.LvsReportStatus.valueOf(newStatus);

            lvsReportService.lvsResolveReport(id, currentUser, adminNote, actionTaken, status);

            redirectAttributes.addFlashAttribute("successMessage", "Report resolved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to resolve report: " + e.getMessage());
        }

        return "redirect:/LvsModeration/LvsReport/LvsDetail/" + id;
    }

    // ========== HIDE/SHOW CONTENT ENDPOINTS ==========

    /**
     * Hide Project
     */
    @PostMapping("/LvsProject/LvsHide/{id}")
    public String lvsHideProject(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsProjectService.lvsHideProject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Project hidden successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to hide project: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Show Project
     */
    @PostMapping("/LvsProject/LvsShow/{id}")
    public String lvsShowProject(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsProjectService.lvsShowProject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Project shown successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to show project: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Hide Post
     */
    @PostMapping("/LvsPost/LvsHide/{id}")
    public String lvsHidePost(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsPostService.lvsHidePost(id, "Hidden by moderator");
            redirectAttributes.addFlashAttribute("successMessage", "Post hidden successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to hide post: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Show Post
     */
    @PostMapping("/LvsPost/LvsShow/{id}")
    public String lvsShowPost(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsPostService.lvsShowPost(id);
            redirectAttributes.addFlashAttribute("successMessage", "Post shown successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to show post: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Hide Comment
     */
    @PostMapping("/LvsComment/LvsHide/{id}")
    public String lvsHideComment(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsCommentService.lvsHideComment(id, "Hidden by moderator");
            redirectAttributes.addFlashAttribute("successMessage", "Comment hidden successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to hide comment: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Show Comment
     */
    @PostMapping("/LvsComment/LvsShow/{id}")
    public String lvsShowComment(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsCommentService.lvsApproveComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Comment shown successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to show comment: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Hide Review
     */
    @PostMapping("/LvsReview/LvsHide/{id}")
    public String lvsHideReview(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsReviewService.lvsHideReview(id, "Hidden by moderator");
            redirectAttributes.addFlashAttribute("successMessage", "Review hidden successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to hide review: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Show Review
     */
    @PostMapping("/LvsReview/LvsShow/{id}")
    public String lvsShowReview(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsReviewService.lvsApproveReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Review shown successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to show review: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Ban User
     */
    @PostMapping("/LvsUser/LvsBan/{id}")
    public String lvsBanUser(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            LvsUser currentUser = (LvsUser) session.getAttribute("LvsCurrentUser");
            lvsUserService.lvsBanUser(id, currentUser.getLvsUserId(), "Banned by moderator");
            redirectAttributes.addFlashAttribute("successMessage", "User banned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to ban user: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }

    /**
     * Unban User
     */
    @PostMapping("/LvsUser/LvsUnban/{id}")
    public String lvsUnbanUser(
            @PathVariable Long id,
            @RequestParam(required = false) Long reportId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!lvsIsModerator(session)) {
            return "redirect:/LvsUser/LvsHome";
        }

        try {
            lvsUserService.lvsUnbanUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User unbanned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unban user: " + e.getMessage());
        }

        if (reportId != null) {
            return "redirect:/LvsModeration/LvsReport/LvsDetail/" + reportId;
        }
        return "redirect:/LvsModeration/LvsReports";
    }
}
