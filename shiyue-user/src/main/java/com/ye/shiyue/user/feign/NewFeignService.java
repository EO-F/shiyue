package com.ye.shiyue.user.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.user.pojo.Favorite;
import com.ye.shiyue.user.pojo.Likes;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("shiyue-news")
public interface NewFeignService {

    @GetMapping("/new/getAllNewByFaIds/{pageNo}/{pageSize}")
    Page getAllNewByIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
                        @RequestBody List<Favorite> favoriteList);

    @GetMapping("/new/getAllNewByLikeIds/{pageNo}/{pageSize}")
    Page getAllNewByLikeIds(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize,
                            @RequestBody List<Likes> likesList);

    @PutMapping("/new/updateNewLike")
    public Result updateNewLike(Integer newId);

}
