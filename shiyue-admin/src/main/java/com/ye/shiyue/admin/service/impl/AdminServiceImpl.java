package com.ye.shiyue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.admin.mapper.AdminMapper;
import com.ye.shiyue.admin.pojo.Admin;
import com.ye.shiyue.admin.service.AdminService;
import com.ye.shiyue.common.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(Admin admin) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getPhone,admin.getPhone());
        queryWrapper.eq(Admin::getPassword, MD5.encrypt(admin.getPassword()));
        Admin selectOne = adminMapper.selectOne(queryWrapper);
        return selectOne;
    }

    @Override
    public boolean register(Admin admin) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getPhone,admin.getPhone());
        Admin selectOne = adminMapper.selectOne(queryWrapper);
        if(selectOne == null){
            admin.setPassword(MD5.encrypt(admin.getPassword()));
            adminMapper.insert(admin);
            return true;
        }
        return false;
    }
}
