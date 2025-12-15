package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý cài đặt hệ thống cho LvsAdmin
 * Xử lý cấu hình hệ thống
 */
@Controller
@RequestMapping("/LvsAdmin/LvsSetting")
public class LvsAdminSettingController {

    @Autowired
    private LvsSettingService lvsSettingService;

    @Autowired
    private LvsUserService lvsUserService;

    // Danh sách cài đặt
    @GetMapping("/LvsList")
    public String lvsListSettings(@RequestParam(required = false) String lvsGroup,
                                  Model model,
                                  HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        List<LvsSetting> lvsSettings;

        if (lvsGroup != null && !lvsGroup.isEmpty()) {
            lvsSettings = lvsSettingService.lvsGetSettingsByGroup(lvsGroup);
        } else {
            lvsSettings = lvsSettingService.lvsGetAllSettings();
        }

        // Nhóm các setting
        Map<String, List<LvsSetting>> lvsSettingsByGroup =
                lvsSettingService.lvsGetSettingsGroupedByGroup();

        model.addAttribute("LvsSettings", lvsSettings);
        model.addAttribute("LvsSettingsByGroup", lvsSettingsByGroup);
        model.addAttribute("LvsSelectedGroup", lvsGroup);

        return "LvsAdmin/LvsSettingList";
    }

    // Chỉnh sửa cài đặt
    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditSettingForm(@PathVariable Integer id,
                                         Model model,
                                         HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        LvsSetting lvsSetting = lvsSettingService.lvsGetSettingById(id);

        if (lvsSetting == null) {
            return "redirect:/LvsAdmin/LvsSetting/LvsList";
        }

        model.addAttribute("LvsSetting", lvsSetting);

        return "LvsAdmin/LvsSettingEdit";
    }

    // Xử lý chỉnh sửa cài đặt
    @PostMapping("/LvsEdit/{id}")
    public String lvsEditSetting(@PathVariable Integer id,
                                 @ModelAttribute LvsSetting lvsSetting,
                                 HttpSession session,
                                 Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsSetting.setLvsSettingId(id);
            lvsSettingService.lvsSaveSetting(lvsSetting);

            model.addAttribute("LvsSuccess", "Cập nhật cài đặt thành công!");
            return "redirect:/LvsAdmin/LvsSetting/LvsList?group=" + lvsSetting.getLvsGroup();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsSettingEdit";
        }
    }

    // Thêm cài đặt mới
    @GetMapping("/LvsAdd")
    public String lvsShowAddSettingForm(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        model.addAttribute("LvsSetting", new LvsSetting());

        return "LvsAdmin/LvsSettingAdd";
    }

    // Xử lý thêm cài đặt
    @PostMapping("/LvsAdd")
    public String lvsAddSetting(@ModelAttribute LvsSetting lvsSetting,
                                HttpSession session,
                                Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsSettingService.lvsSaveSetting(lvsSetting);

            model.addAttribute("LvsSuccess", "Thêm cài đặt thành công!");
            return "redirect:/LvsAdmin/LvsSetting/LvsList?group=" + lvsSetting.getLvsGroup();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAdmin/LvsSettingAdd";
        }
    }

    // Xóa cài đặt
    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteSetting(@PathVariable Integer id,
                                   HttpSession session,
                                   Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsSettingService.lvsDeleteSetting(id);
            model.addAttribute("LvsSuccess", "Đã xóa cài đặt!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsSetting/LvsList";
    }

    // Lưu nhiều cài đặt cùng lúc
    @PostMapping("/LvsBatchSave")
    public String lvsBatchSaveSettings(@RequestParam Map<String, String> lvsSettings,
                                       HttpSession session,
                                       Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsSettingService.lvsBatchSaveSettings(lvsSettings);
            model.addAttribute("LvsSuccess", "Đã lưu cài đặt thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsSetting/LvsList";
    }

    // Cài đặt hệ thống
    @GetMapping("/LvsSystem")
    public String lvsSystemSettings(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        // Lấy các cài đặt hệ thống quan trọng
        List<LvsSetting> lvsSystemSettings = lvsSettingService.lvsGetSettingsByGroup("system");
        List<LvsSetting> lvsEmailSettings = lvsSettingService.lvsGetSettingsByGroup("email");
        List<LvsSetting> lvsPaymentSettings = lvsSettingService.lvsGetSettingsByGroup("payment");

        model.addAttribute("LvsSystemSettings", lvsSystemSettings);
        model.addAttribute("LvsEmailSettings", lvsEmailSettings);
        model.addAttribute("LvsPaymentSettings", lvsPaymentSettings);

        return "LvsAdmin/LvsSystemSettings";
    }

    // Sao lưu cài đặt
    @GetMapping("/LvsBackup")
    public String lvsBackupSettings(Model model, HttpSession session) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            String lvsBackupData = lvsSettingService.lvsBackupSettings();
            model.addAttribute("LvsBackupData", lvsBackupData);
            model.addAttribute("LvsSuccess", "Sao lưu thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "LvsAdmin/LvsSettingBackup";
    }

    // Khôi phục cài đặt
    @PostMapping("/LvsRestore")
    public String lvsRestoreSettings(@RequestParam String lvsBackupData,
                                     HttpSession session,
                                     Model model) {
        // Kiểm tra quyền LvsAdmin
        if (!lvsUserService.lvsIsAdmin(session)) {
            return "redirect:/LvsAuth/LvsLogin.html";
        }

        try {
            lvsSettingService.lvsRestoreSettings(lvsBackupData);
            model.addAttribute("LvsSuccess", "Khôi phục thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsSetting/LvsList";
    }
}