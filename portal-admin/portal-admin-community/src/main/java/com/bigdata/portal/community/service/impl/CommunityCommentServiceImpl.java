package com.bigdata.portal.community.service.impl;

import com.bigdata.portal.community.entity.CommunityComment;
import com.bigdata.portal.community.mapper.CommunityCommentMapper;
import com.bigdata.portal.community.service.CommunityCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社区评论Service实现
 */
@Service
public class CommunityCommentServiceImpl implements CommunityCommentService {

    private final CommunityCommentMapper communityCommentMapper;

    public CommunityCommentServiceImpl(CommunityCommentMapper communityCommentMapper) {
        this.communityCommentMapper = communityCommentMapper;
    }

    @Override
    public CommunityComment getById(Long commentId) {
        return communityCommentMapper.selectById(commentId);
    }

    @Override
    public List<CommunityComment> listByArticleId(Long articleId) {
        return communityCommentMapper.selectByArticleId(articleId);
    }

    @Override
    public List<CommunityComment> list() {
        return communityCommentMapper.selectList();
    }

    @Override
    public boolean save(CommunityComment comment) {
        return communityCommentMapper.insert(comment) > 0;
    }

    @Override
    public boolean update(CommunityComment comment) {
        return communityCommentMapper.update(comment) > 0;
    }

    @Override
    public boolean deleteById(Long commentId) {
        return communityCommentMapper.deleteById(commentId) > 0;
    }
}
