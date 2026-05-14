package com.bigdata.portal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class UpdateChannels {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://192.168.146.128:3306/bigdata_portal?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root123";
        
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("更新前各频道新闻数量:");
            ResultSet rs = stmt.executeQuery("SELECT channel, COUNT(*) as count FROM news GROUP BY channel");
            while (rs.next()) {
                System.out.println("  " + rs.getString("channel") + ": " + rs.getInt("count") + " 条");
            }
            rs.close();
            
            System.out.println("\n开始更新频道...");
            
            // 更新雷锋网的新闻为AI频道
            int rows1 = stmt.executeUpdate("UPDATE news SET channel = 'AI' WHERE source = '雷锋网'");
            System.out.println("更新雷锋网: " + rows1 + " 条");
            
            // 更新机器之心的新闻为AI频道
            int rows2 = stmt.executeUpdate("UPDATE news SET channel = 'AI' WHERE source = '机器之心'");
            System.out.println("更新机器之心: " + rows2 + " 条");
            
            // 更新InfoQ的新闻为大数据频道
            int rows3 = stmt.executeUpdate("UPDATE news SET channel = '大数据' WHERE source = 'InfoQ'");
            System.out.println("更新InfoQ: " + rows3 + " 条");
            
            // 更新虎嗅的新闻为互联网频道
            int rows4 = stmt.executeUpdate("UPDATE news SET channel = '互联网' WHERE source = '虎嗅'");
            System.out.println("更新虎嗅: " + rows4 + " 条");
            
            System.out.println("\n更新后各频道新闻数量:");
            rs = stmt.executeQuery("SELECT channel, COUNT(*) as count FROM news GROUP BY channel");
            while (rs.next()) {
                System.out.println("  " + rs.getString("channel") + ": " + rs.getInt("count") + " 条");
            }
            rs.close();
            
            System.out.println("\n频道更新完成!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}