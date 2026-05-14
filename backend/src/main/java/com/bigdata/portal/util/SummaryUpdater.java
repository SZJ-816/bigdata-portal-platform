package com.bigdata.portal.util;

import com.bigdata.portal.entity.News;
import com.bigdata.portal.mapper.NewsMapper;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SummaryUpdater {

    private static final Logger logger = LoggerFactory.getLogger(SummaryUpdater.class);

    @Autowired
    private NewsMapper newsMapper;

    public int updateAllInvalidSummaries() {
        List<News> newsList = newsMapper.findAll();
        int updatedCount = 0;

        for (News news : newsList) {
            if (!isValidSummary(news.getSummary())) {
                String newSummary = generateBetterSummary(news.getTitle(), news.getContent());
                if (newSummary != null && !newSummary.isEmpty()) {
                    news.setSummary(newSummary);
                    newsMapper.update(news);
                    updatedCount++;
                    logger.info("Updated summary for news: {}", news.getTitle());
                }
            }
        }

        logger.info("Total updated summaries: {}", updatedCount);
        return updatedCount;
    }

    private boolean isValidSummary(String summary) {
        if (summary == null || summary.trim().isEmpty()) {
            return false;
        }
        String trimmed = summary.trim();
        if (trimmed.length() < 20) {
            return false;
        }
        String[] placeholders = {"点击查看", "阅读全文", "查看原文", "更多详情", "来源:", "原文链接", "http://", "https://"};
        for (String placeholder : placeholders) {
            if (trimmed.length() < 50 && trimmed.contains(placeholder)) {
                return false;
            }
        }
        if (trimmed.startsWith("本文介绍了") && trimmed.endsWith("相关内容，点击查看详情。")) {
            return false;
        }
        return true;
    }

    private String generateBetterSummary(String title, String content) {
        if (content != null && !content.isEmpty()) {
            String text = Jsoup.parse(content).text().trim();
            if (text.length() > 50) {
                int endIndex = text.indexOf("。");
                if (endIndex > 0 && endIndex < 200) {
                    return text.substring(0, endIndex + 1);
                }
                endIndex = text.indexOf("；");
                if (endIndex > 0 && endIndex < 200) {
                    return text.substring(0, endIndex + 1);
                }
                endIndex = text.indexOf("!");
                if (endIndex > 0 && endIndex < 200) {
                    return text.substring(0, endIndex + 1);
                }
                endIndex = text.indexOf("?");
                if (endIndex > 0 && endIndex < 200) {
                    return text.substring(0, endIndex + 1);
                }
                if (text.length() > 100) {
                    return text.substring(0, 150) + "...";
                }
                return text;
            }
        }

        return generateDefaultSummary(title);
    }

    private String generateDefaultSummary(String title) {
        if (title == null) {
            return "阅读更多";
        }
        String[] prefixes = {"【", "【", "(", "（", "[", "《"};
        String[] suffixes = {"】", "】", ")", "）", "]", "》"};

        String cleanTitle = title;
        for (int i = 0; i < prefixes.length; i++) {
            if (cleanTitle.startsWith(prefixes[i]) && cleanTitle.endsWith(suffixes[i])) {
                cleanTitle = cleanTitle.substring(1, cleanTitle.length() - 1);
                break;
            }
        }

        if (cleanTitle.contains("｜")) {
            cleanTitle = cleanTitle.split("｜")[0];
        }
        if (cleanTitle.contains("|")) {
            cleanTitle = cleanTitle.split("\\|")[0];
        }

        return "本文围绕" + cleanTitle + "展开，探讨相关背景、技术细节及发展趋势。";
    }
}