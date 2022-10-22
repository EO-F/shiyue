package com.ye.shiyue.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.user.mapper.FreeBackMapper;
import com.ye.shiyue.user.mapper.UserMapper;
import com.ye.shiyue.user.pojo.FreeBack;
import com.ye.shiyue.user.service.FreeBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FreeBackServiceImpl extends ServiceImpl<FreeBackMapper, FreeBack> implements FreeBackService {

    @Autowired
    private FreeBackMapper freeBackMapper;

    @Autowired
    private UserMapper userMapper;


    @Cacheable(value = "pageFreeback",key = "#root.methodName.name")
    @Override
    public IPage<FreeBack> getUserAndFreeBack(Page<FreeBack> pageParam){

        LambdaQueryWrapper<FreeBack> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(FreeBack::getId);

        Page<FreeBack> freeBackPage = freeBackMapper.selectPage(pageParam, queryWrapper);



        return freeBackPage;
    }

}
