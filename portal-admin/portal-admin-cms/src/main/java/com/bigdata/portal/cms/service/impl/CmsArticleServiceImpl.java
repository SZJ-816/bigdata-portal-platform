package com.bigdata.portal.cms.service.impl;

import com.bigdata.portal.cms.entity.CmsArticle;
import com.bigdata.portal.cms.mapper.CmsArticleMapper;
import com.bigdata.portal.cms.service.CmsArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CMS文章Service实现
 */
@Service
public class CmsArticleServiceImpl implements CmsArticleService {

    private final CmsArticleMapper cmsArticleMapper;

    public CmsArticleServiceImpl(CmsArticleMapper cmsArticleMapper) {
        this.cmsArticleMapper = cmsArticleMapper;
    }

    @Override
    public CmsArticle getById(Long articleId) {
        return cmsArticleMapper.selectById(articleId);
    }

    @Override
    public List<CmsArticle> list() {
        return cmsArticleMapper.selectList();
    }

    @Override
    public List<CmsArticle> listByChannelId(Long channelId) {
        return cmsArticleMapper.selectByChannelId(channelId);
    }

    @Override
    public List<CmsArticle> listByChannelKey(String channelKey) {
        return cmsArticleMapper.selectByChannelKey(channelKey);
    }

    @Override
    public boolean save(CmsArticle article) {
        return cmsArticleMapper.insert(article) > 0;
    }

    @Override
    public boolean update(CmsArticle article) {
        return cmsArticleMapper.update(article) > 0;
    }

    @Override
    public boolean deleteById(Long articleId) {
        return cmsArticleMapper.deleteById(articleId) > 0;
    }
}
