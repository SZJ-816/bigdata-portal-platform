package com.bigdata.portal.community.service;

import com.bigdata.portal.community.entity.UserFavorite;

import java.util.List;

/**
 * 用户收藏Service接口
 */
public interface UserFavoriteService {

    UserFavorite getById(Long favoriteId);

    List<UserFavorite> listByUserId(Long userId);

    boolean save(UserFavorite favorite);

    boolean deleteById(Long favoriteId);
}
