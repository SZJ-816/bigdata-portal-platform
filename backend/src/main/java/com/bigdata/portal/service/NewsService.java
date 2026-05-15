package com.bigdata.portal.service;

import com.bigdata.portal.entity.Comment;
import com.bigdata.portal.entity.News;
import com.bigdata.portal.mapper.CommentMapper;
import com.bigdata.portal.mapper.NewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private CommentMapper commentMapper;

    private static final Map<String, String> CHANNEL_MAP = new HashMap<>();
    static {
        CHANNEL_MAP.put("人工智能", "AI");
        CHANNEL_MAP.put("AI", "AI");
        CHANNEL_MAP.put("大数据", "大数据");
        CHANNEL_MAP.put("云计算", "云计算");
        CHANNEL_MAP.put("互联网", "互联网");
        CHANNEL_MAP.put("硬件", "硬件");
        CHANNEL_MAP.put("创业", "创业");
    }

    public Map<String, Object> getList(int page, int size) {
        int offset = (page - 1) * size;
        List<News> list = newsMapper.findPage(offset, size);
        long total = newsMapper.count();
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public News getById(Long id) {
        newsMapper.incrementView(id);
        return newsMapper.findById(id);
    }

    public Map<String, Object> search(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<News> list = newsMapper.searchPage(keyword, offset, size);
        long total = newsMapper.countByKeyword(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public Map<String, Object> getByChannel(String channel, int page, int size) {
        String decodedChannel = channel;
        try {
            decodedChannel = URLDecoder.decode(channel, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        String actualChannel = CHANNEL_MAP.getOrDefault(decodedChannel, decodedChannel);
        int offset = (page - 1) * size;
        List<News> list = newsMapper.findByChannelPage(actualChannel, offset, size);
        long total = newsMapper.countByChannel(actualChannel);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    public List<News> getBreaking() {
        return newsMapper.findBreaking();
    }

    public List<News> getByChannelAll(String channel) {
        String decodedChannel = channel;
        try {
            decodedChannel = URLDecoder.decode(channel, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        String actualChannel = CHANNEL_MAP.getOrDefault(decodedChannel, decodedChannel);
        return newsMapper.findByChannel(actualChannel);
    }

    public List<News> getListAll() {
        return newsMapper.findByChannel(null);
    }

    public void addComment(Long newsId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setNewsId(newsId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setLikeCount(0);
        commentMapper.insert(comment);
        newsMapper.incrementComment(newsId);
    }

    public List<Comment> getCommentsByNewsId(Long newsId) {
        return commentMapper.findByNewsId(newsId);
    }

    public void update(News news) {
        newsMapper.update(news);
    }
}
