package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum MessageStatus implements IEnum<Integer> {

    STATUS_SUCCESS(0, "发送成功"),
    STATUS_DELETE(1, "已删除"),
    STATUS_RECALL(2, "撤回");

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
            case 0 -> STATUS_SUCCESS;
            case 1 -> STATUS_DELETE;
            case 2 -> STATUS_RECALL;
            default -> STATUS_SUCCESS;
        };
    }
}
