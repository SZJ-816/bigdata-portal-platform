package com.bigdata.portal.community.service.impl;

import com.bigdata.portal.community.entity.UserFavorite;
import com.bigdata.portal.community.mapper.UserFavoriteMapper;
import com.bigdata.portal.community.service.UserFavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户收藏Service实现
 */
@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;

    public UserFavoriteServiceImpl(UserFavoriteMapper userFavoriteMapper) {
        this.userFavoriteMapper = userFavoriteMapper;
    }

    @Override
    public UserFavorite getById(Long favoriteId) {
        return userFavoriteMapper.selectById(favoriteId);
    }

    @Override
    public List<UserFavorite> listByUserId(Long userId) {
        return userFavoriteMapper.selectByUserId(userId);
    }

    @Override
    public boolean save(UserFavorite favorite) {
        return userFavoriteMapper.insert(favorite) > 0;
    }

    @Override
    public boolean deleteById(Long favoriteId) {
        return userFavoriteMapper.deleteById(favoriteId) > 0;
    }
}
