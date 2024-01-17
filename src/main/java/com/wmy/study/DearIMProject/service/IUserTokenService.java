package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;

import java.util.List;

public interface IUserTokenService extends IService<UserToken> {
    /**
     * 判断并刷新Token
     *
     * @param token
     * @return
     */
    String checkAndRefreshToken(String token) throws BusinessException;

    /**
     * 获取token
     *
     * @param token
     * @return
     */
    String getToken(String token);


    UserToken getUserToken(String token);

    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    boolean logout(String token) throws BusinessException;
}
