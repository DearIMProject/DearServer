package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;

import java.util.List;

public interface IUserService extends IService<User> {
    /**
     * 注册用户
     *
     * @param email
     * @param password
     * @param confirmPwd
     * @return
     * @throws BusinessException
     */
    boolean register(String email, String password, String confirmPwd) throws BusinessException;

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     */
    String login(String email, String password) throws BusinessException;

    /**
     * 自动登录
     *
     * @param token
     * @return
     */
    String autoLogin(String token) throws BusinessException;

    /**
     * 通过token获取用户
     *
     * @param token token
     * @return user
     */
    User getFromToken(String token);

    List<UserToken> getUserTokens(String email) throws BusinessException;

    List<UserToken> getUserTokens(Long userId) throws BusinessException;
}
