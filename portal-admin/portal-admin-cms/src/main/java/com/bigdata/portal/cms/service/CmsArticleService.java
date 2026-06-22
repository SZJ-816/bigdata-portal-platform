package com.bigdata.portal.cms.service;

import com.bigdata.portal.cms.entity.CmsArticle;

import java.util.List;

/**
 * CMS文章Service接口
 */
public interface CmsArticleService {

    CmsArticle getById(Long articleId);

    List<CmsArticle> list();

    List<CmsArticle> listByChannelId(Long channelId);

    boolean save(CmsArticle article);

    boolean update(CmsArticle article);

    boolean deleteById(Long articleId);
}
