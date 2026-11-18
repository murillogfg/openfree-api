package com.openfree_api.modules.uploads.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadWebConfig
        implements WebMvcConfigurer {

    /*
     * Mesma pasta onde o FileStorageService grava
     * (app.upload.dir / UPLOAD_DIR).
     */
    @Value("${app.upload.dir:uploads}")
    private String uploadDirectory;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        Path uploadDir =
                Paths.get(uploadDirectory)
                        .toAbsolutePath()
                        .normalize();

        String uploadPath =
                uploadDir.toUri().toString();

        registry
                .addResourceHandler(
                        "/uploads/**"
                )
                .addResourceLocations(
                        uploadPath
                );
    }
}