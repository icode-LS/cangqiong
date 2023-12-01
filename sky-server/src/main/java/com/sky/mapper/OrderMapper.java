package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {


    void insertOne(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);


    /**
     * 处理超时订单
     * @param outTime 下单时间
     */
    @Update("update orders set status = 6, cancel_reason = '订单超时', cancel_time = now()" +
            " where status = 1 and order_time < #{outTime}")
    void updateTimeoutOrder(LocalDateTime outTime);

    @Update("update orders set status = 5 where status = 4")
    void updateDelivered();

}

