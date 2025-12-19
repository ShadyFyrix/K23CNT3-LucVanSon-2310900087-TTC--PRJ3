package k23cnt3.lucvanson.project3.LvsConfig;

import k23cnt3.lucvanson.project3.LvsService.LvsFileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component khởi tạo các thư mục cần thiết khi ứng dụng start
 */
@Component
public class LvsStartupConfig implements CommandLineRunner {

    @Autowired
    private LvsFileUploadService lvsFileUploadService;

    @Override
    public void run(String... args) throws Exception {
        // Tạo các thư mục upload khi ứng dụng khởi động
        lvsFileUploadService.lvsInitializeUploadDirectories();
        System.out.println("✓ Đã khởi tạo thư mục uploads");
    }
}
