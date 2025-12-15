package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsSetting;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý cài đặt hệ thống
 * Xử lý CRUD cài đặt, lưu cấu hình hệ thống
 */
public interface LvsSettingService {

    // Lấy cài đặt theo ID
    LvsSetting lvsGetSettingById(Integer lvsSettingId);

    // Lấy cài đặt theo key
    LvsSetting lvsGetSettingByKey(String lvsKey);

    // Lấy tất cả cài đặt
    List<LvsSetting> lvsGetAllSettings();

    // Lấy cài đặt theo group
    List<LvsSetting> lvsGetSettingsByGroup(String lvsGroup);

    // Lấy cài đặt theo public
    List<LvsSetting> lvsGetPublicSettings();

    // Lưu cài đặt
    LvsSetting lvsSaveSetting(LvsSetting lvsSetting);

    // Cập nhật cài đặt
    LvsSetting lvsUpdateSetting(LvsSetting lvsSetting);

    // Xóa cài đặt
    void lvsDeleteSetting(Integer lvsSettingId);

    // Lưu nhiều cài đặt cùng lúc
    void lvsBatchSaveSettings(Map<String, String> lvsSettings);

    // Lấy giá trị cài đặt
    String lvsGetSettingValue(String lvsKey);

    // Lấy giá trị cài đặt với giá trị mặc định
    String lvsGetSettingValue(String lvsKey, String lvsDefaultValue);

    // Lấy giá trị cài đặt dạng số
    Integer lvsGetSettingValueAsInt(String lvsKey, Integer lvsDefaultValue);

    // Lấy giá trị cài đặt dạng boolean
    Boolean lvsGetSettingValueAsBoolean(String lvsKey, Boolean lvsDefaultValue);

    // Lấy giá trị cài đặt dạng double
    Double lvsGetSettingValueAsDouble(String lvsKey, Double lvsDefaultValue);

    // Lấy cài đặt theo nhóm
    Map<String, List<LvsSetting>> lvsGetSettingsGroupedByGroup();

    // Sao lưu cài đặt
    String lvsBackupSettings();

    // Khôi phục cài đặt
    void lvsRestoreSettings(String lvsBackupData);

    // Tải cài đặt mặc định
    void lvsLoadDefaultSettings();

    // Lấy cài đặt hệ thống
    Map<String, String> lvsGetSystemSettings();

    // Lấy cài đặt email
    Map<String, String> lvsGetEmailSettings();

    // Lấy cài đặt thanh toán
    Map<String, String> lvsGetPaymentSettings();
}