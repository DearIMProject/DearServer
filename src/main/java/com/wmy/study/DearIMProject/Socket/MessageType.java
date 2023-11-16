package com.wmy.study.DearIMProject.Socket;

public enum MessageType {

    TEXT,
    PICTURE,
    FILE,
    LINK,
    CHAT_MESSAGE,
    REQUEST_LOGIN;

    public static MessageType fromInt(int x) {
        return switch (x) {
            case 1 -> PICTURE;
            case 2 -> FILE;
            case 3 -> LINK;
            case 4 -> CHAT_MESSAGE;
            case 5 -> REQUEST_LOGIN;
            default -> TEXT;
        };
    }


}
