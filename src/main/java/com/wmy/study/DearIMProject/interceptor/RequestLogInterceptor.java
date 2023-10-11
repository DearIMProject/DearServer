package com.wmy.study.DearIMProject.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
/** 日志拦截器 */
public class RequestLogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("\n--------  LogInterception.preHandle  ---  ");
        log.info("Request  URL:  " + request.getRequestURL());
        log.info("Start  Time:  " + System.currentTimeMillis());
        request.setAttribute("log_startTime", startTime);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("\n-------- LogInterception.postHandle --- ");
        log.info("Request URL: " + request.getRequestURL());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("\n-------- LogInterception.afterCompletion --- ");
        long startTime = (Long) request.getAttribute("log_startTime");
        long endTime = System.currentTimeMillis();
        log.info("Request URL: " + request.getRequestURL());
        log.info("End Time: " + endTime);
        log.info("Time Taken: " + (endTime - startTime));

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
