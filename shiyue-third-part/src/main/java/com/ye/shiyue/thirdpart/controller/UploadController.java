package com.ye.shiyue.thirdpart.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.ye.shiyue.thirdpart.config.YunOssConfig;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
    // 允许上传文件(图片)的格式
    private static final String[] IMAGE_TYPE = new String[]{".bmp", ".jpg",
            ".jpeg", ".gif", ".png"};
    @Autowired
    private OSS ossClient;
    @Autowired
    private YunOssConfig yunOssConfig;

    @RequestMapping("/imgUpload")
    public String upload(@RequestPart("multipartFile") MultipartFile uploadFile) {
        // 获取oss的Bucket名称
        String bucketName = yunOssConfig.getBucketName();
        // 获取oss的地域节点
        String endpoint = yunOssConfig.getEndPoint();
        // 获取oss的AccessKeySecret
        String accessKeySecret = yunOssConfig.getAccessKeySecret();
        // 获取oss的AccessKeyId
        String accessKeyId = yunOssConfig.getAccessKeyId();
        // 获取oss目标文件夹
        String filehost = yunOssConfig.getFileHost();
        // 返回图片上传后返回的url
        String returnImgeUrl = "";

        // 校验图片格式
        boolean isLegal = false;
        for (String type : IMAGE_TYPE) {
            if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {// 如果图片格式不合法
            return "如果图片格式不合法";
        }
        // 获取文件原名称
        String originalFilename = uploadFile.getOriginalFilename();
        // 获取文件类型
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 新文件名称
        String newFileName = UUID.randomUUID().toString() + fileType;
        // 构建日期路径
        String filePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        // 文件上传的路径地址
        String uploadImgeUrl = filehost + "/" + filePath + "/" + newFileName;

        // 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/jpg");

        ossClient.putObject(bucketName, uploadImgeUrl, inputStream, meta);
        /**
         * 注意：在实际项目中，文件上传成功后，数据库中存储文件地址
         */
        // 获取文件上传后的图片返回地址
        returnImgeUrl = "http://" + bucketName + "." + endpoint + "/" + uploadImgeUrl;

        return returnImgeUrl;
    }
}
