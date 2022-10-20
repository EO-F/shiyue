package com.ye.shiyue.news.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ye.shiyue.news.mapper.NewLogsMapper;
import com.ye.shiyue.news.pojo.NewLogs;
import com.ye.shiyue.news.service.NewLogsService;
import org.springframework.stereotype.Service;

@Service
public class NewLogsServiceImpl extends ServiceImpl<NewLogsMapper, NewLogs> implements NewLogsService {
}
