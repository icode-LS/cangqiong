package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO  submit(OrdersSubmitDTO ordersDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询用户的历史订单
     * @param ordersPageQueryDTO 分页查询的数据
     * @return 历史订单数据
     */
    PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 取消订单
     * @param orderId 订单Id
     */
    void cancelOrder(Long orderId);

    /**
     *
     * 再来一单
     * @param orderId 订单id
     */
    void repetitionOrder(Long orderId);

    /**
     * 查询订单详细信息
     * @param orderId 订单id
     * @return 订单的详细信息
     */
    OrderVO getOrderInfo(Long orderId);

    OrderStatisticsVO getOrderStatistics();

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    void adminCancelOrder(OrdersCancelDTO ordersCancelDTO);

    void completeOrder(Long orderId);

    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void deliveryOrder(Long orderId);




}
