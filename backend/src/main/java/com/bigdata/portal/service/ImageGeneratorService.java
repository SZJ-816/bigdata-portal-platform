package com.bigdata.portal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGeneratorService.class);

    @Value("${image.generator.enabled:true}")
    private boolean enabled;

    @Value("${image.generator.api-url:}")
    private String apiUrl;

    @Value("${image.generator.api-key:}")
    private String apiKey;

    private static final Map<String, String> CHANNEL_IMAGES = new HashMap<>();
    static {
        CHANNEL_IMAGES.put("AI", "/images/channel-ai.svg");
        CHANNEL_IMAGES.put("人工智能", "/images/channel-ai.svg");
        CHANNEL_IMAGES.put("创业", "/images/channel-startup.svg");
        CHANNEL_IMAGES.put("互联网", "/images/channel-internet.svg");
        CHANNEL_IMAGES.put("大数据", "/images/channel-bigdata.svg");
        CHANNEL_IMAGES.put("云计算", "/images/channel-cloud.svg");
        CHANNEL_IMAGES.put("硬件", "/images/channel-hardware.svg");
        CHANNEL_IMAGES.put("科技", "/images/channel-ai.svg");
        CHANNEL_IMAGES.put("软件", "/images/channel-cloud.svg");
    }

    private static final String DEFAULT_IMAGE = "/images/channel-internet.svg";

    public String generateImageUrl(String title, String channel) {
        String channelImage = CHANNEL_IMAGES.get(channel);
        if (channelImage != null) {
            return channelImage;
        }
        return DEFAULT_IMAGE;
    }
}
