package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.enumeration.OperationType;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

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
    void save(Category category);


    /**
     *
     * @param categoryPageQueryDTO 分页查询的条件
     * @return 查询到的数据
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     *
     *
     * @param id 要更改状态的分类的Id
     * @param status 更改后的状态
     */
    @Update("update category set status = #{status} where id = #{id}")
    void setStatus(Long id, Integer status);

    /**
     *
     * @param categoryDTO 更新的信息
     */
    void update(CategoryDTO categoryDTO);

    /**
     *
     * @param id 要删除分类的id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);


}
