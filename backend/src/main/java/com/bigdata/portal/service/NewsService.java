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

    public List<News> getList() {
        return newsMapper.findAll();
    }

    public News getById(Long id) {
        newsMapper.incrementView(id);
        return newsMapper.findById(id);
    }

    public List<News> search(String keyword) {
        return newsMapper.search(keyword);
    }

    public List<News> getByChannel(String channel) {
        String decodedChannel = channel;
        try {
            decodedChannel = URLDecoder.decode(channel, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        String actualChannel = CHANNEL_MAP.getOrDefault(decodedChannel, decodedChannel);
        return newsMapper.findByChannel(actualChannel);
    }

    public List<News> getBreaking() {
        return newsMapper.findBreaking();
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
