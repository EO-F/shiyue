package com.ye.shiyue.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.news.pojo.NewLogs;
import com.ye.shiyue.news.pojo.News;
import com.ye.shiyue.news.service.NewLogsService;
import com.ye.shiyue.news.service.NewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newlogs")
@Api("新闻记录")
public class NewLogsController {

    @Autowired
    private NewLogsService newLogsService;

    @Autowired
    private NewService newService;

    /**
     *  根据ids删除新闻记录
     *
     * @author 叶旭晖
     * @date 2022/5/21 16:42
     * @return Result
     */
    @ApiOperation("根据ids删除新闻记录")
    @DeleteMapping("/deleteNewMsg")
    public Result deleteNewMsg(@RequestBody List<Integer> ids){
        LambdaQueryWrapper<NewLogs> queryWrapper = new LambdaQueryWrapper<>();
        for(Integer id : ids){
            queryWrapper.in(NewLogs::getNewId,id).or();
        }
        newLogsService.remove(queryWrapper);
        return Result.ok().message("删除新闻记录成功");
    }

    /**
     *  分页查询新闻记录
     *
     * @author 叶旭晖
     * @date 2022/5/21 17:04
     * @return Result
     */
    @Cacheable(value = "newLogMsg",key = "#root.methodName.name")
    @ApiOperation("分页查询新闻记录")
    @GetMapping("/getNewMsg/{pageNo}/{pageSize}/{userId}")
    public Result getMsg(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                         @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                         @ApiParam("用户的id") @PathVariable("userId") Integer userId){

        LambdaQueryWrapper<NewLogs> newLogsQueryWrapper = new LambdaQueryWrapper<>();
        newLogsQueryWrapper.eq(NewLogs::getUserId,userId);
        List<NewLogs> newLogsList = newLogsService.list(newLogsQueryWrapper);

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        for(NewLogs newLogs : newLogsList){
            queryWrapper.in(News::getId,newLogs.getNewId()).or();
        }

        Page<News> page = new Page<>(pageNo,pageSize);

        Page<News> newLogsPage = newService.page(page,queryWrapper);
        return Result.ok(newLogsPage);
    }
}
