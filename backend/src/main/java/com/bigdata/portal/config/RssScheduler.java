package com.bigdata.portal.config;

import com.bigdata.portal.service.RssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RssScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RssScheduler.class);

    @Autowired
    private RssService rssService;

    @Scheduled(fixedDelay = 300000)
    public void fetchRssNews() {
        logger.info("开始定时抓取 RSS 新闻...");
        try {
            int count = rssService.fetchAndSave();
            logger.info("本次抓取完成，共新增 {} 条新闻", count);
        } catch (Exception e) {
            logger.error("定时抓取 RSS 失败: {}", e.getMessage());
        }
    }
}
