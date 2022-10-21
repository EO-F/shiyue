package com.ye.shiyue.news.feign;

import com.ye.shiyue.common.utils.Result;
import com.ye.shiyue.news.vo.FavoriteVo;
import com.ye.shiyue.news.vo.LikeVo;
import com.ye.shiyue.news.vo.UserMsgVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("shiyue-user")
public interface UserFeignService {

    @DeleteMapping("/favorite/deleteFavorite")
    public Result deleteFavorite(@ApiParam("需要删除的收藏的id") @RequestBody List<Integer> ids);

    @DeleteMapping("/like/deleteLike")
    public Result deleteLike(@RequestBody List<Integer> newIds);

    @GetMapping("/user/getKeyWordById/{userId}")
    public UserMsgVo getKeyWordById(@PathVariable("userId") Integer userId);

    @PutMapping("/user/updateMsg")
    Result updateMsg( @RequestBody UserMsgVo UserMsgVo);

    @GetMapping("/like/getMyLike")
    public LikeVo getMyLike(@RequestParam("userId") Integer userId, @RequestParam("newId") Integer newId);

    @GetMapping("/favorite/getMyFavorite")
    public FavoriteVo getMyFavorite(@RequestParam("userId") Integer userId,@RequestParam("newId") Integer newId);

}
