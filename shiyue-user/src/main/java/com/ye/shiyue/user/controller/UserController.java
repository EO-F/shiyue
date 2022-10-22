package com.ye.shiyue.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.MD5;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.user.pojo.User;
import com.ye.shiyue.user.service.UserService;
import com.ye.shiyue.user.vo.LoginFrom;
import com.ye.shiyue.user.vo.ReturnMonth;
import com.ye.shiyue.user.vo.UserParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api("用户操作")
public class UserController {
//TODO
    @Autowired
    private UserService userService;

    /**
     * 注册用户
     *
     * @return Result
     * @author 叶旭晖
     * @date 2022/5/2 12:08
     */
    @PostMapping("/register")
    @ApiOperation("注册方法")
    private Result register(@ApiParam("注册提交的JSON格式的user对象") @RequestBody UserParam userParam) {

        boolean register = userService.register(userParam);
        if (register) {
            return Result.ok().message("注册成功");
        }
        return Result.fail().message("用户已存在");
    }


//    /**
//     *  发送短信验证码
//     *
//     * @author 叶旭晖
//     * @date 2022/5/29 12:08
//     * @return Result
//     */
//    @ApiOperation("发送短信验证码")
//    @GetMapping("/sendMsg/{phone}")
//    private Result sendMsg(@ApiParam("手机号码") @PathVariable String phone,HttpServletRequest request){
//
//        phone = "86" + phone;
//        //生成随机验证码，我的模板内容的参数只有一个
//        String verificationCode = RandomUtil.getFourBitRandom();
//        request.getSession().setAttribute("verificationCode",verificationCode);
//        boolean sendMsg = userService.sendMsg(phone, verificationCode);
//        if(sendMsg){
//            return Result.ok().message("发送成功");
//        }
//        return Result.fail().message("发送失败");
//    }

    /**
     *  登录方法
     *
     * @author 叶旭晖
     * @date 2022/5/29 12:09
     * @return Result
     */
    @PostMapping("/login")
    @ApiOperation("登录方法")
    private Result login(@ApiParam("登录表单提交的JSON格式的登录信息") @RequestBody LoginFrom loginFrom){

        User user = userService.login(loginFrom);
        if(user == null){
            return Result.fail().message("登录失败");
        }

        return Result.ok(user);
    }

