package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品业务
     * @param dishDTO 新增的菜品
     */
    @Override
    @Transactional
    public void addDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.addDish(dish);
        // 获取口味集合
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dish.getId());});
            dishFlavorMapper.addAll(flavors);
        }
    }

    /**
     * 分页查询业务
     * @param dishPageQueryDTO 分页查询菜品
     * @return 查询到的数据
     */
    @Override
    @Transactional
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> dishVOS = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(dishVOS.getTotal(), dishVOS.getResult());
    }

    /**
     * 更新菜品信息
     * @param dishDTO 更新的信息
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 更新信息
        dishMapper.update(dish);
        // 获取id
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        log.info("更新的菜品的id为：{}", dish.getId());
        if(flavors != null && !flavors.isEmpty()){
            // 设置关联的菜品id
            flavors.forEach((item) -> { item.setDishId(dishId);});
            List<Long> ids = new LinkedList<>();
            ids.add(dishId);
            // 先删除原来的口味
            dishFlavorMapper.deleteByDishId(ids);
            // 再新增flavors
            dishFlavorMapper.addAll(flavors);
        }
    }

    /**
     * 根据id获取菜品
     * @param id 菜品id
     * @return 获取到的菜品数据
     */
    @Override
    @Transactional
    public DishVO getById(Long id) {
        DishVO dish = dishMapper.getById(id);
        // 设置查询到的口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(dish.getId());
        dish.setFlavors(dishFlavors);
        return dish;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 批量删除
     * @param ids 要删除的菜品的id列表
     */
    @Override
    @Transactional
    public void deleteDishes(List<Long> ids) {
        for(Long id : ids){
            // 首先判断遍历的菜品是否可以删除，起售或者关联了套餐则不能删除
            DishVO dish = dishMapper.getById(id);
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(dish.getName()+"正在启用，不允许删除！");
            }
            Integer nums = setmealDishMapper.selectByDishId(id);
            if(nums > 0){
                throw new DeletionNotAllowedException(dish.getName()+"关联了套餐，不允许删除！");
            }
        }
        // 删除彩屏数据
        dishMapper.deleteById(ids);
        // 删除关联口味数据
        dishFlavorMapper.deleteByDishId(ids);
    }

    @Override
    public List<Dish> getByCategoryId(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        return dishMapper.list(dish);
    }


}
