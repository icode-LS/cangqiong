package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "admin店铺操作")
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 改变店铺状态
     * @param status 状态
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("改变店铺状态")
    public Result<String> changeStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态 {}",status==1?"营业中":"打烊中");
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("SHOP_STATUS", status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("查看营业状态")
    public Result<Integer> getStatus(){
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get("SHOP_STATUS");
        log.info("获取到营业状态为 {}", status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
