package k23cnt3.lucvanson.project3.LvsService.LvsImpl;


import k23cnt3.lucvanson.project3.LvsEntity.LvsUser;
import k23cnt3.lucvanson.project3.LvsRepository.LvsUserRepository;
import k23cnt3.lucvanson.project3.LvsService.LvsAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation cho xác thực người dùng
 * Xử lý đăng nhập, đăng ký, quên mật khẩu
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LvsAuthServiceImpl implements LvsAuthService {

    private final LvsUserRepository lvsUserRepository;
    private final PasswordEncoder lvsPasswordEncoder;
    private final Map<String, String> lvsResetTokens = new HashMap<>();
    private final Map<String, String> lvsVerificationTokens = new HashMap<>();
    private final Map<String, LvsUser> lvsAuthTokens = new HashMap<>();

    /**
     * Xác thực đăng nhập
     * @param lvsUsername Tên đăng nhập
     * @param lvsPassword Mật khẩu
     * @return User nếu đăng nhập thành công
     */
    @Override
    public LvsUser lvsAuthenticate(String lvsUsername, String lvsPassword) {
        LvsUser lvsUser = lvsUserRepository.findByLvsUsername(lvsUsername).orElse(null);
        if (lvsUser != null && lvsPasswordEncoder.matches(lvsPassword, lvsUser.getLvsPassword())) {
            lvsUser.setLvsLastLogin(LocalDateTime.now());
            lvsUserRepository.save(lvsUser);
            return lvsUser;
        }
        return null;
    }

    /**
     * Đăng ký người dùng mới
     * @param lvsUser Thông tin user
     * @return User đã đăng ký
     */
    @Override
    public LvsUser lvsRegisterUser(LvsUser lvsUser) {
        // Kiểm tra username và email đã tồn tại
        if (lvsCheckUsernameExists(lvsUser.getLvsUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (lvsCheckEmailExists(lvsUser.getLvsEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Mã hóa mật khẩu
        lvsUser.setLvsPassword(lvsPasswordEncoder.encode(lvsUser.getLvsPassword()));
        lvsUser.setLvsCreatedAt(LocalDateTime.now());
        lvsUser.setLvsUpdatedAt(LocalDateTime.now());

        LvsUser lvsSavedUser = lvsUserRepository.save(lvsUser);

        // Gửi email xác thực
        lvsSendVerificationEmail(lvsSavedUser);

        return lvsSavedUser;
    }

    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa
     * @param lvsUsername Tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại
     */
    @Override
    public boolean lvsCheckUsernameExists(String lvsUsername) {
        return lvsUserRepository.findByLvsUsername(lvsUsername).isPresent();
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     * @param lvsEmail Email cần kiểm tra
     * @return true nếu đã tồn tại
     */
    @Override
    public boolean lvsCheckEmailExists(String lvsEmail) {
        return lvsUserRepository.findByLvsEmail(lvsEmail).isPresent();
    }

    /**
     * Gửi email đặt lại mật khẩu
     * @param lvsEmail Email cần gửi
     */
    @Override
    public void lvsSendPasswordResetEmail(String lvsEmail) {
        LvsUser lvsUser = lvsUserRepository.findByLvsEmail(lvsEmail).orElse(null);
        if (lvsUser != null) {
            String lvsToken = UUID.randomUUID().toString();
            lvsResetTokens.put(lvsToken, lvsEmail);
            // TODO: Gửi email thực tế với link reset
            System.out.println("Reset token for " + lvsEmail + ": " + lvsToken);
        }
    }

    /**
     * Đặt lại mật khẩu bằng token
     * @param lvsToken Token reset
     * @param lvsNewPassword Mật khẩu mới
     * @return true nếu thành công
     */
    @Override
    public boolean lvsResetPassword(String lvsToken, String lvsNewPassword) {
        String lvsEmail = lvsResetTokens.get(lvsToken);
        if (lvsEmail != null) {
            LvsUser lvsUser = lvsUserRepository.findByLvsEmail(lvsEmail).orElse(null);
            if (lvsUser != null) {
                lvsUser.setLvsPassword(lvsPasswordEncoder.encode(lvsNewPassword));
                lvsUser.setLvsUpdatedAt(LocalDateTime.now());
                lvsUserRepository.save(lvsUser);
                lvsResetTokens.remove(lvsToken);
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra token đặt lại mật khẩu
     * @param lvsToken Token cần kiểm tra
     * @return true nếu token hợp lệ
     */
    @Override
    public boolean lvsValidateResetToken(String lvsToken) {
        return lvsResetTokens.containsKey(lvsToken);
    }

    /**
     * Lấy user bằng token
     * @param lvsToken Token reset
     * @return User tìm thấy
     */
    @Override
    public LvsUser lvsGetUserByResetToken(String lvsToken) {
        String lvsEmail = lvsResetTokens.get(lvsToken);
        if (lvsEmail != null) {
            return lvsUserRepository.findByLvsEmail(lvsEmail).orElse(null);
        }
        return null;
    }

    /**
     * Tạo token xác thực
     * @param lvsUser User cần tạo token
     * @return Token xác thực
     */
    @Override
    public String lvsGenerateAuthToken(LvsUser lvsUser) {
        String lvsToken = UUID.randomUUID().toString();
        lvsAuthTokens.put(lvsToken, lvsUser);
        return lvsToken;
    }

    /**
     * Xác thực token
     * @param lvsToken Token cần xác thực
     * @return User nếu token hợp lệ
     */
    @Override
    public LvsUser lvsValidateAuthToken(String lvsToken) {
        return lvsAuthTokens.get(lvsToken);
    }

    /**
     * Đăng xuất
     * @param lvsToken Token cần hủy
     */
    @Override
    public void lvsLogout(String lvsToken) {
        lvsAuthTokens.remove(lvsToken);
    }

    /**
     * Gửi email xác thực tài khoản
     * @param lvsUser User cần xác thực
     */
    @Override
    public void lvsSendVerificationEmail(LvsUser lvsUser) {
        String lvsToken = UUID.randomUUID().toString();
        lvsVerificationTokens.put(lvsToken, lvsUser.getLvsEmail());
        // TODO: Gửi email thực tế
        System.out.println("Verification token for " + lvsUser.getLvsEmail() + ": " + lvsToken);
    }

    /**
     * Xác thực tài khoản bằng token
     * @param lvsToken Token xác thực
     * @return true nếu thành công
     */
    @Override
    public boolean lvsVerifyAccount(String lvsToken) {
        String lvsEmail = lvsVerificationTokens.get(lvsToken);
        if (lvsEmail != null) {
            // TODO: Cập nhật trạng thái xác thực cho user
            lvsVerificationTokens.remove(lvsToken);
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra tài khoản đã xác thực chưa
     * @param lvsUserId ID user
     * @return true nếu đã xác thực
     */
    @Override
    public boolean lvsIsAccountVerified(Long lvsUserId) {
        // TODO: Thêm trường isVerified vào entity LvsUser
        return true;
    }

    /**
     * Gửi lại email xác thực
     * @param lvsUserId ID user
     */
    @Override
    public void lvsResendVerificationEmail(Long lvsUserId) {
        LvsUser lvsUser = lvsUserRepository.findById(lvsUserId).orElse(null);
        if (lvsUser != null) {
            lvsSendVerificationEmail(lvsUser);
        }
    }
}