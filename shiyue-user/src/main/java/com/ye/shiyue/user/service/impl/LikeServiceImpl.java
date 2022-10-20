package com.ye.shiyue.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.user.mapper.LikeMapper;
import com.ye.shiyue.user.pojo.Likes;
import com.ye.shiyue.user.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Likes> implements LikeService {
//TODO
    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private NewMapper newMapper;

    @Override
    public boolean addToLike(Likes likes) {

        Integer newId = likes.getNewId();
        Integer userId = likes.getUserId();
        //查询是否已经点赞
        LambdaQueryWrapper<Likes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Likes::getNewId,newId);
        queryWrapper.eq(Likes::getUserId, userId);
        Likes queryLikes = likeMapper.selectOne(queryWrapper);

        //已经点赞,则进行取消点赞
        if(queryLikes != null){
            Integer id = queryLikes.getId();
            likeMapper.deleteById(id);
            return false;
        }
        //未点赞，给新闻点赞数添加点赞
        //增加新闻点赞数
        LambdaQueryWrapper<News> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(News::getId,newId);
        News news = newMapper.selectOne(lambdaQueryWrapper);
        news.setLikes(news.getLikes() + 1);
        newMapper.updateById(news);
        //添加Like点赞关系
        likeMapper.insert(likes);
        return true;
    }

//    @Override
//    public boolean removeToLike(Integer newId, Integer userId) {
//        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Like::getNewId,newId)
//                .eq(Like::getUserId,userId);
//        Like like = likeMapper.selectOne(queryWrapper);
//        if()
//        return false;
//    }
}
