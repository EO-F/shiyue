package com.ye.shiyue.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ye.shiyue.user.pojo.Favorite;

public interface FavoriteService extends IService<Favorite> {
    boolean addToFavorite(Favorite favorite);
}
