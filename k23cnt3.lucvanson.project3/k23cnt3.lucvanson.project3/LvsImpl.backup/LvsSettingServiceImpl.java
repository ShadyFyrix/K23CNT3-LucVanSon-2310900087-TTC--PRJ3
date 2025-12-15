package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsEntity.LvsSetting;
import k23cnt3.lucvanson.project3.LvsRepository.LvsSettingRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsSettingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation cho quản lý cài đặt hệ thống
 * Xử lý CRUD cài đặt, lưu cấu hình hệ thống
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsSettingServiceImpl implements LvsSettingService {

    private final LvsSettingRepository lvsSettingRepository;
    private final ObjectMapper lvsObjectMapper = new ObjectMapper();

    /**
     * Lấy cài đặt theo ID
     * @param lvsSettingId ID cài đặt
     * @return Cài đặt tìm thấy
     */
    @Override
    public LvsSetting lvsGetSettingById(Integer lvsSettingId) {
        return lvsSettingRepository.findById(lvsSettingId).orElse(null);
    }

    /**
     * Lấy cài đặt theo key
     * @param lvsKey Khóa cài đặt
     * @return Cài đặt tìm thấy
     */
    @Override
    public LvsSetting lvsGetSettingByKey(String lvsKey) {
        return lvsSettingRepository.findByLvsKey(lvsKey).orElse(null);
    }

    /**
     * Lấy tất cả cài đặt
     * @return Danh sách cài đặt
     */
    @Override
    public List<LvsSetting> lvsGetAllSettings() {
        return lvsSettingRepository.findAll();
    }

    /**
     * Lấy cài đặt theo group
     * @param lvsGroup Nhóm cài đặt
     * @return Danh sách cài đặt
     */
    @Override
    public List<LvsSetting> lvsGetSettingsByGroup(String lvsGroup) {
        return lvsSettingRepository.findByLvsGroup(lvsGroup);
    }

    /**
     * Lấy cài đặt theo public
     * @return Danh sách cài đặt công khai
     */
    @Override
    public List<LvsSetting> lvsGetPublicSettings() {
        return lvsSettingRepository.findByLvsIsPublicTrue();
    }

    /**
     * Lưu cài đặt
     * @param lvsSetting Thông tin cài đặt
     * @return Cài đặt đã lưu
     */
    @Override
    public LvsSetting lvsSaveSetting(LvsSetting lvsSetting) {
        lvsSetting.setLvsCreatedAt(LocalDateTime.now());
        lvsSetting.setLvsUpdatedAt(LocalDateTime.now());
        return lvsSettingRepository.save(lvsSetting);
    }

    /**
     * Cập nhật cài đặt
     * @param lvsSetting Thông tin cài đặt cập nhật
     * @return Cài đặt đã cập nhật
     */
    @Override
    public LvsSetting lvsUpdateSetting(LvsSetting lvsSetting) {
        LvsSetting lvsExistingSetting = lvsGetSettingByKey(lvsSetting.getLvsKey());
        if (lvsExistingSetting != null) {
            lvsExistingSetting.setLvsValue(lvsSetting.getLvsValue());
            lvsExistingSetting.setLvsGroup(lvsSetting.getLvsGroup());
            lvsExistingSetting.setLvsLabel(lvsSetting.getLvsLabel());
            lvsExistingSetting.setLvsDataType(lvsSetting.getLvsDataType());
            lvsExistingSetting.setLvsIsPublic(lvsSetting.getLvsIsPublic());
            lvsExistingSetting.setLvsDescription(lvsSetting.getLvsDescription());
            lvsExistingSetting.setLvsUpdatedAt(LocalDateTime.now());
            return lvsSettingRepository.save(lvsExistingSetting);
        }
        return lvsSaveSetting(lvsSetting);
    }

    /**
     * Xóa cài đặt
     * @param lvsSettingId ID cài đặt
     */
    @Override
    public void lvsDeleteSetting(Integer lvsSettingId) {
        lvsSettingRepository.deleteById(lvsSettingId);
    }

    /**
     * Lưu nhiều cài đặt cùng lúc
     * @param lvsSettings Map cài đặt
     */
    @Override
    public void lvsBatchSaveSettings(Map<String, String> lvsSettings) {
        for (Map.Entry<String, String> entry : lvsSettings.entrySet()) {
            LvsSetting lvsSetting = lvsGetSettingByKey(entry.getKey());
            if (lvsSetting != null) {
                lvsSetting.setLvsValue(entry.getValue());
                lvsSetting.setLvsUpdatedAt(LocalDateTime.now());
                lvsSettingRepository.save(lvsSetting);
            } else {
                lvsSetting = new LvsSetting();
                lvsSetting.setLvsKey(entry.getKey());
                lvsSetting.setLvsValue(entry.getValue());
                lvsSetting.setLvsGroup("custom");
                lvsSetting.setLvsLabel(entry.getKey());
                lvsSetting.setLvsDataType("STRING");
                lvsSetting.setLvsIsPublic(false);
                lvsSetting.setLvsCreatedAt(LocalDateTime.now());
                lvsSetting.setLvsUpdatedAt(LocalDateTime.now());
                lvsSettingRepository.save(lvsSetting);
            }
        }
    }

    /**
     * Lấy giá trị cài đặt
     * @param lvsKey Khóa cài đặt
     * @return Giá trị cài đặt
     */
    @Override
    public String lvsGetSettingValue(String lvsKey) {
        LvsSetting lvsSetting = lvsGetSettingByKey(lvsKey);
        return lvsSetting != null ? lvsSetting.getLvsValue() : null;
    }

    /**
     * Lấy giá trị cài đặt với giá trị mặc định
     * @param lvsKey Khóa cài đặt
     * @param lvsDefaultValue Giá trị mặc định
     * @return Giá trị cài đặt hoặc mặc định
     */
    @Override
    public String lvsGetSettingValue(String lvsKey, String lvsDefaultValue) {
        String lvsValue = lvsGetSettingValue(lvsKey);
        return lvsValue != null ? lvsValue : lvsDefaultValue;
    }

    /**
     * Lấy giá trị cài đặt dạng số
     * @param lvsKey Khóa cài đặt
     * @param lvsDefaultValue Giá trị mặc định
     * @return Giá trị số
     */
    @Override
    public Integer lvsGetSettingValueAsInt(String lvsKey, Integer lvsDefaultValue) {
        try {
            String lvsValue = lvsGetSettingValue(lvsKey);
            return lvsValue != null ? Integer.parseInt(lvsValue) : lvsDefaultValue;
        } catch (NumberFormatException e) {
            return lvsDefaultValue;
        }
    }

    /**
     * Lấy giá trị cài đặt dạng boolean
     * @param lvsKey Khóa cài đặt
     * @param lvsDefaultValue Giá trị mặc định
     * @return Giá trị boolean
     */
    @Override
    public Boolean lvsGetSettingValueAsBoolean(String lvsKey, Boolean lvsDefaultValue) {
        String lvsValue = lvsGetSettingValue(lvsKey);
        if (lvsValue != null) {
            return "true".equalsIgnoreCase(lvsValue) || "1".equals(lvsValue);
        }
        return lvsDefaultValue;
    }

    /**
     * Lấy giá trị cài đặt dạng double
     * @param lvsKey Khóa cài đặt
     * @param lvsDefaultValue Giá trị mặc định
     * @return Giá trị double
     */
    @Override
    public Double lvsGetSettingValueAsDouble(String lvsKey, Double lvsDefaultValue) {
        try {
            String lvsValue = lvsGetSettingValue(lvsKey);
            return lvsValue != null ? Double.parseDouble(lvsValue) : lvsDefaultValue;
        } catch (NumberFormatException e) {
            return lvsDefaultValue;
        }
    }

    /**
     * Lấy cài đặt theo nhóm
     * @return Map cài đặt theo nhóm
     */
    @Override
    public Map<String, List<LvsSetting>> lvsGetSettingsGroupedByGroup() {
        List<LvsSetting> lvsAllSettings = lvsGetAllSettings();
        return lvsAllSettings.stream()
                .collect(Collectors.groupingBy(LvsSetting::getLvsGroup));
    }

    /**
     * Sao lưu cài đặt
     * @return Dữ liệu sao lưu dạng JSON
     */
    @Override
    public String lvsBackupSettings() {
        try {
            List<LvsSetting> lvsAllSettings = lvsGetAllSettings();
            Map<String, Object> lvsBackupData = new HashMap<>();
            lvsBackupData.put("backupTime", LocalDateTime.now().toString());
            lvsBackupData.put("settings", lvsAllSettings);
            return lvsObjectMapper.writeValueAsString(lvsBackupData);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sao lưu cài đặt", e);
        }
    }

    /**
     * Khôi phục cài đặt
     * @param lvsBackupData Dữ liệu sao lưu
     */
    @Override
    public void lvsRestoreSettings(String lvsBackupData) {
        try {
            Map<String, Object> lvsBackupMap = lvsObjectMapper.readValue(lvsBackupData, new TypeReference<Map<String, Object>>() {});

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lvsSettingsList = (List<Map<String, Object>>) lvsBackupMap.get("settings");

            // Xóa tất cả cài đặt cũ
            lvsSettingRepository.deleteAll();

            // Khôi phục cài đặt mới
            for (Map<String, Object> lvsSettingMap : lvsSettingsList) {
                LvsSetting lvsSetting = new LvsSetting();
                lvsSetting.setLvsKey((String) lvsSettingMap.get("lvsKey"));
                lvsSetting.setLvsValue((String) lvsSettingMap.get("lvsValue"));
                lvsSetting.setLvsGroup((String) lvsSettingMap.get("lvsGroup"));
                lvsSetting.setLvsLabel((String) lvsSettingMap.get("lvsLabel"));
                lvsSetting.setLvsDataType((String) lvsSettingMap.get("lvsDataType"));
                lvsSetting.setLvsIsPublic((Boolean) lvsSettingMap.get("lvsIsPublic"));
                lvsSetting.setLvsDescription((String) lvsSettingMap.get("lvsDescription"));
                lvsSetting.setLvsCreatedAt(LocalDateTime.now());
                lvsSetting.setLvsUpdatedAt(LocalDateTime.now());

                lvsSettingRepository.save(lvsSetting);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi khôi phục cài đặt", e);
        }
    }

    /**
     * Tải cài đặt mặc định
     */
    @Override
    public void lvsLoadDefaultSettings() {
        // Cài đặt hệ thống
        lvsSaveDefaultSetting("site_name", "LucVanSon Project", "Hệ thống", "Tên website", "STRING", true, "Tên hiển thị của website");
        lvsSaveDefaultSetting("site_description", "Diễn đàn chia sẻ dự án", "Hệ thống", "Mô tả website", "STRING", true, "Mô tả ngắn về website");
        lvsSaveDefaultSetting("site_url", "http://localhost:8080", "Hệ thống", "URL website", "STRING", false, "URL chính của website");
        lvsSaveDefaultSetting("site_logo", "/images/logo.png", "Hệ thống", "Logo website", "STRING", true, "Đường dẫn đến logo");

        // Cài đặt email
        lvsSaveDefaultSetting("smtp_host", "smtp.gmail.com", "Email", "SMTP Host", "STRING", false, "Máy chủ SMTP");
        lvsSaveDefaultSetting("smtp_port", "587", "Email", "SMTP Port", "INT", false, "Cổng SMTP");
        lvsSaveDefaultSetting("smtp_username", "", "Email", "SMTP Username", "STRING", false, "Tên đăng nhập SMTP");
        lvsSaveDefaultSetting("smtp_password", "", "Email", "SMTP Password", "STRING", false, "Mật khẩu SMTP");
        lvsSaveDefaultSetting("email_from", "noreply@lucvanson.com", "Email", "Email gửi", "STRING", false, "Email hiển thị khi gửi");

        // Cài đặt thanh toán
        lvsSaveDefaultSetting("coin_rate", "1000", "Thanh toán", "Tỷ giá coin", "DOUBLE", true, "1 coin = ? VND");
        lvsSaveDefaultSetting("min_deposit", "10000", "Thanh toán", "Nạp tối thiểu", "DOUBLE", true, "Số tiền nạp tối thiểu (VND)");
        lvsSaveDefaultSetting("min_withdraw", "50000", "Thanh toán", "Rút tối thiểu", "DOUBLE", true, "Số tiền rút tối thiểu (VND)");
        lvsSaveDefaultSetting("platform_fee", "20", "Thanh toán", "Phí nền tảng", "DOUBLE", true, "Phần trăm phí nền tảng (%)");

        // Cài đặt dự án
        lvsSaveDefaultSetting("project_auto_approve", "false", "Dự án", "Tự động duyệt", "BOOLEAN", false, "Tự động duyệt dự án mới");
        lvsSaveDefaultSetting("project_max_price", "10000000", "Dự án", "Giá tối đa", "DOUBLE", true, "Giá tối đa của dự án");
        lvsSaveDefaultSetting("project_min_price", "1000", "Dự án", "Giá tối thiểu", "DOUBLE", true, "Giá tối thiểu của dự án");

        // Cài đặt bảo mật
        lvsSaveDefaultSetting("max_login_attempts", "5", "Bảo mật", "Số lần đăng nhập tối đa", "INT", false, "Số lần đăng nhập sai tối đa");
        lvsSaveDefaultSetting("session_timeout", "30", "Bảo mật", "Thời gian hết phiên (phút)", "INT", false, "Thời gian không hoạt động trước khi hết phiên");

        // Cài đặt giao diện
        lvsSaveDefaultSetting("theme", "default", "Giao diện", "Chủ đề", "STRING", true, "Chủ đề giao diện");
        lvsSaveDefaultSetting("items_per_page", "10", "Giao diện", "Số item mỗi trang", "INT", true, "Số lượng item hiển thị mỗi trang");
    }

    /**
     * Lưu cài đặt mặc định
     * @param lvsKey Khóa
     * @param lvsValue Giá trị
     * @param lvsGroup Nhóm
     * @param lvsLabel Nhãn
     * @param lvsDataType Kiểu dữ liệu
     * @param lvsIsPublic Công khai
     * @param lvsDescription Mô tả
     */
    private void lvsSaveDefaultSetting(String lvsKey, String lvsValue, String lvsGroup, String lvsLabel,
                                       String lvsDataType, Boolean lvsIsPublic, String lvsDescription) {
        if (lvsGetSettingByKey(lvsKey) == null) {
            LvsSetting lvsSetting = new LvsSetting();
            lvsSetting.setLvsKey(lvsKey);
            lvsSetting.setLvsValue(lvsValue);
            lvsSetting.setLvsGroup(lvsGroup);
            lvsSetting.setLvsLabel(lvsLabel);
            lvsSetting.setLvsDataType(lvsDataType);
            lvsSetting.setLvsIsPublic(lvsIsPublic);
            lvsSetting.setLvsDescription(lvsDescription);
            lvsSetting.setLvsCreatedAt(LocalDateTime.now());
            lvsSetting.setLvsUpdatedAt(LocalDateTime.now());
            lvsSettingRepository.save(lvsSetting);
        }
    }

    /**
     * Lấy cài đặt hệ thống
     * @return Map cài đặt hệ thống
     */
    @Override
    public Map<String, String> lvsGetSystemSettings() {
        return lvsGetSettingsByGroup("Hệ thống").stream()
                .collect(Collectors.toMap(LvsSetting::getLvsKey, LvsSetting::getLvsValue));
    }

    /**
     * Lấy cài đặt email
     * @return Map cài đặt email
     */
    @Override
    public Map<String, String> lvsGetEmailSettings() {
        return lvsGetSettingsByGroup("Email").stream()
                .collect(Collectors.toMap(LvsSetting::getLvsKey, LvsSetting::getLvsValue));
    }

    /**
     * Lấy cài đặt thanh toán
     * @return Map cài đặt thanh toán
     */
    @Override
    public Map<String, String> lvsGetPaymentSettings() {
        return lvsGetSettingsByGroup("Thanh toán").stream()
                .collect(Collectors.toMap(LvsSetting::getLvsKey, LvsSetting::getLvsValue));
    }
}