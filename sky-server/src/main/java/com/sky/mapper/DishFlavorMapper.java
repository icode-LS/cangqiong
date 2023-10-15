package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入数据
     * @param flavors
     */
    void addAll(List<DishFlavor> flavors);


    /**
     *
     * @param dishId 菜品iD
     * @return 菜品对应的口味
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> selectByDishId(Long dishId);



    /**
     *
     * @param dishIds 要删除的菜品的id
     */

    void deleteByDishId(List<Long> dishIds);

}
