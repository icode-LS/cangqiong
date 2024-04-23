package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private CouponsMapper couponsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String DELAY_EXCHANGE = "ttlOrder.exchange";

    private final String DELAY_KEY = "ttlOrder";

    // websocket
    @Autowired
    WebSocketServer webSocketServer;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Transactional
    @Override
    public OrderSubmitVO submit(OrdersSubmitDTO ordersDTO) {
        // 地址id
        Long addressId = ordersDTO.getAddressBookId();
        // 获取地址数据
        AddressBook address = addressBookMapper.getById(addressId);
        // 地址簿不存在
        if (address == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // 查询购物车数据
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByUserId(userId);
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单表添加一条数据
        // 创建实例

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersDTO, orders);
        orders.setPhone(address.getPhone());
        orders.setAddress(address.getDetail());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setConsignee(address.getConsignee());
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orderMapper.insertOne(orders);
        // 向订单详细表添加n条数据
        List<OrderDetail> orderDetails = new LinkedList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatchs(orderDetails);
        // 清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        // 发送延迟消息
        rabbitTemplate.convertAndSend(DELAY_EXCHANGE, DELAY_KEY, orders.getNumber(), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelay(15*60*1000);
                return message;
            }
        });
        // 封装vo
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount()).build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        // 手动设置订单状态
        Orders orders = Orders.builder()
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .status(Orders.TO_BE_CONFIRMED)
                .number(ordersPaymentDTO.getOrderNumber()).build();
        orderMapper.update(orders);

        // 更新用户优惠券信息
        couponsMapper.updateUserCouponsInfoByIds(ordersPaymentDTO.getCouponsIds(), BaseContext.getCurrentId());
        // 向客户端发送消息
        Map map = new HashMap();
        // 1代表来单提醒，2代表催单提醒
        map.put("type",1);
        // 设置订单id
        map.put("orderId", orders.getId());
        // 设置内容
        map.put("content", orders.getNumber());
        // 转json字符串
        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    @Transactional
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Long userId = BaseContext.getCurrentId();

        // 查询出历史数据
        Page<Orders> orders = orderMapper.getByUserId(userId, ordersPageQueryDTO.getStatus());
        PageResult ans = new PageResult();
        ans.setTotal(orders.getTotal());

        // 封装数据
        List<OrderVO> records = new LinkedList<>();
        for (Orders order : orders) {

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);

            // 查询出关联的订单详细
            List<OrderDetail> orderDetail = orderDetailMapper.getByOrderId(order.getId());
            orderVO.setOrderDetailList(orderDetail);

            // 装入list
            records.add(orderVO);
        }
        ans.setRecords(records);
        return ans;
    }

    @Override
    public void cancelOrder(Long orderId) {
        // 更改订单状态
        orderMapper.updateOrderToCancel(orderId);
    }

    @Override
    public void repetitionOrder(Long orderId) {
        // 查询订单中的商品
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
        // 重新加入到购物车当中
        List<ShoppingCart> carts = new LinkedList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            // 封装数据
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            carts.add(shoppingCart);
        }
        // 批量加入数据库
        shoppingCartMapper.insertBatchs(carts);
    }

    @Override
    public OrderVO getOrderInfo(Long orderId) {
        // 根据id获取订单信息
        Orders order = orderMapper.getById(orderId);
        // 根据订单id获取订单详细信息
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
        // 封装数据
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * @return 各个状态的订单的数量
     */
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        // 查询数据
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(orderMapper.getOrderStatistic(Orders.TO_BE_CONFIRMED));
        orderStatisticsVO.setConfirmed(orderMapper.getOrderStatistic(Orders.CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.getOrderStatistic(Orders.DELIVERY_IN_PROGRESS));
        return orderStatisticsVO;
    }

    /**
     * @param ordersPageQueryDTO 查询信息
     * @return 查询到的订单信息
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        // 开始查询
        Page<Orders> list = orderMapper.getListByCondition(ordersPageQueryDTO);
        PageResult ans = new PageResult();
        ans.setTotal(list.getTotal());
        // 准备封装数据
        List<OrderVO> orderDetails = new LinkedList<>();
        for (Orders order : list.getResult()) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            // 查询order_detail
            List<OrderDetail> details = orderDetailMapper.getByOrderId(order.getId());
            orderVO.setOrderDetailList(details);
            orderDetails.add(orderVO);
        }
        ans.setRecords(orderDetails);
        return ans;
    }

    /**
     * @param ordersCancelDTO 取消订单的信息
     */
    @Override
    public void adminCancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Long orderId = ordersCancelDTO.getId();
        // 获取订单信息
        Orders order = orderMapper.getById(orderId);
        // 检查是否已经支付
        // 如果支付了，那就进行退款
        if(Objects.equals(order.getPayStatus(), Orders.PAID)){
            log.info("向 " + BaseContext.getCurrentId() + "用户退款....");
        }
        // 更新订单状态
        Orders newOrder = Orders.builder()
                .id(orderId)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .status(Orders.CANCELLED)
                .payStatus(Orders.REFUND).build();
        orderMapper.update(newOrder);
    }

    @Override
    public void completeOrder(Long orderId) {
        // 检查订单是否已送达
        Orders order = orderMapper.getById(orderId);
        if(!order.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 更新订单状态
        Orders newOrder = Orders.builder()
                .id(orderId)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now()).build();
        orderMapper.update(newOrder);
    }

    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        // 获取原订单信息
        Long orderId = ordersRejectionDTO.getId();
        Orders order = orderMapper.getById(orderId);
        // 检查状态是否为待接单状态
        if(! order.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 查看用户支付状态
        // 如果支付了就进行退款
        if(order.getPayStatus().equals(Orders.PAID)){
            log.info("向" + BaseContext.getCurrentId() + "用户退款.....");
        }
        // 更新订单信息
        Orders newOrder = Orders.builder()
                .id(orderId)
                .status(Orders.CANCELLED)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .payStatus(Orders.REFUND)
                .cancelTime(LocalDateTime.now()).build();
        orderMapper.update(newOrder);
    }

    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        // 获取订单信息
        Long orderId = ordersConfirmDTO.getId();
        Orders order = orderMapper.getById(orderId);
        // 查看状态是否为待接单
        if(! order.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 更新订单信息
        Orders newOrder = Orders.builder()
                .id(orderId)
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(newOrder);
    }

    @Override
    public void deliveryOrder(Long orderId) {
        // 获取订单信息
        Orders order = orderMapper.getById(orderId);
        // 检查订单信息是否是已接单
        if(! order.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 更新订单信息
        Orders newOrder = Orders.builder()
                .id(orderId)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(newOrder);
    }

    @Override
    public void reminder(Long orderId) {
        // 获取订单信息
        Orders order = orderMapper.getById(orderId);
        // 发送信息
        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId", orderId);
        map.put("content", order.getNumber());
        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }
}
