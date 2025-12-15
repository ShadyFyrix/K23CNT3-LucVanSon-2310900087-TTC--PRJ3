package k23cnt3.lucvanson.project3.LvsController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * LvsTestController - Controller để test các tính năng
 * 
 * @author LucVanSon
 * @version 1.0
 */
@Controller
public class LvsTestController {

    /**
     * Trang test authentication - hiển thị các link đăng nhập
     * 
     * @param model Model để truyền dữ liệu
     * @return Template test page
     */
    @GetMapping("/test-auth")
    public String lvsShowTestAuthPage(Model model) {
        return "test-auth";
    }
}
