package k23cnt3.lucvanson.project3.LvsService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service interface cho quản lý file
 * Xử lý upload, download, xóa file
 */
public interface LvsFileService {

    // Upload file
    String lvsUploadFile(MultipartFile lvsFile, String lvsFolder) throws IOException;

    // Upload nhiều file
    List<String> lvsUploadMultipleFiles(List<MultipartFile> lvsFiles, String lvsFolder) throws IOException;

    // Upload image
    String lvsUploadImage(MultipartFile lvsImage, String lvsFolder) throws IOException;

    // Upload avatar
    String lvsUploadAvatar(MultipartFile lvsAvatar) throws IOException;

    // Upload file dự án
    String lvsUploadProjectFile(MultipartFile lvsFile, Long lvsProjectId) throws IOException;

    // Upload ảnh dự án
    List<String> lvsUploadProjectImages(List<MultipartFile> lvsImages, Long lvsProjectId) throws IOException;

    // Upload file đính kèm tin nhắn
    String lvsUploadMessageAttachment(MultipartFile lvsFile, Long lvsMessageId) throws IOException;

    // Download file
    byte[] lvsDownloadFile(String lvsFilePath) throws IOException;

    // Xóa file
    boolean lvsDeleteFile(String lvsFilePath);

    // Lấy URL file
    String lvsGetFileUrl(String lvsFilePath);

    // Kiểm tra loại file hợp lệ
    boolean lvsIsValidFileType(MultipartFile lvsFile, List<String> lvsAllowedTypes);

    // Kiểm tra kích thước file
    boolean lvsIsValidFileSize(MultipartFile lvsFile, long lvsMaxSize);

    // Nén ảnh
    byte[] lvsCompressImage(byte[] lvsImageData, float lvsQuality) throws IOException;

    // Tạo thumbnail
    byte[] lvsCreateThumbnail(byte[] lvsImageData, int lvsWidth, int lvsHeight) throws IOException;

    // Lấy thông tin file
    Map<String, Object> lvsGetFileInfo(String lvsFilePath) throws IOException;
}
