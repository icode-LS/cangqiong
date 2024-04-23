package com.sky.task;

import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

//    // 每分钟触发一次
//    // 定时处理超时订单
//    @Scheduled(cron = "0 * * * * ?")
//    public void processTimeoutOrder(){
//        log.info("处理超时订单 {}", new Date());
//        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);
//        // 调用处理方法
//        orderMapper.updateTimeoutOrder(localDateTime);
//    }

    // 每天一点中执行
    // 处理还在派送中的订单
    @Scheduled(cron = "0 0 1 * * ?")
    public void processUnDelivery(){
        log.info("更改派送中的订单的状态为已完成 {}", new Date());
        orderMapper.updateDelivered();
    }
}
