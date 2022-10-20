package com.ye.shiyue.news.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("new")
public class News {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "标题不为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "必须设置种类")
    private Integer categoryId;

    private Integer likes;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    public Date publishTime;

    private String keyWord;

    private String newPath;
}
