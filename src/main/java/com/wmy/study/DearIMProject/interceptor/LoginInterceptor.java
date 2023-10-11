package com.wmy.study.DearIMProject.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Utils.TimeUtils;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.UserToken;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Data
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserTokenService userTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("------ LoginInterceptor preHandle ------");
        String token = request.getParameter("token");
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("token", token);
        UserToken userToken = userTokenService.getOne(wrapper);
        if (userToken == null || TimeUtils.isExpire(userToken.getExpireTime())) {
            throw new BusinessException(ErrorCode.ERROR_CODE_TOKEN_EXPIRE, "token 不存在或已过期");
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView)
            throws Exception {
        log.info("LoginInterceptor postHandle");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.info("LoginInterceptor afterCompletion");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
