package com.ye.shiyue.news.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.news.feign.UserFeignService;
import com.ye.shiyue.news.pojo.NewLogs;
import com.ye.shiyue.news.pojo.News;
import com.ye.shiyue.news.service.NewLogsService;
import com.ye.shiyue.news.service.NewService;
import com.ye.shiyue.news.utils.TFIDF;
import com.ye.shiyue.news.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.ansj.app.keyword.Keyword;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/new")
@Api("新闻操作")
public class NewController {

    @Autowired
    private NewService newService;

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    private NewLogsService newLogsService;

    /**
     *  分页查询新闻信息
     *
     * @author 叶旭晖
     * @date 2022/5/2 17:02
     * @return Result
     */
    @ApiOperation("分页查询新闻信息")
    @GetMapping("/getAllNew/{pageNo}/{pageSize}")
    private Result getAllNew(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                             @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                             @ApiParam("分页查询的查询条件") String newTitle){

        Page<News> page = new Page<>(pageNo,pageSize);

        IPage<News> pageRs = newService.getNewByOpr(page,newTitle);

        return Result.ok(pageRs);
    }

    @PostMapping("/getAllNewByFaIds/{pageNo}/{pageSize}")
    private Page<News> getAllNewByIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize, @RequestBody List<Favorite> favoriteList){

        Page<News> page = new Page<>(pageNo,pageSize);

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        for(Favorite favorite : favoriteList){
            queryWrapper.in(News::getId,favorite.getNewId()).or();
        }
        Page<News> favoritePage = newService.page(page,queryWrapper);

        return favoritePage;
    }
    @PostMapping("/getAllNewByLikeIds/{pageNo}/{pageSize}")
    private Page<News> getAllNewByLikeIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize, @RequestBody List<Likes> likesList){

        Page<News> page = new Page<>(pageNo,pageSize);

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        for(Likes likes : likesList){
            queryWrapper.in(News::getId, likes.getNewId()).or();
        }

        Page<News> likePage = newService.page(page,queryWrapper);

        return likePage;
    }

    @PutMapping("/updateNewLike")
    public Result updateNewLike(@RequestParam("newId") Integer newId){
        LambdaQueryWrapper<News> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(News::getId,newId);
        News news = newService.getOne(lambdaQueryWrapper);
        news.setLikes(news.getLikes() + 1);
        newService.updateById(news);
        return Result.ok();
    }
    /**
     *  根据种类查询新闻信息
     *
     * @author 叶旭晖
     * @date 2022/5/2 17:04
     * @return Result
     */
    @ApiOperation("根据种类查询新闻信息")
    @GetMapping("/getByCategory/{pageNo}/{pageSize}/{categoryId}")
    public Result getByCategory(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                                @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                                @ApiParam("需要查询的新闻种类")@PathVariable("categoryId") Integer categoryId){

        IPage<News> pageRs = null;
//        //动态构造key
//        String key = "categoryId_" + categoryId;

//        //先从redis中获取数据
//        pageRs = (IPage<News>) redisTemplate.opsForValue().get(key);
//
//        if(pageRs != null){
//            //如果存在，直接返回，无需查询数据库
//            return Result.ok(pageRs);
//        }

        Page<News> page = new Page<>(pageNo,pageSize);

        pageRs = newService.getNewByCategory(page,categoryId);

        ////如果不存在，需要查询数据库，将查询到的新闻信息缓存到redis
//        redisTemplate.opsForValue().set(key,pageRs,60, TimeUnit.MINUTES);

        return Result.ok(pageRs);
    }

    /**
     *  根据ids删除新闻
     *
     * @author 叶旭晖
     * @date 2022/5/2 11:47
     * @return Result
     */
    @CacheEvict(value = {"pageNews","categoryNews"},key = "#root.methodName.name")
    @ApiOperation("根据ids删除新闻")
    @DeleteMapping("/deleteNew")
    public Result deleteNew(@RequestBody List<Integer> ids){

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        for(Integer id : ids){
            queryWrapper.eq(News::getId,id);
        }
        List<News> newsList = newService.list(queryWrapper);

        //先删除与当前id有关的喜欢 TODO 远程调用测试
        userFeignService.deleteLike(ids);

        //删除与当前id有关的收藏 TODO 远程调用测试
        userFeignService.deleteFavorite(ids);

        //删除与当前id有关的历史记录
        LambdaQueryWrapper<NewLogs> newLogsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        for(Integer id : ids) {
            newLogsLambdaQueryWrapper.eq(NewLogs::getNewId, id).or();
        }
        newLogsService.remove(newLogsLambdaQueryWrapper);

        //最后删除与当前id的新闻
        boolean flag = newService.removeByIds(ids);
        if(flag){
            return Result.ok().message("删除成功");
        }
        return Result.fail().message("删除失败");
    }

    /**
     *  保存或者修改新闻新闻
     *
     * @author 叶旭晖
     * @date 2022/5/2 12:07
     * @return Result
     */
    @CachePut(value = {"pageNews","categoryNews"},key = "#root.methodName.name")
    @ApiOperation("保存或修改新闻信息")
    @PostMapping("/updateNew")
    public Result addOrUpdateNew(@ApiParam("JSON格式的新闻对象") @RequestBody News news){

        news.setPublishTime(new Date());
        String content = news.getContent();
        String title = news.getTitle();
        //重新获取新闻的关键词
        List<Keyword> keywordList = TFIDF.getTFIDE(title, content, 5);
        //将关键词放入对象中
        String list = "";
        for(Keyword keyword : keywordList){
            list += keyword.getName() + "_";
        }
        news.setKeyWord(list);
        //进行保持或者修改
        newService.saveOrUpdate(news);
        return Result.ok();
    }

    /**
     *  获取新闻总数
     *
     * @author 叶旭晖
     * @date 2022/5/2 12:08
     * @return Result
     */
    @ApiOperation("获取新闻总数")
    @GetMapping("/getCount")
    public Result getCount(){

        long count = newService.count();

        return Result.ok(count);
    }

    /**
     *  根据id获取当前新闻的信息
     *
     * @author 叶旭晖
     * @date 2022/5/21 14:49
     * @return Result
     */
    @ApiOperation("根据id查询当前新闻的信息")
        @GetMapping("/getNew/{newId}/{userId}")
    public Result getNew(@ApiParam("当前新闻的id信息") @PathVariable("newId") Integer newId,
                         @ApiParam("用户的id") @PathVariable("userId") Integer userId){

        NewVo newVo = newService.getByIdAndOpr(newId,userId);

        return Result.ok(newVo);
    }

    /**
     *  获取今日新增的用户数量
     *
     * @author 叶旭晖
     * @date 2022/5/22 10:01
     * @return Result
     */
    @Cacheable(value = "todayNews",key = "#root.methodName.name")
    @ApiOperation("获取今日新增的新闻数量")
    @GetMapping("/getTodayNewCount")
    public Result getTodayNewCount(){
        Date date = new Date();
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        //将现在的时间转换为字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTimeFormat = sdf.format(date);
        //判断条件，获取注册时间在今日的用户
        queryWrapper.apply("date_format(publish_time, '%Y-%m-%d') = {0}", startTimeFormat);
        long count = newService.count(queryWrapper);
        return Result.ok(count);
    }

    @Cacheable(value = "monthNews",key = "#root.methodName.name")
    @ApiOperation("获取近x月新闻新增的个数")
    @GetMapping("/getNewMonth")
    public Result getNewMonth(){
        //获取最近的12个月份(含有年份)是什么
        LocalDateTime localDateTime = LocalDateTime.now();
        int nowMonth = localDateTime.getMonthValue();
        int year = localDateTime.getYear();

        //查询当前月份的用户新增的用户个数
        boolean flag = true;
        if(nowMonth >= 10){
            flag = false;
        }

        LambdaQueryWrapper<News> queryWrapper1 = new LambdaQueryWrapper<>();
        String s1;
        if(flag){
            s1 = year + "-" + "0" +  nowMonth;
        }else{
            s1 =  year + "-" + nowMonth;
        }
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper1.apply("date_format(publish_time, '%Y-%m') = {0}", s1);
        long count1 = newService.count(queryWrapper1);
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
        LambdaQueryWrapper<News> queryWrapper2 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper2.apply("date_format(publish_time, '%Y-%m') = {0}", s2);
        Long count2 = newService.count(queryWrapper2);
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
        if(m2 >= 10){
            flag2 = false;
        }
        String s3;
        if(flag3){
            s3 = y3 + "-" + "0" +  m3;
        }else{
            s3 =  y3 + "-" + m3;
        }
        LambdaQueryWrapper<News> queryWrapper3 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper3.apply("date_format(publish_time, '%Y-%m') = {0}", s3);
        Long count3 = newService.count(queryWrapper3);
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
        LambdaQueryWrapper<News> queryWrapper4 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper4.apply("date_format(publish_time, '%Y-%m') = {0}", s4);
        Long count4 = newService.count(queryWrapper4);
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
            s5 = y5 + "-" + "0" +  m5;
        }else{
            s5 =  y5 + "-" + m5;
        }
        LambdaQueryWrapper<News> queryWrapper5 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper5.apply("date_format(publish_time, '%Y-%m') = {0}", s5);
        Long count5 = newService.count(queryWrapper5);
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
        if(m2 >= 10){
            flag2 = false;
        }
        String s6;
        if(flag6){
            s6 = y6 + "-" + "0" +  m6;
        }else{
            s6 =  y6 + "-" + m6;
        }
        LambdaQueryWrapper<News> queryWrapper6 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper6.apply("date_format(publish_time, '%Y-%m') = {0}", s6);
        Long count6 = newService.count(queryWrapper6);
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
        if(m2 >= 10){
            flag2 = false;
        }
        String s7;
        if(flag7){
            s7 = y7 + "-" + "0" +  m7;
        }else{
            s7 =  y7 + "-" + m7;
        }
        LambdaQueryWrapper<News> queryWrapper7 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper7.apply("date_format(publish_time, '%Y-%m') = {0}", s7);
        Long count7 = newService.count(queryWrapper7);
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
        if(m2 >= 10){
            flag2 = false;
        }
        String s8;
        if(flag8){
            s8 = y8 + "-" + "0" +  m8;
        }else{
            s8 =  y8 + "-" + m8;
        }
        LambdaQueryWrapper<News> queryWrapper8 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper8.apply("date_format(publish_time, '%Y-%m') = {0}", s8);
        Long count8 = newService.count(queryWrapper8);
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
            s9 = y9 + "-" + "0" +  m9;
        }else{
            s9 =  y9 + "-" + m9;
        }
        LambdaQueryWrapper<News> queryWrapper9 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper9.apply("date_format(publish_time, '%Y-%m') = {0}", s9);
        Long count9 = newService.count(queryWrapper9);
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
            s10 = y10 + "-" + "0" +  m10;
        }else{
            s10 =  y10 + "-" + m10;
        }
        LambdaQueryWrapper<News> queryWrapper10 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper10.apply("date_format(publish_time, '%Y-%m') = {0}", s10);
        Long count10 = newService.count(queryWrapper10);
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
            s11 = y11 + "-" + "0" +  m11;
        }else{
            s11 =  y11 + "-" + m11;
        }
        LambdaQueryWrapper<News> queryWrapper11 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper11.apply("date_format(publish_time, '%Y-%m') = {0}", s11);
        Long count11 = newService.count(queryWrapper11);
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
            s12 = y12 + "-" + "0" +  m12;
        }else{
            s12 =  y12 + "-" + m12;
        }
        LambdaQueryWrapper<News> queryWrapper12 = new LambdaQueryWrapper<>();
        //判断条件，获取注册时间在第一个月份的用户数量
        queryWrapper12.apply("date_format(publish_time, '%Y-%m') = {0}", s12);
        Long count12 = newService.count(queryWrapper12);
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

    /**
     *  新闻种类分析
     *
     * @author 叶旭晖
     * @date 2022/5/27 19:43
     * @return Result
     */
    @Cacheable(value = "analyzeNews",key = "#root.methodName.name")
    @ApiOperation("新闻种类分析图")
    @GetMapping("/getNewCategory")
    public Result getNewCategory(){

        ReturnCategory returnCategory = new ReturnCategory();

        List<String> categoryList = new ArrayList<>();

        List<Long> numberList = new ArrayList<>();

        //查询出社会种类的数量
        LambdaQueryWrapper<News> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(News::getCategoryId,1);
        long count1 = newService.count(queryWrapper1);
        categoryList.add("社会");
        numberList.add(count1);

        //查询出科技种类的数量
        LambdaQueryWrapper<News> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(News::getCategoryId,2);
        long count2 = newService.count(queryWrapper2);
        categoryList.add("科技");
        numberList.add(count2);

        //查询出房产种类的数量
        LambdaQueryWrapper<News> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.eq(News::getCategoryId,3);
        long count3 = newService.count(queryWrapper3);
        categoryList.add("房产");
        numberList.add(count3);

        //查询出财经种类的数量
        LambdaQueryWrapper<News> queryWrapper4 = new LambdaQueryWrapper<>();
        queryWrapper4.eq(News::getCategoryId,4);
        long count4 = newService.count(queryWrapper4);
        categoryList.add("财经");
        numberList.add(count4);

        //查询出时尚种类的数量
        LambdaQueryWrapper<News> queryWrapper5 = new LambdaQueryWrapper<>();
        queryWrapper5.eq(News::getCategoryId,5);
        long count5 = newService.count(queryWrapper5);
        categoryList.add("时尚");
        numberList.add(count5);

        //查询出游戏种类的数量
        LambdaQueryWrapper<News> queryWrapper6 = new LambdaQueryWrapper<>();
        queryWrapper6.eq(News::getCategoryId,6);
        long count6 = newService.count(queryWrapper6);
        categoryList.add("游戏");
        numberList.add(count6);

        //查询出体育种类的数量
        LambdaQueryWrapper<News> queryWrapper7 = new LambdaQueryWrapper<>();
        queryWrapper7.eq(News::getCategoryId,7);
        long count7 = newService.count(queryWrapper7);
        categoryList.add("体育");
        numberList.add(count7);

        //查询出时政种类的数量
        LambdaQueryWrapper<News> queryWrapper8 = new LambdaQueryWrapper<>();
        queryWrapper8.eq(News::getCategoryId,8);
        long count8 = newService.count(queryWrapper8);
        categoryList.add("时政");
        numberList.add(count8);

        //查询出教育种类的数量
        LambdaQueryWrapper<News> queryWrapper9 = new LambdaQueryWrapper<>();
        queryWrapper9.eq(News::getCategoryId,9);
        long count9 = newService.count(queryWrapper9);
        categoryList.add("教育");
        numberList.add(count9);

        //查询出娱乐种类的数量
        LambdaQueryWrapper<News> queryWrapper10 = new LambdaQueryWrapper<>();
        queryWrapper10.eq(News::getCategoryId,10);
        long count10 = newService.count(queryWrapper10);
        categoryList.add("娱乐");
        numberList.add(count10);

        returnCategory.setCategoryList(categoryList);
        returnCategory.setNumberList(numberList);

        return Result.ok(returnCategory);
    }

    /**
     *  获取热门新闻（按点赞）
     *
     * @author 叶旭晖
     * @date 2022/5/27 19:16
     * @return Result
     */
    @ApiOperation("查询热门新闻")
    @GetMapping("/getHotNew/{pageNo}/{pageSize}")
    public Result getHotNew(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                            @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize){
        IPage<News> pageRs = null;

        String key = "hot_news";

//        //先从redis中获取数据
//        pageRs = (IPage<News>) redisTemplate.opsForValue().get(key);
//
//        if(pageRs != null){
//            //如果存在，直接返回，无需查询数据库
//            return Result.ok(pageRs);
//        }

        Page<News> page = new Page<>(pageNo,pageSize);

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(News::getLikes);

        pageRs = newService.page(page,queryWrapper);

        //如果不存在，进行上面的查询数据库，将查询的新闻信息缓存到redis
//        redisTemplate.opsForValue().set(key,pageRs,60,TimeUnit.MINUTES);

        return Result.ok(pageRs);
    }



    /**
     *  获取热门的四条新闻
     *
     * @author 叶旭晖
     * @date 2022/5/27 19:37
     * @return Result
     */
    @ApiOperation("获取四条热门新闻给前端")
    @GetMapping("/getFourHotNew")
    public Result getFourHotNew(){

        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(News::getPublishTime).last("limit 4");
        List<News> list = newService.list(queryWrapper);

        return Result.ok(list);
    }

    /**
     *  获取推荐新闻
     *
     * @author 叶旭晖
     * @date 2022/5/31 17:10
     * @return Result
     */
    @ApiOperation("获取推荐新闻")
    @GetMapping("/getRecommendNews/{pageNo}/{pageSize}/{userId}")
    public Result getRecommendNews(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                                   @ApiParam("分页查询的大小") @PathVariable("pageSize") Integer pageSize,
                                   @ApiParam("用户的id") @PathVariable("userId") Integer userId){

//        //查询当前用户
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(User::getId,userId);
//        User user = userService.getOne(queryWrapper);
//
//        //获取用户的关键词列表
//        List<String> keyWord = user.getKeyWord();
        // TODO 远程调用测试
        UserMsgVo userMsgVo = userFeignService.getKeyWordById(userId);
        List<String> keyWord = userMsgVo.getKeyWord();

        //使用关键词对新闻进行查询
        LambdaQueryWrapper<News> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        for (String word : keyWord){
            lambdaQueryWrapper.like(News::getKeyWord,word).or();
        }
        Page<News> page = new Page<>(pageNo,pageSize);
        Page<News> newsPage = newService.page(page, lambdaQueryWrapper);

        return Result.ok(newsPage);
    }
}
