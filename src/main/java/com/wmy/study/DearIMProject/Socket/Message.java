package com.wmy.study.DearIMProject.Socket;

import lombok.Data;


@Data
public class Message {
    /*消息id*/
    private long msgId;
    private long fromId;
    private MessageEntityType fromEntity;
    private long toId;
    private MessageEntityType toEntity;
    /*消息内容*/
    private String content;
    private MessageType messageType;
    private long timestamp;
}
