package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void addSetmealWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 新增新套餐
        setmealMapper.addSetmeal(setmeal);
        // 获取套餐的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 插入到套餐和菜品的关联表中
        setmealDishes.forEach((item) -> {
            item.setSetmealId(setmeal.getId());
        });
        setmealDishMapper.addAll(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmeals = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(setmeals.getTotal(),setmeals.getResult());
    }

    @Override
    @Transactional
    public SetmealVO getById(Long id) {
        // 根据id获取套餐信息
        SetmealVO setmeal = setmealMapper.getById(id);
        setmeal.setSetmealDishes(setmealDishMapper.getBySetmealId(id));
        return setmeal;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Long setmealId = setmealDTO.getId();
        // 先更新setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        // 获取套餐中的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach( (item) ->{ item.setSetmealId(setmealId); });
        // 封装成list
        List<Long> ids = new LinkedList<>();
        ids.add(setmealId);
        // 先删除
        setmealDishMapper.deleteBySetmealId(ids);
        // 再新增
        setmealDishMapper.addAll(setmealDishes);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        for(Long setmealId : ids){
            // 判断是否启用
            SetmealVO setmealVO = setmealMapper.getById(setmealId);
            if(Objects.equals(setmealVO.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException("套餐已启用不能删除！");
            }
        }
        setmealMapper.deleteAll(ids);
        setmealDishMapper.deleteBySetmealId(ids);
    }

    @Override
    @Transactional
    public void changeStatus(Setmeal setmeal) {
        // 查询套餐中的菜品是否都是启用状态
        if(Objects.equals(setmeal.getStatus(), StatusConstant.ENABLE)){
            Integer flag = dishMapper.selectBySetmealStatus(setmeal);
            if(flag > 0){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        // 更新信息
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
