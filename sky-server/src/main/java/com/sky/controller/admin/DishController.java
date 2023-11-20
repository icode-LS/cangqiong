package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class  DishController {


    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> newDish(@RequestBody DishDTO dishDTO){
        log.info("开始新增菜品 {}",dishDTO);
        dishService.addDishWithFlavor(dishDTO);
        // 清理缓存
        String redisKey = "dish_"+dishDTO.getCategoryId();
        cleanCache(redisKey);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售或停售")
    public Result<String> changeStatus(@PathVariable("status") Integer status, Long id){
        log.info("开始改变菜品的状态 {}", status);
        DishDTO dishDTO = new DishDTO();
        dishDTO.setStatus(status);
        dishDTO.setId(id);
        dishService.update(dishDTO);
        // 清理所有菜品缓存删除
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("开始菜品的分页查询 ... {}", dishPageQueryDTO);
        PageResult dishVOS = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(dishVOS);
    }

    @PutMapping
    @ApiOperation("更新菜品信息")
    public Result<String> updateDish(@RequestBody DishDTO dishDTO){
        log.info("开始更新菜品信息 {}",dishDTO);
        dishService.update(dishDTO);
        // 清理所有菜品缓存删除
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品信息")
    public Result<DishVO> getDishById(@PathVariable("id") Long id){
        log.info("开始根据id查询菜品信息 {}", id);
        DishVO dish = dishService.getById(id);
        return Result.success(dish);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("开始批量删除菜品 {}", ids);
        dishService.deleteDishes(ids);
        // 清理所有菜品缓存删除
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(DishDTO dishDTO){
        log.info("开始根据分类id查询菜品 {}", dishDTO);
        return Result.success(dishService.getByCategoryId(dishDTO));
    }

    private void cleanCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
