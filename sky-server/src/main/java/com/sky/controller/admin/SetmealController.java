package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Api(tags = "套餐接口")
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmeal", key = "#setmealDTO.categoryId")
    public Result<String> addSetmealWithDish(@RequestBody SetmealDTO setmealDTO){
        log.info("开始新增套餐 {}", setmealDTO);
        setmealService.addSetmealWithDish(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("开始分页查询套餐 {}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("{id}")
    @ApiOperation("根据id查询套餐信息")
    public Result<SetmealVO> getById(@PathVariable("id") Long id){
        log.info("开始根据id查询套餐信息 {}", id);
        SetmealVO setmeal = setmealService.getById(id);
        return Result.success(setmeal);
    }

    @PutMapping
    @ApiOperation("修改套餐信息")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result<String> update(@RequestBody SetmealDTO setmealDTO){
        log.info("开始修改套餐 {}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result<String> changeStatus(@PathVariable Integer status,Long id){
        log.info("开始改变套餐起售状态");
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealService.changeStatus(setmeal);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("开始删除套餐 {}", ids);
        setmealService.delete(ids);
        return Result.success();
    }
}
