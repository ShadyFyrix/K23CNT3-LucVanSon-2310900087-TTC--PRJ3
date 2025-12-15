package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/LvsAdmin")
public class LvsAdminDashboardController {

    @GetMapping("/LvsDashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        // Mock data for dashboard
        model.addAttribute("totalUsers", 150);
        model.addAttribute("totalOrders", 45);
        model.addAttribute("totalRevenue", 12500);
        model.addAttribute("pageTitle", "Dashboard");

        // Thêm activePage để sidebar biết trang nào đang active
        model.addAttribute("activePage", "dashboard");

        return "LvsAreas/LvsAdmin/LvsLayout/LvsDashboard";
    }
}