    /**
     *  查询当前用户的信息
     *
     * @author 叶旭晖
     * @date 2022/5/2 15:28
     * @return Result
     */
    @ApiOperation("查询一个用户的信息")
    @GetMapping("/getMsgById/{userId}")
    public Result getMsgById(@ApiParam("用户的id") @PathVariable("userId") Integer userId){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,userId);
        User user = userService.getOne(queryWrapper);
        return Result.ok(user);
    }

    @GetMapping("/getKeyWordById/{userId}")
    public User getKeyWordById(@ApiParam("用户的id") @PathVariable("userId") Integer userId){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId,userId);
        User user = userService.getOne(queryWrapper);
        return user;
    }

    /**
     *  分页查询用户的信息
     *
     * @author 叶旭晖
     * @date 2022/5/2 16:54
     * @return Result
     */
    @ApiOperation("分页查询用户的信息")
    @GetMapping("/getAllMsg/{pageNo}/{pageSize}")
    private Result getAllMsg(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                             @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                             @ApiParam("分页查询的查询条件") String userName){

        //分页 带条件查询
        Page<User> page = new Page<>(pageNo,pageSize);
        IPage<User> pageRs = userService.getUserByOpr(page,userName);

        return Result.ok(pageRs);
    }

    /**
     *  修改用户的信息
     *
     * @author 叶旭晖
     * @date 2022/5/2 15:23
     * @return Result
     */
    @ApiOperation("修改当前用户的信息")
    @PutMapping("/updateMsg")
    private Result updateMsg(@ApiParam("修改之后提交的JSON格式的user对象") @RequestBody User user){
        if(user.getPassword() != null) {
            user.setPassword(MD5.encrypt(user.getPassword()));
        }
        userService.updateById(user);
        return Result.ok().message("修改成功");
    }

    /**
     *  获取用户总数
     *
     * @author 叶旭晖
     * @date 2022/5/2 12:08
     * @return Result
     */
    @ApiOperation("获取用户总数")
    @GetMapping("/getCount")
    public Result getCount(){
        //直接获取字符串即可
        long count = userService.count();

        return Result.ok(count);
    }

    /**
     *  获取今日新增的用户数量
     *  未测试
     * @author 叶旭晖
     * @date 2022/5/13 11:41
     * @return Result
     */
    @Cacheable(value = "todayUser",key = "#root.methodName.name")
    @ApiOperation("获取今日新增的用户数量")
    @GetMapping("/getTodayUserCount")
    public Result getTodayUserCount(){
        Date date = new Date();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //将现在的时间转换为字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTimeFormat = sdf.format(date);
        //判断条件，获取注册时间在今日的用户
        queryWrapper.apply("date_format(register_time, '%Y-%m-%d') = {0}", startTimeFormat);
        long count = userService.count(queryWrapper);

        return Result.ok(count);
    }

    /**
     *  对用户进行拉黑
     *
     * @author 叶旭晖
     * @date 2022/5/20 17:01
     * @return Result
     */
    @ApiOperation("拉黑用户")
    @PutMapping("/updateUserToBad")
    public Result updateUserToBad(@RequestBody User user){
        Integer status = user.getStatus();
        //当前用户为禁用状态
        if(status == 0){
            //提示已经拉黑过了
            return Result.fail().message("用户已经被拉黑");
        }
        //若当前用户为启用状态，将当前用户变为禁用状态
        user.setStatus(0);
        userService.updateById(user);
        return Result.ok().message("拉黑用户成功");
    }

    /**
     *  将用户移除黑名单
     *
     * @author 叶旭晖
     * @date 2022/5/22 10:01
     * @return Result
     */
    @ApiOperation("将用户移除黑名单")
    @PutMapping("/updateUserToGood")
    public Result updateUserToGood(@RequestBody User user){
        Integer status = user.getStatus();
        //当前用户已经是启用状态
        if(status == 1){
            //提示已经启用了
            return Result.fail().message("用户不在黑名单");
        }
        //若当前用户为禁用状态，将当前用户变成启用状态
        user.setStatus(1);
        userService.updateById(user);
        return Result.ok().message("移除黑名单成功");
    }

    /**
     *  查询出男女性别比
     *
     * @author 叶旭晖
     * @date 2022/5/20 20:13
     * @return Result
     */
    @Cacheable(value = "genderUser",key = "#root.methodName.name")
    @ApiOperation("查询男女性别比")
    @GetMapping("/getUserGender")
    public Result getUserGender(){
        //先查询出男生的数量
        LambdaQueryWrapper<User> queryWrapperMan = new LambdaQueryWrapper<>();
        queryWrapperMan.eq(User::getGender,1);
        long countMan = userService.count(queryWrapperMan);
        //再查询出女生的数量
        LambdaQueryWrapper<User> queryWrapperWomen = new LambdaQueryWrapper<>();
        queryWrapperWomen.eq(User::getGender,0);
        long countWomen = userService.count(queryWrapperWomen);
        long[] parent = new long[2];
        parent[0] = countMan;
        parent[1] = countWomen;
//        System.out.println(countWomen + "_" + countMan);
//        //男女数量相加
//        float count = countMan + countWomen;
//        //给出男女的性别比例
//        float parentMan = (countMan / count) *100 ;
//        float parentWomen = (countWomen / count) * 100;
//        String[] parent = new String[2];
//        //数组获取（0为男生比例，1为女生比例）
//        parent[0] = String.format("%.1f",parentMan) + "%";
//        parent[1] = String.format("%.1f",parentWomen) + "%";
        return Result.ok(parent);
    }

    /**
     *  获取近x月用户新增的个数
     *
     * @author 叶旭晖
     * @date 2022/5/20 20:36
     * @return Result
     */
    @Cacheable(value = "monthUser",key = "#root.methodName.name")
    @ApiOperation("获取近x月用户新增的个数")
    @GetMapping("/getUserMonth")
    public Result getMonth(){
        //获取最近的12个月份(含有年份)是什么
        LocalDateTime localDateTime = LocalDateTime.now();
        int nowMonth = localDateTime.getMonthValue();
        int year = localDateTime.getYear();

        //查询当前月份的用户新增的用户个数
        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
        String s1 = year + "-" + "0" + nowMonth;
       //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper1.apply("date_format(register_time, '%Y-%m') = {0}", s1);
        long count1 = userService.count(queryWrapper1);
        /**********************************************************************/
        //查询第二个月份的用户新增的用户个数
        int m2,y2;
        boolean flag2 = true;
        if(nowMonth - 1 <= 0){
            m2 = 12;
            y2 = year - 1;
        }else{
            m2 = nowMonth - 1;
            y2 = year;
        }
        if(m2 >= 10){
            flag2 = false;
        }
        String s2;
        if(flag2){
            s2 = y2 + "-" + "0" +  m2;
        }else{
            s2 =  y2 + "-" + m2;
        }
        LambdaQueryWrapper<User> queryWrapper2 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper2.apply("date_format(register_time, '%Y-%m') = {0}", s2);
        long count2 = userService.count(queryWrapper2);
        /**********************************************************************/
        //查询第三个月份的用户新增的用户个数
        int m3,y3;
        boolean flag3 = true;
        if(m2 - 1 <= 0){
            m3 = 12;
            y3 = year - 1;
        }else{
            m3 = m2 - 1;
            y3 = year;
        }
        if(m3 >= 10){
            flag3 = false;
        }
        String s3;
        if(flag3){
            s3 = y3 + "-" + "0" +  m3;
        }else{
            s3 =  y3 + "-" + m3;
        }
        LambdaQueryWrapper<User> queryWrapper3 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper3.apply("date_format(register_time, '%Y-%m') = {0}", s3);
        long count3 = userService.count(queryWrapper3);
        /**********************************************************************/
        //查询第四个月份的用户新增的用户个数
        int m4,y4;
        boolean flag4 = true;
        if(m3 - 1 <= 0){
            m4 = 12;
            y4 = year - 1;
        }else{
            m4 = m3 - 1;
            y4 = year;
        }
        if(m4 >= 10){
            flag4 = false;
        }
        String s4;
        if(flag4){
            s4 = y4 + "-" + "0" +  m4;
        }else{
            s4 =  y4 + "-" + m4;
        }
        LambdaQueryWrapper<User> queryWrapper4 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper4.apply("date_format(register_time, '%Y-%m') = {0}", s4);
        long count4 = userService.count(queryWrapper4);
        /**********************************************************************/
        //查询第五个月份的用户新增的用户个数
        int m5,y5;
        boolean flag5 = true;
        if(m4 - 1 <= 0){
            m5 = 12;
            y5 = year - 1;
        }else{
            m5 = m4 - 1;
            y5 = year;
        }
        if(m5 >= 10){
            flag5 = false;
        }
        String s5;
        if(flag5){
            s5 = y5 + "-" + "0" + m5;
        }else{
            s5 =  y5 + "-" +  m5;
        }
        LambdaQueryWrapper<User> queryWrapper5 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper5.apply("date_format(register_time, '%Y-%m') = {0}", s5);
        long count5 = userService.count(queryWrapper5);
        /**********************************************************************/
        //查询第六个月份的用户新增的用户个数
        int m6,y6;
        boolean flag6 = true;
        if(m5 - 1 <= 0){
            m6 = 12;
            y6 = year - 1;
        }else{
            m6 = m5 - 1;
            y6 = year;
        }
        if(m6 >= 10){
            flag6 = false;
        }
        String s6;
        if(flag6){
            s6 = y6 + "-"+ "0" + m6;
        }else{
            s6 =  y6 + "-" +  m6;
        }
        LambdaQueryWrapper<User> queryWrapper6 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper6.apply("date_format(register_time, '%Y-%m') = {0}", s6);
        long count6 = userService.count(queryWrapper6);
        /**********************************************************************/
        //查询第七个月份的用户新增的用户个数
        int m7,y7;
        boolean flag7 = true;
        if(m6 - 1 <= 0){
            m7 = 12;
            y7 = year - 1;
        }else{
            m7 = m6 - 1;
            y7 = year;
        }
        if(m7 >= 10){
            flag7 = false;
        }
        String s7;
        if(flag7){
            s7 = y7 + "-" + "0" + m7;
        }else{
            s7 =  y7 + "-" +  m7;
        }
        LambdaQueryWrapper<User> queryWrapper7 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper7.apply("date_format(register_time, '%Y-%m') = {0}", s7);
        long count7 = userService.count(queryWrapper7);
        /**********************************************************************/
        //查询第八个月份的用户新增的用户个数
        int m8,y8;
        boolean flag8 = true;
        if(m7 - 1 <= 0){
            m8 = 12;
            y8 = year - 1;
        }else{
            m8 = m7 - 1;
            y8 = year;
        }
        if(m8 >= 10){
            flag8 = false;
        }
        String s8;
        if(flag8){
            s8 = y8 + "-" + "0" + m8;
        }else{
            s8 =  y8 + "-" + m8;
        }
        LambdaQueryWrapper<User> queryWrapper8 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper8.apply("date_format(register_time, '%Y-%m') = {0}", s8);
        long count8 = userService.count(queryWrapper8);
        /**********************************************************************/
        //查询第九个月份的用户新增的用户个数
        int m9,y9;
        boolean flag9 = true;
        if(m8 - 1 <= 0){
            m9 = 12;
            y9 = year - 1;
        }else{
            m9 = m8 - 1;
            y9 = year;
        }
        if(m9 >= 10){
            flag9 = false;
        }
        String s9;
        if(flag9){
            s9 = y9 + "-" + "0" + m9;
        }else{
            s9 =  y9 + "-" +  m9;
        }
        LambdaQueryWrapper<User> queryWrapper9 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper9.apply("date_format(register_time, '%Y-%m') = {0}", s9);
        long count9 = userService.count(queryWrapper9);
        /**********************************************************************/
        //查询第十个月份的用户新增的用户个数
        int m10,y10;
        boolean flag10 = true;
        if(m9 - 1 <= 0){
            m10 = 12;
            y10 = year - 1;
        }else{
            m10 = m9 - 1;
            y10 = year;
        }
        if(m10 >= 10){
            flag10 = false;
        }
        String s10;
        if(flag10){
            s10 = y10 + "-" + "0" + m10;
        }else{
            s10 =  y10 + "-" +  m10;
        }
        LambdaQueryWrapper<User> queryWrapper10 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper10.apply("date_format(register_time, '%Y-%m') = {0}", s10);
        long count10 = userService.count(queryWrapper10);
        /**********************************************************************/
        //查询第十一个月份的用户新增的用户个数
        int m11,y11;
        boolean flag11 = true;
        if(m10 - 1 <= 0){
            m11 = 12;
            y11 = year - 1;
        }else{
            m11 = m10 - 1;
            y11 = year;
        }
        if(m11 >= 10){
            flag11 = false;
        }
        String s11;
        if(flag11){
            s11 = y11 + "-" + "0" + m11;
        }else{
            s11 =  y11 + "-" +  m11;
        }
        LambdaQueryWrapper<User> queryWrapper11 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper11.apply("date_format(register_time, '%Y-%m') = {0}", s11);
        long count11 = userService.count(queryWrapper11);
        /**********************************************************************/
        //查询第十二个月份的用户新增的用户个数
        int m12,y12;
        boolean flag12 = true;
        if(m11 - 1 <= 0){
            m12 = 12;
            y12 = year - 1;
        }else{
            m12 = m11 - 1;
            y12 = year;
        }
        if(m12 >= 10){
            flag12 = false;
        }
        String s12;
        if(flag12){
            s12 = y12 + "-" + "0" + m12;
        }else{
            s12 =  y12 + "-" + m12;
        }
        LambdaQueryWrapper<User> queryWrapper12 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper12.apply("date_format(register_time, '%Y-%m') = {0}", s12);
        long count12 = userService.count(queryWrapper12);
        //对月份和对应的数值封装成对象
        List<String> monthList = new ArrayList<>();
        monthList.add(nowMonth + "月");
        monthList.add(m2 + "月");
        monthList.add(m3 + "月");
        monthList.add(m4 + "月");
        monthList.add(m5 + "月");
        monthList.add(m6 + "月");
        monthList.add(m7 + "月");
        monthList.add(m8 + "月");
        monthList.add(m9 + "月");
        monthList.add(m10 + "月");
        monthList.add(m11 + "月");
        monthList.add(m12 + "月");
        List<Long> numberList = new ArrayList<>();
        numberList.add(count1);
        numberList.add(count2);
        numberList.add(count3);
        numberList.add(count4);
        numberList.add(count5);
        numberList.add(count6);
        numberList.add(count7);
        numberList.add(count8);
        numberList.add(count9);
        numberList.add(count10);
        numberList.add(count11);
        numberList.add(count12);
        ReturnMonth returnMonth = new ReturnMonth();
        returnMonth.setMonthList(monthList);
        returnMonth.setNumberList(numberList);
        return Result.ok(returnMonth);
    }
}
