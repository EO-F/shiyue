package com.ye.shiyue.user.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.ye.shiyue.common.handler.ListToStringHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user", autoResultMap = true)
public class User {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "必须设置密码")
    private String password;

    @TableField(fill = FieldFill.INSERT)
    private Date registerTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date loginTime;

    private Integer gender;

    private Date birthday;

    private Integer status;

    @NotBlank(message = "用户名不能为空")
    private String name;

    //TODO 可能有问题
    @TableField(jdbcType = JdbcType.VARCHAR, insertStrategy = FieldStrategy.NOT_NULL, typeHandler = ListToStringHandler.class)
    private List<String> keyWord;

    private String address;

    private String contactNumber;
}
