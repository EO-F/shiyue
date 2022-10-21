package com.ye.shiyue.news.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ye.shiyue.common.handler.ListToStringHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Data
public class UserMsgVo {

    private List<String> keyWord;

    private Integer id;

}
