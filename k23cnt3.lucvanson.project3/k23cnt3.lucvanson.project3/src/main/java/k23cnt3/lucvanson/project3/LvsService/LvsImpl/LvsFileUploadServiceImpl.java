package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsService.LvsFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * LvsFileUploadServiceImpl - Implementation of file upload service
 * 
 * Features:
 * - Unique file naming (UUID + timestamp)
 * - Directory creation and management
 * - File type validation
 * - File size validation
 * - Support for multiple file uploads
 * 
 * @author LucVanSon
 * @version 1.0
 */
@Service
public class LvsFileUploadServiceImpl implements LvsFileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String lvsUploadDir;

    @Value("${app.max-file-size:10485760}")
    private long lvsMaxFileSize; // 10MB default

    // Allowed image extensions
    private static final List<String> LVS_ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "bmp");

    // Allowed MIME types for images
    private static final List<String> LVS_ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp");

    @Override
    public String lvsSaveFile(MultipartFile lvsFile, String lvsSubDirectory) throws IOException {
        // Validate file
        lvsValidateFile(lvsFile);
        lvsValidateImageFile(lvsFile);

        // Create directory if not exists
        Path lvsUploadPath = Paths.get(lvsUploadDir, lvsSubDirectory);
        if (!Files.exists(lvsUploadPath)) {
            Files.createDirectories(lvsUploadPath);
        }

        // Generate unique filename
        String lvsOriginalFilename = lvsFile.getOriginalFilename();
        String lvsExtension = lvsGetFileExtension(lvsOriginalFilename);
        String lvsUniqueFilename = lvsGenerateUniqueFilename(lvsExtension);

        // Save file
        Path lvsFilePath = lvsUploadPath.resolve(lvsUniqueFilename);
        Files.copy(lvsFile.getInputStream(), lvsFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative URL path
        return "/uploads/" + lvsSubDirectory + "/" + lvsUniqueFilename;
    }

    @Override
    public List<String> lvsSaveFiles(List<MultipartFile> lvsFiles, String lvsSubDirectory) throws IOException {
        List<String> lvsFileUrls = new ArrayList<>();

        for (MultipartFile lvsFile : lvsFiles) {
            if (!lvsFile.isEmpty()) {
                String lvsFileUrl = lvsSaveFile(lvsFile, lvsSubDirectory);
                lvsFileUrls.add(lvsFileUrl);
            }
        }

        return lvsFileUrls;
    }

    @Override
    public boolean lvsDeleteFile(String lvsFileUrl) {
        try {
            if (lvsFileUrl == null || lvsFileUrl.isEmpty()) {
                return false;
            }

            // Remove leading slash if present
            String lvsRelativePath = lvsFileUrl.startsWith("/") ? lvsFileUrl.substring(1) : lvsFileUrl;

            // Remove "uploads/" prefix if present
            if (lvsRelativePath.startsWith("uploads/")) {
                lvsRelativePath = lvsRelativePath.substring(8);
            }

            Path lvsFilePath = Paths.get(lvsUploadDir, lvsRelativePath);

            if (Files.exists(lvsFilePath)) {
                Files.delete(lvsFilePath);
                return true;
            }

            return false;
        } catch (IOException e) {
            System.err.println("Error deleting file: " + lvsFileUrl + " - " + e.getMessage());
            return false;
        }
    }

    @Override
    public void lvsValidateFile(MultipartFile lvsFile) throws IllegalArgumentException {
        if (lvsFile == null || lvsFile.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống!");
        }

        if (lvsFile.getSize() > lvsMaxFileSize) {
            throw new IllegalArgumentException(
                    "File quá lớn! Kích thước tối đa: " + (lvsMaxFileSize / 1024 / 1024) + "MB");
        }

        String lvsOriginalFilename = lvsFile.getOriginalFilename();
        if (lvsOriginalFilename == null || lvsOriginalFilename.isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ!");
        }
    }

    @Override
    public void lvsValidateImageFile(MultipartFile lvsFile) throws IllegalArgumentException {
        String lvsOriginalFilename = lvsFile.getOriginalFilename();
        String lvsExtension = lvsGetFileExtension(lvsOriginalFilename);

        if (!LVS_ALLOWED_IMAGE_EXTENSIONS.contains(lvsExtension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Định dạng file không được hỗ trợ! Chỉ chấp nhận: " +
                            String.join(", ", LVS_ALLOWED_IMAGE_EXTENSIONS));
        }

        String lvsContentType = lvsFile.getContentType();
        if (lvsContentType == null || !LVS_ALLOWED_IMAGE_TYPES.contains(lvsContentType.toLowerCase())) {
            throw new IllegalArgumentException("File không phải là ảnh hợp lệ!");
        }
    }

    @Override
    public String lvsGetFullPath(String lvsFileUrl) {
        if (lvsFileUrl == null || lvsFileUrl.isEmpty()) {
            return null;
        }

        // Remove leading slash if present
        String lvsRelativePath = lvsFileUrl.startsWith("/") ? lvsFileUrl.substring(1) : lvsFileUrl;

        // Remove "uploads/" prefix if present
        if (lvsRelativePath.startsWith("uploads/")) {
            lvsRelativePath = lvsRelativePath.substring(8);
        }

        return Paths.get(lvsUploadDir, lvsRelativePath).toString();
    }

    /**
     * Generate unique filename using UUID and timestamp
     * 
     * @param lvsExtension File extension
     * @return Unique filename
     */
    private String lvsGenerateUniqueFilename(String lvsExtension) {
        String lvsUuid = UUID.randomUUID().toString().replace("-", "");
        long lvsTimestamp = System.currentTimeMillis();
        return lvsUuid + "_" + lvsTimestamp + "." + lvsExtension;
    }

    /**
     * Extract file extension from filename
     * 
     * @param lvsFilename Original filename
     * @return File extension (without dot)
     */
    private String lvsGetFileExtension(String lvsFilename) {
        if (lvsFilename == null || !lvsFilename.contains(".")) {
            return "";
        }
        return lvsFilename.substring(lvsFilename.lastIndexOf(".") + 1);
    }
}
