package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "用户端购物车接口")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    @ApiOperation("添加进购物车")
    public Result<String> addCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("开始添加进购物车 {}", shoppingCartDTO);
        // 调用service层
        shoppingCartService.addCart(shoppingCartDTO);
        return  Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> getCartList(){
        log.info("开始查询购物车数据....");
        List<ShoppingCart> cartList = shoppingCartService.getCartList();
        return Result.success(cartList);
    }

    @PostMapping("/sub")
    @ApiOperation("删除购物车中的商品")
    public Result<String> subCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("开始删除购物车中的商品....");
        shoppingCartService.subCart(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> cleanCart(){
        log.info("开始清空购物车");
        shoppingCartService.cleanCart();
        return Result.success();
    }

}
