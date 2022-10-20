package com.ye.shiyue.admin.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("shiyue_admin")
public class Admin {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "手机号必须提交")
    private String phone;

    @NotBlank(message = "必须起用户名")
    private String name;

    @NotBlank(message = "必须设置密码")
    private String password;

    @URL(message = "必须是合法的url地址")
    private String path;
}
