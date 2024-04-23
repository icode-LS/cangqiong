package com.sky.controller.admin;

import com.sky.dto.CouponsDTO;
import com.sky.dto.CouponsQueryDTO;
import com.sky.result.PageResult;
import com.sky.service.CouponsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/coupons")
@Api(tags = "管理端-优惠券接口")
public class CouponsController {


    @Autowired
    private CouponsService couponsService;

    @GetMapping("/list")
    @ApiOperation("查询优惠券列表")
    public PageResult getList(CouponsQueryDTO couponsQueryDTO){
        log.info("查询优惠券列表 {}", couponsQueryDTO);

        // 调用service
        PageResult pageResult = couponsService.pageQuery(couponsQueryDTO);

        return pageResult;
    }

    @PostMapping("/create")
    @ApiOperation("发放优惠券")
    public void createCoupons(@RequestParam CouponsDTO couponsDTO){
        log.info("发放优惠券 {}", couponsDTO);
        couponsService.createCoupons(couponsDTO);
    }

}
