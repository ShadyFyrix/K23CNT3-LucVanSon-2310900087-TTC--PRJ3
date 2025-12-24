package k23cnt3.lucvanson.project3.LvsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.PostConstruct;

/**
 * Service xử lý upload file ảnh
 * Hỗ trợ upload ảnh cho Post và Comment
 */
@Service
public class LvsFileUploadService {

    @Value("${app.upload.dir:src/main/resources/static/uploads/}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostConstruct
    public void init() {
        System.out.println("=".repeat(80));
        System.out.println("📁 UPLOAD DIRECTORY CONFIGURATION");
        System.out.println("=".repeat(80));
        System.out.println("Upload Dir: " + uploadDir);
        System.out.println("Working Dir: " + System.getProperty("user.dir"));
        System.out.println("Absolute Path: " + new java.io.File(uploadDir).getAbsolutePath());
        System.out.println("=".repeat(80));
    }

    /**
     * Lưu file ảnh vào thư mục uploads
     * 
     * @param file   File cần upload
     * @param folder Thư mục con (posts hoặc comments)
     * @return URL tương đối của file đã lưu
     * @throws IOException Nếu có lỗi khi lưu file
     */
    public String lvsSaveFile(MultipartFile file, String folder) throws IOException {
        // Validate file
        if (!lvsIsValidImage(file)) {
            throw new IOException("File không hợp lệ hoặc không phải ảnh");
        }

        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir + folder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = lvsGetFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // Lưu file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL tương đối (Thymeleaf @{} sẽ tự động thêm context path)
        // Format: /uploads/{folder}/{filename}
        return "/uploads/" + folder + "/" + newFilename;
    }

    /**
     * Lưu nhiều file ảnh cùng lúc
     * 
     * @param files  Danh sách file cần upload
     * @param folder Thư mục con (posts hoặc comments)
     * @return Danh sách URL của các file đã lưu
     * @throws IOException Nếu có lỗi khi lưu file
     */
    public List<String> lvsSaveFiles(List<MultipartFile> files, String folder) throws IOException {
        List<String> fileUrls = new java.util.ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String fileUrl = lvsSaveFile(file, folder);
                    fileUrls.add(fileUrl);
                }
            }
        }

        return fileUrls;
    }

    /**
     * Xóa file ảnh
     * 
     * @param fileUrl URL của file cần xóa
     */
    public void lvsDeleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Loại bỏ dấu / đầu tiên nếu có
            String filePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error nhưng không throw exception
            System.err.println("Không thể xóa file: " + fileUrl + " - " + e.getMessage());
        }
    }

    /**
     * Kiểm tra file có phải ảnh hợp lệ không
     * 
     * @param file File cần kiểm tra
     * @return true nếu file hợp lệ
     */
    public boolean lvsIsValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Kiểm tra kích thước
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        // Kiểm tra extension
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String extension = lvsGetFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return false;
        }

        // Kiểm tra content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        return true;
    }

    /**
     * Lấy extension của file
     */
    private String lvsGetFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Lưu file dự án (ZIP/RAR) vào thư mục uploads
     * Không validate như image file
     * 
     * @param file   File cần upload
     * @param folder Thư mục con (projects)
     * @return URL tương đối của file đã lưu
     * @throws IOException Nếu có lỗi khi lưu file
     */
    public String lvsSaveProjectFile(MultipartFile file, String folder) throws IOException {
        // Validate file không null và không empty
        if (file == null || file.isEmpty()) {
            throw new IOException("File không hợp lệ");
        }

        // Tạo thư mục nếu chưa tồn tại
        Path uploadPath = Paths.get(uploadDir + folder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String extension = lvsGetFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // Lưu file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL tương đối (Thymeleaf @{} sẽ tự động thêm context path)
        return "/uploads/" + folder + "/" + newFilename;
    }

    /**
     * Tạo thư mục uploads nếu chưa tồn tại
     */
    public void lvsInitializeUploadDirectories() {
        try {
            Files.createDirectories(Paths.get(uploadDir + "posts"));
            Files.createDirectories(Paths.get(uploadDir + "comments"));
            Files.createDirectories(Paths.get(uploadDir + "avatars"));
            Files.createDirectories(Paths.get(uploadDir + "projects"));
        } catch (IOException e) {
            System.err.println("Không thể tạo thư mục uploads: " + e.getMessage());
        }
    }
}
