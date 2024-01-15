package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum MessageStatus implements IEnum<Integer> {

    STATUS_SUCCESS_UNREADED(0, "发送成功且未读"),
    STATUS_SUCCESS_READED(1, "发送成功且已读"),
    STATUS_NOT_SEND_UNREAD(2, "未发送");

    @EnumValue
    private final Integer type;
    private final String stateName;

    @Override
    public Integer getValue() {
        return this.type;
    }

    MessageStatus(Integer type, String stateName) {
        this.type = type;
        this.stateName = stateName;
    }

    public static MessageStatus fromInt(Integer type) {

        return switch (type) {
            case 1 -> STATUS_SUCCESS_READED;
            case 2 -> STATUS_NOT_SEND_UNREAD;
            default -> STATUS_SUCCESS_UNREADED;
        };
    }
}
