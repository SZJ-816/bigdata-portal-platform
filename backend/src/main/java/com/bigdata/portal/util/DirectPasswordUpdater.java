package com.bigdata.portal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DirectPasswordUpdater {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://192.168.146.128:3306/bigdata_portal?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root123";
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("123456");
        
        System.out.println("Generated BCrypt hash: " + hashedPassword);
        
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            
            String sql1 = "UPDATE `user` SET password = '" + hashedPassword + "' WHERE username = 'admin'";
            String sql2 = "UPDATE `user` SET password = '" + hashedPassword + "' WHERE username = 'test'";
            
            int rows1 = stmt.executeUpdate(sql1);
            int rows2 = stmt.executeUpdate(sql2);
            
            System.out.println("Updated " + rows1 + " rows for admin");
            System.out.println("Updated " + rows2 + " rows for test");
            System.out.println("Password update completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}