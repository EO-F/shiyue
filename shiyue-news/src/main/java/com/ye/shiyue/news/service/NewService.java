package com.ye.shiyue.news.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ye.shiyue.news.pojo.News;
import com.ye.shiyue.news.vo.NewVo;

public interface NewService extends IService<News> {
    IPage<News> getNewByOpr(Page<News> page, String newTitle);

    IPage<News> getNewByCategory(Page<News> page, Integer categoryId);

    NewVo getByIdAndOpr(Integer newId, Integer userId);
}
