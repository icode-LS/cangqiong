package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.enumeration.OperationType;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {


    /**
     *
     * @param category 新增的分类
     */
    @Insert("insert into " +
            "category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " values(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void save(Category category);


    /**
     *
     * @param categoryPageQueryDTO 分页查询的条件
     * @return 查询到的数据
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     *
     * @param category 更新的信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    /**
     *
     * @param id 要删除分类的id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    /**
     *
     * @param type 查询的分类的类型
     * @return 分类列表
     */
    @Select("select * from category where type = #{type};")
    List<Category> selectByType(Integer type);

}
