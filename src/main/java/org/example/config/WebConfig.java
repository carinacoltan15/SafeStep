package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${safestep.frontend.path:}")
    private String frontendPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (frontendPath != null && !frontendPath.isBlank()) {
            String path = frontendPath.replace("\\", "/");
            if (!path.endsWith("/")) path += "/";
            registry.addResourceHandler("/**")
                    .addResourceLocations("file:" + path);
        }
    }
}
