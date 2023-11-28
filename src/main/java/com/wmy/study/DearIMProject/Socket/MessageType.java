package com.wmy.study.DearIMProject.Socket;

public enum MessageType {

    TEXT,
    PICTURE,
    FILE,
    LINK,
    CHAT_MESSAGE,
    REQUEST_LOGIN,
    HEART_BEAT,
    REQUEST_OFFLINE_MESSAGES,
    READED_MESSAGE;

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
            default -> TEXT;
        };
    }


}
