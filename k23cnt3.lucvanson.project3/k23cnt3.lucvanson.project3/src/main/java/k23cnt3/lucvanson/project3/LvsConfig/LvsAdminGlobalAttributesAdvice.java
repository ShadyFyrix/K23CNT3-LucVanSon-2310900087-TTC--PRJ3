package k23cnt3.lucvanson.project3.LvsConfig;

import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsRepository.LvsOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global attributes for all admin pages
 * Adds common data like user count, order count to all admin views
 */
@ControllerAdvice(basePackages = "k23cnt3.lucvanson.project3.LvsController.LvsAdmin")
public class LvsAdminGlobalAttributesAdvice {

    @Autowired
    private LvsUserRepository lvsUserRepository;

    @Autowired
    private LvsOrderRepository lvsOrderRepository;

    /**
     * Add global stats to all admin pages
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // Count total users
        long totalUsers = lvsUserRepository.count();
        model.addAttribute("LvsTotalUsers", totalUsers);

        // Count total orders
        long totalOrders = lvsOrderRepository.count();
        model.addAttribute("LvsTotalOrders", totalOrders);
    }
}
