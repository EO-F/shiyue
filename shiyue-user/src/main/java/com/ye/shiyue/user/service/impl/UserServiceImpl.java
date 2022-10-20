package com.ye.shiyue.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.common.utils.MD5;
import com.ye.shiyue.user.mapper.UserMapper;
import com.ye.shiyue.user.pojo.User;
import com.ye.shiyue.user.service.UserService;
import com.ye.shiyue.user.vo.LoginFrom;
import com.ye.shiyue.user.vo.UserParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(LoginFrom loginFrom) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginFrom.getPhoneNumber());
        queryWrapper.eq(User::getPassword, MD5.encrypt(loginFrom.getPassword()));
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    @Override
    public IPage<User> getUserByOpr(Page<User> pageParam, String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like(User::getName, userName);
        }
        queryWrapper.orderByAsc(User::getId);
        Page<User> page = userMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public boolean register(UserParam userParam) {
        User user = new User();
        BeanUtils.copyProperties(userParam, user);
        user.setRegisterTime(LocalDateTime.now());
        user.setStatus(1);
        //查询是否有相同用户名的用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getPhone());
        User queryUser = userMapper.selectOne(queryWrapper);
        if (queryUser == null) {
            user.setPassword(MD5.encrypt(userParam.getPassword()));
            userMapper.insert(user);
            return true;
        }
        return false;
    }
}

