package com.bigdata.portal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DistributeNews {
    
    private static final String[] TARGET_CHANNELS = {"AI", "大数据", "云计算", "互联网", "硬件", "创业"};
    
    private static final Map<String, List<String>> CHANNEL_KEYWORDS = new HashMap<>();
    static {
        List<String> aiKeywords = new ArrayList<>();
        aiKeywords.add("ai");
        aiKeywords.add("gpt");
        aiKeywords.add("llm");
        aiKeywords.add("大语言模型");
        aiKeywords.add("人工智能");
        aiKeywords.add("机器学习");
        aiKeywords.add("深度学习");
        aiKeywords.add("robot");
        aiKeywords.add("机器人");
        aiKeywords.add("算法");
        aiKeywords.add("模型");
        CHANNEL_KEYWORDS.put("AI", aiKeywords);
        
        List<String> bigDataKeywords = new ArrayList<>();
        bigDataKeywords.add("大数据");
        bigDataKeywords.add("数据");
        bigDataKeywords.add("spark");
        bigDataKeywords.add("hadoop");
        bigDataKeywords.add("数据库");
        bigDataKeywords.add("数据分析");
        CHANNEL_KEYWORDS.put("大数据", bigDataKeywords);
        
        List<String> cloudKeywords = new ArrayList<>();
        cloudKeywords.add("cloud");
        cloudKeywords.add("云");
        cloudKeywords.add("aws");
        cloudKeywords.add("azure");
        cloudKeywords.add("阿里云");
        cloudKeywords.add("云原生");
        cloudKeywords.add("docker");
        cloudKeywords.add("kubernetes");
        CHANNEL_KEYWORDS.put("云计算", cloudKeywords);
        
        List<String> internetKeywords = new ArrayList<>();
        internetKeywords.add("互联网");
        internetKeywords.add("web");
        internetKeywords.add("app");
        internetKeywords.add("应用");
        internetKeywords.add("平台");
        internetKeywords.add("产品");
        internetKeywords.add("运营");
        CHANNEL_KEYWORDS.put("互联网", internetKeywords);
        
        List<String> hardwareKeywords = new ArrayList<>();
        hardwareKeywords.add("硬件");
        hardwareKeywords.add("芯片");
        hardwareKeywords.add("cpu");
        hardwareKeywords.add("gpu");
        hardwareKeywords.add("手机");
        hardwareKeywords.add("iphone");
        hardwareKeywords.add("pc");
        hardwareKeywords.add("电脑");
        hardwareKeywords.add("设备");
        hardwareKeywords.add("半导体");
        CHANNEL_KEYWORDS.put("硬件", hardwareKeywords);
        
        List<String> startupKeywords = new ArrayList<>();
        startupKeywords.add("创业");
        startupKeywords.add("投资");
        startupKeywords.add("融资");
        startupKeywords.add("公司");
        startupKeywords.add("企业");
        startupKeywords.add("techcrunch");
        startupKeywords.add("创业公司");
        CHANNEL_KEYWORDS.put("创业", startupKeywords);
    }
    
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://192.168.146.128:3306/bigdata_portal?useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root123";
        
        Random random = new Random();
        
        System.out.println("======================================================================");
        System.out.println("重新分配新闻到各个频道");
        System.out.println("======================================================================");
        
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            
            // 先查看当前频道分布
            System.out.println("\n当前频道分布:");
            ResultSet rs = stmt.executeQuery("SELECT channel, COUNT(*) as count FROM news GROUP BY channel");
            while (rs.next()) {
                System.out.println("  " + rs.getString("channel") + ": " + rs.getInt("count") + " 条");
            }
            rs.close();
            
            // 获取所有新闻
            System.out.println("\n获取所有新闻...");
            List<Map<String, Object>> allNews = new ArrayList<>();
            rs = stmt.executeQuery("SELECT id, title, summary FROM news");
            while (rs.next()) {
                Map<String, Object> news = new HashMap<>();
                news.put("id", rs.getLong("id"));
                news.put("title", rs.getString("title"));
                news.put("summary", rs.getString("summary"));
                allNews.add(news);
            }
            rs.close();
            
            System.out.println("获取到 " + allNews.size() + " 条新闻");
            
            // 开始重新分配
            System.out.println("\n开始重新分配...");
            int updatedCount = 0;
            
            for (Map<String, Object> news : allNews) {
                Long newsId = (Long) news.get("id");
                String title = ((String) news.getOrDefault("title", "")).toLowerCase();
                String summary = ((String) news.getOrDefault("summary", "")).toLowerCase();
                
                // 根据关键词分配
                String newChannel = null;
                for (Map.Entry<String, List<String>> entry : CHANNEL_KEYWORDS.entrySet()) {
                    String channel = entry.getKey();
                    List<String> keywords = entry.getValue();
                    for (String keyword : keywords) {
                        if (title.contains(keyword) || summary.contains(keyword)) {
                            newChannel = channel;
                            break;
                        }
                    }
                    if (newChannel != null) break;
                }
                
                // 如果没有匹配到，随机分配
                if (newChannel == null) {
                    newChannel = TARGET_CHANNELS[random.nextInt(TARGET_CHANNELS.length)];
                }
                
                // 更新数据库
                String sql = "UPDATE news SET channel = '" + newChannel + "' WHERE id = " + newsId;
                stmt.executeUpdate(sql);
                updatedCount++;
                
                if (updatedCount % 10 == 0) {
                    System.out.println("  已更新 " + updatedCount + " 条...");
                }
            }
            
            System.out.println("\n✓ 成功重新分配 " + updatedCount + " 条新闻!");
            
            // 查看新的频道分布
            System.out.println("\n新的频道分布:");
            rs = stmt.executeQuery("SELECT channel, COUNT(*) as count FROM news GROUP BY channel");
            while (rs.next()) {
                String channel = rs.getString("channel");
                int count = rs.getInt("count");
                String status = count > 0 ? "✓" : "✗";
                System.out.println("  " + status + " " + channel + ": " + count + " 条");
            }
            rs.close();
            
            System.out.println("\n======================================================================");
            System.out.println("分配完成!");
            System.out.println("======================================================================");
            
        } catch (Exception e) {
            System.err.println("\n✗ 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
