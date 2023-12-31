package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    void addDishWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void update(DishDTO dishDTO);

    DishVO getById(Long id);

    List<DishVO> listWithFlavor(Dish dish);

    void deleteDishes(List<Long> ids);

    List<Dish> getByCategoryId(DishDTO dishDTO);

}
