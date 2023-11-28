package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.dao.IUserTokenDao;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import com.wmy.study.DearIMProject.utils.MD5Utils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserTokenServiceImpl extends ServiceImpl<IUserTokenDao, UserToken>
        implements IUserTokenService {
    @Override
    public String checkAndRefreshToken(String token) throws BusinessException {
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<UserToken>();
        queryWrapper.eq("token", token);
        UserToken userToken = getOne(queryWrapper);
        if (userToken == null) {
            throw new BusinessException(ErrorCode.ERROR_CODE_TOKEN_EXPIRE, "token找不到");
        }
        if (userToken.getExpireTime() + 5 * 24 * 60 * 60 * 1000
                < new Date().getTime()) { // 5天换新的token
            // 换新的token
            String newToken = getToken(userToken.getToken());
            userToken.setToken(newToken);
            QueryWrapper<UserToken> updateQuery = new QueryWrapper<>();
            updateQuery.eq("id", userToken.getTokenId());
            boolean update = update(userToken, updateQuery);
            if (update) {
                return newToken;
            }
        }
        if (userToken.getExpireTime() < new Date().getTime()) { // 已过期
            removeById(userToken.getTokenId());
            // TODO: wmy 这里要换一个新的token
            throw new BusinessException(ErrorCode.ERROR_CODE_TOKEN_EXPIRE, "token已失效");
        }
        return token;
    }

    @Override
    public String getToken(String email) {
        if (email != null) {
            String s = MD5Utils.MD5Upper(email + new Date().getTime());
            return s;
        }
        return "";
    }

    @Override
    public List<UserToken> getUserTokens(String email) throws BusinessException {
        if (email == null || email.isEmpty()) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "email 为空");
        }
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        List<UserToken> list = list(wrapper);
//        log.debug(list);
        return list;
    }

    @Override
    public UserToken getUserToken(String token) {
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("token", token);
        return getOne(wrapper);
    }

    @Override
    public boolean logout(String token) throws BusinessException {

        if (token == null || token.isEmpty()) {
            throw new BusinessException(ErrorCode.ERROR_CODE_PARAM, "token 为空");
        }

        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        UserToken userToken = getOne(queryWrapper);
        if (userToken != null) {
            boolean remove = removeById(userToken.getTokenId());
            return remove;
        }
        return false;
    }
}
