package com.sky.service;

import com.sky.dto.CouponsDTO;
import com.sky.dto.CouponsQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.UserCouponsVO;

import java.util.List;

public interface CouponsService {

    PageResult pageQuery(CouponsQueryDTO couponsQueryDTO);

    void createCoupons(CouponsDTO couponsDTO);

    List<UserCouponsVO> getUserCoupons(Long userId);

    String snappedCoupon(Long userId, Long couponId);

}
