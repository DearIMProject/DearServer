package com.wmy.study.DearIMProject.Exception;

import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.log.LogUtils;
import lombok.Data;

@Data
public class BusinessException extends Exception {
    private ErrorCode code;

    public BusinessException(String message) {
        new BusinessException(ErrorCode.ERROR_CODE_PARAM, message);
    }

    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code;
        LogUtils.debug(message);
    }
}
