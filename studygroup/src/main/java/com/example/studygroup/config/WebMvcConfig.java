package com.example.studygroup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // ✅ application.properties의 설정을 읽어오되 없으면 'uploads' 폴더를 기본값으로 사용합니다.
    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 설정 파일에 명시된 외부 경로 매핑
        String formattedPath = ensureTrailingSlash(uploadDir);

        // 2. 프로젝트 루트의 'uploads' 폴더를 절대 경로로 변환하여 매핑 (가장 확실한 방법)
        Path rootPath = Paths.get("uploads").toAbsolutePath();
        String absolutePath = "file:" + rootPath.toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + formattedPath) // 외부 설정 경로
                .addResourceLocations(absolutePath);           // 프로젝트 루트 uploads 폴더
    }

    private String ensureTrailingSlash(String path) {
        if (path == null || path.isBlank()) return "uploads/";
        String res = path.replace("\\", "/"); // 윈도우 경로 대응
        return res.endsWith("/") ? res : (res + "/");
    }
}