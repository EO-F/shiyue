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
@TableName("feedback")
public class FreeBack {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @NotNull(message = "用户信息获取失败")
    private Integer userId;

    @NotBlank(message = "反馈内容不能为空")
    private String content;
}
