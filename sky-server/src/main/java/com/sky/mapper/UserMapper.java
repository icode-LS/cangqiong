package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {


    /**
     *  根据openid查用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openId}")
    User selectByOpenId(String openId);


    /**
     *  插入数据
     * @param user
     */
    void insert(User user);


    @Select("select * from user where id = #{userId};")
    User getById(Long userId);

    @Select("select COUNT(*) from user where create_time between #{beginTime} and #{endTime};")
    Integer getUserCntByDate(LocalDateTime beginTime, LocalDateTime endTime);

    @Select("select COUNT(*) from user where create_time < #{endTime};")
    Integer  getUserCnt(LocalDateTime endTime);

    Integer countByMap(Map map);
}
