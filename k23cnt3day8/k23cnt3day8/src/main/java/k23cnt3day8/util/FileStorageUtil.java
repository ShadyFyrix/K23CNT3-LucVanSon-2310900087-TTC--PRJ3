package k23cnt3day8.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${app.upload.base-dir:./uploads}")
    private String baseUploadDir;

    /**
     * Lưu file và trả về URL để truy cập
     */
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Tạo thư mục con
        Path uploadPath = Paths.get(baseUploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // Lưu file
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath);

        // Trả về URL (không bao gồm base path)
        return String.format("/uploads/%s/%s", subDirectory, newFileName);
    }

    /**
     * Xóa file
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // Extract subDirectory và filename từ URL
            String[] parts = fileUrl.split("/uploads/");
            if (parts.length < 2) {
                return false;
            }

            String relativePath = parts[1];
            Path filePath = Paths.get(baseUploadDir, relativePath);

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get base upload directory
     */
    public String getBaseUploadDir() {
        return baseUploadDir;
    }
}