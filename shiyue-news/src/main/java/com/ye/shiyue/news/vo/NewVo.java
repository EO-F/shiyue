package com.ye.shiyue.news.vo;

import com.ye.shiyue.news.pojo.News;
import lombok.Data;

@Data
public class NewVo extends News {

    private boolean hasLike;

    private boolean hasFavorite;
}
