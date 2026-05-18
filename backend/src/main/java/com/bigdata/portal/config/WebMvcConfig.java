package com.bigdata.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Autowired
    private RequestLogInterceptor requestLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/api/**");

        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/ai/**", "/api/users/send-code");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/admin/**", "/api/tools/**", "/api/ai/**", "/api/analytics/**",
                        "/api/behaviors/**", "/api/news/**", "/api/channel/**", "/api/users/favorites/**",
                        "/api/image/**");
    }
}
