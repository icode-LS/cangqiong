package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Mapper
public interface OrderMapper {


    void insertOne(Orders orders);

    @Select("select * from orders where id = #{orderId};")
    Orders getById(Long orderId);

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

    @Update("update orders set status = 6, cancel_reason = '用户取消订单', cancel_time = now()" +
            "where id = #{orderId}")
    void updateOrderToCancel(Long orderId);

    @Update("update orders set status = 5 where status = 4")
    void updateDelivered();


    Page<Orders> getByUserId(Long userId, Integer status);

    Page<Orders> getListByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select(" select COUNT(1)\n" +
            "        from orders\n" +
            "        where status = #{status};")
    Integer getOrderStatistic(Integer status);

    /**
     *
     * @param begin 一天的开始时间
     * @param end 一天的结束时间
     * @return 一天的营业额数据
     */
    Integer getTurnoverByDate(LocalDateTime begin, LocalDateTime end);

    /**
     *
     * @return 订单总数
     */
    Integer getOrderCntByDateAndStatus(Map<String, Object> map);


    Integer countByMap(Map map);

    Double sumByMap(Map map);
}

