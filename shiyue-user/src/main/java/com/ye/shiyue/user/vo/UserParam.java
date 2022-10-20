package com.ye.shiyue.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserParam {

    private String phone;

    private String password;

    private Integer gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date birthday;

    private String name;

}
