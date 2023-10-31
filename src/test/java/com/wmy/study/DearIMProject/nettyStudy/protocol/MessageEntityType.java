package com.wmy.study.DearIMProject.nettyStudy.protocol;

public enum MessageEntityType {
    /*用户*/
    USER,
    /*消息群*/
    GROUP;

    public static MessageEntityType fromInt(int x) {
        switch (x) {
            case 1:
                return GROUP;

            default:
                return USER;
        }
    }
}
