package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     * @param file 文件流
     * @return oss文件路径
     */
    @PostMapping("/upload")
    @ApiOperation("上传文件的接口")
    public Result<String> upload(MultipartFile file){
        log.info("开始上传文件....");
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取后缀
            String extention = originalFilename.substring(originalFilename.lastIndexOf('.'));
            // 新文件名
            String newName = UUID.randomUUID()+"."+extention;
            String filePath = aliOssUtil.upload(file.getBytes(), newName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败"+e);
        }
        return Result.error("上传失败");
    }

}
