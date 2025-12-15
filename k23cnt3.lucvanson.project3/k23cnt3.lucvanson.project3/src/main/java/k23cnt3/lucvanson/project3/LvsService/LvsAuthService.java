package k23cnt3.lucvanson.project3.LvsService;

import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;

/**
 * Service interface cho xác thực người dùng
 * Xử lý đăng nhập, đăng ký, quên mật khẩu
 */
public interface LvsAuthService {

    // Xác thực đăng nhập
    LvsUser lvsAuthenticate(String lvsUsername, String lvsPassword);

    // Đăng ký người dùng mới
    LvsUser lvsRegisterUser(LvsUser lvsUser);

    // Kiểm tra tên đăng nhập đã tồn tại chưa
    boolean lvsCheckUsernameExists(String lvsUsername);

    // Kiểm tra email đã tồn tại chưa
    boolean lvsCheckEmailExists(String lvsEmail);

    // Gửi email đặt lại mật khẩu
    void lvsSendPasswordResetEmail(String lvsEmail);

    // Đặt lại mật khẩu bằng token
    boolean lvsResetPassword(String lvsToken, String lvsNewPassword);

    // Kiểm tra token đặt lại mật khẩu
    boolean lvsValidateResetToken(String lvsToken);

    // Lấy user bằng token
    LvsUser lvsGetUserByResetToken(String lvsToken);

    // Tạo token xác thực
    String lvsGenerateAuthToken(LvsUser lvsUser);

    // Xác thực token
    LvsUser lvsValidateAuthToken(String lvsToken);

    // Đăng xuất
    void lvsLogout(String lvsToken);

    // Gửi email xác thực tài khoản
    void lvsSendVerificationEmail(LvsUser lvsUser);

    // Xác thực tài khoản bằng token
    boolean lvsVerifyAccount(String lvsToken);

    // Kiểm tra tài khoản đã xác thực chưa
    boolean lvsIsAccountVerified(Long lvsUserId);

    // Gửi lại email xác thực
    void lvsResendVerificationEmail(Long lvsUserId);
}