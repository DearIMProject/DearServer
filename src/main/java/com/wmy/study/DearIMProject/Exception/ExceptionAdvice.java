package com.wmy.study.DearIMProject.Exception;

import com.wmy.study.DearIMProject.domain.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class) // 参数校验异常
    public ResponseBean ConstraintViolationExceptionHandler(BusinessException ex) {
        ResponseBean bean = new ResponseBean(false, ex.getCode(), ex.getMessage());
        log.warn(bean.toString());
        return bean;
    }
}
