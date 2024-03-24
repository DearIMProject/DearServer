package com.wmy.study.DearIMProject.Socket.message;

import com.wmy.study.DearIMProject.Socket.Message;
import com.wmy.study.DearIMProject.Socket.MessageEntityType;
import com.wmy.study.DearIMProject.Socket.MessageStatus;
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
            case HEART_BEAT -> {
                message = new HeartBeatMessage();
            }
            case REQUEST_OFFLINE_MESSAGES -> {
                message = new RequestOfflineMessage();
            }
            case SEND_SUCCESS_MESSAGE -> {
                message = new SendSuccessMessage();
            }
            case READED_MESSAGE -> {
                message = new ReadedMessage();
            }
//            case PICTURE -> {
//                message = new PictureMessage();
//            }

        }
        assert message != null;
        message.setTimestamp(new Date().getTime());
        message.setMessageType(messageType);
        message.setStatus(MessageStatus.STATUS_SUCCESS_READED);
        return message;
    }

}
