package k23cnt3.lucvanson.project3.LvsService;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

/**
 * LvsFileUploadService - Service interface for file upload operations
 * 
 * Handles:
 * - Image upload (thumbnails, project images)
 * - File validation (size, type, extension)
 * - File storage and retrieval
 * - File deletion
 * 
 * @author LucVanSon
 * @version 1.0
 */
public interface LvsFileUploadService {

    /**
     * Save a single file to the upload directory
     * 
     * @param lvsFile         MultipartFile to save
     * @param lvsSubDirectory Subdirectory within uploads (e.g., "projects",
     *                        "avatars")
     * @return Relative URL path to the saved file (e.g.,
     *         "/uploads/projects/image_123.jpg")
     * @throws IOException If file cannot be saved
     */
    String lvsSaveFile(MultipartFile lvsFile, String lvsSubDirectory) throws IOException;

    /**
     * Save multiple files to the upload directory
     * 
     * @param lvsFiles        List of MultipartFiles to save
     * @param lvsSubDirectory Subdirectory within uploads
     * @return List of relative URL paths to the saved files
     * @throws IOException If files cannot be saved
     */
    List<String> lvsSaveFiles(List<MultipartFile> lvsFiles, String lvsSubDirectory) throws IOException;

    /**
     * Delete a file from the upload directory
     * 
     * @param lvsFileUrl Relative URL path to the file (e.g.,
     *                   "/uploads/projects/image_123.jpg")
     * @return true if file was deleted successfully, false otherwise
     */
    boolean lvsDeleteFile(String lvsFileUrl);

    /**
     * Validate file before upload
     * 
     * @param lvsFile MultipartFile to validate
     * @throws IllegalArgumentException If file is invalid
     */
    void lvsValidateFile(MultipartFile lvsFile) throws IllegalArgumentException;

    /**
     * Validate image file (checks if file is an image)
     * 
     * @param lvsFile MultipartFile to validate
     * @throws IllegalArgumentException If file is not a valid image
     */
    void lvsValidateImageFile(MultipartFile lvsFile) throws IllegalArgumentException;

    /**
     * Get the full file system path from a relative URL
     * 
     * @param lvsFileUrl Relative URL path (e.g., "/uploads/projects/image_123.jpg")
     * @return Full file system path
     */
    String lvsGetFullPath(String lvsFileUrl);
}
