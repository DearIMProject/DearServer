package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.dao.IUserDao;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.log.LogUtils;
import com.wmy.study.DearIMProject.service.IEmailService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import com.wmy.study.DearIMProject.Utils.EmailUtils;
import com.wmy.study.DearIMProject.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<IUserDao, User> implements IUserService {

    @Autowired
    private IEmailService emailService;
    @Autowired
    private IUserTokenService userTokenService;

    @Value("${application.magicNumber}")
    private String magicNumber;

    public boolean register(String email, String password, String confirmPwd)
            throws BusinessException {
        if (email == null || email.length() == 0) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "empty email!");
        }
        if (password == null || password.length() == 0) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "empty password!");
        }
        if (confirmPwd == null || confirmPwd.length() == 0) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "empty confirmPassword!");
        }
        if (!password.equals(confirmPwd)) {
            throw new BusinessException(
                    ErrorCode.ERROR_CODE_PARAM, "password is not equal confirmPassword!");
        }
        //  判断email的正确性
        if (!EmailUtils.isEmail(email)) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "invalid email!");
        }
        // 判断之前是否注册过
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        User one = getOne(wrapper);
        if (one != null && one.getUserId() != 0) { // 之前已经注册过也需要返回true
            return true;
        }

        User registerUser = new User();
        registerUser.setEmail(email);
        // 对密码做md5加盐
        registerUser.setPassword(MD5Utils.MD5Upper(password, magicNumber));
        LogUtils.debug(registerUser.toString());
        boolean save = save(registerUser);

        return save;
    }

    @Override
    public String login(String email, String password) throws BusinessException {
        if (email == null || email.length() == 0 || password == null || password.length() == 0) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "email or password is empty");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("password", MD5Utils.MD5Upper(password, magicNumber));
        List<User> list = list(queryWrapper);

        log.debug("用户是否已注册");
        if (list == null || list.size() == 0 || list.size() == 1 && list.get(0).getStatus() == 0) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_NOT_FOUND, "用户暂未成功注册");
        }
        log.debug("用户userToken是否超过5个");
        User user = list.get(0);

        QueryWrapper<UserToken> tokenQueryWrapper = new QueryWrapper<>();
        tokenQueryWrapper.eq("uid", user.getUserId());
        tokenQueryWrapper.lt("expire_time", new Date().getTime());
        List<UserToken> userTokenList = userTokenService.list(tokenQueryWrapper);
        if (userTokenList.size() > 5) {
            throw new BusinessException(ErrorCode.ERROR_CODE_USER_OVER_TOKEN, "超过5台设备登录该账号！");
        }
        UserToken loginUserToken = new UserToken();
        loginUserToken.setUid(user.getUserId());
        //  生成token
        String token = userTokenService.getToken(user.getEmail());
        loginUserToken.setToken(token);
        loginUserToken.setExpireTime(new Date().getTime() + 30 * 24 * 60 * 60 * 1000); // 30天
        boolean save = userTokenService.save(loginUserToken);
        if (!save) {
            return "";
        }
        return token;
    }

    @Override
    public String autoLogin(String token) throws BusinessException {

        if (token == null || token.length() == 0) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "token 为空");
        }
        String newToken = userTokenService.checkAndRefreshToken(token);
        return newToken;
    }

    @Override
    public User getFromToken(String token) {
        QueryWrapper<UserToken> wrapper = new QueryWrapper<UserToken>();
        wrapper.eq("token", token);
        UserToken userToken = userTokenService.getOne(wrapper);
        if (userToken == null) {
            return null;
        }
        User user = getById(userToken.getUid());
        return user;
    }
}
