package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public enum MessageType implements IEnum<Integer> {

    TEXT(0, "文本"),
    PICTURE(1, "图片"),
    FILE(2, "文件"),
    LINK(3, "连接"),
    CHAT_MESSAGE(4, "聊天消息列表"),
    REQUEST_LOGIN(5, "请求登录"),
    HEART_BEAT(6, "心跳"),
    REQUEST_OFFLINE_MESSAGES(7, "请求离线消息"),
    READED_MESSAGE(8, "已读消息"),
    SEND_SUCCESS_MESSAGE(9, "发送成功"),
    TRANSPARENT_MESSAGE(10, "透传消息");

    public static MessageType fromInt(int x) {
        return switch (x) {
            case 1 -> PICTURE;
            case 2 -> FILE;
            case 3 -> LINK;
            case 4 -> CHAT_MESSAGE;
            case 5 -> REQUEST_LOGIN;
            case 6 -> HEART_BEAT;
            case 7 -> REQUEST_OFFLINE_MESSAGES;
            case 8 -> READED_MESSAGE;
            case 9 -> SEND_SUCCESS_MESSAGE;
            case 10 -> TRANSPARENT_MESSAGE;
            default -> TEXT;
        };
    }

    @JsonValue
    @EnumValue
    private final Integer type;
    private final String desc;

    MessageType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }
}
