package com.wmy.study.DearIMProject.domain;

import lombok.Data;

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
        data.put("errorMsg", errorMsg);
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
        data.put("errorMsg", errorMsg);
        this.timestamp = (new Date()).getTime();
    }
}
