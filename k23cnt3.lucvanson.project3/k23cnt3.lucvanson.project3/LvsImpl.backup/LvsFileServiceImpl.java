package k23cnt3.lucvanson.project3.LvsService.LvsImpl;

import k23cnt3.lucvanson.project3.LvsService.LvsFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Service implementation cho quản lý file
 * Xử lý upload, download, xóa file
 */
@Service
@Transactional
public class LvsFileServiceImpl implements LvsFileService {

    @Value("${app.upload.dir}")
    private String lvsUploadDir;

    @Value("${app.max-file-size}")
    private long lvsMaxFileSize;

    /**
     * Upload file
     * 
     * @param lvsFile   File cần upload
     * @param lvsFolder Thư mục đích
     * @return Đường dẫn file
     */
    @Override
    public String lvsUploadFile(MultipartFile lvsFile, String lvsFolder) throws IOException {
        if (lvsFile.isEmpty()) {
            throw new IOException("File trống");
        }

        // Kiểm tra kích thước file
        if (!lvsIsValidFileSize(lvsFile, lvsMaxFileSize)) {
            throw new IOException("File quá lớn. Kích thước tối đa: " + lvsMaxFileSize / (1024 * 1024) + "MB");
        }

        // Tạo tên file duy nhất
        String lvsOriginalFilename = lvsFile.getOriginalFilename();
        String lvsFileExtension = lvsOriginalFilename != null
                ? lvsOriginalFilename.substring(lvsOriginalFilename.lastIndexOf("."))
                : "";
        String lvsFileName = UUID.randomUUID().toString() + lvsFileExtension;

        // Tạo thư mục nếu chưa tồn tại
        Path lvsUploadPath = Paths.get(lvsUploadDir, lvsFolder);
        if (!Files.exists(lvsUploadPath)) {
            Files.createDirectories(lvsUploadPath);
        }

        // Lưu file
        Path lvsFilePath = lvsUploadPath.resolve(lvsFileName);
        Files.copy(lvsFile.getInputStream(), lvsFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về đường dẫn tương đối
        return Paths.get(lvsFolder, lvsFileName).toString().replace("\\", "/");
    }

    /**
     * Upload nhiều file
     * 
     * @param lvsFiles  Danh sách file
     * @param lvsFolder Thư mục đích
     * @return Danh sách đường dẫn
     */
    @Override
    public List<String> lvsUploadMultipleFiles(List<MultipartFile> lvsFiles, String lvsFolder) throws IOException {
        List<String> lvsFilePaths = new ArrayList<>();

        for (MultipartFile lvsFile : lvsFiles) {
            if (!lvsFile.isEmpty()) {
                String lvsFilePath = lvsUploadFile(lvsFile, lvsFolder);
                lvsFilePaths.add(lvsFilePath);
            }
        }

        return lvsFilePaths;
    }

    /**
     * Upload image
     * 
     * @param lvsImage  Ảnh cần upload
     * @param lvsFolder Thư mục đích
     * @return Đường dẫn ảnh
     */
    @Override
    public String lvsUploadImage(MultipartFile lvsImage, String lvsFolder) throws IOException {
        // Kiểm tra loại file
        List<String> lvsAllowedTypes = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
        if (!lvsIsValidFileType(lvsImage, lvsAllowedTypes)) {
            throw new IOException("Loại file không được hỗ trợ. Chỉ chấp nhận: JPEG, PNG, GIF, WebP");
        }

        return lvsUploadFile(lvsImage, lvsFolder);
    }

    /**
     * Upload avatar
     * 
     * @param lvsAvatar Avatar cần upload
     * @return Đường dẫn avatar
     */
    @Override
    public String lvsUploadAvatar(MultipartFile lvsAvatar) throws IOException {
        // Nén ảnh avatar
        byte[] lvsImageData = lvsCompressImage(lvsAvatar.getBytes(), 0.7f);

        // Tạo thumbnail
        byte[] lvsThumbnailData = lvsCreateThumbnail(lvsImageData, 200, 200);

        // Lưu thumbnail
        String lvsFileName = UUID.randomUUID().toString() + ".jpg";
        Path lvsAvatarPath = Paths.get(lvsUploadDir, "avatars");

        if (!Files.exists(lvsAvatarPath)) {
            Files.createDirectories(lvsAvatarPath);
        }

        Path lvsFilePath = lvsAvatarPath.resolve(lvsFileName);
        Files.write(lvsFilePath, lvsThumbnailData);

        return Paths.get("avatars", lvsFileName).toString().replace("\\", "/");
    }

    /**
     * Upload file dự án
     * 
     * @param lvsFile      File cần upload
     * @param lvsProjectId ID dự án
     * @return Đường dẫn file
     */
    @Override
    public String lvsUploadProjectFile(MultipartFile lvsFile, Long lvsProjectId) throws IOException {
        // Kiểm tra loại file
        List<String> lvsAllowedTypes = Arrays.asList(
                "application/zip", "application/x-rar-compressed", "application/x-7z-compressed",
                "application/pdf", "text/plain", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        if (!lvsIsValidFileType(lvsFile, lvsAllowedTypes)) {
            throw new IOException("Loại file không được hỗ trợ cho dự án");
        }

        String lvsFolder = "projects/" + lvsProjectId + "/files";
        return lvsUploadFile(lvsFile, lvsFolder);
    }

    /**
     * Upload ảnh dự án
     * 
     * @param lvsImages    Danh sách ảnh
     * @param lvsProjectId ID dự án
     * @return Danh sách đường dẫn ảnh
     */
    @Override
    public List<String> lvsUploadProjectImages(List<MultipartFile> lvsImages, Long lvsProjectId) throws IOException {
        List<String> lvsImagePaths = new ArrayList<>();
        String lvsFolder = "projects/" + lvsProjectId + "/images";

        for (MultipartFile lvsImage : lvsImages) {
            if (!lvsImage.isEmpty()) {
                String lvsImagePath = lvsUploadImage(lvsImage, lvsFolder);
                lvsImagePaths.add(lvsImagePath);
            }
        }

        return lvsImagePaths;
    }

    /**
     * Upload file đính kèm tin nhắn
     * 
     * @param lvsFile      File cần upload
     * @param lvsMessageId ID tin nhắn
     * @return Đường dẫn file
     */
    @Override
    public String lvsUploadMessageAttachment(MultipartFile lvsFile, Long lvsMessageId) throws IOException {
        // Giới hạn kích thước file đính kèm tin nhắn
        long lvsMaxMessageFileSize = 10 * 1024 * 1024; // 10MB

        if (!lvsIsValidFileSize(lvsFile, lvsMaxMessageFileSize)) {
            throw new IOException("File đính kèm quá lớn. Kích thước tối đa: 10MB");
        }

        String lvsFolder = "messages/" + lvsMessageId + "/attachments";
        return lvsUploadFile(lvsFile, lvsFolder);
    }

    /**
     * Download file
     * 
     * @param lvsFilePath Đường dẫn file
     * @return Dữ liệu file
     */
    @Override
    public byte[] lvsDownloadFile(String lvsFilePath) throws IOException {
        Path lvsFullPath = Paths.get(lvsUploadDir, lvsFilePath);

        if (!Files.exists(lvsFullPath)) {
            throw new IOException("File không tồn tại: " + lvsFilePath);
        }

        return Files.readAllBytes(lvsFullPath);
    }

    /**
     * Xóa file
     * 
     * @param lvsFilePath Đường dẫn file
     * @return true nếu thành công
     */
    @Override
    public boolean lvsDeleteFile(String lvsFilePath) {
        try {
            Path lvsFullPath = Paths.get(lvsUploadDir, lvsFilePath);
            return Files.deleteIfExists(lvsFullPath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Lấy URL file
     * 
     * @param lvsFilePath Đường dẫn file
     * @return URL file
     */
    @Override
    public String lvsGetFileUrl(String lvsFilePath) {
        return "/uploads/" + lvsFilePath;
    }

    /**
     * Kiểm tra loại file hợp lệ
     * 
     * @param lvsFile         File cần kiểm tra
     * @param lvsAllowedTypes Danh sách loại file cho phép
     * @return true nếu hợp lệ
     */
    @Override
    public boolean lvsIsValidFileType(MultipartFile lvsFile, List<String> lvsAllowedTypes) {
        String lvsContentType = lvsFile.getContentType();
        return lvsContentType != null && lvsAllowedTypes.contains(lvsContentType);
    }

    /**
     * Kiểm tra kích thước file
     * 
     * @param lvsFile    File cần kiểm tra
     * @param lvsMaxSize Kích thước tối đa (bytes)
     * @return true nếu hợp lệ
     */
    @Override
    public boolean lvsIsValidFileSize(MultipartFile lvsFile, long lvsMaxSize) {
        return lvsFile.getSize() <= lvsMaxSize;
    }

    /**
     * Nén ảnh
     * 
     * @param lvsImageData Dữ liệu ảnh
     * @param lvsQuality   Chất lượng (0.0 - 1.0)
     * @return Dữ liệu ảnh đã nén
     */
    @Override
    public byte[] lvsCompressImage(byte[] lvsImageData, float lvsQuality) throws IOException {
        try (ByteArrayInputStream lvsBais = new ByteArrayInputStream(lvsImageData);
                ByteArrayOutputStream lvsBaos = new ByteArrayOutputStream()) {

            BufferedImage lvsImage = ImageIO.read(lvsBais);

            // Chuyển đổi sang RGB nếu là ảnh PNG có alpha channel
            if (lvsImage.getColorModel().hasAlpha()) {
                BufferedImage lvsNewImage = new BufferedImage(
                        lvsImage.getWidth(), lvsImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D lvsGraphics = lvsNewImage.createGraphics();
                lvsGraphics.drawImage(lvsImage, 0, 0, Color.WHITE, null);
                lvsGraphics.dispose();
                lvsImage = lvsNewImage;
            }

            // Ghi ảnh với chất lượng đã chỉ định
            javax.imageio.ImageWriter lvsWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            javax.imageio.ImageWriteParam lvsParam = lvsWriter.getDefaultWriteParam();

            if (lvsParam.canWriteCompressed()) {
                lvsParam.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
                lvsParam.setCompressionQuality(lvsQuality);
            }

            try (javax.imageio.stream.ImageOutputStream lvsOutputStream = ImageIO.createImageOutputStream(lvsBaos)) {
                lvsWriter.setOutput(lvsOutputStream);
                lvsWriter.write(null, new javax.imageio.IIOImage(lvsImage, null, null), lvsParam);
            }

            lvsWriter.dispose();
            return lvsBaos.toByteArray();
        }
    }

    /**
     * Tạo thumbnail
     * 
     * @param lvsImageData Dữ liệu ảnh
     * @param lvsWidth     Chiều rộng
     * @param lvsHeight    Chiều cao
     * @return Dữ liệu thumbnail
     */
    @Override
    public byte[] lvsCreateThumbnail(byte[] lvsImageData, int lvsWidth, int lvsHeight) throws IOException {
        try (ByteArrayInputStream lvsBais = new ByteArrayInputStream(lvsImageData);
                ByteArrayOutputStream lvsBaos = new ByteArrayOutputStream()) {

            BufferedImage lvsOriginalImage = ImageIO.read(lvsBais);

            // Tính toán kích thước mới giữ tỷ lệ
            int lvsOriginalWidth = lvsOriginalImage.getWidth();
            int lvsOriginalHeight = lvsOriginalImage.getHeight();

            double lvsAspectRatio = (double) lvsOriginalWidth / lvsOriginalHeight;
            int lvsNewWidth, lvsNewHeight;

            if (lvsOriginalWidth > lvsOriginalHeight) {
                lvsNewWidth = lvsWidth;
                lvsNewHeight = (int) (lvsWidth / lvsAspectRatio);
            } else {
                lvsNewHeight = lvsHeight;
                lvsNewWidth = (int) (lvsHeight * lvsAspectRatio);
            }

            // Tạo thumbnail
            BufferedImage lvsThumbnail = new BufferedImage(lvsNewWidth, lvsNewHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D lvsGraphics = lvsThumbnail.createGraphics();
            lvsGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            lvsGraphics.drawImage(lvsOriginalImage, 0, 0, lvsNewWidth, lvsNewHeight, null);
            lvsGraphics.dispose();

            // Ghi thumbnail
            ImageIO.write(lvsThumbnail, "jpg", lvsBaos);
            return lvsBaos.toByteArray();
        }
    }

    /**
     * Lấy thông tin file
     * 
     * @param lvsFilePath Đường dẫn file
     * @return Thông tin file
     */
    @Override
    public Map<String, Object> lvsGetFileInfo(String lvsFilePath) throws IOException {
        Map<String, Object> lvsInfo = new HashMap<>();
        Path lvsFullPath = Paths.get(lvsUploadDir, lvsFilePath);

        if (!Files.exists(lvsFullPath)) {
            throw new IOException("File không tồn tại");
        }

        java.nio.file.attribute.BasicFileAttributes lvsAttrs = Files.readAttributes(
                lvsFullPath, java.nio.file.attribute.BasicFileAttributes.class);

        lvsInfo.put("fileName", lvsFullPath.getFileName().toString());
        lvsInfo.put("filePath", lvsFilePath);
        lvsInfo.put("fileSize", lvsAttrs.size());
        lvsInfo.put("createdAt", lvsAttrs.creationTime().toMillis());
        lvsInfo.put("modifiedAt", lvsAttrs.lastModifiedTime().toMillis());
        lvsInfo.put("isDirectory", lvsAttrs.isDirectory());
        lvsInfo.put("url", lvsGetFileUrl(lvsFilePath));

        // Lấy MIME type
        String lvsContentType = Files.probeContentType(lvsFullPath);
        lvsInfo.put("contentType", lvsContentType != null ? lvsContentType : "application/octet-stream");

        return lvsInfo;
    }
}