package com.ye.shiyue.news.vo;

import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class ReturnCategory {

    private List<String> categoryList;

    private List<Long> numberList;
}
