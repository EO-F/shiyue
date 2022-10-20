package com.ye.shiyue.news.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("newlogs")
public class NewLogs {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "用户信息获取失败")
    private Integer userId;

    @NotNull(message = "新闻信息获取失败")
    private Integer newId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date logTime;

}
