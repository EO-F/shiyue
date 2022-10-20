package com.ye.shiyue.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ye.shiyue.user.pojo.User;
import com.ye.shiyue.user.vo.LoginFrom;
import com.ye.shiyue.user.vo.UserParam;

public interface UserService extends IService<User> {
    User login(LoginFrom loginFrom);

    IPage<User> getUserByOpr(Page<User> page, String userName);

    boolean register(UserParam userParam);

//    boolean sendMsg(String phone,String verificationCode);
}
