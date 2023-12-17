package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {


    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 设置UserID
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 判断是否是用户点了多份相同的菜品或套餐
        ShoppingCart selectCart = shoppingCartMapper.selectByCartInfo(shoppingCart);
        // 如果是那么直接将数据库number加一
        if(selectCart != null){
            selectCart.setNumber(selectCart.getNumber()+1);
            shoppingCartMapper.updateNumberById(selectCart);
        }else{
            // 如果不是，那么则进行插入操作
            // 查询菜品或套餐信息
            // 判断添加到购物车的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            if(dishId == null){// 是套餐
                SetmealVO setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
            }else{// 是菜品
                DishVO dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
            }
            // 设置共同信息
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.add(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> getCartList() {
        Long userId = BaseContext.getCurrentId();
        // 获取用户的购物车列表
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByUserId(userId);
        return shoppingCarts;
    }

    @Override
    public void subCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCartMapper.deleteByOb(shoppingCart);
    }

    @Override
    public void cleanCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
