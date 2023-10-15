package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务层
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        //复制信息
        BeanUtils.copyProperties(categoryDTO, category);
        // 设置新的信息
        category.setStatus(StatusConstant.DISABLE);
        categoryMapper.save(category);
    }

    /**
     * 分页查询
     *
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //分页查询数据
        Page<Category> categories = categoryMapper.pageQuery(categoryPageQueryDTO);
        return new PageResult(categories.getTotal(), categories.getResult());
    }

    /**
     * 根据id删除分类
     *
     * @param id
     */
    public void deleteById(Long id) {
        // 判断是否有关联的dish
        Integer dishCount = dishMapper.selectByCategoryId(id);
        if (dishCount > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //判断是否有关联的setmeal
        Integer setmealCount = setmealMapper.selectByCategoryId(id);
        if(setmealCount > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 删除分类
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    /**
     * 启用、禁用分类
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    /**
     *
     * @param type 类型
     * @return 分类列表
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.selectByType(type);
    }
}
