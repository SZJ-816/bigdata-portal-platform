package com.bigdata.portal.service;

import com.bigdata.portal.entity.News;
import com.bigdata.portal.mapper.NewsMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RssService {

    private static final Logger logger = LoggerFactory.getLogger(RssService.class);

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private ImageGeneratorService imageGeneratorService;

    @Autowired
    private AiService aiService;

    private static final Map<String, String> RSS_FEEDS = new LinkedHashMap<>();

    static {
        RSS_FEEDS.put("36氪", "https://www.36kr.com/feed");
        RSS_FEEDS.put("IT之家", "https://www.ithome.com/rss");
        RSS_FEEDS.put("雷锋网", "https://www.leiphone.com/feed");
    }

    private static final Map<String, List<String>> CHANNEL_KEYWORDS = new HashMap<>();

    static {
        CHANNEL_KEYWORDS.put("AI", Arrays.asList(
            "AI", "人工智能", "机器学习", "深度学习", "大模型", "大语言模型", "LLM",
            "GPT", "ChatGPT", "Claude", "Gemini", "OpenAI", "DeepSeek",
            "神经网络", "自然语言", "计算机视觉", "智能体", "Agent",
            "AIGC", "生成式", "文生图", "文生视频", "智能驾驶", "自动驾驶",
            "机器人", "Chatbot", "对话式", "多模态", "推理模型"
        ));
        CHANNEL_KEYWORDS.put("大数据", Arrays.asList(
            "大数据", "数据治理", "数据分析", "数据仓库", "数据湖",
            "Hadoop", "Spark", "Flink", "Kafka", "Hive",
            "ETL", "数据中台", "数据资产", "数据安全", "数据可视化",
            "ClickHouse", "Doris", "Presto", "数据平台", "数据工程",
            "实时计算", "流计算", "批处理", "数据集成"
        ));
        CHANNEL_KEYWORDS.put("云计算", Arrays.asList(
            "云计算", "云原生", "云服务", "公有云", "私有云", "混合云",
            "Kubernetes", "K8s", "Docker", "容器", "微服务",
            "Serverless", "DevOps", "CI/CD", "云服务器", "ECS",
            "对象存储", "OSS", "S3", "负载均衡", "CDN",
            "阿里云", "腾讯云", "华为云", "AWS", "Azure",
            "IaaS", "PaaS", "SaaS", "基础设施", "弹性伸缩"
        ));
        CHANNEL_KEYWORDS.put("互联网", Arrays.asList(
            "互联网", "电商", "社交", "短视频", "直播", "外卖",
            "抖音", "微信", "微博", "小红书", "B站",
            "O2O", "SaaS", "平台经济", "共享经济", "数字营销",
            "流量", "用户增长", "产品经理", "用户体验", "UX",
            "移动互联网", "App", "小程序", "Web3", "元宇宙",
            "在线教育", "远程办公", "数字化", "转型"
        ));
        CHANNEL_KEYWORDS.put("创业", Arrays.asList(
            "创业", "融资", "投资", "IPO", "上市", "独角兽",
            "A轮", "B轮", "C轮", "天使轮", "种子轮",
            "风投", "VC", "PE", "估值", "并购",
            "创始人", "CEO", "商业模式", "盈利", "营收",
            "众筹", "孵化器", "加速器"
        ));
        CHANNEL_KEYWORDS.put("硬件", Arrays.asList(
            "硬件", "芯片", "半导体", "CPU", "GPU", "处理器",
            "手机", "电脑", "笔记本", "平板", "智能手表",
            "显卡", "内存", "SSD", "硬盘", "主板",
            "5G", "通信", "射频", "物联网", "IoT",
            "传感器", "嵌入式", "PCB", "光刻机", "晶圆",
            "NVIDIA", "AMD", "Intel", "高通", "联发科",
            "散热", "电源", "机箱", "显示器", "外设"
        ));
    }

    private static final Set<String> ALLOWED_PROTOCOLS = new HashSet<>(Arrays.asList("http", "https"));

    private static final int MAX_CONTENT_LENGTH = 3000;

    public int fetchAndSave() {
        int totalSaved = 0;
        for (Map.Entry<String, String> entry : RSS_FEEDS.entrySet()) {
            String sourceName = entry.getKey();
            String feedUrl = entry.getValue();
            try {
                int saved = fetchFeed(feedUrl, sourceName);
                totalSaved += saved;
                logger.info("抓取 {}: 成功 {} 条", sourceName, saved);
            } catch (Exception e) {
                logger.error("抓取 {} 失败: {}", sourceName, e.getMessage());
            }
        }
        return totalSaved;
    }

    private String categorizeChannel(String title, String summary) {
        String text = (title + " " + summary).toLowerCase();

        Map<String, Integer> scores = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : CHANNEL_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword.toLowerCase())) {
                    score++;
                }
            }
            if (score > 0) {
                scores.put(entry.getKey(), score);
            }
        }

        if (scores.isEmpty()) {
            String[] channels = {"AI", "大数据", "云计算", "互联网", "创业", "硬件"};
            int index = Math.abs(title.hashCode()) % channels.length;
            return channels[index];
        }

        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("互联网");
    }

    private int fetchFeed(String feedUrl, String sourceName) throws Exception {
        HttpURLConnection connection = createConnection(feedUrl);

        try (InputStream rawInputStream = connection.getInputStream()) {
            String xmlContent = readAndCleanXml(rawInputStream);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(
                    xmlContent.getBytes(StandardCharsets.UTF_8))) {
                feed = input.build(new XmlReader(bais));
            }

            int savedCount = 0;
            for (SyndEntry entry : feed.getEntries()) {
                String entryUrl = entry.getLink();
                if (entryUrl == null || entryUrl.isEmpty()) continue;

                if (newsMapper.findBySourceUrl(entryUrl) != null) continue;

                String summary = extractSummary(entry);
                String channel = categorizeChannel(entry.getTitle(), summary);

                News news = new News();
                news.setTitle(entry.getTitle());
                news.setSourceUrl(entryUrl);
                news.setSource(sourceName);
                news.setChannel(channel);
                news.setPublishTime(entry.getPublishedDate() != null ? entry.getPublishedDate() : new Date());
                news.setViewCount(0);
                news.setCommentCount(0);
                news.setIsBreaking(0);

                news.setSummary(summary);

                String content = buildFullContent(entry, summary);
                news.setContent(content);

                news.setImageUrl(extractImageUrl(entry, channel));

                try {
                    String translated = aiService.translateNews(news.getTitle(), news.getSummary());
                    if (translated != null) {
                        String[] parts = translated.split("\\|\\|\\|");
                        if (parts.length >= 1 && !parts[0].trim().isEmpty()) {
                            news.setTitle(parts[0].trim());
                        }
                        if (parts.length >= 2 && !parts[1].trim().isEmpty()) {
                            news.setSummary(parts[1].trim());
                        }
                    }
                } catch (Exception e) {
                    logger.warn("翻译新闻失败: {}", e.getMessage());
                }

                newsMapper.insert(news);
                savedCount++;
            }
            return savedCount;
        } finally {
            connection.disconnect();
        }
    }

    private HttpURLConnection createConnection(String feedUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(feedUrl).openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        connection.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml, */*");
        connection.setInstanceFollowRedirects(true);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
            responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
            responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
            String newUrl = connection.getHeaderField("Location");
            if (newUrl != null) {
                connection.disconnect();
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                connection.setRequestProperty("Accept", "application/rss+xml, application/xml, text/xml, */*");
            }
        }
        return connection;
    }

    private String readAndCleanXml(InputStream inputStream) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int n;
        while ((n = inputStream.read(tmp)) != -1) {
            baos.write(tmp, 0, n);
        }
        byte[] buffer = baos.toByteArray();
        String xml = new String(buffer, StandardCharsets.UTF_8);

        xml = xml.replaceAll("(?i)<!DOCTYPE[^>]*>", "");
        xml = xml.replaceAll("(?i)<!ENTITY[^>]*>", "");

        if (!xml.contains("<?xml")) {
            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
        }

        return xml;
    }

    private String extractSummary(SyndEntry entry) {
        String summary = "";

        if (entry.getDescription() != null) {
            String desc = cleanHtml(entry.getDescription().getValue());
            if (isValidSummary(desc)) {
                summary = desc;
            }
        }

        if (!isValidSummary(summary) && entry.getContents() != null && !entry.getContents().isEmpty()) {
            for (Object contentObj : entry.getContents()) {
                String text = extractContentText(contentObj);
                if (isValidSummary(text)) {
                    summary = text;
                    break;
                }
            }
        }

        if (!isValidSummary(summary)) {
            summary = extractFromContent(entry);
        }

        if (!isValidSummary(summary)) {
            summary = generateSummaryFromTitle(entry.getTitle());
        }

        if (summary.length() > 400) {
            summary = summary.substring(0, 400) + "...";
        }
        return summary.trim();
    }

    private String extractFromContent(SyndEntry entry) {
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            for (Object contentObj : entry.getContents()) {
                if (contentObj instanceof com.rometools.rome.feed.synd.SyndContent) {
                    com.rometools.rome.feed.synd.SyndContent content =
                        (com.rometools.rome.feed.synd.SyndContent) contentObj;
                    String text = cleanHtml(content.getValue());
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
            }
        }
        return "";
    }

    private boolean isValidSummary(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        String trimmed = text.trim();
        if (trimmed.length() < 20) {
            return false;
        }
        String[] placeholders = {"点击查看", "阅读全文", "查看原文", "更多详情", "来源:", "原文链接", "http://", "https://"};
        for (String placeholder : placeholders) {
            if (trimmed.length() < 50 && trimmed.contains(placeholder)) {
                return false;
            }
        }
        return true;
    }

    private String generateSummaryFromTitle(String title) {
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

        return "本文介绍了" + cleanTitle + "相关内容，点击查看详情。";
    }

    private String buildFullContent(SyndEntry entry, String summary) {
        StringBuilder content = new StringBuilder();

        String title = entry.getTitle();
        if (title != null) {
            content.append("<h2>").append(escapeHtml(title)).append("</h2>");
        }

        content.append("<p class=\"lead\">").append(escapeHtml(summary)).append("</p>");

        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            for (Object contentObj : entry.getContents()) {
                String text = extractContentText(contentObj);
                if (text.length() > 100) {
                    String[] paragraphs = text.split("\\n+");
                    for (String p : paragraphs) {
                        p = p.trim();
                        if (p.length() > 50) {
                            content.append("<p>").append(escapeHtml(p)).append("</p>");
                        }
                    }
                }
            }
        }

        if (content.length() < 200 && entry.getDescription() != null) {
            String desc = entry.getDescription().getValue();
            String text = cleanHtml(desc);
            if (!text.equals(summary)) {
                String[] paragraphs = text.split("\\n+");
                for (String p : paragraphs) {
                    p = p.trim();
                    if (p.length() > 30 && !p.equals(escapeHtml(summary))) {
                        content.append("<p>").append(escapeHtml(p)).append("</p>");
                    }
                }
            }
        }

        String link = entry.getLink();
        if (link != null && !link.isEmpty()) {
            content.append("<div class=\"source-link\">");
            content.append("<p>原文链接: <a href=\"").append(link).append("\" target=\"_blank\">").append(link).append("</a></p>");
            content.append("</div>");
        }

        String result = content.toString();
        if (result.length() > MAX_CONTENT_LENGTH) {
            result = result.substring(0, MAX_CONTENT_LENGTH);
        }

        return result;
    }

    private String cleanHtml(String html) {
        if (html == null) return "";
        return Jsoup.parse(html).text().trim();
    }

    private String extractContentText(Object contentObj) {
        if (contentObj == null) {
            return "";
        }
        if (contentObj instanceof com.rometools.rome.feed.synd.SyndContent) {
            return ((com.rometools.rome.feed.synd.SyndContent) contentObj).getValue();
        }
        return contentObj.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    private static final Map<String, String> CHANNEL_DEFAULT_IMAGES = new HashMap<>();

    static {
        CHANNEL_DEFAULT_IMAGES.put("AI", "/images/channel-ai.svg");
        CHANNEL_DEFAULT_IMAGES.put("大数据", "/images/channel-bigdata.svg");
        CHANNEL_DEFAULT_IMAGES.put("云计算", "/images/channel-cloud.svg");
        CHANNEL_DEFAULT_IMAGES.put("互联网", "/images/channel-internet.svg");
        CHANNEL_DEFAULT_IMAGES.put("硬件", "/images/channel-hardware.svg");
        CHANNEL_DEFAULT_IMAGES.put("创业", "/images/channel-startup.svg");
    }

    private String extractImageUrl(SyndEntry entry, String channel) {
        String rssImage = extractImageFromRssEntry(entry);
        if (rssImage != null && !rssImage.isEmpty()) {
            logger.debug("从RSS条目提取到图片: {}", rssImage);
            return rssImage;
        }

        String channelImage = CHANNEL_DEFAULT_IMAGES.get(channel);
        if (channelImage != null) {
            return channelImage;
        }
        return imageGeneratorService.generateImageUrl(entry.getTitle(), channel);
    }

    private String extractImageFromRssEntry(SyndEntry entry) {
        if (entry.getEnclosures() != null && !entry.getEnclosures().isEmpty()) {
            for (Object enc : entry.getEnclosures()) {
                if (enc instanceof com.rometools.rome.feed.synd.SyndEnclosure) {
                    com.rometools.rome.feed.synd.SyndEnclosure enclosure =
                        (com.rometools.rome.feed.synd.SyndEnclosure) enc;
                    String url = enclosure.getUrl();
                    if (url != null && !url.isEmpty() && isImageUrl(url)) {
                        return url;
                    }
                }
            }
        }

        if (entry.getDescription() != null && entry.getDescription().getValue() != null) {
            String img = extractImageFromHtml(entry.getDescription().getValue());
            if (img != null && isImageUrl(img)) {
                return img;
            }
        }

        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            for (Object contentObj : entry.getContents()) {
                if (contentObj instanceof com.rometools.rome.feed.synd.SyndContent) {
                    String value = ((com.rometools.rome.feed.synd.SyndContent) contentObj).getValue();
                    String img = extractImageFromHtml(value);
                    if (img != null && isImageUrl(img)) {
                        return img;
                    }
                }
            }
        }

        return null;
    }

    private boolean isImageUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        String lower = url.toLowerCase();
        if (lower.contains(".jpg") || lower.contains(".jpeg") || lower.contains(".png")
            || lower.contains(".gif") || lower.contains(".webp") || lower.contains(".bmp")) {
            return true;
        }
        if (lower.contains("image") || lower.contains("img") || lower.contains("photo")
            || lower.contains("pic") || lower.contains("thumb") || lower.contains("cover")) {
            return true;
        }
        return false;
    }

    private boolean isAllowedUrl(String url) {
        if (url == null || url.isEmpty()) return false;
        try {
            URL parsedUrl = new URL(url);
            String protocol = parsedUrl.getProtocol().toLowerCase();
            return ALLOWED_PROTOCOLS.contains(protocol);
        } catch (Exception e) {
            return false;
        }
    }

    private String extractImageFromHtml(String html) {
        if (html == null || html.isEmpty()) return null;

        Document doc = Jsoup.parse(html);
        Elements imgs = doc.select("img[src]");
        if (!imgs.isEmpty()) {
            return imgs.first().attr("src");
        }

        return null;
    }

}
