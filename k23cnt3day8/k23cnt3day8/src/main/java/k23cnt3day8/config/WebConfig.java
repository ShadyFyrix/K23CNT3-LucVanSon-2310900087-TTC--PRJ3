package k23cnt3day8.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Folder gốc lưu tất cả ảnh
        String rootFolder = "D:/book_images/";

        // Expose ảnh sách
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + rootFolder + "books/");

        // Expose ảnh tác giả
        registry.addResourceHandler("/uploads/authors/**")
                .addResourceLocations("file:" + rootFolder + "authors/");
    }
}
