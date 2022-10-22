package com.ye.shiyue.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.user.pojo.Favorite;
import com.ye.shiyue.user.pojo.Likes;
import com.ye.shiyue.user.vo.News;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("shiyue-news")
public interface NewFeignService {

    @PostMapping("/new/getAllNewByFaIds/{pageNo}/{pageSize}")
    Page<News> getAllNewByIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
                              @RequestBody List<Favorite> favoriteList);

//    @GetMapping("/getAllNewByFaIds/{pageNo}/{pageSize}")
//    private Page<News> getAllNewByIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
//                                      @RequestBody List<Favorite> favoriteList)

    @PostMapping("/new/getAllNewByLikeIds/{pageNo}/{pageSize}")
    Page<News> getAllNewByLikeIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
                            @RequestBody List<Likes> likesList);

    @PutMapping("/new/updateNewLike")
    public Result updateNewLike(@RequestParam("newId") Integer newId);

}
