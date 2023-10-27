package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

public interface UserService {


    /**
     * 微信登录
     * @param userLoginDTO 微信登录用户信息
     * @return 包含openid的user对象
     */
    User login(UserLoginDTO userLoginDTO);

}
