package com.ye.shiyue.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.user.pojo.FreeBack;
import com.ye.shiyue.user.pojo.User;
import com.ye.shiyue.user.service.FreeBackService;
import com.ye.shiyue.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/freeback")
@Api("反馈操作")
public class FreeBackController {
    //TODO
    @Autowired
    private FreeBackService freeBackService;

    @Autowired
    private UserService userService;

    /**
     *  分页查询所有的反馈
     *
     * @author 叶旭晖
     * @date 2022/5/2 17:58
     * @return Result
     */
    @ApiOperation("分页查询所有的反馈")
    @GetMapping("/getMsg/{pageNo}/{pageSize}")
    public Result getMsg(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                         @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize){

        Page<FreeBack> page = new Page<>(pageNo,pageSize);
        Page<FreeBackDto> pageDto = new Page<>();

        IPage<FreeBack> pageRs = freeBackService.getUserAndFreeBack(page);
        List<FreeBack> records = pageRs.getRecords();

        BeanUtils.copyProperties(pageRs,pageDto,"records");

        List<FreeBackDto> list = records.stream().map((item)->{
            FreeBackDto freeBackDto = new FreeBackDto();
            String content = item.getContent();
            Integer userId = item.getUserId();
            freeBackDto.setContent(content);
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getId,userId);
            User user = userService.getOne(queryWrapper);
            String name = user.getName();
            freeBackDto.setUserName(name);
            return freeBackDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(list);

        return Result.ok(pageDto);
    }

    /**
     *  添加新的反馈
     *
     * @author 叶旭晖
     * @date 2022/5/2 17:58
     * @return Result
     */
    @ApiOperation("添加新的反馈")
    @PostMapping("/addFreeBack")
    public Result addFreeBack(@ApiParam("JSON的FreeBack对象") @RequestBody FreeBack freeBack){

        boolean flag = freeBackService.save(freeBack);
        if(flag){
            return Result.ok().message("添加反馈成功");
        }
        return Result.fail().message("添加反馈失败");
    }

    /**
     *  根据ids删除反馈
     *
     * @author 叶旭晖
     * @date 2022/5/2 17:17
     * @return Result
     */
    @ApiOperation("根据id删除反馈")
    @DeleteMapping("/deleteFreeBack")
    public Result deleteFreeBack(@ApiParam("JSON形式的id集合") @RequestBody List<Integer> ids){

        boolean flag = freeBackService.removeByIds(ids);
        if(flag){
            return Result.ok().message("删除反馈成功");
        }
        return Result.fail().message("删除反馈失败");
    }
}
