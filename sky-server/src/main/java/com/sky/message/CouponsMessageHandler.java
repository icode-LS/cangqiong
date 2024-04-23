package com.sky.message;

import com.sky.entity.Coupons;
import com.sky.mapper.CouponsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CouponsMessageHandler {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CouponsMapper couponsMapper;

    // 更新优惠券缓存信息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "coupons.queue", durable = "true"),
            exchange = @Exchange(name = "coupons.exchange"),
            key = "updateOrInsert"
    ))
    public void updateOrInsertHandler(Coupons coupons) {
        // 存入优惠券数量
        stringRedisTemplate.opsForValue().set("coupons:" + coupons.getId(),
                String.valueOf(coupons.getNum()));
    }

    // 更新优惠券数量信息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "couponsDB.queue", durable = "true"),
            exchange = @Exchange(name = "coupons.exchange"),
            key = "snapped"
    ))
    public void snapped(Long couponsId) {
        couponsMapper.dNum(couponsId);
    }
}
