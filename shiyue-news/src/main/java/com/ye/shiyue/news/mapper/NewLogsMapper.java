package com.ye.shiyue.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ye.shiyue.news.pojo.NewLogs;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NewLogsMapper extends BaseMapper<NewLogs> {
}
