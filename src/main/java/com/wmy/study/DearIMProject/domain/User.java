package com.wmy.study.DearIMProject.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @TableLogic
    private Boolean deleted;
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
     * 注册时间
     */
    private Long registerTime;

    private String icon;
    /**
     * 用户的好用列表
     */
    private String userIds;
    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<Long> contentUserIds;
    /**
     * 用户的群组列表
     */
    private String groupIds;
    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<Long> contentGroupIds;

    public List<Long> getContentGroupIds() {
        String[] numberStrings = groupIds.split(",");

        // 创建一个Long类型的数组
        List<Long> longNumbers = new ArrayList<>();

        // 将字符串数组转换为Long数组
        for (String numberString : numberStrings) {
            longNumbers.add(Long.parseLong(numberString));
        }
        return longNumbers;
    }

    public void setContentGroupIds(List<Long> contentGroupIds) {
        StringBuilder sb = new StringBuilder();
        for (Long userId : contentUserIds) {
            sb.append(userId).append(",");
        }
        this.groupIds = sb.toString();
    }

    public void setContentUserIds(List<Long> contentUserIds) {
        this.contentUserIds = contentUserIds;
        StringBuilder sb = new StringBuilder();
        for (Long userId : contentUserIds) {
            sb.append(userId).append(",");
        }
        this.userIds = sb.toString();
    }

    public List<Long> getContentUserIds() {
        String[] numberStrings = userIds.split(",");

        // 创建一个Long类型的数组
        List<Long> longNumbers = new ArrayList<>();

        // 将字符串数组转换为Long数组
        for (String numberString : numberStrings) {
            longNumbers.add(Long.parseLong(numberString));
        }
        return longNumbers;
    }

    public String getUsername() {
        if (username == null || username.isEmpty()) {
            return email;
        }
        return username;
    }
}
