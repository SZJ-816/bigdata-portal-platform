package com.bigdata.portal.community.service;

import com.bigdata.portal.community.entity.CommunityComment;

import java.util.List;

/**
 * 社区评论Service接口
 */
public interface CommunityCommentService {

    CommunityComment getById(Long commentId);

    List<CommunityComment> listByArticleId(Long articleId);

    List<CommunityComment> list();

    boolean save(CommunityComment comment);

    boolean update(CommunityComment comment);

    boolean deleteById(Long commentId);
}
