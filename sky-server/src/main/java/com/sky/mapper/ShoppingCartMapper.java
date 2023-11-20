package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 插入数据
     * @param shoppingCart
     */
    void add(ShoppingCart shoppingCart);

    /**
     *
     * @param shoppingCart 查询的信息
     * @return 对应的数据库信息
     */
    ShoppingCart selectByCartInfo(ShoppingCart shoppingCart);


    /**
     *
     * @param shoppingCart 更新number信息
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     *
     * @param userId 用户id
     * @return 查询到的购物车数据
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> selectByUserId(Long userId);


    void deleteByOb(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId};")
    void deleteByUserId(Long userId);
}
