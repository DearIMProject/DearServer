package com.wmy.study.DearIMProject.Socket.message;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageType;

import javax.xml.crypto.Data;
import java.util.Date;

public class MessageFactory {
    public static Message factoryWithMessageType(MessageType messageType) {
        Message message = null;
        switch (messageType) {
            case REQUEST_LOGIN -> {
                message = new LoginRequestMessage();
                message.setFromEntity(MessageEntityType.SERVER);
                message.setToEntity(MessageEntityType.USER);
            }
            case TEXT, PICTURE, FILE, LINK, CHAT_MESSAGE -> {
                message = new ChatMessage();
            }
        }
        message.setTimestamp(new Date().getTime());
        message.setMessageType(messageType);
        return message;
    }
}
