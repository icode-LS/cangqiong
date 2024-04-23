package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.dto.CouponsDTO;
import com.sky.dto.CouponsQueryDTO;
import com.sky.entity.Coupons;
import com.sky.enumeration.OperationType;
import com.sky.vo.UserCouponsVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CouponsMapper {

    Page<Coupons> pageQuery(CouponsQueryDTO couponsDTO);

    @AutoFill(OperationType.INSERT)
    void insertOne(Coupons coupons);

    @Select("select c.id as id, c.name, c.full_minus, c.reduce_price, uc.status from coupons c " +
            "join user_coupons uc on uc.coupons_id = c.id" +
            " where uc.user_id = #{userId};")
    List<UserCouponsVO> getByUserId(Long userId);

    @Select("select user_id from user_coupons where user_id = #{userId} and coupons_id = #{couponsId};")
    Long selectByCouponIdUserId(Long userId, Long couponsId);

    @AutoFill(OperationType.UPDATE)
    void update(Coupons coupons);

    @Update("update coupons set num = num - 1 where id = #{couponsId}")
    void dNum(Long couponsId);

    @Insert("insert into user_coupons (user_id, coupons_id) values (#{userId}, #{couponsId});")
    void insertToUserCoupons(Long userId, Long couponsId);
}
