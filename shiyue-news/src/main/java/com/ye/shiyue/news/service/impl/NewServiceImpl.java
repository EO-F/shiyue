package com.ye.shiyue.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.news.mapper.NewLogsMapper;
import com.ye.shiyue.news.mapper.NewMapper;
import com.ye.shiyue.news.pojo.NewLogs;
import com.ye.shiyue.news.pojo.News;
import com.ye.shiyue.news.service.NewService;
import com.ye.shiyue.news.vo.NewVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NewServiceImpl extends ServiceImpl<NewMapper, News> implements NewService {

    @Autowired
    private NewMapper newMapper;

//    @Autowired
//    private LikeMapper likeMapper;
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Autowired
//    private FavoriteMapper favoriteMapper;

    @Autowired
    private NewLogsMapper newLogsMapper;

    @Override
    public IPage<News> getNewByOpr(Page<News> pageParam, String newTitle) {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();

        if(StringUtils.isNotBlank(newTitle)){
            queryWrapper.like(News::getTitle,newTitle);
        }
        queryWrapper.orderByAsc(News::getId);
        Page<News> page = newMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public IPage<News> getNewByCategory(Page<News> pageParam, Integer categoryId) {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getCategoryId,categoryId);
        queryWrapper.orderByDesc(News::getPublishTime);
        Page<News> page = newMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public NewVo getByIdAndOpr(Integer newId, Integer userId) {
        return null;
    }

    //TODO 远程模块调用
//    @Override
//    public NewDto getByIdAndOpr(Integer newId,Integer userId) {
//        NewDto newDto = new NewDto();
//        //先根据id进行查询,对dto进行封装
//        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(News::getId,newId);
//        News news = newMapper.selectOne(queryWrapper);
//        BeanUtils.copyProperties(news,newDto);
//
//        //先对用户的关键词列表进行维护
//        //查询出当前用户
//        LambdaQueryWrapper<User> queryWrapperUser = new LambdaQueryWrapper<>();
//        queryWrapperUser.eq(User::getId,userId);
//        User user = userMapper.selectOne(queryWrapperUser);
//        List<String> keyWord = user.getKeyWord();
//        //移除前五个关键词
//        if(keyWord == null){
//            keyWord = new ArrayList<>();
//        }
//        if(keyWord.size() >= 20) {
//            keyWord.subList(0, 5).clear();
//        }
//        //提取当前新闻的关键词并加入到用户的关键词列表
//        String[] split = news.getKeyWord().split("_");
//        //将新闻关键词加入集合后面即可
//
//        keyWord.addAll(Arrays.asList(split));
//        user.setKeyWord(keyWord);
//        //改变当前用户的关键词列表
//        userMapper.updateById(user);
//        //获取当前new的点赞状态
//        LambdaQueryWrapper<Likes> likeQueryWrapper = new LambdaQueryWrapper<>();
//        likeQueryWrapper.eq(Likes::getNewId,newId)
//                .eq(Likes::getUserId,userId);
//        Likes likes = likeMapper.selectOne(likeQueryWrapper);
//        if(likes != null){
//            //如果已经点赞，设置为true
//            newDto.setHasLike(true);
//        }else {
//            //如果没点赞，设置为false
//            newDto.setHasLike(false);
//        }
//        //获取当前new的收藏状态
//        LambdaQueryWrapper<Favorite> favoriteQueryWrapper = new LambdaQueryWrapper<>();
//        favoriteQueryWrapper.eq(Favorite::getNewId,newId)
//                .eq(Favorite::getUserId,userId);
//        Favorite favorite = favoriteMapper.selectOne(favoriteQueryWrapper);
//        if(favorite != null){
//            //如果已经收藏，设置为true
//            newDto.setHasFavorite(true);
//        }else{
//            //如果没有收藏，设置为false
//            newDto.setHasFavorite(false);
//        }
//
//        //将当前新闻添加到新闻记录上
//        NewLogs newLogs = new NewLogs();
//        newLogs.setNewId(newId);
//        newLogs.setUserId(userId);
//        newLogs.setLogTime(LocalDateTime.now());
//        newLogsMapper.insert(newLogs);
//
//        return newDto;
//    }

}
