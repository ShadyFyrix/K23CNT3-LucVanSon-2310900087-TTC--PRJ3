package k23cnt3day8.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.base-dir:./uploads}")
    private String uploadBaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình resource handler cho tất cả các thư mục con trong uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadBaseDir + "/");
    }
}