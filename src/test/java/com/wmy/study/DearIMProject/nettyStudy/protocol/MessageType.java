package com.wmy.study.DearIMProject.nettyStudy.protocol;

public enum MessageType {

    TEXT,
    PICTURE,
    FILE,
    LINK,
    CHAT_MESSAGE;

    public static MessageType fromInt(int x) {
        switch (x) {
            case 1:
                return PICTURE;
            case 2:
                return FILE;
            case 3:
                return LINK;
            case 4:
                return CHAT_MESSAGE;
            default:
                return TEXT;
        }
    }


}
