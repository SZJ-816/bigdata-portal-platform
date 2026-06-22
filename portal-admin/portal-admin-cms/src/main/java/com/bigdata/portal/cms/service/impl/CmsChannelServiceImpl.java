package com.bigdata.portal.cms.service.impl;

import com.bigdata.portal.cms.entity.CmsChannel;
import com.bigdata.portal.cms.mapper.CmsChannelMapper;
import com.bigdata.portal.cms.service.CmsChannelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CMS频道Service实现
 */
@Service
public class CmsChannelServiceImpl implements CmsChannelService {

    private final CmsChannelMapper cmsChannelMapper;

    public CmsChannelServiceImpl(CmsChannelMapper cmsChannelMapper) {
        this.cmsChannelMapper = cmsChannelMapper;
    }

    @Override
    public CmsChannel getById(Long channelId) {
        return cmsChannelMapper.selectById(channelId);
    }

    @Override
    public List<CmsChannel> list() {
        return cmsChannelMapper.selectList();
    }

    @Override
    public boolean save(CmsChannel channel) {
        return cmsChannelMapper.insert(channel) > 0;
    }

    @Override
    public boolean update(CmsChannel channel) {
        return cmsChannelMapper.update(channel) > 0;
    }

    @Override
    public boolean deleteById(Long channelId) {
        return cmsChannelMapper.deleteById(channelId) > 0;
    }
}
