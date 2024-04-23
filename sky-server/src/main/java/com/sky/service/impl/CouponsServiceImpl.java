package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.CouponsDTO;
import com.sky.dto.CouponsQueryDTO;
import com.sky.entity.Coupons;
import com.sky.mapper.CouponsMapper;
import com.sky.result.PageResult;
import com.sky.service.CouponsService;
import com.sky.vo.UserCouponsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.PathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CouponsServiceImpl implements CouponsService {

    @Autowired
    private CouponsMapper couponsMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult pageQuery(CouponsQueryDTO couponsQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(couponsQueryDTO.getPage(), couponsQueryDTO.getPageSize());

        // 查询数据
        Page<Coupons> pageCoupons = couponsMapper.pageQuery(couponsQueryDTO);

        return new PageResult(pageCoupons.getPages(), pageCoupons.getResult());
    }

    @Override
    public void createCoupons(CouponsDTO couponsDTO) {
        Coupons coupons = new Coupons();
        BeanUtils.copyProperties(couponsDTO, coupons);

        // 插入数据库
        couponsMapper.insertOne(coupons);

        // 更新缓存信息
        rabbitTemplate.convertAndSend("coupons.exchange", "updateOrInsert", coupons);
    }

    @Override
    public List<UserCouponsVO> getUserCoupons(Long userId) {
        // 获取数据
        List<UserCouponsVO> userCoupons = couponsMapper.getByUserId(userId);

        return userCoupons;
    }

    @Override
    public String snappedCoupon(Long userId, Long couponId) {
        // 先检查是否已经抢过了
        boolean isM = !(couponsMapper.selectByCouponIdUserId(userId, couponId) == null);
        if(isM){
            return "已经拥有该优惠券！";
        }
        // 执行lua脚本
        String key = "coupons:"+couponId;
        List<String> keys = Collections.singletonList(key);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new PathResource("sky-server/src/main/java/com/sky/lua/coupons.lua")));
        script.setResultType(Long.class);

        // 执行
        Long r = stringRedisTemplate.execute(script, keys);
        if(r == 0){
            return "数量不足！";
        }

        couponsMapper.insertToUserCoupons(userId, couponId);

        rabbitTemplate.convertAndSend("coupons.exchange", "snapped", couponId);
        return "抢到优惠券！";
    }
}
