package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     *
     * @param cateGoryId 分类id
     * @return 该分类id关联的菜品数量
     */
    @Select("select count(id) from dish where category_id = #{cateGoryId}")
    Integer selectByCategoryId(Long cateGoryId);

    /**
     *
     * @param dish 新增的菜品
     */
    @AutoFill(OperationType.INSERT)
    void addDish(Dish dish);


    /**
     *
     * @param pageQueryDTO 分页查询的数据
     * @return 查询到满足条件的数据
     */
    Page<DishVO> pageQuery(DishPageQueryDTO pageQueryDTO);

    /**
     * @param dish 要更新的菜品信息
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据id查询
     * @param id 菜品id
     * @return 查询到的菜品
     */
    DishVO getById(Long id);

    /**
     *
     * @param ids 要删除的菜品的id
     */

    void deleteById(List<Long> ids);

}
