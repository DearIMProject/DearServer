package com.wmy.study.DearIMProject.Socket;

public enum MessageEntityType {
    /*用户*/
    USER,
    /*消息群*/
    GROUP,
    SERVER;

    public static MessageEntityType fromInt(int x) {
        return switch (x) {
            case 1 -> GROUP;
            case 2 -> SERVER;
            default -> USER;
        };
    }
}
