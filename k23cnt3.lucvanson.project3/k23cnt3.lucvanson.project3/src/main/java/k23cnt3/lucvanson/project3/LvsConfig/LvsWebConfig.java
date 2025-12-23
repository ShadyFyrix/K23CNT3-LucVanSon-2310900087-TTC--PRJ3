package k23cnt3.lucvanson.project3.LvsConfig;

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

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve uploaded files from static/uploads
        // Maps /uploads/** to work in both admin and user contexts
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/", "file:src/main/resources/static/uploads/")
                .setCachePeriod(3600);
    }
}
