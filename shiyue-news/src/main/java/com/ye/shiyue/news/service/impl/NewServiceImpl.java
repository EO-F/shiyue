package com.ye.shiyue.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.common.constant.ResultCodeConstant;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.news.feign.UserFeignService;
import com.ye.shiyue.news.mapper.NewLogsMapper;
import com.ye.shiyue.news.mapper.NewMapper;
import com.ye.shiyue.news.pojo.NewLogs;
import com.ye.shiyue.news.pojo.News;
import com.ye.shiyue.news.service.NewService;
import com.ye.shiyue.news.vo.FavoriteVo;
import com.ye.shiyue.news.vo.LikeVo;
import com.ye.shiyue.news.vo.NewVo;
import com.ye.shiyue.news.vo.UserMsgVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class NewServiceImpl extends ServiceImpl<NewMapper, News> implements NewService {

    @Autowired
    private NewMapper newMapper;

    @Autowired
    private NewLogsMapper newLogsMapper;

    @Autowired
    UserFeignService userFeignService;


    @Cacheable(value = "pageNews",key = "#root.methodName.name")
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

    @Cacheable(value = "categoryNews",key="#root.methodName.name")
    @Override
    public IPage<News> getNewByCategory(Page<News> pageParam, Integer categoryId) {
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getCategoryId,categoryId);
        queryWrapper.orderByDesc(News::getPublishTime);
        Page<News> page = newMapper.selectPage(pageParam, queryWrapper);
        return page;
    }


    //TODO ??????????????????
    @Override
    public NewVo getByIdAndOpr(Integer newId, Integer userId) {
        NewVo newDto = new NewVo();
        //?????????id????????????,???dto????????????
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getId,newId);
        News news = newMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(news,newDto);

        //??????????????????????????????????????????
        //?????????????????????
        LambdaQueryWrapper<UserMsgVo> queryWrapperUser = new LambdaQueryWrapper<>();
        queryWrapperUser.eq(UserMsgVo::getId,userId);
//        UserMsgVo user = userMapper.selectOne(queryWrapperUser);
        UserMsgVo userMsgVo = userFeignService.getKeyWordById(userId);
        List<String> keyWord = userMsgVo.getKeyWord();
        //????????????????????????
        if(keyWord == null){
            keyWord = new ArrayList<>();
        }
        if(keyWord.size() >= 20) {
            keyWord.subList(0, 5).clear();
        }
        //??????????????????????????????????????????????????????????????????
        String[] split = news.getKeyWord().split("_");
        //??????????????????????????????????????????

        keyWord.addAll(Arrays.asList(split));
        userMsgVo.setKeyWord(keyWord);
        //????????????????????????????????????
//        userMapper.updateById(user);
        Result result = userFeignService.updateMsg(userMsgVo);
        if(result.getCode() != ResultCodeConstant.SUCCESS.getCode()){
            //TODO
        }
        //????????????new???????????????
//        LambdaQueryWrapper<Likes> likeQueryWrapper = new LambdaQueryWrapper<>();
//        likeQueryWrapper.eq(Likes::getNewId,newId)
//                .eq(Likes::getUserId,userId);
//        Likes likes = likeMapper.selectOne(likeQueryWrapper);
        LikeVo likes = userFeignService.getMyLike(userId, newId);
        if(likes != null){
            //??????????????????????????????true
            newDto.setHasLike(true);
        }else {
            //???????????????????????????false
            newDto.setHasLike(false);
        }
        //????????????new???????????????
//        LambdaQueryWrapper<Favorite> favoriteQueryWrapper = new LambdaQueryWrapper<>();
//        favoriteQueryWrapper.eq(Favorite::getNewId,newId)
//                .eq(Favorite::getUserId,userId);
//        Favorite favorite = favoriteMapper.selectOne(favoriteQueryWrapper);
        FavoriteVo favorite = userFeignService.getMyFavorite(userId, newId);
        if(favorite != null){
            //??????????????????????????????true
            newDto.setHasFavorite(true);
        }else{
            //??????????????????????????????false
            newDto.setHasFavorite(false);
        }

        //???????????????????????????????????????
        NewLogs newLogs = new NewLogs();
        newLogs.setNewId(newId);
        newLogs.setUserId(userId);
        newLogs.setLogTime(new Date());
        newLogsMapper.insert(newLogs);

        return newDto;
    }

}
