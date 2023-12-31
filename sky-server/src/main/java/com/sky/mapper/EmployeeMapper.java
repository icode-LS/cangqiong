package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into employee(name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)" +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})"
            )
    void addEmployee(Employee employee);

    /**
     *
     * @param employeePageQueryDTO 查询信息
     * @return
     */
    Page<Employee> getPageEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     *
     * @param employee 更新的员工的信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    /**
     *
     * @param id 员工id
     * @return 员工信息
     */
    @Select("select * from employee where id = #{id};")
    Employee getById(Integer id);

}
