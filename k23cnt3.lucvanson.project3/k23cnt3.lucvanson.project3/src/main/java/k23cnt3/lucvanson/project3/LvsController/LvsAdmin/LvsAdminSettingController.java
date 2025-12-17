package k23cnt3.lucvanson.project3.LvsController.LvsAdmin;

import k23cnt3.lucvanson.project3.LvsEntity.*;
import k23cnt3.lucvanson.project3.LvsService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Cài đặt (Setting) trong Admin Panel
 * 
 * @author LucVanSon
 * @version 1.0
 * @since 2024
 */
@Controller
@RequestMapping("/LvsAdmin/LvsSetting")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class LvsAdminSettingController {

    @Autowired
    private LvsSettingService lvsSettingService;

    @Autowired
    private LvsUserService lvsUserService;

    @GetMapping("/LvsList")
    public String lvsListSettings(@RequestParam(required = false) String lvsGroup,
            Model model) {
        List<LvsSetting> lvsSettings;

        if (lvsGroup != null && !lvsGroup.isEmpty()) {
            lvsSettings = lvsSettingService.lvsGetSettingsByGroup(lvsGroup);
        } else {
            lvsSettings = lvsSettingService.lvsGetAllSettings();
        }

        Map<String, List<LvsSetting>> lvsSettingsByGroup = lvsSettingService.lvsGetSettingsGroupedByGroup();

        model.addAttribute("LvsSettings", lvsSettings);
        model.addAttribute("LvsSettingsByGroup", lvsSettingsByGroup);
        model.addAttribute("LvsSelectedGroup", lvsGroup);

        return "LvsAreas/LvsAdmin/LvsSetting/LvsList";
    }

    @GetMapping("/LvsEdit/{id}")
    public String lvsShowEditSettingForm(@PathVariable Integer id, Model model) {
        LvsSetting lvsSetting = lvsSettingService.lvsGetSettingById(id);
        if (lvsSetting == null) {
            return "redirect:/LvsAdmin/LvsSetting/LvsList";
        }

        model.addAttribute("LvsSetting", lvsSetting);
        return "LvsAreas/LvsAdmin/LvsSetting/LvsEdit";
    }

    @PostMapping("/LvsEdit/{id}")
    public String lvsEditSetting(@PathVariable Integer id, @ModelAttribute LvsSetting lvsSetting, Model model) {
        try {
            lvsSetting.setLvsSettingId(id);
            lvsSettingService.lvsSaveSetting(lvsSetting);

            model.addAttribute("LvsSuccess", "Cập nhật cài đặt thành công!");
            return "redirect:/LvsAdmin/LvsSetting/LvsList?group=" + lvsSetting.getLvsGroup();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsSetting/LvsEdit";
        }
    }

    @GetMapping("/LvsAdd")
    public String lvsShowAddSettingForm(Model model) {
        model.addAttribute("LvsSetting", new LvsSetting());
        return "LvsAreas/LvsAdmin/LvsSetting/LvsCreate";
    }

    @PostMapping("/LvsAdd")
    public String lvsAddSetting(@ModelAttribute LvsSetting lvsSetting, Model model) {
        try {
            lvsSettingService.lvsSaveSetting(lvsSetting);

            model.addAttribute("LvsSuccess", "Thêm cài đặt thành công!");
            return "redirect:/LvsAdmin/LvsSetting/LvsList?group=" + lvsSetting.getLvsGroup();
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
            return "LvsAreas/LvsAdmin/LvsSetting/LvsCreate";
        }
    }

    @PostMapping("/LvsDelete/{id}")
    public String lvsDeleteSetting(@PathVariable Integer id, Model model) {
        try {
            lvsSettingService.lvsDeleteSetting(id);
            model.addAttribute("LvsSuccess", "Đã xóa cài đặt!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsSetting/LvsList";
    }

    @PostMapping("/LvsBatchSave")
    public String lvsBatchSaveSettings(@RequestParam Map<String, String> lvsSettings, Model model) {
        try {
            lvsSettingService.lvsBatchSaveSettings(lvsSettings);
            model.addAttribute("LvsSuccess", "Đã lưu cài đặt thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsSetting/LvsList";
    }

    @GetMapping("/LvsSystem")
    public String lvsSystemSettings(Model model) {
        List<LvsSetting> lvsSystemSettings = lvsSettingService.lvsGetSettingsByGroup("system");
        List<LvsSetting> lvsEmailSettings = lvsSettingService.lvsGetSettingsByGroup("email");
        List<LvsSetting> lvsPaymentSettings = lvsSettingService.lvsGetSettingsByGroup("payment");

        model.addAttribute("LvsSystemSettings", lvsSystemSettings);
        model.addAttribute("LvsEmailSettings", lvsEmailSettings);
        model.addAttribute("LvsPaymentSettings", lvsPaymentSettings);

        return "LvsAreas/LvsAdmin/LvsSetting/LvsSystem";
    }

    @GetMapping("/LvsBackup")
    public String lvsBackupSettings(Model model) {
        try {
            String lvsBackupData = lvsSettingService.lvsBackupSettings();
            model.addAttribute("LvsBackupData", lvsBackupData);
            model.addAttribute("LvsSuccess", "Sao lưu thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "LvsAreas/LvsAdmin/LvsSetting/LvsBackup";
    }

    @PostMapping("/LvsRestore")
    public String lvsRestoreSettings(@RequestParam String lvsBackupData, Model model) {
        try {
            lvsSettingService.lvsRestoreSettings(lvsBackupData);
            model.addAttribute("LvsSuccess", "Khôi phục thành công!");
        } catch (Exception e) {
            model.addAttribute("LvsError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/LvsAdmin/LvsSetting/LvsList";
    }
}
