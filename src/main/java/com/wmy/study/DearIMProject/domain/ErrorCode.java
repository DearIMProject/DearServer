package com.wmy.study.DearIMProject.domain;

public enum ErrorCode {
    /**
     * 参数值不正确
     */
    ERROR_CODE_PARAM,
    // token
    ERROR_CODE_TOKEN_EXPIRE,
    /**
     * 验证码未生成
     */
    ERROR_CODE_CHECK_CODE_NOT_GENERATE,
    // 用户相关
    /**
     * 用户找不到
     */
    ERROR_CODE_USER_NOT_FOUND,
    /**
     * 过期？
     */
    ERROR_CODE_USER_OVER_TOKEN,
    /**
     * 用户已注册
     */
    ERROR_CODE_USER_HAS_REGISTERED,
    ERROR_CODE_NOT_PREMISSION,
    // 账本相关
    /**
     * 账本创建失败
     */
    ERROR_CODE_BOOK_CREATE_FAILURE,
    /**
     * 找不到账本
     */
    ERROR_CODE_BOOK_NOT_FOUND,
    /**
     * 找不到记录
     */
    ERROR_CODE_RECORD_NOT_FOUND,
}
