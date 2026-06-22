package com.bigdata.portal.cms.service;

import com.bigdata.portal.cms.entity.CmsChannel;

import java.util.List;

/**
 * CMS频道Service接口
 */
public interface CmsChannelService {

    CmsChannel getById(Long channelId);

    List<CmsChannel> list();

    boolean save(CmsChannel channel);

    boolean update(CmsChannel channel);

    boolean deleteById(Long channelId);
}
