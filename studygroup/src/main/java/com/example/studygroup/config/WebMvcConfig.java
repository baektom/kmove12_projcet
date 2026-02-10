package com.example.studygroup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 예: file.upload-dir=C:/studygroup_uploads/room_photos/
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:studygroup/src/main/resources/static/uploads/");

        // (추가) 스터디룸 사진
        // /uploads/xxx.jpg -> C:/studygroup_uploads/room_photos/xxx.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + ensureTrailingSlash(uploadDir));
    }

    private String ensureTrailingSlash(String path) {
        if (path == null || path.isBlank()) return path;
        return path.endsWith("/") ? path : (path + "/");
    }
}
