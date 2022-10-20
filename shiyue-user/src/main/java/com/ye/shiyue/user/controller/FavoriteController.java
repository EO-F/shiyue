package com.ye.shiyue.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.user.pojo.Favorite;
import com.ye.shiyue.user.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@Api("收藏操作")
public class FavoriteController {
//TODO
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private NewService newService;


    /**
     *  添加或删除收藏
     *
     * @author 叶旭晖
     * @date 2022/5/2 16:13
     * @return Result
     */
    @ApiOperation("添加或删除收藏")
    @PostMapping("/addOrDeleteFavorite")
    public Result addOrDeleteFavorite(@RequestBody Favorite favorite){
//        HttpSession session = request.getSession();
//        Integer userId = (Integer) session.getAttribute("userId");
//        Cookie[] cookies = request.getCookies();
//        for(Cookie cookie1 : cookies){
//            boolean sessionId = cookie1.getName().equals("sessionId");
//            System.out.println(sessionId);
//            System.out.println(cookie1.getValue());
//        }
        System.out.println(favorite.getUserId());
        boolean flag = favoriteService.addToFavorite(favorite);
        if(flag){
            return Result.ok().message("添加收藏成功");
        }
        return Result.ok().message("取消收藏成功");
    }

//    /**
//     *  删除收藏
//     *
//     * @author 叶旭晖
//     * @date 2022/5/2 16:13
//     * @return Result
//     */
//    @ApiOperation("删除收藏")
//    @DeleteMapping("/deleteFavorite")
//    public Result deleteFavorite(@ApiParam("需要删除的收藏的id") Integer newId){
//
//        boolean flag = favoriteService.removeById(newId);
//        if(flag){
//            return Result.ok().message("删除收藏成功");
//        }
//        return Result.fail().message("删除收藏失败");
//    }


    /**
     *  分页获取所有收藏
     *
     * @author 叶旭晖
     * @date 2022/5/21 17:01
     * @return Result
     */
    @ApiOperation("分页获取所有收藏")
    @GetMapping("/getAllFavorite/{pageNo}/{pageSize}/{userId}")
    public Result getAllFavorite(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                                 @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                                 @ApiParam("用户的id") @PathVariable("userId") Integer userId){

        Page<News> page = new Page<>(pageNo,pageSize);

        LambdaQueryWrapper<Favorite> favoriteQueryWrapper = new LambdaQueryWrapper<>();
        favoriteQueryWrapper.eq(Favorite::getUserId,userId);
        List<Favorite> favoriteList = favoriteService.list(favoriteQueryWrapper);

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        for(Favorite favorite : favoriteList){
            queryWrapper.in(News::getId,favorite.getNewId()).or();
        }
        Page<News> favoritePage = newService.page(page,queryWrapper);

        return Result.ok(favoritePage);
    }
}
