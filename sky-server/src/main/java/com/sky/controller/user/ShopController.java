package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "user店铺操作")
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @GetMapping("/status")
    @ApiOperation("查看营业状态")
    public Result<Integer> getStatus(){
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get("SHOP_STATUS");
        log.info("获取到营业状态为 {}", status==1?"营业中":"打烊中");
        return Result.success(status);
    }

}
