package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select count(id) from setmeal where category_id = #{cateGoryId}")
    Integer selectByCategoryId(Long cateGoryId);

    @AutoFill(OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    void deleteAll(List<Long> ids);

}
