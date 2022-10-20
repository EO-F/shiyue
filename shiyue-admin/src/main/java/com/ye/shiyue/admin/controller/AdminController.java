package com.ye.shiyue.admin.controller;

import com.ye.shiyue.admin.pojo.Admin;
import com.ye.shiyue.admin.service.AdminService;
import com.ye.shiyue.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Api("管理员操作")
public class AdminController {

    @Autowired
    private AdminService adminService;

    //TODO 使用第三方模块进行图片上传
//    @Autowired
//    private FileUploadService fileUploadService;
//
//    @ApiOperation("文件上传统一入口")
//    @PostMapping("/imgUpload")
//    public Result imgUpload(
//            @ApiParam("图片文件") @RequestPart("multipartFile") MultipartFile multipartFile){
//
//        //图片的真实路径
//        String portraitPath = fileUploadService.upload(multipartFile);
//
//        return Result.ok(portraitPath);
//    }

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result login(@ApiParam("登录提交的json表单") @RequestBody Admin admin){
        Admin a = adminService.login(admin);
        if(a == null){
            return Result.fail().message("登录失败");
        }
        return Result.ok(a);
    }

    @ApiOperation("管理员注册")
    @PostMapping("/register")
    public Result register(@ApiParam("注册提交的json表单") @RequestBody Admin admin){
        boolean register = adminService.register(admin);
        if (register) {
            return Result.ok().message("注册成功");
        }
        return Result.fail().message("管理员已存在");
    }

    @ApiOperation("查询管理员的信息")
    @GetMapping("/getAdminMsg/{adminId}")
    public Result getAdminMsg(@ApiParam("管理员的id") @PathVariable Integer adminId){
        Admin admin = adminService.getById(adminId);
        return Result.ok(admin);
    }
}
