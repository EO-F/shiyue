package com.ye.shiyue.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("likes")
public class Likes {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "用户信息获取失败")
    private Integer userId;

    @NotNull(message = "新闻信息获取失败")
    private Integer newId;
}
