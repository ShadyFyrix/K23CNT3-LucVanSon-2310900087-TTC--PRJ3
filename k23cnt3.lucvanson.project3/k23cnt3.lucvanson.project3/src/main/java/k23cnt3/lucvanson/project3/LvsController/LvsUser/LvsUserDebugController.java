package k23cnt3.lucvanson.project3.LvsController.LvsUser;

import jakarta.servlet.http.HttpSession;
import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/LvsUser")
public class LvsUserDebugController {

    @GetMapping("/LvsDebug")
    public String lvsDebug(HttpSession session, Model model) {
        // Get current authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("authenticated", auth != null && auth.isAuthenticated());
        model.addAttribute("authName", auth != null ? auth.getName() : "null");
        model.addAttribute("authorities", auth != null ? auth.getAuthorities() : "null");

        // Get session user
        LvsUser sessionUser = (LvsUser) session.getAttribute("LvsCurrentUser");
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sessionUserName", sessionUser != null ? sessionUser.getLvsUsername() : "null");

        return "LvsAreas/LvsUsers/LvsDebug";
    }
}
