package k23cnt3.lucvanson.project3.LvsConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LvsWebConfig - Web configuration for static resources
 * 
 * Configures:
 * - Static resource handlers for uploaded files
 * - Maps /uploads/** URLs to filesystem directory
 * 
 * @author LucVanSon
 * @version 1.0
 */
@Configuration
public class LvsWebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String lvsUploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Get absolute path to uploads directory
        Path lvsUploadPath = Paths.get(lvsUploadDir).toAbsolutePath().normalize();
        String lvsUploadLocation = "file:" + lvsUploadPath.toString() + "/";

        // Map /uploads/** to the uploads directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(lvsUploadLocation)
                .setCachePeriod(3600); // Cache for 1 hour

        System.out.println("[LVS CONFIG] Upload directory mapped: /uploads/** -> " + lvsUploadLocation);
    }
}
