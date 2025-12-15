package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý trang dashboard cho LvsAdmin
 * Hiển thị thống kê hệ thống, biểu đồ
 */
@Controller
@RequestMapping("/LvsAdmin")
public class LvsAdminDashboardController {

    @Autowired
    private LvsUserService lvsUserService;

    @Autowired
    private LvsProjectService lvsProjectService;

    @Autowired
    private LvsPostService lvsPostService;

    @Autowired
    private LvsOrderService lvsOrderService;

    @Autowired
    private LvsTransactionService lvsTransactionService;

    @Autowired
    private LvsReportService lvsReportService;

    // Trang dashboard LvsAdmin
    @GetMapping("/LvsDashboard")
    public String lvsAdminDashboard(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Lấy thống kê tổng quan
        Long lvsTotalUsers = lvsUserService.lvsCountTotalUsers();
        Long lvsTotalProjects = lvsProjectService.lvsCountTotalProjects();
        Long lvsTotalPosts = lvsPostService.lvsCountTotalPosts();
        Long lvsTotalOrders = lvsOrderService.lvsCountTotalOrders();
        Double lvsTotalRevenue = lvsTransactionService.lvsGetTotalRevenue();

        // Lấy thống kê trạng thái
        Long lvsPendingProjects = lvsProjectService.lvsCountProjectsByStatus("PENDING");
        Long lvsPendingTransactions = lvsTransactionService.lvsCountTransactionsByStatus("PENDING");
        Long lvsPendingReports = lvsReportService.lvsCountReportsByStatus("PENDING");

        // Lấy thống kê theo thời gian
        Map<String, Long> lvsUserStats = lvsUserService.lvsGetUserStatsLast30Days();
        Map<String, Double> lvsRevenueStats = lvsTransactionService.lvsGetRevenueStatsLast30Days();

        model.addAttribute("LvsTotalUsers", lvsTotalUsers);
        model.addAttribute("LvsTotalProjects", lvsTotalProjects);
        model.addAttribute("LvsTotalPosts", lvsTotalPosts);
        model.addAttribute("LvsTotalOrders", lvsTotalOrders);
        model.addAttribute("LvsTotalRevenue", lvsTotalRevenue);
        model.addAttribute("LvsPendingProjects", lvsPendingProjects);
        model.addAttribute("LvsPendingTransactions", lvsPendingTransactions);
        model.addAttribute("LvsPendingReports", lvsPendingReports);
        model.addAttribute("LvsUserStats", lvsUserStats);
        model.addAttribute("LvsRevenueStats", lvsRevenueStats);

        return "LvsAdmin/LvsDashboard";
    }

    // Lấy dữ liệu biểu đồ
    @GetMapping("/LvsChartData")
    @ResponseBody
    public Map<String, Object> lvsGetChartData(@RequestParam String lvsType) {
        Map<String, Object> lvsData = new HashMap<>();

        switch (lvsType) {
            case "LvsUsers":
                lvsData = lvsUserService.lvsGetUserRegistrationChartData();
                break;
            case "revenue":
                lvsData = lvsTransactionService.lvsGetRevenueChartData();
                break;
            case "projects":
                lvsData = lvsProjectService.lvsGetProjectStatsChartData();
                break;
        }

        return lvsData;
    }

    // Thống kê chi tiết
    @GetMapping("/LvsStatistics")
    public String lvsViewStatistics(@RequestParam(required = false) String lvsPeriod,
                                    Model model,
                                    HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LocalDate lvsStartDate = null;
        LocalDate lvsEndDate = LocalDate.now();

        if (lvsPeriod != null) {
            switch (lvsPeriod) {
                case "week":
                    lvsStartDate = lvsEndDate.minusWeeks(1);
                    break;
                case "month":
                    lvsStartDate = lvsEndDate.minusMonths(1);
                    break;
                case "quarter":
                    lvsStartDate = lvsEndDate.minusMonths(3);
                    break;
                case "year":
                    lvsStartDate = lvsEndDate.minusYears(1);
                    break;
                default:
                    lvsStartDate = lvsEndDate.minusMonths(1);
            }
        } else {
            lvsStartDate = lvsEndDate.minusMonths(1);
        }

        // Lấy thống kê theo thời gian
        Map<String, Object> lvsStats = new HashMap<>();
        lvsStats.put("newUsers", lvsUserService.lvsCountNewUsers(lvsStartDate, lvsEndDate));
        lvsStats.put("newProjects", lvsProjectService.lvsCountNewProjects(lvsStartDate, lvsEndDate));
        lvsStats.put("newOrders", lvsOrderService.lvsCountNewOrders(lvsStartDate, lvsEndDate));
        lvsStats.put("totalRevenue", lvsTransactionService.lvsGetTotalRevenue(lvsStartDate, lvsEndDate));

        // Top dự án bán chạy
        List<LvsProject> lvsTopProjects = lvsProjectService.lvsGetTopSellingProjects(10);

        // Top user mua nhiều nhất
        List<LvsUser> lvsTopBuyers = lvsUserService.lvsGetTopBuyers(10);

        model.addAttribute("LvsStats", lvsStats);
        model.addAttribute("LvsPeriod", lvsPeriod);
        model.addAttribute("LvsStartDate", lvsStartDate);
        model.addAttribute("LvsEndDate", lvsEndDate);
        model.addAttribute("LvsTopProjects", lvsTopProjects);
        model.addAttribute("LvsTopBuyers", lvsTopBuyers);

        return "LvsAdmin/LvsStatistics";
    }

    // Báo cáo hệ thống
    @GetMapping("/LvsReport")
    public String lvsGenerateReport(@RequestParam String lvsReportType,
                                    @RequestParam(required = false) String lvsStartDate,
                                    @RequestParam(required = false) String lvsEndDate,
                                    Model model,
                                    HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Xử lý báo cáo
        // (Cần triển khai service tạo báo cáo)

        model.addAttribute("LvsReportType", lvsReportType);
        model.addAttribute("LvsStartDate", lvsStartDate);
        model.addAttribute("LvsEndDate", lvsEndDate);

        return "LvsAdmin/LvsReportView";
    }
}