package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户
 */
@Data
@TableName("tb_user")
public class User {
    @TableField(exist = false)
    private String token;
    /**
     * token过期时间
     */
    @TableField(exist = false)
    private long expireTime;

    private String username;

    @TableId(value = "userId", type = IdType.AUTO)
    private Long userId;

    private String email;
    private String password;
    /**
     * 账户状态 0: 未注册; 1: 已注册; 2: 已注销
     */
    private Integer status;
    /**
     * 0 不是VIP 1 充值过VIP
     */
    private Integer vipStatus;
    /**
     * vip过期时间`
     */
    private Integer vipExpired;
    /**
     * 机型
     */
    private String os;
    /**
     * 注册时间
     */
    private Long registerTime;

    public String getUsername() {
        if (username == null || username.isEmpty()) {
            return email;
        }
        return username;
    }
}
