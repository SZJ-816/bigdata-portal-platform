package com.bigdata.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/admin/**", "/api/tools/**")
                .excludePathPatterns(
                        "/api/users/login",
                        "/api/users/register",
                        "/api/users/send-code",
                        "/api/news/**",
                        "/api/ai/**",
                        "/api/image/**",
                        "/api/behaviors",
                        "/api/analytics/**"
                );
    }
}
