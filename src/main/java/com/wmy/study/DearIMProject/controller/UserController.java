package com.wmy.study.DearIMProject.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.*;
import com.wmy.study.DearIMProject.service.IEmailService;
import com.wmy.study.DearIMProject.service.ISecurityCodeService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private IUserTokenService userTokenService;
    @Resource
    private IEmailService emailService;
    @Resource
    private ISecurityCodeService securityCodeService;

    /**
     * 注册
     *
     * @param email           邮箱
     * @param password        密码
     * @param confirmPassword 确认密码
     * @return 返回成功或者失败
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseBean register(String email, String password, String confirmPassword)
            throws BusinessException, MessagingException {
        log.debug("/user/register");
        boolean isRegisterSuccess = userService.register(email, password, confirmPassword);
        if (!isRegisterSuccess) {
            log.debug("isRegisterSuccess = false");
            return new ResponseBean(false, null);
        }
        // 如果已经有验证码，且没有过期，就不send
        // 生成验证码
        SecurityCode registerCode = new SecurityCode();
        registerCode.setUniKey(email);
        SecurityCode securityCode = securityCodeService.checkAndSave(registerCode);
        log.debug(securityCode.toString());
        boolean sendSuccess = emailService.sendSecurityCodeToEmail(securityCode);
        if (sendSuccess) {
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, null);
    }

    /**
     * 注销用户
     *
     * @param token 用户登录token
     * @return 注销成功
     */
    @PostMapping("/unregister")
    @ResponseBody
    public ResponseBean unregister(String token) {
        log.debug("/user/unregister");
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("token", token);
        UserToken userToken = userTokenService.getOne(wrapper);
        if (userToken != null && userToken.getUid() != 0) {
            User user = userService.getById(userToken.getUid());
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            user.setStatus(2);
            updateWrapper.setEntity(user);
            boolean updated = userService.update(updateWrapper);
            if (updated) {
                return new ResponseBean(true, null);
            } else {
                return new ResponseBean(false, ErrorCode.ERROR_CODE_PARAM, "注销失败！");
            }
        } else {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_USER_NOT_FOUND, "用户找不到！");
        }
    }

    /**
     * 判断验证码是否一致
     *
     * @param email 邮箱
     * @param code  验证码
     * @return 返回是否一致
     */
    @PostMapping("/checkCode")
    @ResponseBody
    public ResponseBean checkCode(String email, String code) throws BusinessException {
        boolean isRight = securityCodeService.checkCodeIsRight(email, code);
        if (isRight) {
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, ErrorCode.ERROR_CODE_PARAM, "验证码不正确或已过期！");
    }

    /**
     * 登录
     *
     * @param email    邮箱
     * @param password 密码
     * @return 返回登录成功或者失败
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseBean login(String email, String password) throws BusinessException {
        log.debug("email = " + email);
        log.debug("password = " + password);
        String token = userService.login(email, password);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        User user = userService.getOne(wrapper);
        user.setToken(token);
        QueryWrapper<UserToken> tokenQueryWrapper = new QueryWrapper<>();
        tokenQueryWrapper.eq("token", token);
        UserToken one = userTokenService.getOne(tokenQueryWrapper);
        user.setExpireTime(one.getExpireTime());

        if (token != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user", user);
            return new ResponseBean(true, hashMap);
        }
        return new ResponseBean(false, null);
    }

    /**
     * 自动登录
     *
     * @param token token
     * @return bean
     * @throws BusinessException
     */
    @RequestMapping("/autologin")
    @ResponseBody
    public ResponseBean autoLogin(String token) throws BusinessException {
        String resultToken = userService.autoLogin(token);
        if (resultToken != null) {
            QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
            wrapper.eq("token", token);
            UserToken userToken = userTokenService.getOne(wrapper);
            User user = userService.getById(userToken.getUid());
            user.setToken(resultToken);
            user.setExpireTime(userToken.getExpireTime());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user", user);
            return new ResponseBean(true, hashMap);
        }
        return new ResponseBean(false, null);
    }

    @RequestMapping("/sendCheckCode")
    @ResponseBody
    public ResponseBean sendCheckCode(String email) throws BusinessException {
        SecurityCode code = securityCodeService.sendCheckCode(email);
        return new ResponseBean(code != null, null);
    }

    /**
     * 获取所有的tokens
     *
     * @param email 邮箱
     * @return bean
     */
    @RequestMapping("/userTokens")
    @ResponseBody
    public ResponseBean userTokens(String email) throws BusinessException {

        List<UserToken> userTokens = userService.getUserTokens(email);
        if (userTokens != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("tokens", userTokens);
            return new ResponseBean(true, map);
        }
        return new ResponseBean(true, null);
    }

    //
    @RequestMapping("/logoutToken")
    @ResponseBody
    public ResponseBean logoutToken(String token) throws BusinessException {
        boolean success = userTokenService.logout(token);
        return new ResponseBean(success, null);
    }

    /**
     * 获取用户信息
     *
     * @param email 邮箱
     * @return bean
     */
    @RequestMapping("/userInfo")
    @ResponseBody
    public ResponseBean userInfo(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        User user = userService.getOne(wrapper);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        return new ResponseBean(true, map);
    }

    /**
     * 登出逻辑
     *
     * @param token 用户token
     * @return 登出成功或失败
     */
    @ResponseBody
    @RequestMapping("/logout")
    public ResponseBean logout(String token) throws BusinessException {
        boolean isLogout = userTokenService.logout(token);

        if (isLogout) {
            return new ResponseBean(true, null);
        }
        return new ResponseBean(false, null);
    }

    @ResponseBody
    @RequestMapping("/clearToken")
    public ResponseBean clearToken() {
        userTokenService.remove(null);
        return new ResponseBean(true, null);
    }
}
