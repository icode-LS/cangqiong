package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "管理端-订单")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {


    @Autowired
    private OrderService orderService;

    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getStatistics(){
        log.info("开始查询各个状态的订单数量");
        OrderStatisticsVO orderStatistics = orderService.getOrderStatistics();
        return Result.success(orderStatistics);
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<String> cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单 {}", ordersCancelDTO);
        orderService.adminCancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<String> completeOrder(@PathVariable Long id){
        log.info("完成订单 " + id);
        orderService.completeOrder(id);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String> rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒绝接单 {}", ordersRejectionDTO);
        orderService.rejectionOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String> confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单 {}", ordersConfirmDTO);
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查看订单详细")
    public Result<OrderVO> checkDetails(@PathVariable Long id){
        log.info("查看订单相信 "+ id);
        OrderVO orderInfo = orderService.getOrderInfo(id);
        return Result.success(orderInfo);
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String > deliveryOrder(@PathVariable Long id){
        log.info("开始派送订单 "+ id);
        orderService.deliveryOrder(id);
        return Result.success();
    }

    @GetMapping("/conditionSearch")
    @ApiOperation("查询订单")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("开始查询订单 {}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
}
