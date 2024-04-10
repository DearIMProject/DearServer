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

    /**
     * 找不到记录
     */
    ERROR_CODE_RECORD_NOT_FOUND,

    ERROR_CODE_FILE,
    ERROR_CODE_EMPTY_PARAM,
    ERROR_CODE_CREATE_FAILURE,
    ERROR_CODE_NO_PERMISSION,
    /**
     * 添加失败
     */
    ERROR_CODE_ADD_FAILURE,
    ERROR_CODE_DELETE_FAILURE,

    //  --- 群组 ---
    /**
     * 群组找不到
     */
    ERROR_CODE_GROUP_NOT_EXIST,
    /**
     * 群组已存在
     */
    ERROR_CODE_GROUP_HAS_EXIST,
    /**
     * 群组已存在用户
     */
    ERROR_CODE_GROUP_HAS_EXIST_USER,


}
