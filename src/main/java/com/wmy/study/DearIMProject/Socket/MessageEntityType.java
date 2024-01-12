package com.wmy.study.DearIMProject.Socket;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.ibatis.annotations.Case;

public enum MessageEntityType implements IEnum<Integer> {


    /*用户*/
    USER(0, "用户"),
    /*消息群*/
    GROUP(1, "群"),
    SERVER(2, "服务");

    @EnumValue
    private final Integer type;
    private final String stateName;

    public static MessageEntityType fromInt(Integer type) {

        return switch (type) {
            case 1 -> GROUP;
            case 2 -> SERVER;
            default -> USER;
        };
    }

    MessageEntityType(Integer type, String stateName) {
        this.type = type;
        this.stateName = stateName;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }

}
