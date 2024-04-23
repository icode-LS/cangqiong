package com.sky.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class OrdersPaymentDTO implements Serializable {
    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;

    // 优惠券id
    private List<Long> couponsIds;

}
