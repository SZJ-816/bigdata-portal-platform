package com.bigdata.portal.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 配置静态资源映射（上传图片等）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.path:/app/news-images}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传图片目录
        String location = uploadPath;
        if (!location.startsWith("file:")) {
            location = "file:" + location;
        }
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/image/**")
                .addResourceLocations(location);
    }
}
