package com.ye.shiyue.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ye.shiyue.user.pojo.Likes;


public interface LikeService extends IService<Likes> {
    boolean addToLike(Likes likes);

//    boolean removeToLike(Integer newId, Integer userId);
}
