package com.ye.shiyue.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.user.pojo.Likes;
import com.ye.shiyue.user.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
@Api("点赞操作")
public class LikeController {
//TODO
    @Autowired
    private LikeService likeService;

    @Autowired
    private NewService newService;


    /**
     *  点赞或取消点赞
     *
     * @author 叶旭晖
     * @date 2022/5/2 16:24
     * @return Result
     */
    @ApiOperation("点赞或取消点赞")
    @PostMapping("/addOrDeleteLike")
    public Result addOrDeleteLike(@ApiParam("进行点赞的新闻") @RequestBody Likes likes){

        boolean flag = likeService.addToLike(likes);
        if(flag){
            return Result.ok().message("点赞成功");
        }
        return Result.fail().message("取消点赞");
    }

//    /**
//     *  取消点赞
//     *
//     * @author 叶旭晖
//     * @date 2022/5/2 16:25
//     * @return Result
//     */
//    @ApiOperation("取消点赞")
//    @DeleteMapping("/deleteLike/{newId}")
//    public Result deleteLike(@ApiParam("取消点赞的新闻id") @PathVariable("newId") Integer newId,HttpServletRequest request){
//        Integer userId = (Integer) request.getSession().getAttribute("userId");
//        boolean flag = likeService.removeToLike(newId,userId);
//        if(flag){
//            return Result.ok().message("取消点赞成功");
//        }
//        return Result.fail().message("取消点赞失败");
//    }

    /**
     *  分页查询所有点赞新闻
     *
     * @author 叶旭晖
     * @date 2022/5/21 17:03
     * @return Result
     */
    @ApiOperation("分页查询所有点赞新闻")
    @GetMapping("/getAllLikeNew/{pageNo}/{pageSize}/{userId}")
    public Result getAllLikeNew(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                                @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                                @ApiParam("用户的id") @PathVariable("userId") Integer userId){

        Page<News> page = new Page<>(pageNo,pageSize);

        LambdaQueryWrapper<Likes> likeQueryWrapper = new LambdaQueryWrapper<>();
        likeQueryWrapper.eq(Likes::getUserId,userId);
        List<Likes> likesList = likeService.list(likeQueryWrapper);

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        for(Likes likes : likesList){
            queryWrapper.in(News::getId, likes.getNewId()).or();
        }

        Page<News> likePage = newService.page(page,queryWrapper);
        return Result.ok(likePage);
    }
}
