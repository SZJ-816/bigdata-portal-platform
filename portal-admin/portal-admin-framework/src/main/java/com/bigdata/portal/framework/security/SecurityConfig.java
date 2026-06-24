package com.bigdata.portal.framework.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置
 * 采用JWT无状态认证方式
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（使用JWT不需要）
                .csrf().disable()
                // 无状态Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置请求授权
                .authorizeRequests()
                // 登录接口放行
                .antMatchers("/auth/login", "/auth/register").permitAll()
                // 用户注册相关接口放行
                .antMatchers("/users/login", "/users/register", "/users/send-code").permitAll()
                // 静态资源放行
                .antMatchers(HttpMethod.GET, "/static/**", "/*.html", "/*.js", "/*.css").permitAll()
                // Swagger文档放行
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 前端公开API放行（GET请求）
                .antMatchers(HttpMethod.GET,
                    "/api/news", "/api/news/**",
                    "/api/analytics/**",
                    "/api/channels", "/api/channels/**",
                    "/api/image/**",
                    "/cms/article/list", "/cms/article/channels", "/cms/article/channel/**", "/cms/article/channel-key/**", "/cms/channel/list",
                    "/cms/article/detail/*",
                    "/statistics/**",
                    "/ai/search", "/ai/search/stream", "/ai/hot-summary", "/ai/translate",
                    "/ai/config/list", "/ai/config/default", "/ai/config/*",
                    "/community/comment/list", "/community/comment/article/**",
                    "/system/user/list", "/system/user/*",
                    "/image/**"
                ).permitAll()
                // 图片上传接口放行
                .antMatchers(HttpMethod.POST, "/image/upload").permitAll()
                // 图片访问和代理接口放行（所有方法）
                .antMatchers("/image/**").permitAll()
                // AI配置管理接口放行（POST/PUT/DELETE）
                .antMatchers("/ai/config/**").permitAll()
                // 行为上报接口放行（支持GET和POST）
                .antMatchers(HttpMethod.GET, "/api/behaviors", "/api/behaviors/**", "/community/behavior", "/community/behavior/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/behaviors", "/api/behaviors/**", "/community/behavior", "/community/behavior/**").permitAll()
                // 管理后台接口放行（新闻增删改、用户管理、评论管理）
                .antMatchers("/cms/article/**").permitAll()
                .antMatchers("/cms/channel/**").permitAll()
                .antMatchers("/community/comment/**").permitAll()
                .antMatchers("/system/user/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
                .and()
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 禁用默认登录页
                .formLogin().disable()
                // 禁用默认登出
                .logout().disable()
                // 禁用缓存
                .headers().cacheControl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 留空，实际认证逻辑由JwtAuthenticationFilter处理
    }
}
