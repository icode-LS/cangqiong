package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {


    /**
     * 添加菜品或者套餐进购物车
     * @param shoppingCartDTO
     */
    void addCart(ShoppingCartDTO shoppingCartDTO);

    /**
     *
     * @return 用户的购物车数据
     */
    List<ShoppingCart> getCartList();


    /**
     *
     * @param shoppingCartDTO 要删除的商品的信息
     */
    void subCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void cleanCart();
}
