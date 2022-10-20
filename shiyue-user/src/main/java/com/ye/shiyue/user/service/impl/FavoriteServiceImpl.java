package com.ye.shiyue.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.user.mapper.FavoriteMapper;
import com.ye.shiyue.user.pojo.Favorite;
import com.ye.shiyue.user.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean addToFavorite(Favorite favorite) {
        Integer newId = favorite.getNewId();
        Integer userId = favorite.getUserId();
        //查询是否已经收藏
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getNewId,newId)
                .eq(Favorite::getUserId,userId);
        Favorite queryFavorite = favoriteMapper.selectOne(queryWrapper);

        //已经收藏，则取消收藏
        if(queryFavorite != null){
            Integer id = queryFavorite.getId();
            favoriteMapper.deleteById(id);
            return false;
        }

        //没有收藏，则添加收藏
        favoriteMapper.insert(favorite);
        return true;
    }
}
