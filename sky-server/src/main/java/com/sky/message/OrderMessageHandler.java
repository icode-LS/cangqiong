package com.sky.message;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class OrderMessageHandler {

    @Autowired
    private OrderMapper orderMapper;

    // 延迟消费30分钟
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ttlOrder.queue", durable = "true"),
            exchange = @Exchange(name = "ttlOrder.exchange", delayed = "true"),
            key = "ttlOrder"
    ))
    public void ttlOrderHand(String orderNumber){
      log.info("检查订单号状态 ：" + orderNumber);
      // 检查支付状态， 如果是还是未支付那么直接取消订单，如果不是那就不干预操作
        Orders order = orderMapper.getByNumber(orderNumber);
        if(Objects.equals(order.getStatus(), Orders.PENDING_PAYMENT)){
            order.setStatus(Orders.CANCELLED);
            orderMapper.updateOrderToCancel(order.getId());
        }
    }
}
