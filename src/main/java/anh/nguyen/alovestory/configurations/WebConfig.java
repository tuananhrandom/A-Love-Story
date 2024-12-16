package anh.nguyen.alovestory.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để phục vụ file tĩnh từ thư mục uploads nằm ngoài source code
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:E:/uploads/");
    }
}
