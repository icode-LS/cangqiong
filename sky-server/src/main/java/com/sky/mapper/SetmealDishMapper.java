package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper  {

    @Select("select count(id) from setmeal_dish where dish_id = #{id};")
    Integer selectByDishId(Long id);


    void addAll(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id = #{id};")
    List<SetmealDish> getBySetmealId(Long id);

    void deleteBySetmealId(List<Long> ids);

}
