package com.ye.shiyue.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("shiyue-third-part")
public interface UploadFeignService {

    @RequestMapping("/upload/imgUpload")
    public String upload(@RequestPart("multipartFile") MultipartFile uploadFile);
}
