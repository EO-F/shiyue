package com.ye.shiyue.user.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {

    private Integer id;

    private String title;

    private String content;

    private Integer categoryId;

    private Integer likes;

    public Date publishTime;

    private String keyWord;

    private String newPath;
}
