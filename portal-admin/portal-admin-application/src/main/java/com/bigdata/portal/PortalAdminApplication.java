package com.bigdata.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 智讯科技资讯聚合平台 - 后台管理系统启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.bigdata.portal")
public class PortalAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalAdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("   智讯科技资讯聚合平台 启动成功！");
        System.out.println("========================================");
    }
}
