package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_security_code")
public class SecurityCode {
    @TableId(value = "codeId", type = IdType.AUTO)
    private Long codeId;
    /**
     * 注册时为userId 登录或者忘记密码时为userId+token
     */
    private String uniKey;
    /**
     * 验证码
     */
    private String code;
    @TableLogic
    private Boolean deleted;

    private Long expireTime;
}
