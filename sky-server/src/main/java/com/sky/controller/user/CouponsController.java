package com.sky.controller.user;

import com.sky.entity.Coupons;
import com.sky.result.Result;
import com.sky.service.CouponsService;
import com.sky.vo.UserCouponsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/coupons")
@Api(tags = "用户端-优惠券接口")
public class CouponsController {


    @Autowired
    private CouponsService couponsService;

    @GetMapping("/myCoupons/{id}")
    @ApiOperation("查询用户拥有的优惠券")
    public Result<List<UserCouponsVO>> getMyCoupons(@PathVariable Long id){
        log.info("查询用户拥有的优惠券 {}", id);

        List<UserCouponsVO> userCoupons = couponsService.getUserCoupons(id);

        return Result.success(userCoupons);
    }

    @PostMapping("/snapped/{userId}/{couponId}")
    @ApiOperation("抢优惠券")
    public Result<String> snappedCoupon(@PathVariable Long userId, @PathVariable Long couponId){
        log.info("开始抢优惠券 {}", userId);

        String r = couponsService.snappedCoupon(userId, couponId);

        return Result.success(r);
    }

}
