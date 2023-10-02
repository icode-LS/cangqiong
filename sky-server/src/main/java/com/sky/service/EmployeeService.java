package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService{

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void addEmployee(EmployeeDTO employeeDTO);

    PageResult getPageEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    void setEmployeeStatus(Integer status,Long employeeId);

    void editEmployeeInfo(EmployeeDTO employeeDTO);

    Employee getEmployeeById(Integer id);

}
