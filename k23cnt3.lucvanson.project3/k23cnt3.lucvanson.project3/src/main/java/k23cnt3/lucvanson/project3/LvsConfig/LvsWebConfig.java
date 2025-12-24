package k23cnt3.lucvanson.project3.LvsConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Web MVC
 * Serve static files từ thư mục uploads
 */
@Configuration
public class LvsWebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve uploaded files from uploads directory
        // Uses dynamic path from application.properties for portability
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "classpath:/static/uploads/", // For files in JAR
                        "file:" + uploadDir // For external uploads
                )
                .setCachePeriod(3600);
    }
}
