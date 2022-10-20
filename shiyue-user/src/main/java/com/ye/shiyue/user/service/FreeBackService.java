package com.ye.shiyue.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ye.shiyue.user.pojo.FreeBack;


public interface FreeBackService extends IService<FreeBack> {

    IPage<FreeBack> getUserAndFreeBack(Page<FreeBack> page);
}
