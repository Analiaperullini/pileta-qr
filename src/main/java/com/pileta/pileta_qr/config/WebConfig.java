package com.pileta.pileta_qr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String base = Paths.get(System.getProperty("user.dir"), "uploads", "fotos")
                .toUri().toString();

        registry.addResourceHandler("/fotos/**")
                .addResourceLocations(base);
    }
}
