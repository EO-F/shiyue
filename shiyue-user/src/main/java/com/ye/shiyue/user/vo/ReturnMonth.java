package com.ye.shiyue.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class ReturnMonth {

    //月份的返回
    private List<String> monthList;

    //对应数值的放回
    private List<Integer> numberList;

}
