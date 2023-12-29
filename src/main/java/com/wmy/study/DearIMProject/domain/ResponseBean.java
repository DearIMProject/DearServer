package com.wmy.study.DearIMProject.domain;

import com.wmy.study.DearIMProject.SpringUtils;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseBean {
    private boolean success;
    private Map<String, Object> data;
    private long timestamp;
    private String apiName;
    private ErrorCode errorCode;
    private int code;

    @Resource
    private MessageSource messageSource = SpringUtils.getBean(MessageSource.class);

    ResponseBean() {

    }

    public ResponseBean(boolean success, Map<String, Object> data) {
        this.success = success;
        this.data = data;
        this.timestamp = (new Date()).getTime();
    }

    public ResponseBean(boolean success, ErrorCode errorCode, String errorMsg) {
        this.success = success;
        this.errorCode = errorCode;
        data = new HashMap<>();
        data.put("errorCode", errorCode.ordinal());
        data.put("errorParam", errorCode);
        String msg = messageSource.getMessage(errorMsg, null, LocaleContextHolder.getLocale());
        if (msg.isEmpty()) {
            data.put("errorMsg", errorMsg);
        } else {
            data.put("errorMsg", msg);
        }
        this.timestamp = (new Date()).getTime();
    }

    public ResponseBean(String apiName, boolean success, Map<String, Object> data) {
        this.success = success;
        this.apiName = apiName;
        this.data = data;
        this.timestamp = (new Date()).getTime();
    }

    public ResponseBean(String apiName, boolean success, ErrorCode errorCode, String errorMsg) {
        this.success = success;
        this.apiName = apiName;
        this.errorCode = errorCode;
        data = new HashMap<>();
        data.put("errorCode", errorCode.ordinal());
        data.put("errorParam", errorCode);
        String msg = messageSource.getMessage(errorMsg, null, LocaleContextHolder.getLocale());
        if (msg.isEmpty()) {
            data.put("errorMsg", errorMsg);
        } else {
            data.put("errorMsg", msg);
        }
        this.timestamp = (new Date()).getTime();
    }
}
